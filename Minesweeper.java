/* Minesweeper.java
 * by Jack Song
 * 
 * Part of Final Project
 * This is the driver class to Game.java
 * 
 */ 

import javax.swing.*;

public class Minesweeper{
	public static void main(String[] args) {

		// Required
		try {
			UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
		} catch (Exception e) {
			e.printStackTrace();
		}

		String hi = "Welcome to Minesweeper" + 
		"\nSelect your difficulty" + 
		"\n 1. Easy    (10x10, 10 mines) " + 
		"\n 2. Medium  (15x15, 35 mines) " + 
		"\n 3. Hard    (20x20, 99 mines) ";

		// Prompt for game difficulty
		String sel = JOptionPane.showInputDialog(null, hi);
		while (!isNumeric(sel) || Integer.parseInt(sel) < 1 || Integer.parseInt(sel) > 3) {
			userExit(sel);
			sel = JOptionPane.showInputDialog(null, hi);
		}

		// The actual game
		JFrame windowsMS = new JFrame("Minesweeper");
		windowsMS.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowsMS.setResizable(false);

		windowsMS.getContentPane().add(new Game(Integer.parseInt(sel)));

		windowsMS.pack();
		windowsMS.setVisible(true);

	}

	// Validating input as an integer
	public static boolean isNumeric(String a) {
		try {
			Integer.parseInt(a);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	// User-controlled exit
	// To be used in combination with "isNumeric"
	public static void userExit(String a) {
		if (a == null) { //When user press anything else other than "Yes"
			int sel = JOptionPane.showConfirmDialog(null, 
				"Do you wish to exit?", 
				"Exit?", 
				JOptionPane.YES_NO_OPTION);
			if (sel == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(null, 
					"Exiting...", 
					"Bye", 
					JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
		} else { //When user entered nothing/incorrectly and pressed "Yes"
			JOptionPane.showMessageDialog(null, 
				"Input invalid!", 
				"Hey", 
				JOptionPane.ERROR_MESSAGE);
		}
	}
}
