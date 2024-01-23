package checkers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Purpose: AP CSA Checkers Game, GamePanel class
 * 
 * @author Nehal
 * @version 1.0a
 */
public class GamePanel extends JPanel implements Runnable, MouseListener {
	private static final long serialVersionUID = 1L; // idk what this is but it errors without it according to eclipse
	// screen settings
	final int ogTileSize = 16; 
	final int scale = 3; 
	
	public final int tileSize = ogTileSize * scale; // 48x48
	
	public final int maxScreenX = 8;
	public final int maxScreenY = 9; 
	public final int screenWidth = tileSize * maxScreenX;  // 384 pixels
	public final int screenHeight = tileSize * maxScreenY; // 432 pixels
	
	// FPS
	int FPS = 60;
	
	Thread gameThread; // keeps program running until you stop it
	public Graphics2D g2;
	public int gameState; // 0 startScreen, 1 board, 2 pause, 3 instructions
	public int boardNum; // 0 red&black, 1 white&black, 2 white&red
	
	BoardManager bm;
	
	// Mouse stuff
	int clickXCood; int clickYCood; int clickX; int clickY;
	
	// screen display stuff
	String msg;
	Font courierF;
	
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // sets screen size
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
//		this.addKeyListener(keys);
		this.setFocusable(true); // focused to receive key input		
		bm = new BoardManager(this);
		
		this.addMouseListener(this);
		
		courierF = new Font("Courier", Font.PLAIN, 40);
	}
	
	/** 
	 * sets the first values for when the game starts running
	 */
	public void setupGame() {
		gameState = 0;
		boardNum = 0; // eventually user can change this
		
//		repaint();
	}
	
	/** 
	 * starts game thread
	 */
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	/** 
	 * overriding the run method, updates the screen repeatedly based on fps
	 */
	@Override
	public void run() {
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;
		int drawCount = 0;
		
		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			if (delta >= 1) {
				repaint();
				delta--;
				drawCount++;
			}
			if (timer >= 1000000000) {
//				System.out.println("FPS:" + drawCount); // to check whether it's working
				drawCount = 0;
				timer = 0;				
			}
		}
		
	}
	
	/**
	 * redraws the screen
	 */
	public void paintComponent(Graphics g) { // called as repaint
		super.paintComponent(g);
		g2 = (Graphics2D)g;
		bm.g2 = g2;
		
		if (gameState == 0) {
			bm.drawBoard(g2);
			
			setMsg(g2, "1v1 or AI?");
		}
		
		if (gameState == 1) {
			// draw the board
			bm.drawBoard(g2);
			String tempTurn = "";
			if (bm.turn == 0) {
				tempTurn = "Black's turn";
			}
			else {
				tempTurn = "Red's turn";
			}
			
			setMsg(g2, tempTurn);
//			drawMoves();
//			System.out.println("Drawn");
//			showMoves(g2, bm.map);
			
			
			
		}
		
		
		// this is like closing the scanner but for Graphics instead
		g2.dispose();
	}
	
	/** 
	 * method to change the text at the bottom of the screen
	 * 
	 * @param gFont this is the graphics handler for it
	 * @param txt this is the text you want displayed
	 */
	public void setMsg(Graphics gFont, String txt) {
		msg = txt;
		
		gFont.setFont(courierF);
		gFont.setColor(Color.white);
		
//		int msgLen = textLength(msg); // if you wanna center
//		int x = screenWidth/2 - msgLen/2;
		int x = 9;
		int y = 48*9-9;
		
		// should position the text to bottom left
		g2.drawString(msg, x, y);
	}
	
	/** 
	 * method to return the length of a string
	 * 
	 * @param text the string that you want the length of
	 * @return returns an int with the length of the text
	 */
	public int textLength(String text) {
		int textLen = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		return textLen;
	}

	/** 
	 * when the mouse is clicked
	 */
	@Override
	public void mouseClicked(MouseEvent e) {		
		clickXCood = e.getX();
		clickYCood = e.getY();
		clickX = clickXCood / tileSize;
		clickY = clickYCood / tileSize;
		
		bm.clickedAt(clickX, clickY);
		
//		for (int i = 0; i < ranges.length; i++) {
//			
//		}
		System.out.println("Clicked at: " + clickXCood + ", " + clickYCood + "\nIn the grid: " + clickX + ", " + clickY + "\nGameState: " + gameState);
		
		
	}

	/** 
	 * when the mouse is pressed
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/** 
	 * when the mouse is released
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/** 
	 * when the mouse enters a thing
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/** 
	 * when the mouse exits a thing
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	

}
