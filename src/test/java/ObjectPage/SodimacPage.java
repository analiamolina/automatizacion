package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.text.Normalizer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class SodimacPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    // Encabezado y categoría
    private final By encabezadoPrincipal = By.cssSelector(
            "div.banner-plp.mt20 img.dn-sm"
    );

    private final By tituloCategoria = By.cssSelector(
            "main h1, h1"
    );

    // Selectores actualizados para capturar las tarjetas de productos de Sodimac basados en el DOM actual
    private final By tarjetasProductos = By.cssSelector(
            "[data-testid='ssr-pod'], " +
                    "[data-pod*='product'], " +
                    "[data-testid*='product'], " +
                    "[class*='pod-item'], " +
                    "article"
    );

    public SodimacPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException(
                    "WebDriver no puede ser null."
            );
        }

        this.driver = driver;
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(30)
        );
        this.js = (JavascriptExecutor) driver;
    }

    // =========================
    // CARGA DE LA PÁGINA
    // =========================

    public void esperarCargaVisual() {
        wait.until(d ->
                "complete".equals(
                        js.executeScript("return document.readyState")
                )
        );

        try {
            wait.until(d -> tituloCategoriaVisible());
        } catch (Exception e) {
            System.out.println("Aviso: El título de categoría tardó o no se encontró con el selector actual.");
        }

        for (int i = 0; i < 3; i++) {
            js.executeScript(
                    "window.scrollBy(0, Math.max(window.innerHeight * 0.75, 400));"
            );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }

            if (!obtenerTarjetasValidas().isEmpty()) {
                break;
            }
        }

        js.executeScript("window.scrollTo(0, 0);");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    // =========================
    // ENCABEZADO Y CATEGORÍA
    // =========================

    public boolean tituloCategoriaVisible() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(tituloCategoria)
            ).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean encabezadoPrincipalVisible() {
        try {
            WebElement encabezado = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            encabezadoPrincipal
                    )
            );

            Boolean imagenCargada = (Boolean)
                    ((JavascriptExecutor) driver).executeScript(
                            "return arguments[0].complete && " +
                                    "arguments[0].naturalWidth > 0;",
                            encabezado
                    );

            return encabezado.isDisplayed()
                    && Boolean.TRUE.equals(imagenCargada);

        } catch (Exception e) {
            return false;
        }
    }

    public boolean contenedorProductosVisible() {
        return !obtenerTarjetasValidas().isEmpty();
    }

    // =========================
    // TARJETAS DE PRODUCTOS
    // =========================

    public List<WebElement> obtenerTarjetasValidas() {
        List<WebElement> candidatas;
        try {
            candidatas = driver.findElements(tarjetasProductos);
        } catch (org.openqa.selenium.NoSuchWindowException e) {
            return new ArrayList<>();
        }

        List<WebElement> tarjetas = new ArrayList<>();

        for (WebElement candidata : candidatas) {
            try {
                if (estaVisible(candidata)
                        && tieneDatosProducto(candidata)) {
                    tarjetas.add(candidata);
                }
            } catch (StaleElementReferenceException | org.openqa.selenium.NoSuchWindowException ignored) {
                // El DOM pudo actualizarse o la ventana cerrarse durante la consulta.
            }
        }

        List<WebElement> resultado = new ArrayList<>();

        for (WebElement candidata : tarjetas) {
            boolean contieneTarjetaMasEspecifica = false;

            for (WebElement otra : tarjetas) {
                if (candidata.equals(otra)) {
                    continue;
                }

                try {
                    boolean contiene = Boolean.TRUE.equals(
                            js.executeScript(
                                    "return arguments[0].contains(arguments[1]);",
                                    candidata, otra
                            )
                    );

                    if (contiene) {
                        contieneTarjetaMasEspecifica = true;
                        break;
                    }
                } catch (Exception ignored) {
                    // Se omite el elemento que dejó de existir.
                }
            }

            if (!contieneTarjetaMasEspecifica) {
                resultado.add(candidata);
            }
        }

        return resultado;
    }

    public int cantidadProductosVisibles() {
        return obtenerTarjetasValidas().size();
    }

    private boolean tieneDatosProducto(WebElement tarjeta) {
        return !obtenerNombre(tarjeta).isBlank()
                && !obtenerPrecio(tarjeta).isBlank()
                && !obtenerEnlace(tarjeta).isBlank();
    }

    private boolean estaVisible(WebElement elemento) {
        try {
            if (driver == null) {
                return false;
            }
            return elemento.isDisplayed();
        } catch (org.openqa.selenium.NoSuchWindowException e) {
            return false;
        } catch (StaleElementReferenceException e) {
            return false;
        } catch (org.openqa.selenium.WebDriverException e) {
            return false;
        }
    }

    private String textoVisible(WebElement elemento) {
        try {
            String texto = elemento.getText();
            return texto == null ? "" : texto.trim();
        } catch (StaleElementReferenceException e) {
            return "";
        }
    }

    private WebElement primeraCoincidencia(
            WebElement padre, By selector) {
        for (WebElement elemento :
                padre.findElements(selector)) {
            if (estaVisible(elemento)) {
                return elemento;
            }
        }
        return null;
    }

    // =========================
    // NOMBRE DEL PRODUCTO
    // =========================

    private String obtenerNombre(WebElement tarjeta) {
        WebElement nombre = primeraCoincidencia(
                tarjeta,
                By.cssSelector(
                        "[id*='testId-pod-displaySubTitle'], " +
                                "[itemprop='name'], " +
                                "[class*='product-name'], " +
                                "[class*='productName'], " +
                                "[class*='product-title'], " +
                                "[class*='productTitle'], " +
                                "h2, h3, b"
                )
        );

        if (nombre != null) {
            String texto = textoVisible(nombre);

            if (!texto.isBlank()) {
                return texto;
            }

            String aria = nombre.getAttribute("aria-label");

            if (aria != null && !aria.isBlank()) {
                return aria.trim();
            }
        }

        WebElement enlace = primeraCoincidencia(
                tarjeta, By.cssSelector("a[href]")
        );

        if (enlace != null) {
            String aria = enlace.getAttribute("aria-label");

            if (aria != null && !aria.isBlank()) {
                return aria.trim();
            }
        }

        WebElement imagen = obtenerImagen(tarjeta);

        if (imagen != null) {
            String alt = imagen.getAttribute("alt");

            if (alt != null && !alt.isBlank()) {
                return alt.trim();
            }
        }

        return "";
    }

    // =========================
    // PRECIO DEL PRODUCTO
    // =========================

    private String obtenerPrecio(WebElement tarjeta) {
        WebElement precio = primeraCoincidencia(
                tarjeta,
                By.cssSelector(
                        "[id*='testId-pod-prices'], " +
                                "[data-event-price], " +
                                "[data-normal-price], " +
                                "[itemprop='price'], " +
                                "[data-testid*='price'], " +
                                "[class*='prices-'], " +
                                "[class*='product-price'], " +
                                "[class*='price']"
                )
        );

        if (precio == null) {
            return "";
        }

        String texto = textoVisible(precio);

        if (!texto.isBlank()) {
            return texto;
        }

        String dataEvent = precio.getAttribute("data-event-price");
        if (dataEvent != null && !dataEvent.isBlank()) {
            return "$" + dataEvent.trim();
        }

        String dataNormal = precio.getAttribute("data-normal-price");
        if (dataNormal != null && !dataNormal.isBlank()) {
            return "$" + dataNormal.trim();
        }

        String contenido = precio.getAttribute("content");

        return contenido == null ? "" : contenido.trim();
    }
    public boolean todosLosPreciosSonCLP() {
        List<WebElement> tarjetas = obtenerTarjetasValidas();

        if (tarjetas.isEmpty()) {
            return false;
        }

        // Expresión regular flexible para aceptar formatos de precios chilenos (CLP) como "$ 28.990", "$28.990", "28.990", etc.
        Pattern precioCLP = Pattern.compile(
                "^.*\\$?\\s*[0-9]{1,3}(\\.[0-9]{3})*([\\.,][0-9]+)?.*$"
        );

        for (WebElement tarjeta : tarjetas) {
            String precio = obtenerPrecio(tarjeta);

            if (precio == null || precio.isBlank()) {
                return false;
            }

            // Limpiamos saltos de línea por si el bloque trae precios anteriores y actuales juntos
            String primerPrecio = precio.split("\\r?\\n")[0].trim();

            if (!precioCLP.matcher(primerPrecio).matches() && !primerPrecio.contains("$")) {
                return false;
            }
        }

        return true;
    }

    // =========================
    // ENLACE DEL PRODUCTO
    // =========================

    private String obtenerEnlace(WebElement tarjeta) {
        WebElement enlace = primeraCoincidencia(
                tarjeta, By.cssSelector("a[href], [data-testid*='product-link'] a, a")
        );

        if (enlace == null) {
            String hrefTarjeta = tarjeta.getAttribute("href");
            if (hrefTarjeta != null && !hrefTarjeta.isBlank()) {
                return hrefTarjeta.trim();
            }
            return "";
        }

        String href = enlace.getAttribute("href");

        return href == null ? "" : href.trim();
    }

    public boolean todosLosProductosTienenEnlace() {
        List<WebElement> tarjetas = obtenerTarjetasValidas();

        if (tarjetas.isEmpty()) {
            return false;
        }

        for (WebElement tarjeta : tarjetas) {
            String href = obtenerEnlace(tarjeta);

            if (href.isBlank()
                    || !(href.startsWith("https://")
                    || href.startsWith("http://"))) {
                return false;
            }
        }

        return true;
    }

    // =========================
    // IMÁGENES
    // =========================

    private WebElement obtenerImagen(WebElement tarjeta) {
        return primeraCoincidencia(
                tarjeta,
                By.cssSelector("picture img, img[id*='testId-pod-image'], img[src*='sodimac'], img[src*='falabella'], img")
        );
    }

    public boolean todosLosProductosTienenImagen() {
        List<WebElement> tarjetas = obtenerTarjetasValidas();

        if (tarjetas.isEmpty()) {
            return false;
        }

        for (WebElement tarjeta : tarjetas) {
            WebElement imagen = obtenerImagen(tarjeta);

            if (imagen == null) {
                return false;
            }

            try {
                // Forzar scroll hacia la tarjeta para activar la carga perezosa
                js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", tarjeta);

                Object cargada = js.executeScript(
                        "return arguments[0].complete " +
                                "&& typeof arguments[0].naturalWidth != 'undefined' " +
                                "&& arguments[0].naturalWidth > 0;",
                        imagen
                );

                if (Boolean.TRUE.equals(cargada)) {
                    continue;
                }

                String src = imagen.getAttribute("src");
                String srcset = imagen.getAttribute("srcset");
                String dataSrc = imagen.getAttribute("data-src");

                boolean tieneFuenteValida = (src != null && !src.isBlank() && !src.contains("data:image/svg"))
                        || (srcset != null && !srcset.isBlank())
                        || (dataSrc != null && !dataSrc.isBlank());

                if (!tieneFuenteValida) {
                    return false;
                }

            } catch (StaleElementReferenceException e) {
                return false;
            }
        }

        js.executeScript("window.scrollTo(0, 0);");
        return true;
    }

    public boolean todosLosNombresNoEstanVacios() {
        List<WebElement> tarjetas = obtenerTarjetasValidas();

        return !tarjetas.isEmpty()
                && tarjetas.stream().allMatch(
                tarjeta -> !obtenerNombre(tarjeta).isBlank()
        );
    }

    // =========================
    // SUPERPOSICIÓN DE TARJETAS
    // =========================

    public boolean noHayTarjetasSuperpuestas() {
        List<WebElement> tarjetas = obtenerTarjetasValidas();

        for (int i = 0; i < tarjetas.size(); i++) {
            for (int j = i + 1; j < tarjetas.size(); j++) {
                try {
                    Object superpuestas = js.executeScript(
                            "const a = arguments[0].getBoundingClientRect();" +
                                    "const b = arguments[1].getBoundingClientRect();" +
                                    "const ancho = Math.min(a.right,b.right) -" +
                                    " Math.max(a.left,b.left);" +
                                    "const alto = Math.min(a.bottom,b.bottom) -" +
                                    " Math.max(a.top,b.top);" +
                                    "return ancho > 1 && alto > 1;",
                            tarjetas.get(i), tarjetas.get(j)
                    );

                    if (Boolean.TRUE.equals(superpuestas)) {
                        return false;
                    }

                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }
        }

        return true;
    }

    // =========================
    // TARJETAS DUPLICADAS
    // =========================

    private String normalizar(String texto) {
        return Normalizer.normalize(
                        texto == null ? "" : texto,
                        Normalizer.Form.NFD
                )
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String obtenerFuenteImagen(WebElement tarjeta) {
        WebElement imagen = obtenerImagen(tarjeta);

        if (imagen == null) {
            return "";
        }

        String src = imagen.getAttribute("src");

        if (src == null || src.isBlank()) {
            src = imagen.getAttribute("data-src");
        }

        if (src == null || src.isBlank()) {
            src = imagen.getAttribute("srcset");
        }

        return src == null ? "" : src.trim();
    }

    public boolean noHayProductosDuplicados() {
        Set<String> productos = new HashSet<>();

        for (WebElement tarjeta : obtenerTarjetasValidas()) {
            String nombre = normalizar(obtenerNombre(tarjeta));
            String precio = normalizar(obtenerPrecio(tarjeta));
            String imagen = normalizar(obtenerFuenteImagen(tarjeta));

            String clave = nombre + "|" + precio + "|" + imagen;

            if (!productos.add(clave)) {
                return false;
            }
        }

        return true;
    }
}