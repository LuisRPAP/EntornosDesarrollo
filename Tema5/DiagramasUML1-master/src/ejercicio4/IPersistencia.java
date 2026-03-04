package ejercicio4;

import java.util.Date;
import java.util.List;

public interface IPersistencia {
	
	public List<String> obtenerNombreAlumnos();
	public Boolean cambiarNombreAlumno(String idAlumno);
	public Boolean eliminarAlumno(String idAlumno);
	public Boolean aniadirAlumno(String idAlumno, String nombre, String apellido, Date fechaNamiciento);

}
