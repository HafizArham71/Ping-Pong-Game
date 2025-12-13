package com.example.pingponggame.Game;

public class Paddle {

    public static final double WIDTH  = 25;
    public static final double HEIGHT = 100;

    private double x, y;
    private double vel = 0;
    private final double speed = 14;
    private int score;
    private final boolean left;

    public Paddle(boolean left) {
        this.left = left;
        x = left ? 0 : GameConfig.FIELD_WIDTH - WIDTH;
        y = GameConfig.FIELD_HEIGHT / 2.0 - HEIGHT / 2.0;
    }

    public void addPoint() {
        score++;
    }

    public void update(Ball ball) {
        // move, stay inside field
        if (y < 0) y = 0;
        else if (y > GameConfig.FIELD_HEIGHT - HEIGHT) y = GameConfig.FIELD_HEIGHT - HEIGHT;
        else y += vel;

        double ballX = ball.getX();
        double ballY = ball.getY();
        double ballRight = ballX + Ball.SIZE;
        double ballBottom = ballY + Ball.SIZE;

        if (left) {
            boolean overlapX = ballX <= WIDTH && ballRight >= 0;
            boolean overlapY = ballBottom >= y && ballY <= y + HEIGHT;
            if (overlapX && overlapY) {
                ball.bounceFromLeftPaddle(WIDTH);
            }
        } else {
            boolean overlapX = ballRight >= x && ballX <= x + WIDTH;
            boolean overlapY = ballBottom >= y && ballY <= y + HEIGHT;
            if (overlapX && overlapY) {
                ball.bounceFromRightPaddle(x);
            }
        }
    }

    public void switchDirection(int dir) { vel = speed * dir; }
    public void stop() { vel = 0; }

    public int getScore() { return score; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isLeft() { return left; }
}
