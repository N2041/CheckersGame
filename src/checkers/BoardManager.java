package checkers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.plugins.bmp.BMPImageWriteParam;

/**
 * Purpose: AP CSA Checkers Game, BoardManager class
 * 
 * @author Nehal
 * @version 1.0a
 */
public class BoardManager {
	GamePanel gp;
	Graphics2D g2;
	
	// image files for the board square
	public BufferedImage lightSq; // light square
	public BufferedImage darkSq;
	public BufferedImage movePic;
	public boolean ai;
	
	// for location of pieces
	// 0 emptyCantMove, 1 redReg piece, 2 redking, 3 blackReg, 4 blackKing, 5 emptyCanMove, 6 possible move
	public int[][] map = {{3, 0, 3, 0, 3, 0, 3, 0}, 
						  {0, 3, 0, 3, 0, 3, 0, 3},
						  {3, 0, 3, 0, 3, 0, 3, 0}, 
						  {0, 5, 0, 5, 0, 5, 0, 5},
						  {5, 0, 5, 0, 5, 0, 5, 0},
						  {0, 1, 0, 1, 0, 1, 0, 1},
						  {1, 0, 1, 0, 1, 0, 1, 0},
						  {0, 1, 0, 1, 0, 1, 0, 1}}; 
	// arrays to hold the pieces
	public Piece[] redPieces = new Piece[12];
	public Piece[] blackPieces = new Piece[12];
	Piece clickedPiece;
	
	boolean showMoves;
	
	int turn; // 0 = red, 1 = black
	
	public BoardManager(GamePanel gp) {
		this.gp = gp;
		pieceSetup();
		turn = 0;
		
		try {
			movePic = ImageIO.read(getClass().getResourceAsStream("/dot.png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		// a temp piece if nothing is clicked so it doesn't error out being null
		clickedPiece = new Piece(this, movePic, true, false, true, -144, -144);
		
			   
	}
	
	/** 
	 *  this just assigns a square color on whichever board color preference was picked
	 */
	public void assignSquare() {
		String light; String dark; // temp vars to store file name/path (there isn't rlly a path)
		if (gp.boardNum == 2) {
			light = "/whiteSquare.png";
			dark = "/redSquare.png";
		}
		else if (gp.boardNum == 1) { 
			light = "/whiteSquare.png";
			dark = "/blackSquare.png";
		}
		else {
			light = "/redSquare.png";
			dark = "/blackSquare.png";
		}
		
		
		try {
			lightSq = ImageIO.read(getClass().getResourceAsStream(light));
			darkSq = ImageIO.read(getClass().getResourceAsStream(dark));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * draws the board background (the checkerboard)
	 * 
	 * @param g2 the graphics component used to draw the board
	 */
	public void drawBoard(Graphics2D g2) {
		if (gp.gameState == 0) {
			BufferedImage redS = null;
			BufferedImage whiteS = null;
			
			try {
				redS = ImageIO.read(getClass().getResourceAsStream("/redSquare.png"));
				whiteS = ImageIO.read(getClass().getResourceAsStream("/whiteSquare.png"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			g2.drawImage(redS,   0*48, 7*48, null);
			g2.drawImage(whiteS, 3*48, 7*48, null);
		}
		
		if (gp.gameState == 1) {
			assignSquare();
			
			for (int j = 0; j < 8; j++) {
				for (int i = 0; i < 8; i++) {
					if (j % 2 != 0) {
						if (i % 2 != 0) {
							g2.drawImage(darkSq, i*gp.tileSize, j*gp.tileSize, null);
						}
						else {
							g2.drawImage(lightSq, i*gp.tileSize, j*gp.tileSize, null);
						}
					}
					else {
						if (i % 2 != 0) {
							g2.drawImage(lightSq, i*gp.tileSize, j*gp.tileSize, null);
						}
						else {
							g2.drawImage(darkSq, i*gp.tileSize, j*gp.tileSize, null);
						}
					}
				}
			}
			
			// draw the pieces
			for (Piece p : redPieces) {
				p.checkForKing(); // repeatedly checks for king possibility
				if (p.alive) {
					g2.drawImage(p.pic, p.yCood, p.xCood, null);
				}
				
			}
			for (Piece p : blackPieces) {
				p.checkForKing();
				if (p.alive) {
					g2.drawImage(p.pic, p.yCood, p.xCood, null);
				}
				
				
			}
			
			// draw moves
			for(int i = 0; i<map.length; i++) {
				for (int j = 0; j < map[0].length; j++) {
					if (map[i][j] == 6) {
						g2.drawImage(movePic, j*48, i*48, null);
	//					System.out.println("Drew move pic at: " + i + ", " + j);
					}
				}
			}
			
	//		printMap();
			
			
			
		}
	}
	
	/**
	 * method that deals with wherever the mouse was clicked
	 * 
	 * @param x the x-coordinate mouse was clicked at
	 * @param y the y-coordinate mouse was clicked at
	 */
	public void clickedAt(int x, int y) {
		if (gp.gameState == 0) {
			System.out.println("x*y: " + x + ", ");
//			if ((x<48 && x > 0) && (y < 8*48 && y> 7*48)) {
			if (x == 0 && y == 7) {
				ai = false;
				gp.gameState = 1;
				System.out.println("clicked 1v1");
			}
//			else if ((x > 3*48 && x < 4*48) && (y > 7*48 && y < 8*48)) {
			else if (x == 3 && y == 7) {
				ai = true;
				gp.gameState = 1;
				System.out.println("clicked ai");
			}
		}
		
		
		if (gp.gameState == 1) {
			int[] jumped = new int[2];
			int redJumpCount = 0;
			int blackJumpCount = 0;
			boolean alreadyJumped = false;
			
			if (map[y][x] != 0 && map[y][x] != 5 && map[y][x] != 6) {
//				System.out.println("Not-empty grid clicked: " + map[y][x]);
				
				if (turn == 1) {
					for(Piece p : redPieces) {
	//					p.handleJumps(); // check jumps for all pieces
	//					if (p.jumpAt != 0) {
	//						redJumpCount++;
	//					}
						p.handleJumps();
						System.out.println("Jumped coods: " + jumped[0] + ", " + jumped[1]);
						if(p.y == x && p.x == y) {
							clickedPiece = p;
							p.showMoves();
							System.out.println(p);
						}
						if (!alreadyJumped && p.jumpAt !=0) {
							jumped = clickedPiece.jump(clickedPiece.jumpAt);
							for (Piece k : blackPieces) {
								if (k.y == jumped[0]-1 && k.x == jumped[1]-1) {
									k.death(); // kill the piece that was jumped
									System.out.println("Killed this piece: \n" + k);
									
								}
							}
							alreadyJumped = true;
						}
					}
	//				if (redJumpCount == 1) { // if there's only one piece that can jump
	////					for (Piece p : redPieces) {
	////						if (p.jumpAt !=0) { // find the piece that can jump
	////							jumped = p.jump(p.jumpAt);
	////							System.out.println("Jumped[] : " +jumped[0] + ", " + jumped[1]);
	////						}
	////					}
	//					clickedPiece.handleJumps();
	//					jumped = clickedPiece.jump(clickedPiece.jumpAt);
	//					for (Piece p : blackPieces) {
	//						if (p.y == jumped[0] && p.x == jumped[1]) {
	//							p.death(); // kill the piece that was jumped
	//							System.out.println("Killed this piece: \n" + p);
	//							
	//						}
	//					}
	//				} 
	//				else if (redJumpCount != 0 && redJumpCount != 1 && redJumpCount > 0) { // should be greater than 0 but js a precaution
	//					
	//				}
				}
				if (turn == 0) {
					if (!ai) {
						for(Piece p : blackPieces) {
							p.handleJumps();
							if (p.jumpAt != 0) {
								blackJumpCount++;
							}
							if(p.y == x && p.x == y) {
								clickedPiece = p;
								p.showMoves();
								System.out.println(p);
							}
							
							if (!alreadyJumped && p.jumpAt !=0) {
								jumped = clickedPiece.jump(clickedPiece.jumpAt);
								for (Piece k : redPieces) {
									if (k.y == jumped[0]-1 && k.x == jumped[1]-1) {
										k.death(); // kill the piece that was jumped
//										System.out.println("Killed this piece: \n" + k);
										
									}
								}
								alreadyJumped = true;
							}
						}
						
		//			}
						
		//			// deal with the clicked piece's jumps
		//				clickedPiece.handleJumps();
		//				
		//				jumped = clickedPiece.jump(clickedPiece.jumpAt);
		//				
		//				if (clickedPiece.isRed) {
		//					for (Piece p : blackPieces) {
		//						if (p.x == jumped[0] && p.y == jumped[1]) {
		//							p.death();
		//						}
		//					}
		//				}
		//				else { // if it's not red
		//					for (Piece p : redPieces) {
		//						
		//					}
		//				}
						
					}
					else { // if ai
						boolean moved = false;
						do {
						int rand = (int) Math.random()*blackPieces.length;
						System.out.println("rand int: " + rand + "| len: " + blackPieces.length);
						blackPieces[rand].showMoves();
						if (blackPieces[rand].d1x >= 0 && blackPieces[rand].d1y >= 0) {
							blackPieces[rand].setXY(blackPieces[rand].d1x, blackPieces[rand].d1y);
							moved = true;
							turn = 1;
						}
						else if (blackPieces[rand].d2x >= 0 && blackPieces[rand].d2y >= 0) {
							blackPieces[rand].setXY(blackPieces[rand].d2x, blackPieces[rand].d2y);
							moved = true;
							turn = 1;
						}
						else if (blackPieces[rand].d3x >= 0 && blackPieces[rand].d3y >= 0) {
							blackPieces[rand].setXY(blackPieces[rand].d3x, blackPieces[rand].d3y);
							moved = true;
							turn = 1;
						}
						else if ((blackPieces[rand].d4x >= 0 && blackPieces[rand].d4y >= 0)) {
							blackPieces[rand].setXY(blackPieces[rand].d4x, blackPieces[rand].d4y);
							moved = true;
							turn = 1;
						}
						else {
							moved = false;
						}
						} while(moved = false);
					}
				}
					
			} 
			else if (map[y][x] == 6) {
				clickedPiece.setXY(y, x);
				if (turn == 0) {
					turn = 1;
				}
				else if (turn == 1) {
					turn = 0;
				}
				
				clickedPiece.resetMoveChoices();
			}
			else { 
				System.out.println("Empty grid clicked: " + map[y][x]);
			}
//			printMap();
//			System.out.println("RedJumpCount: " + redJumpCount + " || BlackJumpCount: " + blackJumpCount);
		}
	}
	
	/** 
	 * sets up the pieces
	 */
	public void pieceSetup() {
		BufferedImage tempPieceImage = null;
		try {
			tempPieceImage = ImageIO.read(getClass().getResourceAsStream("/dot.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int redCount = 0;
		int blackCount = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {		
//				if (redCount >= redPieces.length) {
//					break;
//				}
//				if (blackCount >= blackPieces.length) {
//					break;
//				}
				
				switch(map[i][j]) { 
					case 1: 
						try {
							tempPieceImage = ImageIO.read(getClass().getResourceAsStream("/redPiece.png"));
							redPieces[redCount] = new Piece(this, tempPieceImage, true, false, true, i, j);
							redCount++;
						}
						catch(IOException e) {
							e.printStackTrace();
						}
						break;
					case 2: 
						try {
							tempPieceImage = ImageIO.read(getClass().getResourceAsStream("/redPieceKing.png"));
							redPieces[redCount] = new Piece(this, tempPieceImage, true, true, true, i, j);
							redCount++;
						}
						catch(IOException e) {
							e.printStackTrace();
						}
						break;
					case 3: 
						try {
							tempPieceImage = ImageIO.read(getClass().getResourceAsStream("/blackPiece.png"));
							blackPieces[blackCount] = new Piece(this, tempPieceImage, false, false, true, i, j);
							blackCount++;
						}
						catch(IOException e) {
							e.printStackTrace();
						}
						break;
					case 4: 
						try {
							tempPieceImage = ImageIO.read(getClass().getResourceAsStream("/blackPieceKing.png"));
							blackPieces[blackCount] = new Piece(this, tempPieceImage, false, true, true, i, j);
							blackCount++;
						}
						catch(IOException e) {
							e.printStackTrace();
						}
						break;				
					case 6: 
//						g2.drawImage(tempPieceImage, i, j, null);
//						setMap(i, j, 6);
						break;
					}
			}
		}
	}
	
	/** 
	 * prints the current layout of pieces, mainly used for debugging purposes
	 */
	public void printMap() {
		System.out.println("\nMap: ");
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	/** 
	 * changes the value in the 2d array for the mapped out pieces
	 * 
	 * @param x the x coordinate of the 2d array
	 * @param y the y coordinate of the 2d array
	 * @param val the new value you want that position to have
	 */
	public void setMap(int x, int y, int val) {
		map[x][y] = val;
	}
	
	
}
