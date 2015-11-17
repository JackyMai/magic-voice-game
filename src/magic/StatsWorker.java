package magic;
import java.io.IOException;

import javax.swing.SwingWorker;

public class StatsWorker extends SwingWorker<Void, Void> {
	private String username;
	private String score;
	private int position;
	private int total;
	private Process process;
	
	public StatsWorker(String username, String score, int position, int total) {
		this.username = username;
		this.score = score;
		this.position = position;
		this.total = total;
	}
	
	@Override
	protected Void doInBackground() {
		String cmd = "echo \"" + username + " has a score of " + score + " and is in position " + (position+1) 
				+ " on the leader board with a total of " + total + " players registered for the magic number game\" | festival --tts";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		
		try {
			process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Process getProcess() {
		return process;
	}
}
