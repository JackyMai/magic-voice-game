package magic;
import java.io.IOException;

import javax.swing.SwingWorker;

public class GreetingsWorker extends SwingWorker<Void, Void> {
	private String mode;
	private String currentUser;
	
	public GreetingsWorker(String mode, String currentUser) {
		this.mode = mode;
		this.currentUser = currentUser;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		String cmd;
		
		if(mode.equals("m")) {
			cmd = "echo \"Welcome to the game, it is now $(date +\"%l:%M %p %A\"). Please select from one of the following options\" | festival --tts &";
		} else {
			cmd = "echo \"Welcome "+ currentUser +" , let's play.\" | festival --tts";
		}
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
