/** Assignment 2
 * @author Andrew Dybka
 * @studentID 101041087
 * 
 * READ ME describes the objective and how the program works.
 */
package myAssignment2;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.System;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Kitchen2 {
	
	//A3 measuring stuff
	private int sampleTime = 20000;
	private ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
	private RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
	private OperatingSystemMXBean osMxBean = ManagementFactory
			.getOperatingSystemMXBean();
	private Map<Long, Long> threadInitialCPU = new HashMap<Long, Long>();
	private Map<Long, Float> threadCPUUsage = new HashMap<Long, Float>();
	private long initialUptime = System.nanoTime();
	
	private static String BREAD="bread", PB = "peanut butter", JAM = "jam";//all ingredients
	private ArrayList<String> table, ingriedents;
	private static int count;
	private Random rand;
	private boolean empty;//boolean for if table is empty
	
	
	
	public Kitchen2(){
		//create random object
		rand = new Random(); 
		//list for whats on table and total ingredients
		table = new ArrayList<String>();
		ingriedents = new ArrayList<String>();
		//add all ingredients
		ingriedents.add(BREAD);
		ingriedents.add(PB);
		ingriedents.add(JAM);
		//tables starts as emtpy
		empty = true;
		
	}
	
	//put class called by agent
	public void put()throws InterruptedException {
		while(count<20) {
			synchronized(this) {//synch this instance
				//wait if kitchen is not empty
				while(!empty) {
					wait();
				}
				//clear table, get two ingredients from ingredient list add to table
				table.clear();
				for (int i = 0; i < 2; i++) {
					int randomIndex = rand.nextInt(ingriedents.size());
					String randomElement = ingriedents.get(randomIndex);
					ingriedents .remove(randomElement);
					table.add(randomElement);
				}
				//refill ingredient list
				ingriedents.addAll(table); 		
				
				System.out.println("---------------------------------------");
				System.out.println(count + ": Agent placed ingrdients in the system: "+ table);
				empty=false;
				notifyAll();
			}
		}
		
	}
	
	//make class called by chef
	public void make(String chefName, String chefIngredient)throws InterruptedException {
		while(count<20) {
			synchronized(this) {//synch this instance of the kitchen
				//wait if kitchen is empty
				while(empty) {
					wait();
				}
				//determine what chef calls this class an ingredients in kitchen
				if ((table.contains(BREAD) && (table.contains(PB) && chefIngredient==JAM))) {
					System.out.println("The " + chefName + " spreads on " + chefIngredient + " to finish the sandwich and then he eats it \n");
					empty = true;
					notifyAll();
					count++;
				} else if ((table.contains(BREAD) && (table.contains(JAM) && chefIngredient==PB))) {
					System.out.println("The " + chefName + " spreads on " + chefIngredient + " to finish the sandwich and then he eats it \n");
					empty = true;
					notifyAll();
					count++;
				} else if ((table.contains(PB) && (table.contains(JAM) && chefIngredient==BREAD))) {
					System.out.println("The " + chefName + " uses his " + chefIngredient + " to finish the sandwich and then he eats it \n");
					empty = true;
					notifyAll();
					count++;
				}
				
			}

		}
		
	}
	public void measure(Thread agentThread, Thread pbThread, Thread breadThread, Thread jamThread) { 
		Thread[] threadInfos = {agentThread, pbThread, breadThread, jamThread};
 
		try {
			Thread.sleep(sampleTime);
		} catch (InterruptedException e) {
		}
 
		long upTime = runtimeMxBean.getUptime();
 
		Map<Long, Long> threadCurrentCPU = new HashMap<Long, Long>();
		//threadInfos = threadMxBean.dumpAllThreads(false, false);
		for (Thread info : threadInfos) {
			threadCurrentCPU.put(info.getId(),
					System.nanoTime());
		}
 
		// CPU over all processes
		int nrCPUs = osMxBean.getAvailableProcessors();
		// total CPU: CPU % can be more than 100% (devided over multiple cpus)
		//long nrCPUs = 1;
		// elapsedTime is in ms.
		long elapsedTime = (upTime - initialUptime);
		System.out.println("Elapsed time: "+ elapsedTime);
		for (Thread info : threadInfos) { 
			// elapsedCpu is in ns
			Long initialCPU = threadInitialCPU.get(info.getId());
			if (initialCPU != null) {
				float elapsedCpu = System.nanoTime()
						- initialCPU;
				System.out.println(info.getName()+ " Elapsed CPU: "+ elapsedCpu);
				float cpuUsage = elapsedCpu * 100/ (elapsedTime * 1000000F * nrCPUs);
				threadCPUUsage.put(info.getId(), cpuUsage);
			}
		}
 
		// threadCPUUsage contains cpu % per thread
		for (Thread info : threadInfos) {
			System.out.println(info.getName()+ ":       "+ info.getId());
		}
		System.out.println(threadCPUUsage);
		// You can use osMxBean.getThreadInfo(theadId) to get information on
		// every thread reported in threadCPUUsage and analyze the most CPU
		// intentive threads
 
	}
	
	
	public static void main(String[] args) {
		
		//creates kitchen, chefs and agents
		Kitchen system = new Kitchen();
		Agent agent = new Agent(system);
		chef breadMaker = new chef("breadChef", BREAD, system);
		chef PBMaker = new chef("PBChef", PB, system);
		chef JAMMaker = new chef("JamChef", JAM, system);
		//initialize count
		count = 0;
		//start threads
		agent.start();
		breadMaker.start();
		PBMaker.start();
		JAMMaker.start();
		system.measure(agent, PBMaker, breadMaker, JAMMaker);
		
	}
	

}
