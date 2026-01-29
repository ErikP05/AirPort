import App.DesktopApp.Forms.AppSplashScreen;
import App.DesktopApp.Forms.AppStart;

public class App {
    public static void main(String[] args) {
        try {
            new AppSplashScreen();
            new AppStart("Airport");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}