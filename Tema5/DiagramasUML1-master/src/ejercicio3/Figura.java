package ejercicio3;

public abstract class Figura {
	
	protected String color;
	protected Integer x;
	protected Integer y;
	
	public abstract Double area();
	public abstract Double perimetro();
	public String getColor() {
		return this.color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public void setY(Integer y) {
		this.y = y;
	}


}
