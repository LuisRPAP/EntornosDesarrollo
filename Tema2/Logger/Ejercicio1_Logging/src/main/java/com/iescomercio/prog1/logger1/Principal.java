package com.iescomercio.prog1.logger1;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import java.util.logging.*;

public class Principal {
		private static Logger logger ;
	// funcion principal donde se inicia la aplicacion
		public static void main(String[] args) {
			configurarLogger();
			
			long aux;
        
        //logger.setLevel(Level.WARNING);
        logger.info("Aplicación iniciada.");
			do {
				aux = nModuloM();
				if (aux != -1) {
					System.out.println(aux);
				}
			} while (aux != -1);
		logger.info("Aplicación finalizada.");
		}

		// funcion que solicita una línea al usuario con el patrón X % Y y devuelve el resultado 
		// de la operación X modulo Y. El usuario debera introducir -1 para finalizar 
		public static long nModuloM() {
			long n;
			int m;
			String str = "";
			str = pideLinea();
			logger.info("Línea Leída ."+str);
			n = Long.valueOf(str.substring(0, str.indexOf(' ')));
			m = Integer.valueOf(str.substring(str.lastIndexOf(' ') + 1));
			logger.fine("Valor de n ."+n);
			logger.fine("Valor de m ."+m);
			if (n != 0 && m != 0) {
			logger.info("Resultado : "+(n % m));
				return (n % m);
			} else {
			logger.warning("Se devuelve -1");
				return -1;
			}
		}

		// pide linea de texto al usuario
		public static String pideLinea() {
			Scanner sc = new Scanner(System.in);
			String str = "";
			System.out.print("Introduce una línea: ");
			str = sc.nextLine();
			return str;
		
		}
		 // configuración sencilla del logger
    private static void configurarLogger() {
        try {
            logger = Logger.getLogger("LRP");

			// Crear carpeta logs si no existe
            File dir = new File("logs");
            if (!dir.exists()) dir.mkdir();

            // Formato legible
            FileHandler archivo = new FileHandler("logs/app.log", true);

            // Formato legible
            archivo.setFormatter(new SimpleFormatter());

			// Quitar todos los handlers que imprimen por consola
            Logger rootLogger = Logger.getLogger("");
            for (Handler h : rootLogger.getHandlers()) {
                rootLogger.removeHandler(h);
            }


            // Asociar archivo al logger
            logger.addHandler(archivo);
			logger.setUseParentHandlers(false);

            // Mostrar todos los niveles de mensajes
            logger.setLevel(Level.WARNING);

        } catch (IOException e) {
            System.err.println("No se pudo crear el archivo de log.");
        }
    }

}


