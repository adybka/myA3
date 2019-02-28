/** Assignment 2
 * @author Andrew Dybka
 * @studentID 101041087
 * 
 * READ ME describes the objective and how the program works.
 */

package myAssignment2;

public class chef extends Thread{
	
	private String name, ingredient ;
	private Kitchen system;
	
	public chef (String name, String ingredient, Kitchen theSystem) {
		super(name);
		this.name = name;
		this.ingredient = ingredient;
		this.system = theSystem;
	}
	
	
	//run class to call the make in kitchen system
	//overrides the default run class in thread
	@Override
	public void run() {
		try {
			system.make(name, ingredient);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
