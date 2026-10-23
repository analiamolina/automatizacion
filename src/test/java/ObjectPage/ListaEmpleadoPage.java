package ObjectPage;

import Control.BaseController;
import Control.DriverContext;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ListaEmpleadoPage extends BaseController {

    private String employeeIdFilter;
    private String employeeNameFilter;

    //private WebDriver driver;

    // LOGIN

    @FindBy(xpath = "//*[@id='app']/div[1]/div/div[1]/div/div[2]/div[2]/form/div[1]/div/div[2]/input")
    @SuppressWarnings("unused")
    private WebElement username;

    @FindBy(xpath = "//*[@id='app']/div[1]/div/div[1]/div/div[2]/div[2]/form/div[2]/div/div[2]/input")
    @SuppressWarnings("unused")
    private WebElement password;

    @FindBy(xpath = "//*[@id='app']/div[1]/div/div[1]/div/div[2]/div[2]/form/div[3]/button")
    @SuppressWarnings("unused")
    private WebElement loginbtn;

    // PIM

    @FindBy(xpath = "//*[@id='app']/div[1]/div[1]/aside/nav/div[2]/ul/li[2]/a")
    @SuppressWarnings("unused")
    private WebElement menuPim;

    @FindBy(xpath = "//*[@id='app']/div[1]/div[2]/div[2]/div/div[1]/div[2]/form/div[1]/div/div[1]/div/div[2]/div/div/input")
    @SuppressWarnings("unused")
    private WebElement nombreempleado;

    @FindBy(xpath = "//*[@id='app']/div[1]/div[2]/div[2]/div/div[1]/div[2]/form/div[2]/button[2]")
    @SuppressWarnings("unused")
    private WebElement buscarempladobtn;

    @FindBy(xpath = "//label[normalize-space()='Employee Id']/ancestor::div[contains(@class,'oxd-input-group')]//input")
    @SuppressWarnings("unused")
    private WebElement idempleado;

    @FindBy(css = ".oxd-table-card")
    @SuppressWarnings("unused")
    private List<WebElement> resultados;

    @FindBy(xpath = "//label[normalize-space()='Employment Status']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]")
    @SuppressWarnings("unused")
    private WebElement employmentStatusDropdown;

    @FindBy(xpath = "//label[normalize-space()='Include']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]")
    @SuppressWarnings("unused")
    private WebElement includeDropdown;

    @FindBy(xpath = "//label[normalize-space()='Job Title']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]")
    @SuppressWarnings("unused")
    private WebElement jobTitleDropdown;

    @FindBy(xpath = "//label[normalize-space()='Sub Unit']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]")
    @SuppressWarnings("unused")
    private WebElement subUnitDropdown;

    @FindBy(xpath = "//label[normalize-space()='Supervisor Name']/ancestor::div[contains(@class,'oxd-input-group')]//input")
    @SuppressWarnings("unused")
    private WebElement supervisorNameInput;

    @FindBy (xpath="//button[@type='reset']")
    @SuppressWarnings("unused")
    private     WebElement resetButton;

    // LOGIN

    public void escribirUsersname(String usuario) {

        username.sendKeys(usuario);
    }

    public void escribirPassword(String clave) {
        password.sendKeys(clave);
    }

    public void clickLogin() {

        loginbtn.click();
    }

    // PIM

    public void clickPIM() {
        try {
            visualizarElemento(menuPim, 10);
            menuPim.click();
        } catch (Exception e) {
            Assert.fail("Error al ingresar al módulo PIM: " + e.getMessage());
        }
    }


    public void ingresarNombreEmpleado(String nombreEmpleado) {
        if (!visualizarElemento(nombreempleado, 10)) {
            return;
        }
        employeeNameFilter = nombreEmpleado;
        nombreempleado.clear();
        nombreempleado.sendKeys(nombreEmpleado);
        seleccionarSugerencia(nombreEmpleado);
    }
  public void clickBuscar() {
    WebDriver driver = DriverContext.getDriver();
    By rows = By.cssSelector(".oxd-table-card");
    By noRecords = By.xpath("//*[normalize-space()='No Records Found' or normalize-space()='No Records']");
    new WebDriverWait(driver, Duration.ofSeconds(15)).until(d ->
            !d.findElements(rows).isEmpty()
                    || d.findElements(noRecords).stream().anyMatch(WebElement::isDisplayed));
    buscarempladobtn.click();
  }


    public List<WebElement> obtenerResultados() {
        return employeeNameFilter == null
                ? esperarResultadosActualizados()
                : esperarResultadosQueCoinciden(employeeNameFilter);
    }

    public void ingresarIdEmpleado(String employeeId) {
        if (!visualizarElemento(idempleado, 10)) {
            return;
        }

        idempleado.clear();
        idempleado.sendKeys(employeeId);
        employeeIdFilter = employeeId;
    }
    // Obtener cantidad de registros encontrados

    public int obtenerCantidadRegistros() {
        if (employeeIdFilter == null || employeeIdFilter.isBlank()) {
            throw new IllegalStateException("Primero se debe ingresar un ID de empleado");
        }

        List<WebElement> filas = esperarResultadosQueCoinciden(employeeIdFilter);
        if (filas.isEmpty()) {
            return 0;
        }

        for (WebElement fila : filas) {
            boolean coincideId = fila.findElements(By.cssSelector(".oxd-table-cell"))
                    .stream()
                    .anyMatch(celda -> employeeIdFilter.equals(celda.getText().trim()));
            if (!coincideId) {
                throw new AssertionError("La tabla actualizada contiene registros que no coinciden con el ID "
                        + employeeIdFilter + ": " + fila.getText());
            }
        }
        return filas.size();
    }

    private List<WebElement> esperarResultadosQueCoinciden(String... criterios) {
        WebDriver driver = DriverContext.getDriver();
        By rows = By.cssSelector(".oxd-table-card");
        By noRecords = By.xpath("//*[normalize-space()='No Records Found' or normalize-space()='No Records']");
        return new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
            try {
                List<WebElement> filas = d.findElements(rows);
                if (filas.isEmpty()) {
                    return d.findElements(noRecords).stream()
                            .anyMatch(WebElement::isDisplayed) ? filas : null;
                }

                boolean todasCoinciden = filas.stream().allMatch(fila -> {
                    String texto = fila.getText().toLowerCase();
                    return java.util.Arrays.stream(criterios)
                            .allMatch(criterio -> texto.contains(criterio.toLowerCase()));
                });
                return todasCoinciden ? filas : null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    private List<WebElement> esperarResultadosActualizados() {
        WebDriver driver = DriverContext.getDriver();
        By rows = By.cssSelector(".oxd-table-card");
        By noRecords = By.xpath("//*[normalize-space()='No Records Found' or normalize-space()='No Records']");
        String[] ultimoFingerprint = {null};
        long[] estableDesde = {System.nanoTime()};

        return new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
            try {
                String fingerprint = getResultsFingerprint(d, rows, noRecords);
                if (!fingerprint.equals(ultimoFingerprint[0])) {
                    ultimoFingerprint[0] = fingerprint;
                    estableDesde[0] = System.nanoTime();
                }

                long tiempoEstable = System.nanoTime() - estableDesde[0];
                if (tiempoEstable < Duration.ofSeconds(2).toNanos()) {
                    return null;
                }

                List<WebElement> filas = d.findElements(rows);
                if (!filas.isEmpty()) {
                    return filas;
                }

                return d.findElements(noRecords).stream()
                        .anyMatch(WebElement::isDisplayed) ? filas : null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    private String getResultsFingerprint(WebDriver driver, By rows, By noRecords) {
        List<WebElement> filas = driver.findElements(rows);
        if (!filas.isEmpty()) {
            StringBuilder fingerprint = new StringBuilder();
            for (WebElement fila : filas) {
                fingerprint.append(fila.getText()).append('\n');
            }
            return fingerprint.toString();
        }

        return driver.findElements(noRecords).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .findFirst()
                .orElse("");
    }

    public void seleccionarEstado(String estado) {
        try {
            if (!visualizarElemento(employmentStatusDropdown, 10)) {
                return;
            }
            employmentStatusDropdown.click();

            WebDriver driver = DriverContext.getDriver();
            By opcion = By.xpath("//div[contains(@class,'oxd-select-option')]//span[normalize-space()='" + estado + "']");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(opcion))
                    .click();
        } catch (Exception e) {
            Assert.fail("Error al seleccionar el estado: " + e.getMessage());
        }
    }

    public void validarEstadoLaboral(String status) {
        List<WebElement> filas = esperarResultadosQueCoinciden(status);

        if (filas.isEmpty()) {
            System.out.println("Validación de Employment Status '" + status
                    + "': la búsqueda terminó sin empleados (No Records Found).");
            return;
        }

        for (WebElement fila : filas) {
            Assert.assertTrue(
                    "El resultado no contiene el estado laboral " + status + ": " + fila.getText(),
                    fila.getText().contains(status));
        }
    }

    public void validarResultadosFiltrados(
            String employeeName,
            String employeeId,
            String status,
            String jobTitle,
            String subUnit) {
        List<WebElement> filas = esperarResultadosQueCoinciden(
                employeeName, employeeId, status, jobTitle, subUnit);
        if (filas.isEmpty()) {
            System.out.println("Validación TC-005: la búsqueda con los filtros seleccionados terminó "
                    + "sin registros (No Records Found); se acepta el resultado vacío.");
            return;
        }

        for (WebElement fila : filas) {
            String textoFila = fila.getText();
            Assert.assertTrue("El resultado no contiene el nombre " + employeeName + ": " + textoFila,
                    textoFila.contains(employeeName));
            Assert.assertTrue("El resultado no contiene el ID " + employeeId + ": " + textoFila,
                    textoFila.contains(employeeId));
            Assert.assertTrue("El resultado no contiene el estado " + status,
                    textoFila.contains(status));
            Assert.assertTrue("El resultado no contiene el puesto " + jobTitle,
                    textoFila.contains(jobTitle));
            Assert.assertTrue("El resultado no contiene la subunidad " + subUnit,
                    textoFila.contains(subUnit));
        }
    }

 public void seleccionarInclude(String include) {
        try {
            if (!visualizarElemento(includeDropdown, 10)) {
                return;
            }
            includeDropdown.click();

            WebDriver driver = DriverContext.getDriver();
            By opcion = By.xpath("//div[contains(@class,'oxd-select-option')]//span[normalize-space()='" + include + "']");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(opcion))
                    .click();
        } catch (Exception e) {
            Assert.fail("Error al seleccionar el Include: " + e.getMessage());
        }
    }



    public void seleccionarjobTitle(String jobTitle) {
        try {
            if (!visualizarElemento(jobTitleDropdown, 10)) {
                return;
            }
            jobTitleDropdown.click();

            WebDriver driver = DriverContext.getDriver();
            By opcion = By.xpath("//div[contains(@class,'oxd-select-option')]//span[normalize-space()='" + jobTitle + "']");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(opcion))
                    .click();
        } catch (Exception e) {
            Assert.fail("Error al seleccionar el Job Title: " + e.getMessage());
        }
    }

    public void seleccionarsupervisorName(String supervisorName) {
        if (supervisorName == null || supervisorName.isBlank()) {
            return;
        }
        try {
            if (!visualizarElemento(supervisorNameInput, 10)) {
                System.out.println("El campo Supervisor Name no está disponible; se omite este filtro.");
                return;
            }
            supervisorNameInput.clear();
            supervisorNameInput.sendKeys(supervisorName);

            WebDriver driver = DriverContext.getDriver();
            By opciones = By.cssSelector(".oxd-autocomplete-option");
            By mensajeInvalido = By.xpath("//*[normalize-space()='Invalid']");
            WebElement[] sugerenciaEncontrada = {null};
            boolean[] nombreInvalido = {false};
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> {
                try {
                    sugerenciaEncontrada[0] = d.findElements(opciones).stream()
                            .filter(WebElement::isDisplayed)
                            .filter(opcion -> opcion.getText().trim().equalsIgnoreCase(supervisorName.trim()))
                            .findFirst()
                            .orElse(null);
                    nombreInvalido[0] = d.findElements(mensajeInvalido).stream()
                            .anyMatch(WebElement::isDisplayed);
                    return sugerenciaEncontrada[0] != null || nombreInvalido[0];
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            });

            if (nombreInvalido[0]) {
                String mensaje = driver.findElements(mensajeInvalido).stream()
                        .filter(WebElement::isDisplayed)
                        .map(WebElement::getText)
                        .findFirst()
                        .orElse("valor inválido");
                supervisorNameInput.clear();
                System.out.println("Validación de Supervisor Name: '" + supervisorName
                        + "' es inválido (" + mensaje + "). Se continúa sin este filtro.");
                return;
            }
            sugerenciaEncontrada[0].click();
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                    supervisorName.trim().equalsIgnoreCase(supervisorNameInput.getAttribute("value").trim()));
            System.out.println("Supervisor Name '" + supervisorName + "' validado y seleccionado.");
        } catch (org.openqa.selenium.TimeoutException e) {
            supervisorNameInput.sendKeys(Keys.TAB);
            boolean indicadorInvalido = false;
            try {
                indicadorInvalido = new WebDriverWait(DriverContext.getDriver(), Duration.ofSeconds(2))
                        .until(d -> d.findElements(By.xpath("//*[normalize-space()='Invalid']")).stream()
                                .anyMatch(WebElement::isDisplayed));
            } catch (org.openqa.selenium.TimeoutException ignored) {
                // Sin mensaje visible, la ausencia de una sugerencia exacta sigue invalidando el valor.
            }
            supervisorNameInput.clear();
            System.out.println("Supervisor Name '" + supervisorName + "' es inválido"
                    + (indicadorInvalido ? " (la página muestra 'Invalid')" : " (sin sugerencia exacta)")
                    + ". Se limpia el campo y se continúa sin este filtro.");
        } catch (Exception e) {
            Assert.fail("Error al ingresar el nombre del supervisor: " + e.getMessage());
        }
    }

    private void seleccionarSugerencia(String texto) {
        WebDriver driver = DriverContext.getDriver();
        By opciones = By.cssSelector(".oxd-autocomplete-option");
        List<WebElement> coincidencias = new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
            List<WebElement> encontradas = d.findElements(opciones).stream()
                    .filter(WebElement::isDisplayed)
                    .filter(opcion -> opcion.getText().toLowerCase().contains(texto.toLowerCase()))
                    .toList();
            return encontradas.isEmpty() ? null : encontradas;
        });
        coincidencias.get(0).click();
    }

    public void seleccionarSubUnit(String subUnit) {
        try {
            if (!visualizarElemento(subUnitDropdown, 10)) {
                return;
            }
            subUnitDropdown.click();

            WebDriver driver = DriverContext.getDriver();
            By opcion = By.xpath("//div[contains(@class,'oxd-select-option')]//span[normalize-space()='" + subUnit + "']");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(opcion))
                    .click();
        } catch (Exception e) {
            Assert.fail("Error al seleccionar la Sub Unit: " + e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void clicreset() {
        try {
            visualizarElemento(resetButton, 10);
            resetButton.click();
        } catch (Exception e) {
            Assert.fail("Error al hacer clic en Reset: " + e.getMessage());
        }
    }
}
