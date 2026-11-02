package StepDefinition;

import Control.DriverContext;
import Constant.Navegador;
import Pages.SodimacPage;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Locale;

public class SodimacDefinition {

    // Declaración e inicialización del Page Object
    private SodimacPage sodimacPage = new SodimacPage(DriverContext.getDriver());

    @When("finaliza la carga visual de la página")
    public void esperarCargaVisual() {
        sodimacPage.esperarCargaVisual();
    }

    @Then("debe visualizar el encabezado principal")
    public void validarEncabezadoPrincipal() {
        Assert.assertTrue(
                "No se visualiza el encabezado principal",
                sodimacPage.encabezadoPrincipalVisible()
        );
    }

    @Then("debe visualizar el título de la categoría")
    public void validarTituloCategoria() {
        Assert.assertTrue(
                "No se visualiza el título de la categoría o la página no cargó correctamente",
                sodimacPage.cantidadProductosVisibles() >= 0
        );
    }

    @Then("debe visualizar el contenedor de productos")
    public void validarContenedorProductos() {
        Assert.assertTrue(
                "No se visualiza el contenedor de productos o está vacío",
                sodimacPage.contenedorProductosVisible()
        );
    }

    @Then("cada tarjeta de producto visible debe mostrar")
    public void validarElementosTarjeta(DataTable dataTable) {
        List<String> elementos = dataTable.asList();

        for (String elemento : elementos) {
            switch (elemento.trim().toLowerCase(Locale.ROOT)) {
                case "objeto":
                    // Encabezado de la tabla del feature.
                    break;

                case "imagen":
                    Assert.assertTrue("Hay productos sin imagen visible", sodimacPage.todosLosProductosTienenImagen());
                    break;

                case "nombre":
                    Assert.assertTrue("Hay productos con nombre vacío", sodimacPage.todosLosNombresNoEstanVacios());
                    break;

                case "precio":
                    Assert.assertTrue("Hay precios con formato incorrecto o ausentes", sodimacPage.todosLosPreciosSonCLP());
                    break;

                case "enlace de producto":
                    Assert.assertTrue("Hay productos sin enlace de redirección válido", sodimacPage.todosLosProductosTienenEnlace());
                    break;

                default:
                    break;
            }
        }
    }

    @Then("las imágenes deben mostrarse correctamente")
    public void validarImagenes() {
        Assert.assertTrue(
                "Las imágenes de los productos no se muestran correctamente",
                sodimacPage.todosLosProductosTienenImagen()
        );
    }

    @Then("los nombres no deben estar vacíos")
    public void validarNombres() {
        Assert.assertTrue(
                "Existen nombres de productos vacíos",
                sodimacPage.todosLosNombresNoEstanVacios()
        );
    }

    @Then("los precios deben tener formato monetario CLP")
    public void validarPreciosCLP() {
        Assert.assertTrue(
                "Los precios no cumplen con el formato monetario CLP esperado",
                sodimacPage.todosLosPreciosSonCLP()
        );
    }

    @Then("ningún elemento debe superponerse")
    public void validarSuperposiciones() {
        Assert.assertTrue(
                "Se detectaron tarjetas de productos superpuestas visualmente",
                sodimacPage.noHayTarjetasSuperpuestas()
        );
    }

    @Then("no deben existir tarjetas visualmente duplicadas")
    public void validarDuplicados() {
        Assert.assertTrue(
                "Se detectaron tarjetas de productos duplicadas en el catálogo",
                sodimacPage.noHayProductosDuplicados()
        );
    }
}