//M. M. Kuttel 2024 mkuttel@gmail.com
//Modified by B. Tshwane 2024 tshbon035
//Class to represent a swimmer swimming a race
//Swimmers have one of four possible swim strokes: backstroke, breaststroke, butterfly and freestyle
package medleySimulation;

import java.awt.Color;

import java.util.Random;

public class Swimmer extends Thread {
	
	public static StadiumGrid stadium; //shared 
	private FinishCounter finish; //shared
	
		
	GridBlock currentBlock;
	private Random rand;
	private int movingSpeed;
	
	private PeopleLocation myLocation;
	private int ID; //thread ID 
	private int team; // team ID
	private GridBlock start;

	public enum SwimStroke { 
		Backstroke(1,2.5,Color.black),
		Breaststroke(2,2.1,new Color(255,102,0)),
		Butterfly(3,2.55,Color.magenta),
		Freestyle(4,2.8,Color.red);
	    	
	     private final double strokeTime;
	     private final int order; // in minutes
	     private final Color colour;   

	     SwimStroke( int order, double sT, Color c) {
	            this.strokeTime = sT;
	            this.order = order;
	            this.colour = c;
	        }
	  
	        public int getOrder() {return order;}

	        public  Color getColour() { return colour; }
	    }  
	    private final SwimStroke swimStroke;
	
	//Constructor
	Swimmer( int ID, int t, PeopleLocation loc, FinishCounter f, int speed, SwimStroke s) {
		this.swimStroke = s;
		this.ID=ID;
		movingSpeed=speed; //range of speeds for swimmers
		this.myLocation = loc;
		this.team=t;
		start = stadium.returnStartingBlock(team);
		finish=f;
		rand=new Random();
	}
	
	//getter
	public   int getX() { return currentBlock.getX();}	
	
	//getter
	public   int getY() {	return currentBlock.getY();	}
	
	//getter
	public   int getSpeed() { return movingSpeed; }

	
	public SwimStroke getSwimStroke() {
		return swimStroke;
	}

	//!!!You do not need to change the method below!!!
	//swimmer enters stadium area
	public void enterStadium() throws InterruptedException {
		currentBlock = stadium.enterStadium(myLocation);  //
		sleep(200);  //wait a bit at door, look around
	}
	
	//!!!You do not need to change the method below!!!
	//go to the starting blocks
	//printlns are left here for help in debugging
	public void goToStartingBlocks() throws InterruptedException {		
		int x_st= start.getX();
		int y_st= start.getY();
	//System.out.println("Thread "+this.ID + " has start position: " + x_st  + " " +y_st );
	// System.out.println("Thread "+this.ID + " at " + currentBlock.getX()  + " " +currentBlock.getY() );
	 while (currentBlock!=start) {
		//	System.out.println("Thread "+this.ID + " has starting position: " + x_st  + " " +y_st );
		//	System.out.println("Thread "+this.ID + " at position: " + currentBlock.getX()  + " " +currentBlock.getY() );
			sleep(movingSpeed*3);  //not rushing 
			currentBlock=stadium.moveTowards(currentBlock,x_st,y_st,myLocation); //head toward starting block
		//	System.out.println("Thread "+this.ID + " moved toward start to position: " + currentBlock.getX()  + " " +currentBlock.getY() );
		}
	System.out.println("-----------Thread "+this.ID + " at start " + currentBlock.getX()  + " " +currentBlock.getY() );
	}
	
	//!!!You do not need to change the method below!!!
	//dive in to the pool
	private void dive() throws InterruptedException {
		int x= currentBlock.getX();
		int y= currentBlock.getY();
		currentBlock=stadium.jumpTo(currentBlock,x,y-2,myLocation);
	}
	
	//!!!You do not need to change the method below!!!
	//swim there and back
	private void swimRace() throws InterruptedException {
		int x= currentBlock.getX();
		while((boolean) ((currentBlock.getY())!=0)) {
			currentBlock=stadium.moveTowards(currentBlock,x,0,myLocation);
			//System.out.println("Thread "+this.ID + " swimming " + currentBlock.getX()  + " " +currentBlock.getY() );
			sleep((int) (movingSpeed*swimStroke.strokeTime)); //swim
			System.out.println("Thread "+this.ID + " swimming  at speed" + movingSpeed );	
		}

		while((boolean) ((currentBlock.getY())!=(StadiumGrid.start_y-1))) {
			currentBlock=stadium.moveTowards(currentBlock,x,StadiumGrid.start_y,myLocation);
			//System.out.println("Thread "+this.ID + " swimming " + currentBlock.getX()  + " " +currentBlock.getY() );
			sleep((int) (movingSpeed*swimStroke.strokeTime));  //swim
		}
		
	}
	
	//!!!You do not need to change the method below!!!
	//after finished the race
	public void exitPool() throws InterruptedException {		
		int bench=stadium.getMaxY()-swimStroke.getOrder(); 			 //they line up
		int lane = currentBlock.getX()+1;//slightly offset
		currentBlock=stadium.moveTowards(currentBlock,lane,currentBlock.getY(),myLocation);
	   while (currentBlock.getY()!=bench) {
		 	currentBlock=stadium.moveTowards(currentBlock,lane,bench,myLocation);
			sleep(movingSpeed*3);  //not rushing 
		}
	}

	public void run() {
		try {
			// Swimmer arrives at the stadium
			sleep(movingSpeed + (rand.nextInt(10))); // Simulate the time it takes for the swimmer to arrive, adding some randomness
			myLocation.setArrived(); // Update swimmer's location to "arrived"
			enterStadium(); // Simulate the swimmer entering the stadium

			synchronized (this) {
				notify(); // Notify any waiting thread that this swimmer has arrived and is ready to proceed
			}

			goToStartingBlocks(); // Simulate the swimmer moving to their starting blocks

			// Synchronize on the stadium object to ensure team members coordinate their actions
			synchronized (stadium) {
				// Wait for the current team to start; if the team's turn hasn't arrived, the swimmer waits
				while (stadium.getTeams(team)) {
					stadium.wait(); // Wait until it's the team's turn
				}
				stadium.setTeams(team, true); // Set the team's turn flag to true when it's their turn to swim
			}

			// Countdown latch: used to ensure all swimmers in the relay race are ready before starting the race
			stadium.latch.countDown(); // Indicate that this swimmer is ready (decrease the latch count)
			stadium.latch.await(); // Wait until all swimmers are ready

			dive(); // Simulate the dive into the pool

			swimRace(); // Simulate the swimmer's stroke during the race

			// After finishing the race, the swimmer signals their team that they are done
			synchronized (stadium) {
				stadium.setTeams(team, false); // Indicate that this swimmer has finished
				stadium.notifyAll(); // Notify all waiting threads to continue
			}

			// Check if this swimmer is the last swimmer in the medley (if their swim stroke is the final one)
			if (swimStroke.order == 4) {
				finish.finishRace(ID, team); // Mark the finish and record the team and swimmer ID at the finish line
			}
			else {
				//System.out.println("Thread "+this.ID + " done " + currentBlock.getX()  + " " +currentBlock.getY() );			
				exitPool();//if not last swimmer leave pool
			}
			
		} catch (InterruptedException e) {  //do nothing
		}
	}
	
}
