package ejerciciodebug.ed;
import java.util.Scanner;

public class Principal {

	// funcion principal donde se inicia la aplicacion
	public static void main(String[] args) {
		long aux;

		do {
			aux = nModuloM();
			if (aux != -1) {
				System.out.println(aux);
			}
		} while (aux != -1);

	}

	// funcion que solicita una línea al usuario con el patrón X % Y y devuelve el resultado 
	// de la operación X modulo Y. El usuario debera introducir -1 para finalizar 
	public static long nModuloM() {
		long n;
		int m;
		String str = "";
		str = pideLinea();

		n = Long.valueOf(str.substring(0, str.indexOf(' ')));
		m = Integer.valueOf(str.substring(str.lastIndexOf(' ') + 1));
		if (n != 0 && m != 0) {
			return (n % m);
		} else {
			return -1;
		}
	}

	// pide linea de texto al usuario
	public static String pideLinea() {
		Scanner sc = new Scanner(System.in);
		String str = "";
		System.out.print("Introduce una linea: ");
		str = sc.nextLine();
		return str;
	}

}
