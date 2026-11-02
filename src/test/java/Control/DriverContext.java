package Control;

import Constant.Navegador;
import org.openqa.selenium.WebDriver;

public class DriverContext {
    private static DriverManager driverManager = new DriverManager();
    private static Navegador tipoNavegador;

    private static String getString(){
        return tipoNavegador.toString();
    }

    public static void setTipoNavegador(Navegador tipoNavegador){
        DriverContext.tipoNavegador = tipoNavegador;
    }

    public static void setUp(Navegador nav, String url){
        setTipoNavegador(nav);
        System.out.println("Driver context -> Navegador:"+nav);
        driverManager.resolverDriver(nav, url);
    }

    public static WebDriver getDriver(){
        return driverManager.getDriver();
    }

    public static void quitDriver(){
        if (driverManager != null) {
            WebDriver driver = driverManager.getDriver();
            if (driver != null) {
                driver.quit();
            }
            driverManager = new DriverManager();
        }
    }
}
