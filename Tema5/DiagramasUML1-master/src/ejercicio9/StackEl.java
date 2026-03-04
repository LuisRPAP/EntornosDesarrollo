package ejercicio9;

public class StackEl {
	
	private Object content;
	private StackEl elementoAnterior;
	
	public void setProcedingEl(StackEl elemento) {
		this.elementoAnterior=elemento;
	}
	
	public Object getContent() {
		return this.content;
	}

	public StackEl getProceedingEl() {
		return this.elementoAnterior;
	}

}
