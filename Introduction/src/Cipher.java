
public class Cipher {
	
	public float[] distribution;

	public static void main(String[] args) {
		String plainText = "Nz!obnf!jt!Xbmfz";
		System.out.println(caesarDecrypt(plainText, 1));
	}
	
	// Useful ASCII characters goes from 32 to 126
	public static String caesarEncrypt(String plainText, int key) {
		char[] letters = new char[plainText.length()];
		
		for (int i = 0; i < letters.length; i++) {
			char oldChar = plainText.charAt(i);
			char newChar = (char) (findRemainder(oldChar - 32 + key, 95) + 32);
			letters[i] = newChar;
		}
		
		return new String(letters);
	}
	
	public static String caesarDecrypt(String cipherText, int key) {
		return caesarEncrypt(cipherText, -key);
	}
	
	private static int findRemainder(int dividend, int divisor) {
		return (dividend - divisor * (dividend / divisor) + divisor) % divisor;
	}
	
	// {1, 1, 5, 1} -> 10
	// {1} -> 1
	public static int findSum(int[] array) {
		int nowAdded = 0;
		int needToAdd;
		
		for (int X = 0; X < array.length; X++) {
			needToAdd = array[X];
			nowAdded = nowAdded + needToAdd;
		}
		return nowAdded;
	}
		
}
