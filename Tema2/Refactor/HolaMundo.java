/**
* Muestra en pantalla "!Hola mundo!"
* @author LRP
* @version 1.1
*/

import java.util.logging.*;

public class HolaMundo {
    public static void main(String[] args) {

        var logger = Logger.getLogger("CBM");
        logger.info("Aplicación iniciada.");
        System.out.println();
        System.out.println("\033[33m !Hola mundo!");
        System.out.println("\033[31m !Hasta la vista, baby!"); // se añade despedida
        logger.info("Aplicación finalizada.");
    }
}
