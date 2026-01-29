
package Infrastructure;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import Infrastructure.Tools.CMD;

public abstract class AppConfig {
    private static final Properties props = new Properties();

    private static final String APP_PROPERTIES = "src/app.properties";
    private static final String KEY_DB_NAME = "db.File";
    private static final String KEY_FILE_LOG = "df.logFile";
    private static final String KEY_FILE_DATA = "df.AntCoor";
    private static final String KEY_FILE_ANTNEST = "df.AntNest";
    private static final String KEY_FILE_ANTFOOD = "df.AntFood";
    private static final String KEY_RES_IMG_MAIN = "res.img.Main";
    private static final String KEY_RES_IMG_LOGO = "res.img.Logo";
    private static final String KEY_RES_IMG_SPLASH = "res.img.Splash";

    // Configuración dinámica (Sin recompilar)
    public static final String getDATABASE() {
        return getProperty(KEY_DB_NAME);
    }

    public static final String getLOGFILE() {
        return getProperty(KEY_FILE_LOG);
    }

    public static final String getDATAFILE() {
        return getProperty(KEY_FILE_DATA);
    }

    public static final String getANTFOOD() {
        return getProperty(KEY_FILE_ANTFOOD);
    }

    public static final String getANTNEST() {
        return getProperty(KEY_FILE_ANTNEST);
    }

    // Resources . Recurso estático empaquetado
    public static final URL getImgMain() {
        return getAppResource(KEY_RES_IMG_MAIN);
    }

    public static final URL getImgLogo() {
        return getAppResource(KEY_RES_IMG_LOGO);
    }

    public static final URL getImgSplash() {
        return getAppResource(KEY_RES_IMG_SPLASH);
    }

    // AppMSGs
    public static final String MSG_DEFAULT_ERROR = "Ups! Error inesperado. Por favor, contacte al administrador del sistema.";
    public static final String MSG_DEFAULT_CLASS = "undefined";
    public static final String MSG_DEFAULT_METHOD = "undefined";

    static {
        try (InputStream appProperties = new FileInputStream(APP_PROPERTIES)) {
            props.load(appProperties);
        } catch (IOException e) {
            CMD.printlnError("ERROR al cargar ❱❱ " + e.getMessage());
        }
    }

    private AppConfig() {
    }

    static String getProperty(String key) {
        String path = props.getProperty(key);
        CMD.println("AppConfig ❱❱ " + APP_PROPERTIES + "." + key + " : " + path);
        if (path != null)
            return path;
        else
            CMD.printlnError("ERROR ❱❱ " + APP_PROPERTIES + "." + key + " : " + path);
        return null;
    }

    static URL getAppResource(String key) {
        String path = getProperty(key);
        if (path != null)
            return AppConfig.class.getResource(path);
        else
            CMD.printlnError("ERROR ❱❱ getAppResource : " + APP_PROPERTIES + "." + key + " : " + path);
        return null;
    }
}
