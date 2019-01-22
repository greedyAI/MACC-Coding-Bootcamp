import java.util.Scanner;
public class Calculator {
	
	public static float EPSILON = 0.00000000001f;
	
	public static void main(String[] args) {
		
		Scanner s = new Scanner(System.in);
		while (true) {
			float variable1;
			float variable2;
			String operator;
			float result;
			
			variable1 = s.nextFloat();
			s.nextLine();
			operator = s.nextLine();
			
			if (operator.equals("++")) {
				variable1++;
				System.out.println(variable1);
			} else if (operator.equals("--")) {
				variable1--;
				System.out.println(variable1);
			} else {
				variable2 = s.nextFloat();
				
				switch(operator) {
				case "+":
					result = variable1 + variable2;
					System.out.println("Answer: " + result);
					break;
				case "-":
					result = variable1 - variable2;
					System.out.println("Answer: " + result);
					break;
				case "*":
					result = variable1 * variable2;
					System.out.println("Answer: " + result);
					break;
				case "/":
					if (variable2 < EPSILON && variable2 > -EPSILON) {
						System.out.println("Dividing by zero!");
					} else {
						result = variable1 / variable2;
						System.out.println("Answer: " + result);
					}
					break;
				}
			}
		}		
	}
	
	public static void countdown() {
		int start = 60;
		while (start >= 0) {
			System.out.println(start);
			start--;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("TIME'S UP!!!");
	}
}
