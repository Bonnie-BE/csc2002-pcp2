//Modified by B. Tshwane 2024 tshbon035
// Simple class to record when someone has crossed the line first and wins
package medleySimulation;

public class FinishCounter {
	private boolean firstAcrossLine; //flag
	private int[] topTeams = new int[3]; // to store the first three teams
	private int[] finishTimes = new int[3]; // to store finish times for top 3 teams
	private int placeCounter; // counter to track place
	private long startTime; // to track the race start time

	FinishCounter() {
		firstAcrossLine = true; // no-one has won at start
		placeCounter = 0; // no places assigned yet
		startTime = System.currentTimeMillis(); // track when the race starts
	}

	// This is called by a swimmer when they touch the finish line
	public synchronized void finishRace(int swimmer, int team) {
		long currentTime = System.currentTimeMillis();
		int timeTaken = (int) (currentTime - startTime); // calculate time taken for this swimmer

		if (placeCounter < 3) { // only store the top 3 teams
			topTeams[placeCounter] = team;
			finishTimes[placeCounter] = timeTaken;
			placeCounter++;
		}
	}

	// Return true if all 3 places are filled
	public boolean isRaceWon() {
		return placeCounter >= 3;
	}

	// Get the winning team
	public int getWinningTeam() {
		return topTeams[0]; // the team that finished first
	}

	// Get the second team
	public int getSecondTeam() {
		return topTeams[1];
	}

	// Get the third team
	public int getThirdTeam() {
		return topTeams[2];
	}

	// Get the time taken by the first, second, and third teams
	public int getWinningTime() {
		return finishTimes[0];
	}

	public int getSecondTime() {
		return finishTimes[1];
	}

	public int getThirdTime() {
		return finishTimes[2];
	}
}



