//M. M. Kuttel 2024 mkuttel@gmail.com
//Modified by B. Tshwane 2024 tshbon035
// Simple Thread class to update the display of a text field
package medleySimulation;

import java.awt.Color;
import javax.swing.JLabel;

public class CounterDisplay implements Runnable {
	private FinishCounter results;
	private JLabel win;

	CounterDisplay(JLabel w, FinishCounter score) {
		this.win = w;
		this.results = score;
	}

	public void run() { // this thread just updates the display of a text field
		while (true) {
			// Check if the race is finished (i.e., the top 3 teams have crossed the line)
			if (results.isRaceWon()) {
				win.setForeground(Color.RED);
				win.setText("<html>1st Team: " + results.getWinningTeam() + " Time: " + results.getWinningTime() + "ms<br>" +
						"2nd Team: " + results.getSecondTeam() + " Time: " + results.getSecondTime() + "ms<br>" +
						"3rd Team: " + results.getThirdTeam() + " Time: " + results.getThirdTime() + "ms</html>");
			} else {
				win.setForeground(Color.BLACK);
				win.setText("------");
			}
		}
	}
}

