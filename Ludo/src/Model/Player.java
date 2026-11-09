package Model;

// Represents a player with a name and selected color
public class Player {
	private String name;
	private String color;
	
	// Creates a player with a name and color
	public Player(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "Player [name=" + name + ", color=" + color + "]";
	}
	// Inside Player.java
	private boolean finishedGame = false;

	public boolean hasFinishedGame() {
	    return finishedGame;
	}

	public void setFinishedGame(boolean finishedGame) {
	    this.finishedGame = finishedGame;
	}
}
