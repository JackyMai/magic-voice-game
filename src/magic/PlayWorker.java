package magic;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingWorker;

public class PlayWorker extends SwingWorker<Void, Void>{
	private String currentUser;
	private int guess;
	private int diceRoll;
	
	public PlayWorker(String currentUser, int guess, int diceRoll) {
		this.currentUser = currentUser;
		this.guess = guess;
		this.diceRoll = diceRoll;
	}

	@Override
	protected Void doInBackground() throws Exception {
		String cmd;
		
		// If the user guessed correctly, read from the stats file of that user and increase the win count
		// by 1, then overwrite the existing score with the new score
		if(guess == diceRoll) {
			cmd = "echo \"Well done "+ currentUser +", you guessed correctly\" | festival --tts";
			File userStats = new File(".magicgame"+File.separator+currentUser+".txt");
			
			BufferedReader br = new BufferedReader(new FileReader(userStats));
			String score = br.readLine();
			int newScore = Integer.parseInt(score) + 1;
			br.close();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(".magicgame"+File.separator+currentUser+".txt", false));
			bw.write(String.valueOf(newScore));
			bw.close();
		} else {
			cmd = "echo \"Sorry "+ currentUser +". You guessed "+ guess +", but it was "+ diceRoll +"\" | festival --tts";
		}

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		
		try {
			builder.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}