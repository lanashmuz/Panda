package Model;

import java.util.List;

// Stores the players and difficulty setting for a game
public class GameConfiguration {
	// List of players participating in the game
	private List<Player> players;
	
	// Selected game difficulty
	private String difficulty;

	// Creates a game configuration with players and difficulty
	public GameConfiguration(List<Player> players, String difficulty) {
		this.players = List.copyOf(players);
		this.difficulty = difficulty;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public String getDifficulty() {
		return difficulty;
	}

}
