
package ObjectPage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Control.BaseController;
import Control.DriverContext;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Page extends BaseController {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Filtros de fecha
    private final By fromDate = By.xpath(
            "//label[contains(normalize-space(),'From Date')]/ancestor::div[contains(@class,'oxd-input-group')]//input"
    );

    private final By toDate = By.xpath(
            "//label[contains(normalize-space(),'To Date')]/ancestor::div[contains(@class,'oxd-input-group')]//input"
    );

    // Empleado y autocompletado
    private final By employeeName = By.xpath(
            "//label[contains(normalize-space(),'Employee Name')]/ancestor::div[contains(@class,'oxd-input-group')]//input"
    );

    private final By autocompleteOptions = By.xpath(
            "//div[@role='listbox']//div[@role='option']"
    );

    // Unidad organizativa
    private final By subUnitDropdown = By.xpath(
            "//label[contains(normalize-space(),'Sub Unit')]/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]"
    );

    // Estado de solicitud
    private final By statusDropdown = By.xpath(
            "//label[contains(normalize-space(),'Show Leave with Status')]/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]"
    );

    // Empleados anteriores
    private final By pastEmployeeCheckbox = By.xpath(
            "//label[contains(normalize-space(),'Include Past Employees')]/preceding-sibling::input"
    );

    // Botones
    private final By searchButton = By.xpath(
            "//button[normalize-space()='Search']"
    );

    private final By resetButton = By.xpath(
            "//button[normalize-space()='Reset']"
    );

    // Tabla de resultados
    private final By resultRows = By.xpath(
            "//div[contains(@class,'oxd-table-body')]//div[contains(@class,'oxd-table-card')]"
    );

    private final By tableHeaders = By.xpath(
            "//div[contains(@class,'oxd-table-header')]//div[contains(@class,'oxd-table-cell')]"
    );

    private final By nextPageButton = By.xpath(
            "//button[.//i[contains(@class,'right')]]"
    );

    private final By loadingSpinner = By.cssSelector(
            ".oxd-loading-spinner"
    );

    public Page() {
        this.driver = DriverContext.getDriver();
        if (this.driver == null) {
            throw new IllegalStateException("El WebDriver no está inicializado");
        }
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(15)
        );
    }

    // ---------- Navegación ----------

    public void esperarPantalla() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                searchButton
        ));
    }

    // ---------- Fechas ----------

    private void ingresarFecha(By locator, String fecha) {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});",
                input
        );
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(fecha);
        input.sendKeys(Keys.TAB);
    }

    public void ingresarFechaInicio(String fecha) {
        ingresarFecha(fromDate, fecha);
    }

    public void ingresarFechaFin(String fecha) {
        ingresarFecha(toDate, fecha);
    }

    // ---------- Empleado ----------

    public void ingresarEmpleado(String empleado) {
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(employeeName)
        );

        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(empleado);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        autocompleteOptions
                )
        );
    }

    public void seleccionarEmpleadoAutocompletado(
            String empleado) {

        List<WebElement> opciones = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        autocompleteOptions
                )
        );

        for (WebElement opcion : opciones) {
            if (opcion.getText().trim().equalsIgnoreCase(empleado)) {
                opcion.click();
                return;
            }
        }

        throw new AssertionError(
                "No se encontró el empleado en el autocompletado: "
                        + empleado
        );
    }

    // ---------- Listas desplegables ----------

    private void seleccionarOpcion(
            By dropdown, String valor) {

        wait.until(
                ExpectedConditions.elementToBeClickable(dropdown)
        ).click();

        By opcion = By.xpath(
                "//div[@role='option' and normalize-space()='"
                        + valor + "']"
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(opcion)
        ).click();
    }

    public void seleccionarEstado(String estado) {
        seleccionarOpcion(statusDropdown, estado);
    }

    public void seleccionarUnidad(String unidad) {
        seleccionarOpcion(subUnitDropdown, unidad);
    }

    // ---------- Empleados anteriores ----------

    public void configurarEmpleadosAnteriores(String valor) {
        WebElement checkbox = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        pastEmployeeCheckbox
                )
        );

        boolean debeEstarSeleccionado =
                valor.equalsIgnoreCase("Yes");

        if (checkbox.isSelected() != debeEstarSeleccionado) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            pastEmployeeCheckbox
                    )
            ).click();
        }
    }

    // ---------- Búsqueda y reset ----------

    public void buscar() {
        wait.until(
                ExpectedConditions.elementToBeClickable(searchButton)
        ).click();

        esperarCargaFinalizada();
    }

    public void resetear() {
        wait.until(
                ExpectedConditions.elementToBeClickable(resetButton)
        ).click();

        esperarCargaFinalizada();
    }

    private void esperarCargaFinalizada() {
        try {
            wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            loadingSpinner
                    )
            );
        } catch (org.openqa.selenium.TimeoutException e) {
            // Puede que esta versión no muestre un spinner.
        }
    }

    // ---------- Resultados ----------

    public boolean tablaVisible() {
        try {
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            tableHeaders
                    )
            );
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public List<String> obtenerEncabezados() {
        List<String> encabezados = new ArrayList<>();

        for (WebElement header : driver.findElements(tableHeaders)) {
            encabezados.add(header.getText().trim());
        }

        return encabezados;
    }

    public List<String> obtenerFilasPaginaActual() {
        wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".oxd-table-body")
                )
        );

        List<String> filas = new ArrayList<>();

        for (WebElement fila : driver.findElements(resultRows)) {
            String texto = fila.getText().trim();

            if (!texto.isEmpty()) {
                filas.add(texto.replaceAll("\\s+", " "));
            }
        }

        return filas;
    }

    public List<String> obtenerTodasLasFilas() {
        List<String> todasLasFilas = new ArrayList<>();
        Set<String> paginasVisitadas = new HashSet<>();

        while (true) {
            List<String> filasPagina = obtenerFilasPaginaActual();

            for (String fila : filasPagina) {
                todasLasFilas.add(fila);
            }

            String firmaPagina = String.join("|", filasPagina);

            if (!paginasVisitadas.add(firmaPagina)) {
                break;
            }

            List<WebElement> botonesSiguiente =
                    driver.findElements(nextPageButton);

            if (botonesSiguiente.isEmpty()) {
                break;
            }

            WebElement siguiente = botonesSiguiente.get(0);

            if (!siguiente.isDisplayed()
                    || !siguiente.isEnabled()
                    || "true".equals(
                    siguiente.getAttribute("disabled"))) {
                break;
            }

            siguiente.click();

            wait.until(driver ->
                    !String.join("|", obtenerFilasPaginaActual())
                            .equals(firmaPagina)
            );
        }

        return todasLasFilas;
    }

    public String obtenerValorFiltro(By locator) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        ).getAttribute("value");
    }

    public String obtenerFechaInicio() {
        return obtenerValorFiltro(fromDate);
    }

    public String obtenerFechaFin() {
        return obtenerValorFiltro(toDate);
    }

    public String obtenerEmpleado() {
        return obtenerValorFiltro(employeeName);
    }

    public boolean empleadosAnterioresSeleccionados() {
        return driver.findElement(pastEmployeeCheckbox).isSelected();
    }
}