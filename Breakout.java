/*
 * File: Breakout.java
 * -------------------
 * Name: Amy R. Weiner
 * Section Leader: Molly Mackinlay 
 * 
 * This file implements the game of Breakout.
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

	public static void main(String[] args) {
		new Breakout().start(args);
	}

	/* Runs the Breakout program. */
	public void run() {
		addMouseListeners();
		setupGame();
		playGame();

	}

	/* Sets up the game */
	private void setupGame() {
		setupBricks();
		createPaddle();
		displayScore(0);
		displayTurns(NTURNS);
		displayWelcome();
	}

	/* Sets up the grid of bricks */
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

	/* Creates a mouseMoved event so that the paddle tracks the mouse */
	public void mouseMoved(MouseEvent e) {
		double dx = e.getX();
		double paddleY = HEIGHT - PADDLE_Y_OFFSET;
		paddle.setLocation(dx, paddleY);
		if (dx > WIDTH - PADDLE_WIDTH) {
			paddle.setLocation(WIDTH - PADDLE_WIDTH, paddleY);
		}
	}

	/* Starts the play of the game */
	private void playGame() {
		brickCounter = NBRICKS_PER_ROW * NBRICK_ROWS;
		while (turnCounter > 0) {
			createBall();
			waitForClick();
			remove(welcome);
			launchBall();
		}
	}

	/* Displays the number of turns remaining */
	private void displayTurns(int counter) {
		turnCounter = NTURNS;
		double x = 0;
		double y = 0;
		turns = new GLabel ("Turns: " + counter);
		add(turns);
		double ly = turns.getAscent();
		turns.setLocation(x, y + ly);
	}

	/* Displays the current score */
	private void displayScore(int scoreCounter) {
		double x = WIDTH;
		double y = 0;
		score = new GLabel ("Score: " + scoreCounter);
		add(score);
		double lx = x - OFFSET_X;
		double ly = score.getAscent();
		score.setLocation(lx, y + ly);
	}
	
	/* Displays the welcome message */
	private void displayWelcome() {
		double y = (HEIGHT / 2) + (BALL_RADIUS * 3) ;
		welcome = new GLabel("HELLO, CLICK TO PLAY");
		add(welcome);
		double x = WIDTH / 2;
		welcome.setColor(Color.RED);
		welcome.setFont("Helvetics-20");
		welcome.setLocation(x - (welcome.getWidth() / 2), y);
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

	/* Launches the ball with a random velocity in the x-direction, and with a constant velocity in the y-direction */
	private void launchBall() {
		vx = rgen.nextDouble(MIN_X_VELOCITY, MAX_X_VELOCITY);
		if (rgen.nextBoolean()) vx = -vx;
		vy = 5.0;
		while (true) {
			ball.move(vx, vy);
			pause(PAUSE_TIME);
			GObject collider = getCollidingObject();
			if (ball.getY() + (2 *BALL_RADIUS) > HEIGHT) {						//checks if ball has the bottom of application screen
				break;
			} else if (ball.getX() + (2 *BALL_RADIUS) > WIDTH) {				//checks if ball has hit right wall
				vx=-vx;
				pause(PAUSE_TIME);
			} else if (ball.getX() < 0) {										//checks if ball has hit left wall
				vx = -vx;
				pause(PAUSE_TIME);
			} else if (ball.getY() < 0){										//checks if ball has hit the top of the application screen
				vy = -vy;
				pause(PAUSE_TIME);
			} else if (collider == paddle) {                                   //checks if ball has hit the paddle 
				bounceClip.play();
				vy = -vy;	
			} else if (collider != null && collider != turns && collider != score) {	//checks if ball has hit a brick
				remove(collider);
				bounceClip.play();
				vy = -vy;
				brickCounter --;
				updateScore();
				updateVelocity();
				if (brickCounter == 0) {
					break;
				}
			}
		}
		if (brickCounter == 0){
			winGameClip.play();
			displayYouWin();
			resetGame();
		}else {
			updateTurns();
			if (turnCounter == 0) {
				loseGameClip.play();
				displayYouLose();
				resetGame();
			}
		} 
	}

	/* Determines if the ball has collided with an object by checking four points around the ball */
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
		loseTurnClip.play();
		turnCounter --;
		turns.setLabel("Turns: " + turnCounter);
		remove(ball);
	}

	/* Updates the player's score */
	private void updateScore() {
		scoreCounter += POINTS_PER_BRICK;
		score.setLabel("Score: " + scoreCounter);
	}

	/* Updates the velocity of the ball in the y-direction for every ten bricks removed */
	private void updateVelocity() {
		int startBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
		int decrement = 10;
		double deltaVelocity = 0.5;
		if (brickCounter == startBricks - decrement) {
			vy = vy + deltaVelocity;
		} else if (brickCounter == startBricks - (decrement * 2)) {
			vy = vy + (deltaVelocity * 2);
		} else if (brickCounter == startBricks - (decrement * 3)) {
			vy = vy + (deltaVelocity * 3);
		} else if (brickCounter == startBricks - (decrement * 4)) {
			vy = vy + (deltaVelocity * 4);
		} else if (brickCounter == startBricks - (decrement * 5)) {
			vy = vy + (deltaVelocity * 5);
		} else if (brickCounter == startBricks - (decrement * 6)) {
			vy = vy + (deltaVelocity * 6);
		} else if (brickCounter == startBricks - (decrement * 7)) {
			vy = vy + (deltaVelocity * 7);
		} else if (brickCounter == startBricks - (decrement * 8)) {
			vy = vy + (deltaVelocity * 8);
		} else if (brickCounter == startBricks - (decrement * 9)) {
			vy = vy + (deltaVelocity * 9);
		}
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

	/* Resets the game */
	private void resetGame() {
		waitForClick();
		removeAll();
		setupGame();
		playGame();
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

	/* Create an instance variable for a counter that counts the number of brinks remaining */
	private int brickCounter;

	/* Create an instance variable for a counter that keeps a running tally of the score */
	private int scoreCounter;

	/* Create an instance variable for a message indicating that the game is over, and that the player has lost */
	private GLabel youLose;

	/* Create an instance variable for a message indicating that the game is over, and that the player has won */
	private GLabel youWin;

	/* Create an instance variable for the label that displays the current score */
	private GLabel score;
	
	/* Create an instance variable for the label that displays the welcome message */
	private GLabel welcome;

	/* Create an instance variable for the audio clip used when the ball collides with a brick or with the paddle */
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

	/* Create an instance variable for the audio clip used when the player loses a turn */
	AudioClip loseTurnClip = MediaTools.loadAudioClip("26SINVADE3.wav");

	/* Create an instance variable for the audio clip used when the player wins the game */
	AudioClip winGameClip = MediaTools.loadAudioClip("win.wav");

	/* Create an instance variable for the audio clip used when the player loses the game */
	AudioClip loseGameClip = MediaTools.loadAudioClip("mrdo_end.wav");

}
