/* Game.java
 * by Jack Song
 * 
 * Part of Final project
 * 
 * Researched:
 * swing timer
 * mouse listener
 * recursion
 * 
 * Enjoy!
 */ 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Game extends JPanel {

	// Panels
	private JPanel status;
	private JPanel grid;
	// Parameters
	private JButton[][] boxes;
	private boolean[][] mines;
	private int[][] nums;
	private int gridSize;
	private int mineNum;
	private Random rng = new Random();
	// Display variables
	final private int boxSize = 25;
	private ImageIcon miniFlag = new ImageIcon(new ImageIcon("flag.png").getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));;
	private ImageIcon flag = new ImageIcon(new ImageIcon("flag.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon clock = new ImageIcon("clock.png");
	private ImageIcon mine = new ImageIcon(new ImageIcon("bomb.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon noMine = new ImageIcon(new ImageIcon("nobomb.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon one = new ImageIcon(new ImageIcon("1.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon two = new ImageIcon(new ImageIcon("2.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon three = new ImageIcon(new ImageIcon("3.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon four = new ImageIcon(new ImageIcon("4.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon five = new ImageIcon(new ImageIcon("5.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon six = new ImageIcon(new ImageIcon("6.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon seven = new ImageIcon(new ImageIcon("7.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private ImageIcon eight = new ImageIcon(new ImageIcon("8.png").getImage().getScaledInstance(boxSize, boxSize, Image.SCALE_DEFAULT));
	private JLabel flagNum;
	private JLabel clockNum;
	private JButton restart;
	private Color hui = new Color(185,185,185);
	// User-controlled variables
	private int labeledMineNum;
	private boolean[][] visited;
	// Timer
	private javax.swing.Timer tim;
	private int sec;

	public Game(int a) {
		// Set the proper size for the board
		if (a == 1) { // Easy
			gridSize = 10;
			mineNum = 10;
		} else if (a == 2) { // Medium
			gridSize = 15;
			mineNum = 35;
		} else if (a == 3) { // Hard
			gridSize = 20;
			mineNum = 99;
		}

		// Initializations
		labeledMineNum = mineNum;
		boxes = new JButton[gridSize][gridSize];
		mines = new boolean[gridSize][gridSize];
		nums = new int[gridSize][gridSize];
		visited = new boolean[gridSize][gridSize];
		flagNum = new JLabel("" + labeledMineNum, miniFlag, SwingConstants.LEFT);
		clockNum = new JLabel("Hi", clock, SwingConstants.RIGHT);
		restart = new JButton("Restart");

		// Status Panel
		status = new JPanel();
		status.setPreferredSize(new Dimension(gridSize*boxSize, boxSize+12));
		status.setBackground(Color.white);
		// Initialize the button
		restart.addActionListener(new Restart());
		// Add stuff
		status.add(flagNum);
		status.add(restart);
		status.add(clockNum);

		// Grid Panel
		grid = new JPanel();
		grid.setLayout(new GridLayout(gridSize,gridSize));
		grid.setPreferredSize(new Dimension(gridSize*boxSize,gridSize*boxSize));
		// Initialize the board
		initializeGame();

		// Adding everything
		add(status);
		add(grid);
		// Properties
		setPreferredSize(new Dimension(gridSize*boxSize,gridSize*boxSize+boxSize+22));
		setBackground(Color.white);
	}

	// Initialize the board
	private void initializeGame(){
		// Initialize the board
		for (int row = 0; row < mines.length; row++) {
			for (int column = 0; column < mines[row].length; column++) {
				boxes[row][column] = new JButton();
				boxes[row][column].addMouseListener(new ClicksOn());
				// Add it
				grid.add(boxes[row][column]);
			}
		}
		// Timer
		tim = new Timer(1000, new TimerListener());
		restartGame();
	}
	// Restart the game
	private void restartGame() {
		// Reset board
		for (int row = 0; row < mines.length; row++) {
			for (int column = 0; column < mines[row].length; column++) {
				boxes[row][column].setIcon(null);
				boxes[row][column].setBackground(Color.white);
				mines[row][column] = false;
				nums[row][column] = 0;
				visited[row][column] = false;
			}
		}
		// Setting the mines
		while (!reachedMineNum(mines)) { // Needs to be specific amount of mines in game
			for (int row = 0; row < gridSize; row++) {
				for (int column = 0; column < boxes[row].length; column++) {
					if (reachedMineNum(mines)) { // Not adding when mineNum reached
						break;
					} else if (rng.nextInt(9) == 0) { // Assign box to be a mine
						mines[row][column] = true;
					}
				}
			}
		}
		// Setting the numbers
		for (int row = 0; row < gridSize; row++) {
			for (int column = 0; column < gridSize; column++) {
				if (!mines[row][column]) {
					nums[row][column] = aroundMine(row, column);
				}
			}
		}
		// Reset display 
		labeledMineNum = mineNum;
		flagNum.setText("" + labeledMineNum);
		// Reset timer
		sec = 0;
		tim.stop();
		clockNum.setText("" + sec);
	}
	// Timer
	private class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			sec++;
			clockNum.setText("" + sec);
		}
	}

	// For generation of mines
	// Number of mines around a specific location
	private int aroundMine(int x, int y) {
		int res = 0;
		for (int row = x - 1; row < x + 2; row++) {
			for (int column = y - 1; column < y + 2; column++) {
				if (row >= 0 && column >= 0 && row < gridSize && column < gridSize) {
					if (mines[row][column]) {
						res++;
					}
				}
			}
		}
		return res;
	}
	// Prevent the program from generate over the desired amount of mines
	private boolean reachedMineNum(boolean[][] a) {
		int res = 0;
		for (int row = 0; row < gridSize; row++) {
			for (int column = 0; column < gridSize; column++) {
				if (a[row][column] == true) {
					res++;
				}
			}
		}
		if (res == mineNum) {
			return true;
		}
		return false;
	}

	// Win or lose
	// Game win condition check
	private boolean gameWon() {
		for (int row = 0; row < gridSize; row++) {
			for (int column = 0; column < gridSize; column++) {
				int boxNum = 0;
				if (boxes[row][column].getBackground() == hui) { // Blank space
					boxNum = 0;
				} else if (boxes[row][column].getIcon() == one) { // 1
					boxNum = 1;
				} else if (boxes[row][column].getIcon() == two) { // 2
					boxNum = 2;
				} else if (boxes[row][column].getIcon() == three) { // 3
					boxNum = 3;
				} else if (boxes[row][column].getIcon() == four) { // 4
					boxNum = 4;
				} else if (boxes[row][column].getIcon() == five) { // 5
					boxNum = 5;
				} else if (boxes[row][column].getIcon() == six) { // 6
					boxNum = 6;
				} else if (boxes[row][column].getIcon() == seven) { // 7
					boxNum = 7;
				} else if (boxes[row][column].getIcon() == eight) { // 8
					boxNum = 8;
				}
				if (boxNum != nums[row][column]) {
					return false;
				}
			}
		}
		return true;
	}
	// Game lose display
	private void gameLost() {
		for (int row = 0; row < gridSize; row++) {
			for (int column = 0; column < gridSize; column++) {
				if (!mines[row][column] && boxes[row][column].getIcon() == flag) { // Not mine labeled
					// label the one with an X
					boxes[row][column].setIcon(noMine);
				} else if (mines[row][column] && boxes[row][column].getIcon() != flag){ // Mine not labeled
					// reveal the mine
					boxes[row][column].setIcon(mine);
				}
			}
		}
		tim.stop();
		JOptionPane.showMessageDialog(null, "You lose", "GG", JOptionPane.PLAIN_MESSAGE);    
		restartGame();
	}

	// Adding stuff to the game
	// For when clicked on blank
	// Recursion
	private void revealSurrounding(int x, int y) {
		if (visited[x][y]) { // not checking the same over and over again
			return;
		}
		if (!mines[x][y] && nums[x][y] == 0) { // number or blank
			boxes[x][y].setBackground(hui);
			boxes[x][y].setIcon(null);
			visited[x][y] = true;

			// call the method to go to the else part
			for (int i = x - 1; i < x + 2; i++) {
				for (int j = y - 1; j < y + 2; j++) { 
					if (i >= 0 && i < nums.length && j >=0 && j < nums[0].length) {
						revealSurrounding(i, j);
					}   
				}
			}
		} else {
			if (mines[x][y]) { // stop upon reaching a mine
				return;
			}
			addIconsToGame(x, y); // add the number
			visited[x][y] = true; // checked
		}
	}
	// Add icon based on where user clicked
	private void addIconsToGame(int x, int y) {
		if (!mines[x][y]) { // The box is not a mine
			if (nums[x][y] == 0) {
				boxes[x][y].setBackground(hui);
			} else {
				boxes[x][y].setBackground(null);
				if (nums[x][y] == 1) {
					boxes[x][y].setIcon(one);
				} else if (nums[x][y] == 2) {
					boxes[x][y].setIcon(two);
				} else if (nums[x][y] == 3) {
					boxes[x][y].setIcon(three);
				} else if (nums[x][y] == 4) {
					boxes[x][y].setIcon(four);
				} else if (nums[x][y] == 5) {
					boxes[x][y].setIcon(five);
				} else if (nums[x][y] == 6) {
					boxes[x][y].setIcon(six);
				} else if (nums[x][y] == 7) {
					boxes[x][y].setIcon(seven);
				} else if (nums[x][y] == 8) {
					boxes[x][y].setIcon(eight);
				}
			}
		}
	}
	// For middle click
	private void revealMiddleClick(int x, int y) {
		if (nums[x][y] != aroundFlag(x, y)) { // surrounding flags different from num
			// do nothing
		} else { // surrounding flags same as num
			for (int row = x - 1; row < x + 2; row++) {
				for (int column = y - 1; column < y + 2; column++) {
					if (row >= 0 && column >= 0 && row < gridSize && column < gridSize) {
						if (mines[row][column] && boxes[row][column].getIcon() != flag) { // mine not labeled
							// lose
							gameLost();
						} else if (!mines[row][column] && nums[row][column] == 0) { // it is a blank
							revealSurrounding(row, column);
						} else { // reveal it
							addIconsToGame(row, column);
						}
					}
				}
			}
		}
	}
	// Number of flag around a specific location
	private int aroundFlag(int x, int y) {
		int res = 0;
		for (int row = x - 1; row < x + 2; row++) {
			for (int column = y - 1; column < y + 2; column++) {
				if (row >= 0 && column >= 0 && row < gridSize && column < gridSize) {
					if (boxes[row][column].getIcon() == flag) {
						res++;
					}
				}
			}
		}
		return res;
	}
	// Restart the game so that user "clicks" on blank
	private void firstClick(int x, int y) {
		restartGame();
		if (mines[x][y] || nums[x][y] != 0) { // mine or num
			firstClick(x, y);
		} else { // blank
			revealSurrounding(x, y);
		}
		tim.start();
	}
	// Check if no box is revealed
	private boolean firstClickCheck() {
		for (int row = 0; row < gridSize; row++) {
			for (int column = 0; column < gridSize; column++) {
				if (boxes[row][column].getBackground() != Color.white) {
					return false;
				}
			}
		}
		return true;
	}

	// MouseListener for the boxes
	private class ClicksOn implements MouseListener {
		public void mouseClicked(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			JButton input = (JButton) e.getSource(); // Get the box
			for (int row = 0; row < gridSize; row++) {
				for (int column = 0; column < gridSize; column++) {

					if (input == boxes[row][column]) { // Making sure we are on the box we want to do stuff on
						if (e.getButton() == MouseEvent.BUTTON1) { // User left clicks
							if (mines[row][column] && boxes[row][column].getIcon() != flag) { // on mine and not flagged
								if (firstClickCheck()) { // first click should be blank
									firstClick(row, column);
								} else { // Game over
									gameLost();
								}
							} else if (boxes[row][column].getIcon() == flag) { // on flag
								// do nothing
							} else if (!mines[row][column] && nums[row][column] != 0) { // on number
								if (firstClickCheck()) { // first click should be blank
									firstClick(row, column);
								} else { // add it to the game
									addIconsToGame(row, column);
								}
							} else if (nums[row][column] == 0) { // on a blank space
								// reveal the surrounding
								revealSurrounding(row, column);
							}
						} else if (e.getButton() == MouseEvent.BUTTON3) { // User right clicks
							if (boxes[row][column].getIcon() == flag) { // on flagged button
								// deflag it 
								boxes[row][column].setIcon(null);
								boxes[row][column].setBackground(Color.white);
								labeledMineNum+=1;
								flagNum.setText("" + labeledMineNum);
							} else if (boxes[row][column].getIcon() != flag && boxes[row][column].getIcon() != null) { // on number or balnk
								// do nothing
							} else if (boxes[row][column].getBackground() == Color.white) { // flag it
								boxes[row][column].setIcon(flag);
								boxes[row][column].setBackground(null);
								labeledMineNum-=1;
								flagNum.setText("" + labeledMineNum);
							}
						} else if (e.getButton() == MouseEvent.BUTTON2) { // User middle clicks
							if (boxes[row][column].getBackground() == Color.white) { // on unrevealed
								// do nothing
							} else if (!mines[row][column] && nums[row][column] != 0) { // on revealed number
								revealMiddleClick(row, column);
							}
						}
					}

				}
			}

			// End game
			if (gameWon()) {
				tim.stop();
				JOptionPane.showMessageDialog(null, "You won!", "Yay", JOptionPane.PLAIN_MESSAGE);
				restartGame();
			}
		}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}

	// MouseListener for the restart button
	private class Restart implements ActionListener {
		public void actionPerformed (ActionEvent p) {
			restartGame();
		}
	}
}
