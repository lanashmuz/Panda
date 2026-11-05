package Model;

import java.util.Random;

public class StepsDie {
	
	private final Random random = new Random();
	
	// Generates a random dice result between 1 and 6
	public int roll() {
		return random.nextInt(6) + 1;
	}
}
