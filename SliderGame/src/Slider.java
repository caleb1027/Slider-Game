import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * Slider: generates a gameboard which the slider game is played on
 *
 * @author Caleb Li
 * @date 25-January-2021
 */
public class Slider extends JFrame implements ActionListener{
	//eX and eY are coordinates of the empty button
	private int eX=2;
	private int eY=2;
	//sLevel: dimensions of board, difficulty
	private int sLevel=4;
	//menuEnabled: stores if menu has been initialized yet
	private boolean menuEnabled=false;
	//moves: stores # of moves performed
	private int moves=0;
	//difficulty: stores difficulty in string
	private String difficulty;
	//c1: stores user's chosen color
	private Color c1=Color.pink;
	JPanel gameboard =null;
	JMenuBar menubar = new JMenuBar();
	JButton [][] button =null;
	JButton startButton=new JButton("Start!"); 
	JButton instructions=new JButton("Instructions"); 
	Font f = new Font("SansSerif", Font.BOLD, 36);
	JMenu topMenu = null;
	JMenuItem gameLevelMenuItem = null;
	JMenuItem unDoMenuItem = null;
	JMenuItem restartMenuItem = null;
	JMenuItem showScoreMenuItem = null;
	JMenuItem changeColorMenuItem = null;
	ArrayList<Integer> xMoves =new ArrayList<Integer>();
	ArrayList<Integer> yMoves =new ArrayList<Integer>();

	 /**
     *  Default constructor which creates the gameboard and start screen
     */

	public Slider () {
		setSize(600, 600);
		//create default panel
		CreateGrid(0);
		//add the start button
		gameboard.add(startButton);
		gameboard.add(instructions);
		instructions.addActionListener(this);
		startButton.addActionListener(this);
		this.add(gameboard);
		showFrame();
	}
	
	 /**
     *  Method that creates the menu bar
     */

	public void CreateMenu() {
		if(menuEnabled==false) {
		this.setJMenuBar(menubar);
		topMenu = new JMenu("Menu");
		topMenu.setMnemonic(KeyEvent.VK_M);
		menubar.add(topMenu);
		//restart button
		restartMenuItem = new JMenuItem("Restart");
		restartMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_R, ActionEvent.ALT_MASK));
		restartMenuItem.getAccessibleContext().setAccessibleDescription(
		"Restarts the game");
		topMenu.add(restartMenuItem);
		restartMenuItem.addActionListener(this);
		//game level button
		 gameLevelMenuItem = new JMenuItem("Game Level");
		 gameLevelMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_G, ActionEvent.ALT_MASK));
		 gameLevelMenuItem.getAccessibleContext().setAccessibleDescription(
		"Set the game level");
		topMenu.add(gameLevelMenuItem);
		gameLevelMenuItem.addActionListener(this);
		//undo button
		unDoMenuItem= new JMenuItem("Undo");
		unDoMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_U, ActionEvent.ALT_MASK));
		unDoMenuItem.getAccessibleContext().setAccessibleDescription(
		"Undo event");
		topMenu.add(unDoMenuItem);
		unDoMenuItem.addActionListener(this);
		//show scores button
		showScoreMenuItem= new JMenuItem("Show Scores");
		showScoreMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_S, ActionEvent.ALT_MASK));
		showScoreMenuItem.getAccessibleContext().setAccessibleDescription(
		"Shows previous scores");
		topMenu.add(showScoreMenuItem);
		showScoreMenuItem.addActionListener(this);
		//change colors button
		changeColorMenuItem= new JMenuItem("Change Colors");
		changeColorMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		KeyEvent.VK_C, ActionEvent.ALT_MASK));
		changeColorMenuItem.getAccessibleContext().setAccessibleDescription(
		"Changes colors");
		topMenu.add(changeColorMenuItem);
		changeColorMenuItem.addActionListener(this);
		menuEnabled=true;
		}
	}
	
	 /**
     *  Sets the level/dimensions of the game
     *  @param level (dimensions of game)
     */

	public void setLevel(int level) {
		sLevel=level;
		//create new level
		eX = sLevel-1;
		eY = sLevel-1;
		Cleanup();
		CreateGrid(sLevel);
	}
	
	 /**
     *  Method that constructs grid and buttons based on dimensions given
     * @param level  Dimension of grid
     */

	public void CreateGrid(int level) {
		//remove old panel
		if(gameboard!=null) {
		   this.remove(gameboard);
		}
		if(level ==0) {
			//start/restart , create new empty panel.
			gameboard =new JPanel();
			return;
		}
		//create the grid panel for game.
		gameboard = new JPanel(new GridLayout(level,level));
		this.add(gameboard);
		CreateMenu();
		button = new JButton [level][level];
		for (int i =0; i<level; i++) {
			for (int j =0; j<level; j++) {
				int x=i*level+j+1;
				button[i][j]=new JButton(Integer.toString(x));
				button[i][j].setFont(f);
				button[i][j].addActionListener(this);
				button[i][j].setBackground(c1);
				gameboard.add(button[i][j]);
			}
		}
		//scrambles the board if board dimensions are greater than 1
		scramble((level>1));
		moves=0;
	}
	 /**
     *  Method that makes frame visible
     */

	public void showFrame() {
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	 /**
     *  Method that pops up difficulty selector and stores user's choice
     */
	public void setDifficulty()
	{
		Object[] options = {"Easy", "Medium","Hard"};
		String s = (String)JOptionPane.showInputDialog(
		                    this,
		                    "Choose your difficulty",
		                    "",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null, options,
		                    "");
		difficulty =s;
		if(s!=null) {
		if(s.equals("Easy")) {
			setLevel(3);
		}
		else if(s.equals("Medium")) {
			setLevel(4);
		}
		else if(s.equals("Hard")) {
			setLevel(5);
		}
		}

	}
	 /**
     * Method that swaps button with empty space and stores the move, will not store
     * if the swap is an undo
     * @param x       x position coordinate
     * @param y       y position coordinate
     * @param isUndo  flag to determine whether the move is undo or not
     */
	public void swap(int x, int y,boolean isUndo) {
		//Stores move if it is not an undo move
		 if(!isUndo) {
			 KeepPosition(eX,eY);
		 }
		 if (x == eX + 1 && y == eY) {
			 button[eX][eY].setText(button[x][y].getText());
			 button[eX][eY].setBackground(c1);
             eX = x;
             eY = y;
             button[x][y].setBackground(Color.BLACK);
             button[x][y].setText("");
             moves++;
	        } else if (x == eX - 1 && y == eY) {
	        	 button[eX][eY].setText(button[x][y].getText());
				 button[eX][eY].setBackground(c1);
	             eX = x;
	             eY = y;
	             button[x][y].setBackground(Color.BLACK);
	             button[x][y].setText("");
	             moves++;
	        } else if (y == eY+1&& x == eX){
	        	 button[eX][eY].setText(button[x][y].getText());
				 button[eX][eY].setBackground(c1);
	             eX = x;
	             eY = y;
	             button[x][y].setBackground(Color.BLACK);
	             button[x][y].setText("");
	             moves++;
	        } else if (y==eY-1&&x==eX) {
	        	 button[eX][eY].setText(button[x][y].getText());
				 button[eX][eY].setBackground(c1);
	             eX = x;
	             eY = y;
	             button[x][y].setBackground(Color.BLACK);
	             button[x][y].setText("");
	             moves++;
	        }
	}
	 /**
     *  Method that scrambles the board if enable is true
     * @param enable  
     */
	public void scramble(boolean enable) {
		if(enable) {
		button[eX][eY].setBackground(Color.BLACK);
		button[eX][eY].setText("");
		for(int i =0; i<100*sLevel; i++) {
			int x = (int) (Math.random()*sLevel);
			int y = (int) (Math.random()*sLevel);
			swap(x,y,false);
			}
		}
	}
	 /**
     *  Method that checks if the user has won
     * @return true/false, depending on if the user has won or not
     */
	public boolean victory() {
		//checks if buttons are in order using their text (integer)
		for (int i =0; i<sLevel; i++) {
			for (int j =0; j<sLevel; j++) {
				if(!(i==(sLevel-1) && j==(sLevel-1))) {
					if(!(button[i][j].getText().equals(""+(i*sLevel+j+1)))){
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	 /**
     *  Method that checks user input
     * @param e, object that user has interacted with
     */
	public void actionPerformed(ActionEvent e) {
		//if restart item is clicked, board will be reset
		if(e.getSource()==restartMenuItem ) {
			setLevel(sLevel);
		}
		//if instructions are clicked, dialog with instructions will pop up.
		else if(e.getSource()==instructions) {
			JOptionPane.showMessageDialog(this, "Slide the pieces into order to win by pressing on them. \n"
					+ "Menu be used to reset game, change difficulty and color, and undo moves.");
		}
		//if show score is pressed, previous scores are shown in pop-up
		else if(e.getSource()==showScoreMenuItem) {
			fromText();
		}
		//if change colors is pressed, menu to switch colors will open
		else if(e.getSource()==changeColorMenuItem) {
			color();
		}
		//if game level is pressed, game level change dialog opens
		else if(e.getSource()==gameLevelMenuItem||e.getSource()==startButton)
		{
			setDifficulty();
		}
		//if undo is pressed, will undo the move
		else if (e.getSource()==unDoMenuItem) {
			UnDo();
			moves--;
		}
		//check to see which button was pressed, and if it can be swapped
		//with the empty one
		else {
			for (int i =0; i<sLevel; i++) {
				for (int j =0; j<sLevel; j++) {
					if(e.getSource()==button[i][j]) {
						swap(i, j,false);
						//if user has won, there will be a pop-up,
						//and score is added to Scores.txt
						if(victory()==true) {
							JOptionPane.showMessageDialog(this, "You win");
							toText(difficulty, moves);
						}
					}
				}
			}
		}
	}
	 /**
     *  Opens the color change pop-up
     */

	private void color() {
		//array of possible color changes
		Object[] options = {"Red", "Green","Blue","Pink"};
		String s = (String)JOptionPane.showInputDialog(
		                    this,
		                    "Choose a color",
		                    "",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null, options,
		                    "");
		//changes c1 to color chosen
		if(s!=null) {
			if(s.equals("Red")) {
				c1=Color.red;
				changeColor();
			}
			else if(s.equals("Green")) {
				c1=Color.green;
				changeColor();
			}
			else if(s.equals("Pink")) {
				c1=Color.pink;
				changeColor();
			}
			else if(s.equals("Blue")) {
				c1=Color.blue;
				changeColor();
			}
		}
	}
	 /**
     *  Changes the color of the buttons on board to c1
     */

	private void changeColor() {
		for (int i =0; i<sLevel; i++) {
			for (int j =0; j<sLevel; j++) {
				button[i][j].setBackground(c1);
			}
		}
		button[eX][eY].setBackground(Color.BLACK);
	}
	 /**
     *  Method to add score to text file
     *  @param difficulty  stores difficulty of solve
     *  @param numOfMoves  stores number of moves for solve
     */

	private void toText(String difficulty, int numOfMoves) {
		try {
			FileWriter fw = new FileWriter("Scores.txt", true);
			PrintWriter pw = new PrintWriter (fw);
			pw.println(difficulty+", Moves: " +numOfMoves+" ");
			pw.close();
		}
		catch(IOException e) {
			System.out.println("Error in File writing");
		}
	}
	 /**
     *  Method to read text file, and print the scores out in a pop-up
     */

	private void fromText() {
		try {
		String scoreLine = "";
		FileReader fr = new FileReader("Scores.txt");
		BufferedReader br = new BufferedReader(fr);
		scoreLine = br.readLine();
		String message =scoreLine;
		while(scoreLine != null) {
			if((scoreLine = br.readLine())!=null) {
				 message +="\n"+scoreLine;
			}
		}
		JOptionPane.showMessageDialog(this, message);
        br.close();
		}
		catch (IOException e) {
			System.out.println("Error in file reading");
		}
	}
	 /**
     *  Clears previous moves, and resets the # of moves
     */
	private void Cleanup() {
		moves=0;
		xMoves.clear();
		yMoves.clear();
	}
	 /**
     * Adds previous move to an array
     * @param x   x position of button which was moved
     * @param y   y position of button which was moved
     */
	private void KeepPosition(int x, int y) {
		xMoves.add(x);
		yMoves.add(y);
	}
	 /**
     *  Method to undo the previous move
     */
	private void UnDo() {
		if(xMoves.size()>0 ) {
			int index = xMoves.size() - 1; 
			//get the previous position 
			int pre_x = xMoves.get(index);
			int pre_y = yMoves.get(index);
			//remove  previous position from array
			xMoves.remove(index);
			yMoves.remove(index);
			//move to previous position;
			swap(pre_x,  pre_y,true);
		}
	}
}
