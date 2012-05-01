/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/* Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/* Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/* Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/* Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/* Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/* Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/* Separation between bricks */
	private static final int BRICK_SEP = 4;

	/* Width of a brick */
	private static final int BRICK_WIDTH =
		(WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/* Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/* Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/* Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/* Number of turns */
	private static final int NTURNS = 3;

	/* Minimum x velocity of the ball */
	private static final double MIN_X_VELOCITY = 1.0;

	/* Maximum x velocity of the ball */
	private static final double MAX_X_VELOCITY = 3.0;

	private static final double PAUSE_TIME = 20;
	
	/* Number of points per brick */
	private static final int POINTS_PER_BRICK = 10;
	
	private static final double OFFSET_X = 70;

	/* Runs the Breakout program. */
	public void run() {
		addMouseListeners();
		setupGame();
		playGame();

	}

	private void setupGame() {
		setupBricks();
		createPaddle();
		displayScore(0);
		displayTurns(NTURNS);
	}

	private void setupBricks() {
		for (int i = 0; i < NBRICK_ROWS; i ++) {
			for (int j = 0; j < NBRICKS_PER_ROW; j ++) {
				double x = ((WIDTH - ((NBRICKS_PER_ROW * (BRICK_WIDTH + BRICK_SEP)) - BRICK_SEP)) / 2) + j * (BRICK_WIDTH + BRICK_SEP);
				double y = i * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET;
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				add(brick);
				brick.setFilled(true);
				if (i % 10 == 0 || i % 10 == 1) {
					brick.setColor(Color.RED);
				} else if (i % 10 == 2 || i % 10 == 3) {
					brick.setColor(Color.ORANGE);
				} else if (i % 10 == 4 || i % 10 == 5) {
					brick.setColor(Color.YELLOW);
				}else if (i % 10 == 6 || i % 10 == 7) {
					brick.setColor(Color.GREEN);
				} else {
					brick.setColor(Color.CYAN);
				}
			}
		}
	}

	/* Creates the game paddle */
	private void createPaddle() {
		int x = (WIDTH - PADDLE_WIDTH)/ 2;
		int y = HEIGHT - PADDLE_Y_OFFSET;
		paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		add(paddle);
		paddle.setFilled(true);
	}

	public void mouseMoved(MouseEvent e) {
		double dx = e.getX();
		double paddleY = HEIGHT - PADDLE_Y_OFFSET;
		paddle.setLocation(dx, paddleY);
		if (dx > WIDTH - PADDLE_WIDTH) {
			paddle.setLocation(WIDTH - PADDLE_WIDTH, paddleY);
		}
	}

	private void playGame() {
		turnCounter = NTURNS;
		brickCounter = NBRICKS_PER_ROW * NBRICK_ROWS;
		while (turnCounter > 0) {
			createBall();
			waitForClick();
			launchBall();
		}
	}

	/* Displays the number of turns remaining */
	private void displayTurns(int counter) {
		double x = 0;
		double y = 0;
		turns = new GLabel ("Turns: " + counter);
		add(turns);
		double ly = turns.getAscent();
		turns.setLocation(x, y + ly);
	}

	private void displayScore(int scoreCounter) {
		double x = WIDTH;
		double y = 0;
		score = new GLabel ("Score: " + scoreCounter);
		add(score);
		double lx = x - OFFSET_X;
		double ly = score.getAscent();
		score.setLocation(lx, y + ly);
	}
	
	/* Creates the game ball */
	private void createBall() {
		double x = WIDTH / 2;
		double y = HEIGHT / 2;
		double r = BALL_RADIUS;
		ball = new GOval(x - r, y - r, 2 * r, 2 * r);
		add(ball);
		ball.setFilled(true);
	}

	private void launchBall() {
		vx = rgen.nextDouble(MIN_X_VELOCITY, MAX_X_VELOCITY);
		if (rgen.nextBoolean()) vx = -vx;
		vy = 5.0;
		while (true) {
			ball.move(vx, vy);
			pause(PAUSE_TIME);
			GObject collider = getCollidingObject();
			if (ball.getY() + (2 *BALL_RADIUS) > HEIGHT) {
				break;
			} else if (ball.getX() + (2 *BALL_RADIUS) > WIDTH) {
				vx=-vx;
				pause(PAUSE_TIME);
			} else if (ball.getX() < 0) {
				vx = -vx;
				pause(PAUSE_TIME);
			} else if (ball.getY() < 0){
				vy = -vy;
				pause(PAUSE_TIME);
			} else if (collider == paddle) {
				bounceClip.play();
				vy = -vy;
			} else if (collider != null && collider != turns && collider != score) {
				remove(collider);
				bounceClip.play();
				vy = -vy;
				brickCounter --;
				updateScore();
				if (brickCounter == 0) {
					break;
				}
			}
		}
		if (brickCounter == 0){
			displayYouWin();
			waitForClick();
			remove(youWin);
		}else {
			updateTurns();
			if (turnCounter == 0) {
				displayYouLose();
				waitForClick();
				resetGame();
			}
		} 
	}

	private GObject getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null) {
			return getElementAt(ball.getX(), ball.getY());
		} else if (getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY()) != null) {
			return getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		} else if (getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS)) != null) {
			return getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));
		}else {
			return getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
		}
	}

	/* Updates the number of turns remaining */
	private void updateTurns() {
		turnCounter --;
		turns.setLabel("Turns: " + turnCounter);
		remove(ball);
	}
	
	/* Updates the player's score */
	private void updateScore() {
		scoreCounter += POINTS_PER_BRICK;
		score.setLabel("Score: " + scoreCounter);
	}

	/* Displays a massage notifying user that game is over, and that player has lost */
	private void displayYouLose() {
		double x = WIDTH / 2;
		double y = HEIGHT / 2;
		youLose = new GLabel("GAME OVER, YOU LOSE");
		add(youLose);
		double lx = x - youLose.getWidth();
		youLose.setColor(Color.RED);
		youLose.setFont("Helvetics-24");
		youLose.setLocation(lx, y);
	}

	/* Displays a massage notifying user that game is over, and that player has won */
	private void displayYouWin() {
		double x = WIDTH / 2;
		double y = HEIGHT / 2;
		youWin = new GLabel("GAME OVER, YOU WIN");
		add(youWin);
		double lx = x - youWin.getWidth();
		youWin.setColor(Color.RED);
		youWin.setFont("Helvetics-24");
		youWin.setLocation(lx, y);
	}
	
	private void resetGame() {
		remove(youLose);
		turnCounter = NTURNS;
		scoreCounter = 0;
		//playGame();
	}

	/* Create an instance variable for the paddle */	
	private GRect paddle;

	/* Create an instance variable for the ball */
	private GOval ball;

	/* Create an instance variable for the velocity in the x and y directions */
	private double vx, vy;

	/* Create an instance variable for the random number generator */
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/* Create an instance variable for the counter of current number of turns */
	private int turnCounter;

	/* Create an instance variable for the label that displays the number of turns remaining */
	private GLabel turns;

	private int brickCounter;
	
	private int scoreCounter;
	
	private GLabel youLose;
	
	private GLabel youWin;
	
	private GLabel score;
	
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

}
