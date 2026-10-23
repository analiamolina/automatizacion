package StepDefinition;

import ObjectPage.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginDefinition {

    private LoginPage loginPage = new LoginPage();

    @And("ingresar el usuario {string}")
    public void ingresarElUsuario(String nombreUsuario) {

        loginPage.escribirUsersname(nombreUsuario);
    }

    @And("ingresar la pass {string}")
    public void ingresarLaPass(String password) {

        loginPage.escribirPassword(password);
    }

    @When("presiono el boton Submit")
    public void presionoElBotonSubmit() {

        loginPage.clickBtnSubmit();
    }

    @Then("se valida el mensaje Your username is invalid!")
    public void seValidaElMensajeYourUsernameIsInvalid() {

        loginPage.validarMsgUsuarioInvalido();
    }

    @Then("se valida el mensaje Logged In Successfully")
    public void seValidaElMensajeLoggedInSuccessfully() {

        loginPage.validarMsgLoginExitoso();

    }
}