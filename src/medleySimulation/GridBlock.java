//M. M. Kuttel 2024 mkuttel@gmail.com
//Modified by B. Tshwane 2024 tshbon035
// GridBlock class to represent a block in the grid.
// only one thread at a time "owns" a GridBlock - this must be enforced

package medleySimulation;

import java.util.concurrent.atomic.AtomicInteger;

public class GridBlock {
	
	private AtomicInteger isOccupied;  //atomicity prevents race conditions
	
	private final boolean isStart;  //is this a starting block?
	private int [] coords; // the coordinate of the block.
	
	GridBlock(boolean startBlock) throws InterruptedException {
		isStart = startBlock;
		isOccupied = new AtomicInteger(-1);  //atomicity prevents race conditions
	}
	
	GridBlock(int x, int y, boolean startBlock) throws InterruptedException {
		this(startBlock);
		coords = new int [] {x,y};
	}

	public synchronized int getX() {return coords[0];}  //added synchronized this to prevent race conditions
	
	public synchronized int getY() {return coords[1];}  //added synchronized this to prevent race conditions

	//Get a block
	public synchronized boolean get(int threadID) throws InterruptedException {  //added synchronized this to prevent race conditions
			if (isOccupied.get() == threadID)
				return true; //thread Already in this block

			if (isOccupied.get() >= 0)
				return false; //space is occupied

			isOccupied.set(threadID);  //set ID to thread that had block
			return true;
	}

	//release a block
	public synchronized void release() {  //added synchronized this to prevent race conditions
		isOccupied.set(-1);
		notifyAll();  //ADDED THIS!
	}

	//is a bloc already occupied?
	public synchronized boolean occupied() {  //added synchronized this to prevent race conditions
		if(isOccupied.get() == -1)
			return false;
		return true;
	}

	//is a start block
	public synchronized boolean isStart() {
		return isStart;
	}  //added synchronized this to prevent race conditions

}