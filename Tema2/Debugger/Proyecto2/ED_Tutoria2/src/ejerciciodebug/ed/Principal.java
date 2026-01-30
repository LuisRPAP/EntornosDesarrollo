
package ejerciciodebug.ed;

/* Entrada
 * El programa deberá procesar varios casos de prueba, 
 * cada uno en una línea. Cada caso de prueba es una 
 * sucesión de numeros 1, 2, o 0 indicando, 
 * respectivamente,  que una prenda es de verano, de 
 * invierno o que sirve para ambas estaciones 
 * indistintamente.  La sucesión termina con un -1.
 * El último caso de prueba, que no debe procesarse, 
 * es una lista sin prendas.
 * Salida
 * Para cada caso de prueba el programa escribirá “1”, 
 * “2” o “0” dependiendo de la estación para la que más 
 * ropa tengas. 1 será el Verano, 2 será el Invierno, y 
 * 0 será ambos.
 */
import java.util.Scanner;

public class Principal {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		int n = -2;
		System.out.print("Dale, empieza:");
		while (n != -1) {

			n = sc.nextInt();

			if (n != -1) {
				n = mayor(n);
				switch (n) {
					case 1:
						System.out.println("VERANO");
						break;
					case 2:
						System.out.println("INVIERNO");
						break;
					case 0:
						System.out.println("EMPATE");
						break;
				}
			}
		}
		System.out.println("Se ha acabado");
		// sc.close(); // hay que cerrar el Scanner cuando se sale
	}

	public static byte mayor(int n) {

		int v = 0; // verano
		int i = 0; // invierno
		Scanner pc = new Scanner(System.in); // Cambio el nombre del sc a pc para evitar conflicto
		while (n != -1) {

			switch (n) {
				case 1:
					v++;
					break;
				case 2:
					i = i++;
					break;
			}
			n = pc.nextInt();
		}
		pc.close(); // hay que cerrar el Scanner cuando se sale
		if (v > i) {// cambiamos el signo de < a > ya que se devuelve 1 cuando v es > que i
			return 1;
		} else if (i > v) {// cambiamos el signo de < a > por lo mismo se devuelve 2 cuando i > v
			return 2;
		} else {
			return 0;
		}

	}

}