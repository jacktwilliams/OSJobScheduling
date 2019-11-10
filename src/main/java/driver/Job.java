package driver;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

public class Job {
	private int id;
	private String name;
	private int priority;
	private Deque<String> execSeq; //use as stack
	
	private int arrival = 0;
	private int completion;
	private int totalReadyWait = 0;
	private Integer placed;
	private Integer ioCompletionTime; //if we are doing IO (waiting) this field holds the time at which IO will be done.
	private String waitingFor; //holds friendly string naming what we are waiting for.
	private boolean ready = false, completed = false, waiting = false;
	private boolean lastPicked = false; //using for round robin
	

	public Job(String name, int id, int priority, String execSeq) {
		this.name = name;
		this.id = id;
		this.priority = priority;
		this.execSeq = new LinkedList<String>(Arrays.asList(execSeq.split("\\s")));
	}
	
	public void placeInReadyQueue(int time) {
		ready = true;
		placed = time;
	}
	
	public void retrieveFromReadyQueue(int time) {
		try {
			totalReadyWait += (time - placed);
		} catch (NullPointerException e) {
			System.err.println("placeInReadyQueue was not called.");
			throw e;
		}
		placed = null;
		ready = false;
	}
	

	public void setCompletion(int completion) {
		completed = true;
		if (ready) {
			retrieveFromReadyQueue(completion);
		}
		waiting = false;
		this.completion = completion;
	}
	
	public String nextOP() {
		if (execSeq.size() > 0) {
			return execSeq.pop();
		}
		return null;
	}
	
	public Integer peekNextCPUBurst() {
		for (String op : execSeq) {
			Integer burst = null;
			boolean failed = false;
			try {
				burst = Integer.parseInt(op);
			} catch (Exception e) {
				failed = true;
			} finally {
				if (!failed) {
					return burst;
				}
			}
		}
		return null;
	}
	
	public void returnNonCompletedOP(String op) {
		execSeq.push(op);
	}
	
	public void wait(int time, String waitingFor) {
		this.waiting = true;
		if (ready) {
			retrieveFromReadyQueue(time);
		}
		this.waitingFor = waitingFor;
		this.ioCompletionTime = time;
	}
	
	public void setIODone() {
		this.waiting = false;
		this.ioCompletionTime = null;
	}
	
	public String getWaitingFor() {
		return waitingFor;
	}
	
	public boolean isWaiting() {
		return waiting;
	}

	public Integer getIoCompletionTime() {
		return ioCompletionTime;
	}
	
	/*
	 * Indicates the job could be moved to completed.
	 */
	public boolean isFinished() {
		return !completed && execSeq.size() == 0 && !waiting;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Deque<String> getExecSeq() {
		return execSeq;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getArrival() {
		return arrival;
	}

	public int getCompletion() {
		return completion;
	}

	public int getTotalReadyWait() {
		return totalReadyWait;
	}
	
	
	public boolean isReady() {
		return ready;
	}

	public boolean isCompleted() {
		return completed;
	}

	public boolean isLastPicked() {
		return lastPicked;
	}

	public void setLastPicked(boolean lastPicked) {
		this.lastPicked = lastPicked;
	}

	public String toString() {
		return "" + id + ". " + name + ": " + priority + ", " + execSeq;
	}
}
