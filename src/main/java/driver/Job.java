package driver;

public class Job {
	private int id;
	private String name;
	private int priority;
	private String execSeq;
	
	private int arrival = 0;
	private int completion;
	private int totalReadyWait = 0;
	private Integer placed;
	private boolean ready = false, completed = false, waiting = false;
	

	public Job(String name, int id, int priority, String execSeq) {
		this.name = name;
		this.id = id;
		this.priority = priority;
		this.execSeq = execSeq;
	}
	
	public void placeInReadyQueue(int time) {
		ready = true;
		placed = time;
	}
	
	public void retrieveFromReadyQueue(int time) {
		totalReadyWait += (time - placed);
		placed = null;
	}
	

	public void setCompletion(int completion) {
		completed = true;
		ready = false;
		this.completion = completion;
	}
	
	
	public boolean isWaiting() {
		return waiting;
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
	public String getExecSeq() {
		return execSeq;
	}
	public void setExecSeq(String execSeq) {
		this.execSeq = execSeq;
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

	public String toString() {
		return "" + id + ". " + name + ": " + priority + ", " + execSeq;
	}
}
