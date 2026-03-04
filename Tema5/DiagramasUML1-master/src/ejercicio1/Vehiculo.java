package ejercicio1;

public class Vehiculo {
	
	private String matricula;
	private Integer anios;
	public Double kilometros;
	protected String numBastidor;
	
	public String getMatricula() {
		return this.matricula;
	}
	
	public void setNumBastidor(String bastidor) {
		this.numBastidor=bastidor;
	}
	
	private Boolean masDe10Anios() {
		return anios>10;
	}

}
