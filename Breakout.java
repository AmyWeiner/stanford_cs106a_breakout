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
	
/* Runs the Breakout program. */
	public void run() {
		setupGame();
		playGame();
		
	}

	private void setupGame() {
		setupBricks();
		createPaddle();
		addMouseListeners();
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
	
	private void createPaddle() {
		int x = (WIDTH - PADDLE_WIDTH)/ 2;
		int y = HEIGHT - PADDLE_Y_OFFSET;
		GRect paddle = new GRect (x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		add(paddle);
		paddle.setFilled(true);
	}
	
	public void mouseMoved(MouseEvent e) {
		double paddleX = e.getX();
		double paddleY = HEIGHT - PADDLE_Y_OFFSET;
		paddle.move(paddleX, paddleY);
	}
	
	private void playGame() {
		createBall();
		waitForClick();
		launchBall();
	}
	
	private void createBall() {
		double x = WIDTH / 2;
		double y = HEIGHT / 2;
		double r = BALL_RADIUS;
		GOval ball = new GOval(x - r, y - r, 2 * r, 2 * r);
		add(ball);
		ball.setFilled(true);
	}
	                       
	private void launchBall() {
		vx = rgen.nextDouble(MIN_X_VELOCITY, MAX_X_VELOCITY);
        if (rgen.nextBoolean()) vx = -vx;
        vy = 3.0;
        while (true) {
        	//ball.move(vx, vy);
        	
        }
	}
	
/* Create an instance variable for the paddle */	
	private GRect paddle;
	
	private GOval ball;
	
/* Create an instance variable for the velocity in the x and y directions */
	private double vx, vy;
	
/* Create an instance variable for the random number generator */
	private RandomGenerator rgen = RandomGenerator.getInstance();
}
