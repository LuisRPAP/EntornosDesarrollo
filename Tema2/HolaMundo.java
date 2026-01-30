/**
* Muestra en pantalla "!Hola mundo!"
* @version 1.1
* @author LRP
*/
import java.util.logging.*;


public class HolaMundo {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("LRP");
        logger.setLevel(Level.WARNING);
        logger.info("Aplicación iniciada.");
        System.out.println();
        System.out.println("!Hola mundo!");
        System.out.println();
        logger.severe("Aplicación finalizada.");
    }
}