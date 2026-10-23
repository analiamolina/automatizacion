package StepDefinition;

import ObjectPage.ListaEmpleadoPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.WebElement;

public class OrangeDefinition {

    private final ListaEmpleadoPage listaEmpleadoPage = new ListaEmpleadoPage();

    @And("que el usuario inicia sesión en OrangeHRM con el usuario {string} y la contraseña {string}")
    public void ingresarUsuario(String nombreUsuario, String password) {

        listaEmpleadoPage.escribirUsersname(nombreUsuario);
        listaEmpleadoPage.escribirPassword(password);
        listaEmpleadoPage.clickLogin();

    }

    @And("que el usuario ingresa al módulo PIM")
    public void moduloPIM() {

        listaEmpleadoPage.clickPIM();

    }

    @When("el usuario ingresa {string} en el campo Nombre del empleado de PIM")
    public void ingresarNombreEmpleado(String nombreEmpleado) throws InterruptedException {
        listaEmpleadoPage.ingresarNombreEmpleado(nombreEmpleado);
        Thread.sleep(5000);

    }

    @And("hace clic en Buscar")
    public void clickBuscar() {

        listaEmpleadoPage.clickBuscar();

    }

    @Then("se deben mostrar los resultados de la búsqueda")
    public void validarResultadosBusqueda() {
        Assert.assertFalse(listaEmpleadoPage.obtenerResultados().isEmpty());
    }

    @And("cada resultado debe contener {string}")
    public void validarNombreEncontrado(String expectedName) {
        for (WebElement fila : listaEmpleadoPage.obtenerResultados()) {
            Assert.assertTrue(fila.getText().contains(expectedName));
        }

    }

    @When("el usuario introduce el ID de empleado {string}")
    public void ingresarIdEmpleado(String employeeId) {
        listaEmpleadoPage.ingresarIdEmpleado(employeeId);
    }

    @Then("el sistema debería devolver {string} registros")
    public void elSistemaDeberiaDevolverRegistros(String expectedRecords) {

        int esperado = Integer.parseInt(expectedRecords);

        int actual = listaEmpleadoPage.obtenerCantidadRegistros();

        Assert.assertEquals(
                "La cantidad de registros encontrados no coincide",
                esperado,
                actual
        );
    }

    @When("el usuario selecciona el estado laboral {string}")
    public void seleccionarEstado(String estado) {
        listaEmpleadoPage.seleccionarEstado(estado);
    }

    @Then("cada resultado debe tener el estado laboral {string}")
    public void validarEstadoLaboral(String status) {
        listaEmpleadoPage.validarEstadoLaboral(status);
    }

    @And ("el usuario selecciona la opción de inclusión {string}")
    public void seleccionaropcionInlusion(String inclusionOption) {
        listaEmpleadoPage.seleccionarInclude(inclusionOption);
    }

    @And("el usuario selecciona el puesto de trabajo {string}")
    public void seleccionarPuestoTrabajo(String jobTitle) {
        listaEmpleadoPage.seleccionarjobTitle(jobTitle);
    }

    @And("el usuario selecciona la subunidad {string}")
    public void seleccionarSubUnidad(String subUnit) {
        listaEmpleadoPage.seleccionarSubUnit(subUnit);
    }

    @And("el usuario introduce el nombre del supervisor {string}")
    public void ingresarSupervisor(String supervisor) {
        listaEmpleadoPage.seleccionarsupervisorName(supervisor);
    }
    //@And("hace clic en Buscar")
    //public void clicBuscar() {
      //  pimPage.clickBuscar();
    //}

    @Then("todos los resultados deben coincidir con empleado {string}, ID {string}, estado {string}, puesto {string} y subunidad {string}")
    public void validarResultadosFiltrados(
            String employeeName,
            String employeeId,
            String status,
            String jobTitle,
            String subUnit) {
        listaEmpleadoPage.validarResultadosFiltrados(
                employeeName, employeeId, status, jobTitle, subUnit);
    }
}