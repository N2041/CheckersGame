package checkers;

import java.awt.Component;

import javax.swing.JFrame;
//import javax.swing.*;

/**
 * Purpose: AP CSA Checkers Game, Runner class
 * 
 * @author Nehal
 * @version 1.0a
 */
public class Runner {

	/** 
	 * Main method, makes it run
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Make a screen
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false); // might look into changing this later after I get it working
		window.setTitle("Checkers");
		
		GamePanel gp = new GamePanel();
		window.add(gp);
		
		window.pack(); // follows stuff in GamePanel
		
		window.setLocationRelativeTo(null); // should center it
		window.setVisible(true);
		
		gp.setupGame();
		gp.startGameThread();
		
		Component glassPane = window.getGlassPane();
		glassPane.addMouseListener(gp);
		

	}

}
