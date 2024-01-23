package checkers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Purpose: AP CSA Checkers Game, Piece class
 * 
 * @author Nehal
 * @version 1.0a
 */
public class Piece {
	BoardManager bm;
	int[][] map;
	boolean isRed; 
	public boolean isKing;
	boolean alive;
	public BufferedImage pic;
	public BufferedImage redKingPic;
	public BufferedImage blackKingPic;
	public int x, y, xCood, yCood; 
	boolean clickOnPlayer;
	BufferedImage movePic;
	int d1x, d1y, d2x, d2y, d3x, d3y, d4x, d4y; // location of a possible diagonal move (by 48s)
	boolean d1j, d2j, d3j, d4j; // the jumps
	int jumpAt; // if only one jump, where can you jump
	boolean moreThanOneJump;
	int k1cx, k1cy, k2cx, k2cy, k3cx, k3cy, k4cx, k4cy; // kill choices x & y (not by 48s)
	
	public Piece(BoardManager bm, BufferedImage piecePic, boolean red, boolean king, boolean inPlay, int x, int y) {
		this.bm = bm;
		map = bm.map;
		pic = piecePic;
		isRed = red;
		isKing = king;
		alive = inPlay;
		this.x = x;
		this.y = y;
		xCood = x*48;
		yCood = y*48;
		clickOnPlayer = false;
		d1x = -144; d1y = -144; d2x = -144; d2y = -144; d3x = -144; d3y = -144; d4x = -144; d4y = -144;
		jumpAt = 0;
		moreThanOneJump = false;
		k1cx = -2; k1cy = -2; k2cx = -2; k2cy = -2; k3cx = -2; k3cy = -2; k4cx = -2; k4cy = -2;
		
		
		try {
			movePic = ImageIO.read(getClass().getResourceAsStream("/dot.png"));
			redKingPic = ImageIO.read(getClass().getResourceAsStream("/redPieceKing.png"));
			blackKingPic = ImageIO.read(getClass().getResourceAsStream("/blackPieceKing.png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!alive) {
			death();
		}
	}
	
	/** 
	 * changes the (x,y) position of the piece and changes the old position to 5
	 * 
	 * @param x int that has the new x value
	 * @param y int that has teh new y value
	 */
	public void setXY(int x, int y) {
		if (alive) {
			bm.printMap();
			bm.map[this.x][this.y] = 5;
			bm.printMap();
			
			this.x = x;
			this.y = y;
			
			int tempInt = 0;
			if (isRed && !isKing) {
				tempInt = 1;
			} else if(isRed && isKing) {
				tempInt = 2;
			} else if(!isRed && !isKing) {
				tempInt = 3;
			} else if(!isRed && isKing) {
				tempInt = 4;
			}
			
			bm.setMap(x, y, tempInt);
			bm.printMap();
			
			xCood = this.x*48;
			yCood = this.y*48;
			
			clickOnPlayer = false;
		}
	}
	
	/** 
	 * called when a piece has been killed
	 */
	public void death() {
		alive = false;
		bm.map[x][y] = 5;
		x=-3;
		y=-3;
		xCood = -144; 
		yCood = -144;
		resetMoveChoices();
		jumpAt = 0;
		bm.printMap();
	}
	
	/** 
	 * sets the positions that are represent possible moves that the piece has
	 */
	public void showMoves() {  
		if (!clickOnPlayer) {
			// specific x and y valuesa are flipped js bc of how the logic is
			
			removeMoves();
			
	//		g2.drawImage(movePic, 48, 48, null);
			
			int enemy = 3, ally = 1, enemyK = 4, allyK = 2;
			if (isRed) {
				System.out.println("Red piece clicked");
				
				// left side 
				if (y != 0 && x != 0) {
					if (bm.map[x-1][y-1] == 5) {
						d1x = (y-1)*48;
						d1y = (x-1)*48;								
	//					bm.map[y-1][x-1] = 6;
						bm.setMap(x-1, y-1, 6);
						
					} 
					else if ((bm.map[x-1][y-1] == enemy || bm.map[x-1][y-1] == enemyK) && y!=1 && x!=1 && (bm.map[x-2][y-2] == 5)) {
						d1x = (y-2)*48;
						d1y = (x-2)*48;
						d1j = true;
						k1cx = y-1;
						k1cy = x-1;
						bm.setMap(x-2, y-2, 6);
					}
				}
				
				// right side
				if (y != 7 && x != 0) {
					if (bm.map[x-1][y+1] == 5) {
						d2x = (y+1)*48;
						d2y = (x-1)*48;
						bm.setMap(x-1, y+1, 6);
						
					} 
					else if ((bm.map[x-1][y+1] == enemy || bm.map[x-1][y+1] == enemyK) && y!=6 && x!=1 && (bm.map[x-2][y+2] == 5)) {
						d2x = (y+2)*48;
						d2y = (x-2)*48;
						d2j = true;
						k2cx = y+1;
						k2cy = x-1;
						bm.setMap(x-2, y+2, 6);
					}
				}
				
				if (isKing) { 
					// assign d3x d3y & d4x d4y using black's logic
					// left side
					if (y != 0 && x != 7) {
						if (bm.map[x+1][y-1] == 5) {
							d3x = (y-1)*48;
							d3y = (x+1)*48;
							bm.setMap(x+1, y-1, 6);
						} 
						else if ((bm.map[x+1][y-1] == enemy || bm.map[x+1][y-1] == enemyK) && y!=1 && x!=1 && (bm.map[x+2][y-2] == 5)) {
							d3x = (y-2)*48;
							d3y = (x+2)*48;
							d3j = true;
							k3cx = y-1;
							k3cy = x+1;
							bm.setMap(x+2, y-2, 6);
						}
					}
					
					// right side
					if (y != 7 && x != 7) {
						if (bm.map[x+1][y+1] == 5) {
							d4x = (y+1)*48;
							d4y = (x+1)*48;
							bm.setMap(x+1, y+1, 6);
						} 
						else if ((bm.map[x+1][y+1] == enemy || bm.map[x+1][y+1] == enemyK) && y!=6 && x!=1 && (bm.map[x+2][y+2] == 5)) {
							d4x = (y+2)*48;
							d4y = (x+2)*48;
							d4j = true;
							k4cx = y+1;
							k4cy = x+1;
							bm.setMap(x+2, y+2, 6);
						}
					}
				}
				
			} else { // if it's a black piece
				System.out.println("Black piece clicked");
				
				enemy = 1;
				enemyK = 2;
				ally = 3; 
				allyK = 4;
				
				// left side
				if (y != 0 && x != 7) {
					if (bm.map[x+1][y-1] == 5) {
						d1x = (y-1)*48;
						d1y = (x+1)*48;
	//					bm.map[y-1][x+1] = 6;
						bm.setMap(x+1, y-1, 6);
					} 
					else if ((bm.map[x+1][y-1] == enemy || bm.map[x+1][y-1] == enemyK) && y!=1 && x!=1 && (bm.map[x+2][y-2] == 5)) {
						d1x = (y-2)*48;
						d1y = (x+2)*48;
						d1j = true;
						k1cx = y-1;
						k1cy = x+1;
						bm.setMap(x+2, y-2, 6);
					}
				}
				
				// right side
				if (y != 7 && x != 7) {
					if (bm.map[x+1][y+1] == 5) {
						d2x = (y+1)*48;
						d2y = (x+1)*48;
	//					bm.map[y+1][x+1] = 6;
						bm.setMap(x+1, y+1, 6);
					} 
					else if ((bm.map[x+1][y+1] == enemy || bm.map[x+1][y+1] == enemyK) && y!=6 && x!=1 && (bm.map[x+2][y+2] == 5)) {
						d2x = (y+2)*48;
						d2y = (x+2)*48;
						d2j = true;
						k2cx = y+1;
						k2cy = x+1;
						bm.setMap(x+2, y+2, 6);
					}
				}
				
				if (isKing) { 
					// assign d3x d3y & d4x d4y using red logic
					// left side 
					if (y != 0 && x != 0) {
						if (bm.map[x-1][y-1] == 5) {
							d3x = (y-1)*48;
							d3y = (x-1)*48;	
	//						bm.map[y-1][x-1] = 6;
							bm.setMap(x-1, y-1, 6);
							
						} 
						else if ((bm.map[x-1][y-1] == enemy || bm.map[x-1][y-1] == enemyK) && y!=1 && x!=1 && (bm.map[x-2][y-2] == 5)) {
							d3x = (y-2)*48;
							d3y = (x-2)*48;
							d3j = true;
							k3cx = x-1;
							k3cy = y-1;
							bm.setMap(x-2, y-2, 6);
						}
					}
					
					// right side
					if (y != 7 && x != 0) {
						if (bm.map[x-1][y+1] == 5) {
							d4x = (y+1)*48;
							d4y = (x-1)*48;
	//						bm.map[y+1][x-1] = 6;
							bm.setMap(x-1, y+1, 6);
							
						} 
						else if ((bm.map[x-1][y+1] == enemy || bm.map[x-1][y+1] == enemyK) && y!=6 && x!=1 && (bm.map[x-2][y+2] == 5)) {
							d4x = (y+2)*48;
							d4y = (x-2)*48;
							d4j = true;
							k4cx = y+1;
							k4cy = x-1;
							bm.setMap(x-2, y+2, 6);
						}
					}
				} 		
				
			}	
			System.out.println("Drew moves at: " + d1x/48 + ", " + d1y/48 + " || " + d2x/48 + ", " + d2y/48 + " || " + d3x/48 + ", " + d3y/48 + " || " + d4x/48 + ", " + d4y/48);
			clickOnPlayer = true;
		}
		
		else { // if clickOnPlayer
			removeMoves();
			clickOnPlayer = false;
			
		}
		
	}
	
	/** 
	 * resets all the move positions
	 */
	public void removeMoves() {
		for (int i = 0; i < bm.map.length; i++) {
			for (int j = 0; j < bm.map[0].length; j++) {
				if (bm.map[i][j] == 6) {
					bm.setMap(i, j, 5);
				}
			}
		}
	}
	
	/** 
	 * checks for jumps 
	 */
	public void handleJumps() {
		if (d1j || d2j || d3j || d4j) {
			// if only 1 of the jumps are true
			if (d1j && !(d2j || d3j || d4j)) {
				jumpAt = 1;
			}
			else if (d2j && !(d1j || d3j || d4j)) {
				jumpAt = 2;
			}
			else if (d3j && !(d2j || d1j || d4j)) {
				jumpAt = 3;
			} 
			else if (d4j && !(d2j || d3j || d1j)) {
				jumpAt = 4;
			}
			
			// if more than 1 of the jumps are true
			else {
				moreThanOneJump = true;
			}
		}
	}
	
	/**
	 * actually jumps and 
	 * 
	 * @param where int that corresponds with the position the piece can jump
	 * @return an array with the 2 coordinates that represent the piece that was jumped
	 */
	public int[] jump(int where) { // returns x and y as one number, like 0,1 is returned as 01
		int[] jumpedCoods = new int[2]; 
		if (where == 1) {
			setXY(d1x/48, d1y/48);
			jumpedCoods[0] = k1cx;
			jumpedCoods[1] = k1cy;
//			bm.map[k1cx][k1cy] = 5;
		}
		else if (where == 2) {
			setXY(d2x/48, d2y/48);
			jumpedCoods[0] = k2cx;
			jumpedCoods[1] = k2cy;
//			bm.map[k2cx][k2cy] = 5;
		}
		else if (where == 3) {
			setXY(d3x/48, d3y/48);
			jumpedCoods[0] = k3cx;
			jumpedCoods[1] = k3cy;
//			bm.map[k3cx][k3cy] = 5;
		}
		else {
			setXY(d4x/48, d4y/48);
			jumpedCoods[0] = k4cx;
			jumpedCoods[1] = k4cy;
//			bm.map[k4cx][k4cy] = 5;
			
		}
		return jumpedCoods; // in bm find the piece that was on these coods and use death()
	}
	
	/** 
	 * resets all the calculations done from finding the moves
	 */
	public void resetMoveChoices() {
		d1x = -3*48; d1y = -3*48; 
		d2x = -3*48; d2y = -3*48; 
		d3x = -3*48; d3y = -3*48; 
		d4x = -3*48; d4y = -3*48; 
		k1cx = -2; k1cy = -2; k2cx = -2; k2cy = -2; k3cx = -2; k3cy = -2; k4cx = -2; k4cy = -2;
//		jumpAt = 0;
		moreThanOneJump = false;
		
		for (int i = 0; i < bm.map.length; i++) {
			for (int j = 0; j < bm.map[0].length; j++) {
				if (bm.map[i][j] == 6) {
					bm.setMap(i, j, 5);
				}
			}
		}
	}
	
	/** 
	 * updates piece to king if applicable (called constantly when drawing board)
	 */
	public void checkForKing() {
		if (isRed) {
			if (x==0) {
				bm.map[x][y] = 2; 
				isKing = true;
				pic = redKingPic;
			}
		}
		else { // is black
			if (x == 7) {
				bm.map[x][y] = 4;
				isKing = true;
				pic = blackKingPic;
			}
		}
		
	}
	
	/** 
	 * prints out all the info on a piece
	 */
	public String toString() {
		return ("Red?: " + isRed + "\nKing?: " + isKing + "\nAlive?: " + alive + "\nPos: (" + x + ", " + y + ")\nCood: (" + xCood + ", " + yCood + ")\nMoves at: " + d1x/48 + ", " + d1y/48 + " || " + d2x/48 + ", " + d2y/48 + " || " + d3x/48 + ", " + d3y/48 + " || " + d4x/48 + ", " + d4y/48+"\nd1j: " + d1j + " | d2j: " + d2j  + " | d3j: " + d3j + " | d4j: " + d4j + "\njumpAt: " + jumpAt + "\nmoreThanOneJump: " + moreThanOneJump + "\n");
				
	}
	
	
	
	
	
}

