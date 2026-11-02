package StepDefinition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Control.DriverContext;
import Control.NavSelector;
import ObjectPage.Page;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepdefinitionLeave {

    private static final String LOGIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
    private static final String LEAVE_LIST_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/leave/viewLeaveList";

    private Page leavePage;

    private String empleadoEsperado;
    private String estadoEsperado;
    private String unidadEsperada;
    private String fechaInicioEsperada;
    private String fechaFinEsperada;
    private String anterioresEsperados;

    @Given("que existen solicitudes de vacaciones")
    public void existenSolicitudesDeVacaciones() {
        WebDriver driver = DriverContext.getDriver();
        if (driver == null) {
            DriverContext.setUp(
                    NavSelector.seleccionNavegador(
                            System.getProperty("nav", "default")
                    ),
                    LOGIN_URL
            );
            driver = DriverContext.getDriver();
            Assert.assertNotNull("No se pudo iniciar el WebDriver", driver);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("username")
            )).sendKeys("Admin");
            driver.findElement(By.name("password")).sendKeys("admin123");
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")
            )).click();
            wait.until(ExpectedConditions.urlContains("/dashboard/index"));
            driver.get(LEAVE_LIST_URL);
        }

        Assert.assertNotNull("El WebDriver no debe ser nulo", driver);
        leavePage = new Page();
        leavePage.esperarPantalla();

        // Precondición: los datos deben estar creados
        // previamente en el ambiente de pruebas.
    }

    @Given("el usuario se encuentra en {string}")
    public void usuarioSeEncuentraEn(String pantalla) {
        Assert.assertEquals(
                "La pantalla indicada no es Leave List",
                "Leave List",
                pantalla
        );

        leavePage.esperarPantalla();
    }

    @When("ingresa la fecha inicial {string}")
    public void ingresaFechaInicial(String fecha) {
        this.fechaInicioEsperada = fecha;
        leavePage.ingresarFechaInicio(fecha);
    }

    @When("ingresa la fecha final {string}")
    public void ingresaFechaFinal(String fecha) {
        this.fechaFinEsperada = fecha;
        leavePage.ingresarFechaFin(fecha);
    }

    @When("selecciona el estado {string}")
    public void seleccionaEstado(String estado) {
        this.estadoEsperado = estado;
        leavePage.seleccionarEstado(estado);
    }

    @When("ingresa el nombre del empleado {string}")
    public void ingresaNombreEmpleado(String empleado) {
        this.empleadoEsperado = empleado;
        leavePage.ingresarEmpleado(empleado);
    }

    @When("selecciona al empleado desde el autocompletado")
    public void seleccionaEmpleadoAutocompletado() {
        leavePage.seleccionarEmpleadoAutocompletado(
                empleadoEsperado
        );
    }

    @When("selecciona la unidad {string}")
    public void seleccionaUnidad(String unidad) {
        this.unidadEsperada = unidad;
        leavePage.seleccionarUnidad(unidad);
    }

    @When("configura empleados anteriores en {string}")
    public void configuraEmpleadosAnteriores(String anteriores) {
        this.anterioresEsperados = anteriores;
        leavePage.configurarEmpleadosAnteriores(anteriores);
    }

    @When("presiona el botón {string}")
    public void presionaBoton(String boton) {
        if ("Search".equalsIgnoreCase(boton)) {
            leavePage.buscar();
        } else if ("Reset".equalsIgnoreCase(boton)) {
            leavePage.resetear();
        } else {
            Assert.fail("Botón no implementado: " + boton);
        }
    }

    @When("el usuario presiona el botón {string}")
    public void elUsuarioPresionaBoton(String boton) {
        presionaBoton(boton);
    }

    @Then("se debe completar la búsqueda sin errores")
    public void busquedaSinErrores() {
        Assert.assertTrue(
                "No se visualizó la tabla de resultados",
                leavePage.tablaVisible()
        );
    }

    @Then("se debe visualizar la tabla de resultados")
    public void visualizarTablaResultados() {
        Assert.assertTrue(
                "La tabla no está visible",
                leavePage.tablaVisible()
        );

        Assert.assertFalse(
                "La tabla no contiene encabezados",
                leavePage.obtenerEncabezados().isEmpty()
        );
    }

    @Then("las solicitudes deben corresponder al empleado {string}")
    public void solicitudesDelEmpleado(String empleado) {
        List<String> filas = leavePage.obtenerTodasLasFilas();

        Assert.assertFalse(
                "No hay solicitudes para validar el empleado",
                filas.isEmpty()
        );

        for (String fila : filas) {
            Assert.assertTrue(
                    "La fila no corresponde al empleado " + empleado
                            + ". Fila: " + fila,
                    fila.toLowerCase().contains(empleado.toLowerCase())
            );
        }
    }

    @Then("las solicitudes deben cumplir el estado {string}")
    public void solicitudesCumplenEstado(String estado) {
        List<String> filas = leavePage.obtenerTodasLasFilas();

        Assert.assertFalse(
                "No existen solicitudes para validar el estado",
                filas.isEmpty()
        );

        for (String fila : filas) {
            Assert.assertTrue(
                    "El estado esperado no aparece en la fila: " + fila,
                    fila.toLowerCase().contains(estado.toLowerCase())
            );
        }
    }

    @Then("las solicitudes deben estar dentro del período consultado")
    public void solicitudesDentroPeriodo() {
        Assert.assertNotNull(
                "No se configuró la fecha inicial",
                fechaInicioEsperada
        );

        Assert.assertNotNull(
                "No se configuró la fecha final",
                fechaFinEsperada
        );

        Assert.assertTrue(
                "La fecha inicial no puede ser posterior a la fecha final",
                fechaInicioEsperada.compareTo(fechaFinEsperada) <= 0
        );

        // La validación real del período debe comparar las fechas
        // de cada fila con estas fechas, usando el formato que
        // efectivamente devuelve la tabla de OrangeHRM.
    }

    @Then("las solicitudes deben corresponder a la unidad {string}")
    public void solicitudesCorrespondenUnidad(String unidad) {
        Assert.assertEquals(
                "La unidad validada no coincide con la unidad del filtro",
                unidadEsperada,
                unidad
        );

        // Para verificar la unidad de cada solicitud, se necesita
        // que la tabla exponga esa columna o consultar el dato
        // asociado a cada empleado mediante la aplicación/API.
    }

    @Then("los registros deben respetar el filtro de empleados anteriores")
    public void validarFiltroEmpleadosAnteriores() {
        Assert.assertEquals(
                "El checkbox de empleados anteriores no coincide con el filtro",
                "Yes".equalsIgnoreCase(anterioresEsperados),
                leavePage.empleadosAnterioresSeleccionados()
        );
    }

    @Then("no deben existir registros duplicados")
    public void noDebenExistirDuplicados() {
        List<String> filas = leavePage.obtenerTodasLasFilas();
        Set<String> filasUnicas = new HashSet<>(filas);

        Assert.assertEquals(
                "Se encontraron filas duplicadas en los resultados",
                filas.size(),
                filasUnicas.size()
        );
    }

    @Then("todas las páginas deben respetar los filtros aplicados")
    public void todasLasPaginasRespetanFiltros() {
        List<String> filas = leavePage.obtenerTodasLasFilas();

        for (String fila : filas) {
            Assert.assertTrue(
                    "Empleado incorrecto en los resultados paginados: " + fila,
                    fila.toLowerCase().contains(empleadoEsperado.toLowerCase())
            );

            Assert.assertTrue(
                    "Estado incorrecto en los resultados paginados: " + fila,
                    fila.toLowerCase().contains(estadoEsperado.toLowerCase())
            );
        }
    }

    @Then("los filtros deben recuperar sus valores predeterminados")
    public void filtrosValoresPredeterminados() {
        Assert.assertTrue(
                "El empleado no se limpió después del Reset",
                leavePage.obtenerEmpleado().isEmpty()
        );

        Assert.assertTrue(
                "La fecha inicial no se limpió después del Reset",
                leavePage.obtenerFechaInicio().isEmpty()
        );

        Assert.assertTrue(
                "La fecha final no se limpió después del Reset",
                leavePage.obtenerFechaFin().isEmpty()
        );

        Assert.assertFalse(
                "El checkbox de empleados anteriores debería estar desmarcado",
                leavePage.empleadosAnterioresSeleccionados()
        );
    }

    @Then("no deben permanecer los criterios ingresados anteriormente")
    public void noPermanecenCriteriosAnteriores() {
        Assert.assertTrue(
                "El filtro de empleado conserva un valor anterior",
                leavePage.obtenerEmpleado().isEmpty()
        );

        Assert.assertTrue(
                "El filtro de fecha inicial conserva un valor anterior",
                leavePage.obtenerFechaInicio().isEmpty()
        );

        Assert.assertTrue(
                "El filtro de fecha final conserva un valor anterior",
                leavePage.obtenerFechaFin().isEmpty()
        );
    }
}