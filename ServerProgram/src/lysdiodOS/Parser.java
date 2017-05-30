package lysdiodOS;

public class Parser {
	private final int REGISTER = 0, STRING = 1, NUMBER = 2;
	
	private final Instruction[] INSTRUCTIONS = new Instruction[]{
			new Instruction("NOP", 0b000000, new int[]{-1}), 
			new Instruction(":", 0b100000, new int[]{STRING}),
			new Instruction("JMP", 0b010000, new int[]{STRING}),
			new Instruction("LDI", 0b001000, new int[]{REGISTER, NUMBER}),
			new Instruction("OUT", 0b000100, new int[]{REGISTER, REGISTER}),
			new Instruction("IN", 0b000010, new int[]{REGISTER, REGISTER}),
			new Instruction("INC", 0b000001, new int[]{REGISTER}),
			new Instruction("ADD", 0b110000, new int[]{REGISTER, REGISTER}),
			new Instruction("SUB", 0b101000, new int[]{REGISTER, REGISTER}),
			new Instruction("LD", 0b100100, new int[]{REGISTER, REGISTER}),
			new Instruction("AND", 0b100010, new int[]{REGISTER, REGISTER}),
			new Instruction("ANDI", 0b100001, new int[]{REGISTER, NUMBER}),
			new Instruction("OR", 0b111000, new int[]{REGISTER, REGISTER}),
			new Instruction("ORI", 0b110100, new int[]{REGISTER, NUMBER}),
			new Instruction("CPI", 0b110010, new int[]{REGISTER, NUMBER}),
			new Instruction("BREQ", 0b110001, new int[]{STRING}),
			new Instruction("BRGE", 0b111100, new int[]{STRING}),
			new Instruction("BRLT", 0b111010, new int[]{STRING}),
			new Instruction("SBI", 0b111001, new int[]{REGISTER, NUMBER}),
			new Instruction("CBI", 0b111110, new int[]{REGISTER, NUMBER}),
	};
	
	private final String ENTER_INSTRUCTION = "-2";
	
	private final String PINS_PORTS[] = new String[]{
		"PORTD",
		"DDRD",
		"PIND",
		"PORTB",
		"DDRB",
		"PINB",
		"PORTC",
		"DDRC",
		"PINC",
	};
	
	private String total;
	private String currentLine;
	private String input;
	
	private String currentPart;
	
	private int count;
	
	private boolean firstBitHalf = false;
	
	private Instruction currentInstruction;
	
	public Parser() {
		currentPart = "000";
		total = "";
		currentLine = "";
	}
	
	public void update(String input) {
		if(!input.equals("-2"))
			for(int i = 0; i < 3; i++) {
				if(Integer.parseInt(input.charAt(0) + "") == (i+1)) {
					if(currentPart.charAt(i) == '0') {
						String t = "";
						for(int j = 0; j < 3; j++) {
							t += (j == i) ? '1' : currentPart.charAt(j);
						}
						currentPart = t;
					} else {
						String t = "";
						for(int j = 0; j < 3; j++) {
							t += (j == i) ? '0' : currentPart.charAt(j);
						}
						currentPart = t;
					}
				}
			}
		
		if(input.equals(ENTER_INSTRUCTION)) {
			currentLine += currentPart;
			System.out.println(currentLine);
			if(!firstBitHalf) {
				if(count == 0) {
					currentInstruction = getInstruction(Integer.parseInt(currentLine));
				}
				
				currentLine += " ";
				
				count += 1;
				
				if(currentInstruction != null && count > currentInstruction.getParameters().length) {
					total += currentLine + "\n";
					currentLine = "";
					currentPart = "000";
					count = 0;
				}
			}
			
			firstBitHalf = !firstBitHalf;
			input = "";
		}
	}
	
	public String translateParamter(int value, int index, int parameter) {
		if(getInstruction(value).getParameters()[index] == REGISTER) {
			if(parameter <= 32) return "r"+parameter;
			if(parameter <= 41) return PINS_PORTS[parameter-33];
			else return parameter + "";
		} else if(getInstruction(value).getParameters()[index] == STRING) {
			return (char)(64+parameter) + "";
		} else if(getInstruction(value).getParameters()[index] == NUMBER) {
			return parameter + "";
		}
		
		return "";
	}
	
	public String translate(int value) {
		return getInstruction(value).getName();
	}
	
	public Instruction getInstruction(String name) {
		for(int i = 0; i < INSTRUCTIONS.length; i++) {
			if(INSTRUCTIONS[i].getName().equals(name)) 
				return INSTRUCTIONS[i];
		}
		
		return null;
	}
	
	public Instruction getInstruction(int value) {
		for(int i = 0; i < INSTRUCTIONS.length; i++) {
			if(INSTRUCTIONS[i].getValue() == value) 
				return INSTRUCTIONS[i];
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		Parser p = new Parser();
		
		p.update("2");
		p.update("-2");
		p.update("1");
		p.update("-2");
	}
}
