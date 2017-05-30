package lysdiodOS;

public class Instruction {
	private int value;
	
	private int[] parameters;
	
	private String name;
	
	public Instruction(String name, int value, int[] parameters) {
		this.name = name;
		this.value = value;
		this.parameters = parameters;
	}
	
	public String getName() {
		return name;
	}
	
	public int getValue() {
		return value;
	}
	
	public int[] getParameters() {
		return parameters;
	}
}
