package driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Driver {
	public static final String fname = "jobs.dat";
	public static int timer; //length of time slice
	public static int degree; //degree of multiprogramming -- #of jobs that can share cpu
	public static int time = 0;
	public static List<Job> jobs = new LinkedList<Job>();
	public static final int IO = 50;
	public static final int INTERACTIVE = 200;
	
	public static void main(String[] args) throws Exception {
		readData();
		System.out.println(jobs);
		run();
	}
	
	private static void run() throws Exception {
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
				runTimeSlot(pick);
			} else {
				//all jobs could be waiting
				jumpToNextIOCompletionEvent();
			}
			contextSwitch();
		}
	}
	
	private static void jumpToNextIOCompletionEvent() {
		Job nextUp = jobs.stream().filter(j -> j.isWaiting()).sorted(new Comparator<Job>() {
			@Override
			public int compare(Job arg0, Job arg1) {
				// TODO Auto-generated method stub
				if (arg0.getIoCompletionTime() < arg1.getIoCompletionTime()) {
					return -1;
				} else if (arg1.getIoCompletionTime() < arg0.getIoCompletionTime()){
					return 1;
				}
				return 0;
			}
		}).findFirst().get();
		
		if (nextUp != null) {
			time = nextUp.getIoCompletionTime();
		}
	}
	
	private static void runTimeSlot(Job job) throws Exception {
		boolean done = false;
		int remainingTime = timer;
		while (!done) {
			String op = job.nextOP();
			if (op == null) {
				throw new Exception("Don't think we should get here.");
			} else if (op.equals("I")) {
				job.wait(time + IO, "Input");
				jobMessage(job, "needs Input", "in wait");
				done = true;
			} else if (op.equals("O")) {
				job.wait(time + IO, "Output");
				jobMessage(job, "needs Output", "in wait");
				done = true;
			} else if (op.equals("T")) {
				job.wait(time + INTERACTIVE, "Terminal");
				jobMessage(job, "needs Interactive", "in wait");
				done = true;
			} else {
				//cpu burst
				jobMessage(job, "running");
				int burst = Integer.parseInt(op);
				if (burst < remainingTime) {
					remainingTime -= burst;
					time += burst;
					if (job.getExecSeq().size() == 0) { //could've consumed last burst here.
						done = true;
					}
				} else if (burst == remainingTime) {
					done = true;
					time += burst;
					jobMessage(job, "timed out", "in ready");
					job.placeInReadyQueue(time);
				} else if (burst > remainingTime) {
					done = true;
					time += remainingTime;
					jobMessage(job, "timed out", "in ready");
					job.placeInReadyQueue(time);
					job.returnNonCompletedOP(String.valueOf(burst - remainingTime));
				}
			}
		}
	}
	
	/*
	 * Check for completed IO.
	 * Check for completed jobs.
	 */
	private static void contextSwitch() {
		for (Job j : jobs) {
			if (j.isWaiting() && j.getIoCompletionTime() <= time) {
				j.setIODone();
				jobMessage(j, "got " + j.getWaitingFor(), "in ready");
				j.placeInReadyQueue(time);
			}
			if (j.isFinished()) { 
				//job not yet completed but is ready to be moved to completed state
				j.setCompletion(time);
				jobMessage(j, "DONE");
				loadJobs();
			}
		}
	}
	
	private static void loadJobs() {
		Job[] candidates = jobs.stream().filter(j -> !j.isCompleted() && !j.isReady() && !j.isWaiting()).toArray(Job[]::new);
		long numLoaded = jobs.stream().filter(j -> j.isReady() || j.isWaiting()).count();
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
			readL.close();
		}
		readIn.close();
	}
	
	private static void printMessage(String msg) {
		System.out.println(String.format("%5d: %s", time, msg));
	}
	
	private static void jobMessage(Job j, String msg) {
		printMessage("'" + j.getName() + "' " + msg);
	}
	
	private static void jobMessage(Job j, String msg, String msg1) {
		String jobString = "'" + j.getName() + "' ";
		printMessage(String.format("%-35s%s", jobString + msg, " " + jobString + msg1));
	}

}
