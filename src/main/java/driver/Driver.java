package driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Driver {
	public static final String fname = "jobs.dat";
	public static int timer; //length of time slice
	public static int degree; //degree of multiprogramming -- #of jobs that can share cpu
	public static int time = 0;
	public static List<Job> jobs = new LinkedList<Job>();
	
	public static void main(String[] args) throws FileNotFoundException {
		readData();
		System.out.println(jobs);
		run();
	}
	
	private static void run() {
		for (Job j : jobs) {
			jobMessage(j, "has arrived.");
		}
//		Iterator<Job> itr = jobs.iterator();
//		for (int i = 0; i < degree; ++ i) {
//			if (itr.hasNext()) {
//				Job current = itr.next();
//				jobMessage(current, " loaded and ready");
//				current.placeInReadyQueue(time);
//			}
//		}
		loadJobs();
		
		while (jobs.stream().anyMatch(j -> !j.isCompleted())) {
			Job[] candidates = jobs.stream().filter((j) -> j.isReady()).toArray(Job[]::new);
			if (candidates.length > 0) {
				Job pick = candidates[0]; //TODO different selection strategies
				
			}			
		}
		
	}
	
	private static void loadJobs() {
		Job[] candidates = jobs.stream().filter(j -> !j.isReady() && !j.isWaiting()).toArray(Job[]::new);
		int numLoaded = jobs.size() - candidates.length;
		for (int i = 0; i < degree - numLoaded; ++i) {
			if (i < candidates.length) {
				Job toLoad = candidates[i];
				jobMessage(toLoad, " loaded and ready");
				toLoad.placeInReadyQueue(time);
			}
		}
	}
	
	private static void readData() throws FileNotFoundException {
		Scanner readIn = new Scanner(new File(fname));
		timer = readIn.nextInt();
		degree = readIn.nextInt();
		readIn.nextLine(); //some BS I don't remember why 
		
		int id = 1;
		while (readIn.hasNextLine()) {
			String line = readIn.nextLine();
			Scanner readL = new Scanner(line);
			String name = readL.next();
			int priority = readL.nextInt();
			StringBuilder exec = new StringBuilder(readL.next());
			while (readL.hasNext()) {
				exec.append(" " + readL.next());
			}
			jobs.add(new Job(name, id++, priority, exec.toString()));
		}
	}
	
	private static void printMessage(String msg) {
		System.out.println(String.format("%5d: %s", time, msg));
	}
	
	private static void jobMessage(Job j, String msg) {
		printMessage("'" + j.getName() + "' " + msg);
	}
	
	private static void jobMessage(Job j, String msg, String msg1) {
		printMessage("'" + j.getName() + "' " + String.format("%10s%s", msg,msg1));
	}

}
