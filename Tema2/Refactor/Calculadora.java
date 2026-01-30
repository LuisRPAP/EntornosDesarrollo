/**
 * La clase Calculadora ofrece operaciones matemáticas básicas
 * como suma, resta, multiplicación y división.
 * 
 * <p>Este ejemplo sirve para practicar la generación de
 * documentación usando la herramienta javadoc.</p>
 * 
 * @author LRP
 * @version 1.0
 * @since 25/11/2025
 */
public class Calculadora {

    /**
     * Suma dos números enteros.
     *
     * @param a Primer sumando.
     * @param b Segundo sumando.
     * @return La suma de a y b.
     */
    public int sumar(int a, int b) {
        return a + b;
    }

    /**
     * Resta dos números enteros.
     *
     * @param a Minuendo.
     * @param b Sustraendo.
     * @return El resultado de a menos b.
     */
    public int restar(int a, int b) {
        return a - b;
    }

    /**
     * Multiplica dos números enteros.
     *
     * @param a Primer número.
     * @param b Segundo número.
     * @return El producto de a por b.
     */
    public int multiplicar(int a, int b) {
        return a * b;
    }

    /**
     * Divide dos números enteros.
     *
     * @param a Dividendo.
     * @param b Divisor. No debe ser 0.
     * @return El cociente de a entre b.
     * @throws ArithmeticException si b es 0.
     */
    public int dividir(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("No se puede dividir entre 0");
        }
        return a / b;
    }

    /**
     * Método principal para probar la calculadora.
     *
     * @param args Argumentos de línea de comandos (no usados).
     */
    public static void main(String[] args) {
        Calculadora calc = new Calculadora();

        System.out.println("Suma: " + calc.sumar(5, 3));
        System.out.println("Resta: " + calc.restar(5, 3));
        System.out.println("Multiplicación: " + calc.multiplicar(5, 3));
        System.out.println("División: " + calc.dividir(5, 3));
    }
}

