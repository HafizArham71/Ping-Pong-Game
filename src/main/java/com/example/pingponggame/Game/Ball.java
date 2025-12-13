package com.example.pingponggame.Game;

public class Ball {
    public static final double SIZE = 16;

    private double x, y;          // TOP‑LEFT of the ball, same as in AWT
    private double xVel, yVel;
    private double speed = 7;

    public Ball() {
        reset();
    }

    public void reset() {
        x = GameConfig.FIELD_WIDTH / 2.0 - SIZE / 2.0;
        y = GameConfig.FIELD_HEIGHT / 2.0 - SIZE / 2.0;

        xVel = Math.random() < 0.5 ? -1 : 1;
        yVel = Math.random() < 0.5 ? -1 : 1;
    }

    public void update(Paddle p1, Paddle p2) {
        x += xVel * speed;
        y += yVel * speed;

        // top / bottom walls (FIELD_HEIGHT is the logical play area height)
        if (y <= 0) {
            y = 0;
            yVel *= -1;
        } else if (y >= GameConfig.FIELD_HEIGHT - SIZE) {
            y = GameConfig.FIELD_HEIGHT - SIZE;
            yVel *= -1;
        }

        // scoring (passes completely out of left / right)
        if (x <= 0) {
            p2.addPoint();
            reset();
        } else if (x >= GameConfig.FIELD_WIDTH - SIZE) {
            p1.addPoint();
            reset();
        }
    }

    /* --- helpers for paddle collisions --- */

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