//M. M. Kuttel 2024 mkuttel@gmail.com
//Modified by B. Tshwane 2024 tshbon035
// MedleySimulation main class, starts all threads
package medleySimulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class MedleySimulation {
	static final int numTeams=10;
	static int frameX=400; // frame width
	static int frameY=580;  // frame height

	static JFrame frame;
	static SwimTeam[] teams;
	static PeopleLocation[] peopleLocations;
	static StadiumView stadiumView;
	static StadiumGrid stadiumGrid;
	static FinishCounter finishLine;
	static CounterDisplay counterDisplay;
	static CountDownLatch latch = new CountDownLatch(1);  //countdown latch for threads to wait until countdown hits zero

	public static void main(String[] args) throws InterruptedException {
		finishLine = new FinishCounter();
		stadiumGrid = new StadiumGrid(50, 150, numTeams, finishLine);
		SwimTeam.stadium = stadiumGrid;
		Swimmer.stadium = stadiumGrid;
		peopleLocations = new PeopleLocation[numTeams * SwimTeam.sizeOfTeam];
		teams = new SwimTeam[numTeams];
		for (int i = 0; i < numTeams; i++) {
			teams[i] = new SwimTeam(i, finishLine, peopleLocations);
		}

		frame = new JFrame("Swim Medley Relay");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(frameX, frameY);
		frame.setLocationRelativeTo(null);

		// Display the splash screen with a start button
		setupSplashScreen();
		frame.setVisible(true);

		// Wait for the latch to be counted down when the start button is clicked
		latch.await();

		// After clicking start, initialize the relay simulation
		setupRelaySimulation();
	}

	// Method to set up the splash screen
	public static void setupSplashScreen() {
		JPanel splashPanel = new JPanel();
		splashPanel.setLayout(new BorderLayout());

		// Add an image (replace "path/to/image.jpg" with the actual path to your image)
		JLabel imageLabel = new JLabel(new ImageIcon("input/relay.jpg"));
		splashPanel.add(imageLabel, BorderLayout.CENTER);

		// Add start button
		JButton startButton = new JButton("Start Relay");
		splashPanel.add(startButton, BorderLayout.SOUTH);

		// Button action to remove the splash screen and proceed to the simulation
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();  // Clear the splash screen
				latch.countDown();  // Start the simulation
			}
		});

		frame.setContentPane(splashPanel);  // Set the splash screen as the content pane
		frame.revalidate();
		frame.repaint();
	}

	// Method to set up the relay simulation after the splash screen
	public static void setupRelaySimulation() throws InterruptedException {
		JPanel relayPanel = new JPanel();
		relayPanel.setLayout(new BoxLayout(relayPanel, BoxLayout.Y_AXIS));

		stadiumView = new StadiumView(peopleLocations, stadiumGrid);
		relayPanel.add(stadiumView);

		JLabel winnerLabel = new JLabel("");
		relayPanel.add(winnerLabel);

		counterDisplay = new CounterDisplay(winnerLabel, finishLine);
		Thread resultsThread = new Thread(counterDisplay);
		resultsThread.start();

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(e -> System.exit(0));
		buttonPanel.add(quitButton);

		relayPanel.add(buttonPanel);

		frame.setContentPane(relayPanel);  // Set the relay panel as the content pane
		frame.revalidate();
		frame.repaint();

		Thread viewThread = new Thread(stadiumView);
		viewThread.start();

		// Start the teams
		for (SwimTeam team : teams) {
			team.start();
		}
	}
}

