/** Assignment 3
 * @author Andrew Dybka
 * @studentID 101041087
 * 
 * READ ME describes the objective and how the program works.
 */

package myAssignment2;

public class chef extends Thread implements Runnable{
	
	private String name, ingredient ;
	private Kitchen system;
	public long time;
	
	public chef (String name, String ingredient, Kitchen theSystem) {
		super(name);
		this.name = name;
		this.ingredient = ingredient;
		this.system = theSystem;
	}
	
	
	//run class to call the make in kitchen system

	public void run() {
		//startTime = System.nanoTime();
		while(system.count<100000) {
			try {
				system.make(this, ingredient);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//endTime = System.nanoTime();
	}

}
