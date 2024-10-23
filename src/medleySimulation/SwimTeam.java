//M. M. Kuttel 2024 mkuttel@gmail.com
//Modified by B. Tshwane 2024 tshbon035
//Class to represent a swim team - which has four swimmers
package medleySimulation;

import medleySimulation.Swimmer.SwimStroke;

public class SwimTeam extends Thread {
	
	public static StadiumGrid stadium; //shared 
	private Swimmer [] swimmers;
	private int teamNo; //team number
	
	public static final int sizeOfTeam=4;
	
	SwimTeam( int ID, FinishCounter finish,PeopleLocation [] locArr ) {
		this.teamNo=ID;
		
		swimmers= new Swimmer[sizeOfTeam];
	    SwimStroke[] strokes = SwimStroke.values();  // Get all enum constants
		stadium.returnStartingBlock(ID);

		for(int i=teamNo*sizeOfTeam,s=0;i<((teamNo+1)*sizeOfTeam); i++,s++) { //initialise swimmers in team
			locArr[i]= new PeopleLocation(i,strokes[s].getColour());
	      	int speed=(int)(Math.random() * (3)+30); //range of speeds 
			swimmers[s] = new Swimmer(i,teamNo,locArr[i],finish,speed,strokes[s]); //hardcoded speed for now
		}
	}


	public void run() {
		try {
			// Loop through each swimmer in the team
			for (int s = 0; s < sizeOfTeam; s++) {
				// Synchronize on the individual swimmer thread
				synchronized (swimmers[s]) {
					// Start the swimmer thread
					swimmers[s].start();
					// Wait for the swimmer to notify that they are ready to start
					swimmers[s].wait(); // This allows the swimmer to signal that they are ready and waiting
				}
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	

