/** Assignment 2
 * @author Andrew Dybka
 * @studentID 101041087
 * 
 * READ ME describes the objective and how the program works.
 */

package myAssignment2;


public class Agent extends Thread{
	
	private Kitchen system;
	
	public Agent(Kitchen system) {
		super("agent");
		this.system = system;
		
	}
	
	//called put class in kitchen system 
	//Overrides the run class in Thread
	@Override
	public void run() {
		try {
			system.put();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
