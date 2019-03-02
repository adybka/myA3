/** Assignment 3
 * @author Andrew Dybka
 * @studentID 101041087
 * 
 * READ ME describes the objective and how the program works.
 */

package myAssignment2;


public class Agent extends Thread implements Runnable{
	
	private Kitchen system;
	public long time;
	public Agent(Kitchen system) {
		super("agent");
		this.system = system;
		
		
	}
	
	//called put class in kitchen system 
	
	public void run() {
		//startTime = System.nanoTime();
		while(system.count<100000) {
			try {
				system.put(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//endTime = System.nanoTime();
	}
	

}
