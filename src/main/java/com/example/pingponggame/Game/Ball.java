package com.example.pingponggame.Game;

public class Ball {
    public static final double SIZE = 16;

    private double x, y;
    private double xVel, yVel;
    private double speed = 7;

    public Ball() {
        reset();
    }

    public void reset() {
        x = GameConfig.FIELD_WIDTH / 2.0 - SIZE / 2.0;      // Center on x-axis
        y = GameConfig.FIELD_HEIGHT / 2.0 - SIZE / 2.0;     // Center on y-axis

        xVel = Math.random() < 0.5 ? -1 : 1;                // -1 or 1
        yVel = Math.random() < 0.5 ? -1 : 1;                // -1 or 1
    }

    public void update(Paddle p1, Paddle p2) {
        x += xVel * speed;                                  // x = x + [(-1 or 1) * 7]
        y += yVel * speed;                                  // y = y + [(-1 or 1) * 7]

        if (y <= 0) {       // Whenever the ball reaches out of the top boundary of playing field
            y = 0;          // Place the ball at the start of the top boundary of playing field
            yVel *= -1;     // Reverse the direction of the velocity
        } else if (y >= GameConfig.FIELD_HEIGHT - SIZE) {   // Whenever the ball reaches out of the bottom boundary of playing field
            y = GameConfig.FIELD_HEIGHT - SIZE;             // Place the ball at the start of the bottom boundary of playing field
            yVel *= -1;                                     // Reverse the direction of the velocity
        }

        // Scoring
        if (x <= 0) {       // If left player miss the ball
            SoundManager.play("/sound/point1.wav");
            p2.addPoint();  // Right player gets one point
            reset();        // Reset the ball position and its direction
        } else if (x >= GameConfig.FIELD_WIDTH - SIZE) {    // If right player miss the ball
            SoundManager.play("/sound/point1.wav");
            p1.addPoint();                                  // Right player gets one point
            reset();                                        // Reset the ball position and its direction
        }
    }

    // Helpers for paddle collisions

    public void bounceFromLeftPaddle(double paddleRightX) {
        x = paddleRightX;         // place ball just to the RIGHT of the paddle
        xVel = Math.abs(xVel);    // ensure it moves right
    }

    public void bounceFromRightPaddle(double paddleLeftX) {
        x = paddleLeftX - SIZE;   // place ball just to the LEFT of the paddle
        xVel = -Math.abs(xVel);   // ensure it moves left
    }

    public double getX() { return x; }
    public double getY() { return y; }

}