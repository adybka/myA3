/** Assignment 3
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
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Kitchen {
	
	//A3 measuring stuff
	private int sampleTime = 20000;
	private ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
	private RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
	private OperatingSystemMXBean osMxBean = ManagementFactory
			.getOperatingSystemMXBean();
	private Map<Long, Long> threadInitialCPU = new HashMap<Long, Long>();
	private Map<Long, Float> threadCPUUsage = new HashMap<Long, Float>();
	private long initialUptime = runtimeMxBean.getUptime();
	
	private static String BREAD="bread", PB = "peanut butter", JAM = "jam";//all ingredients
	private ArrayList<String> table, ingriedents;
	public int count=0;
	private Random rand;
	private boolean empty;//boolean for if table is empty
	
	//private long AgentstartTime, AgentendTime, chefStart, chefEnd;
	//private boolean starting = true;
	
	
	
	public Kitchen(){
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
	public void put(Agent theAgent)throws InterruptedException {
		
			synchronized(this) {//synch this instance
				//wait if kitchen is not empty
				while(!empty) {
					wait();
				}
				
				//get start execution time
				long start = System.nanoTime();
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
				//add total execution time to agent's time
				theAgent.time+= (System.nanoTime() - start);
			}
		
	}
	
	//make class called by chef
	public void make(chef chefName, String chefIngredient)throws InterruptedException {
	
			synchronized(this) {//synch this instance of the kitchen
				//wait if kitchen is empty
				while(empty) {
					wait();
				}
				//get start execution time
				long start = System.nanoTime();
				//determine what chef calls this class an ingredients in kitchen
				if ((table.contains(BREAD) && (table.contains(PB)))) {
					System.out.println("The " + chefName + " spreads on " + chefIngredient + " to finish the sandwich and then he eats it \n");
					empty = true;
					notifyAll();
					count++;
				} else if ((table.contains(BREAD) && (table.contains(JAM)))) {
					System.out.println("The " + chefName + " spreads on " + chefIngredient + " to finish the sandwich and then he eats it \n");
					empty = true;
					notifyAll();
					count++;
				} else if ((table.contains(PB) && (table.contains(JAM)))) {
					System.out.println("The " + chefName + " uses his " + chefIngredient + " to finish the sandwich and then he eats it \n");
					empty = true;
					notifyAll();
					count++;
				}
				//add total execution to threads time
				chefName.time+=(System.nanoTime()-start);
			}


		
	}
	public void measure(Thread agentThread, Thread pbThread, Thread breadThread, Thread jamThread) { 
		
		
		Thread[] threadInfos = {agentThread, pbThread, breadThread, jamThread};
		for (Thread info : threadInfos) {
			threadInitialCPU.put(info.getId(),
					threadMxBean.getThreadCpuTime(info.getId()));
		}
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}
 	
		long upTime = runtimeMxBean.getUptime();
 
		Map<Long, Long> threadCurrentCPU = new HashMap<Long, Long>();
		for (Thread info : threadInfos) {
			threadCurrentCPU.put(info.getId(),
					threadMxBean.getThreadCpuTime(info.getId()));
		}
 
		// CPU over all processes
		int nrCPUs = osMxBean.getAvailableProcessors();
		// total CPU: CPU % can be more than 100% (devided over multiple cpus)
		//long nrCPUs = 1;
		// elapsedTime is in ms.
		long elapsedTime = (upTime - initialUptime);
		for (Thread info : threadInfos) {
			// elapsedCpu is in ns
			//Long initialCPU = threadInitialCPU.get(info.getId());
			//if (initialCPU != null) {
				float elapsedCpu = threadCurrentCPU.get(info.getId())
						- threadInitialCPU.get(info.getId());
				//System.out.println(elapsedCpu + " = " + threadCurrentCPU.get(info.getId()) + " - " + threadInitialCPU.get(info.getId()));
				float cpuUsage = elapsedCpu * 100/ (elapsedTime * 1000000F * nrCPUs);
				threadCPUUsage.put(info.getId(), cpuUsage);
			//}
		}
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
		}
		
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
		//start threads
		agent.start();
		breadMaker.start();
		PBMaker.start();
		JAMMaker.start();
		new Kitchen().measure(agent, breadMaker, PBMaker, JAMMaker);
		System.out.println("Bread Chef Proccess " + (breadMaker.time) + "ns or " + (breadMaker.time)/1000000000 + "s");
		System.out.println("PB Chef Proccess " + (PBMaker.time) + "ns or " + (PBMaker.time)/1000000000 + "s");
		System.out.println("Jam Chef Proccess " + (JAMMaker.time) + "ns or " + (JAMMaker.time)/1000000000 + "s");
		System.out.println("Agent Proccess " + (agent.time) + "ns or " + (agent.time) /1000000000 + "s");
		//System.exit(0);
		

		
	}
	

}
