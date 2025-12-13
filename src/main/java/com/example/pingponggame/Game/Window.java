package com.example.pingponggame.Game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Window extends Application {

    // Window size
    private static final double APP_WIDTH  = 1200;
    private static final double APP_HEIGHT = 720;

    // Logical playfield size
    private static final double FIELD_WIDTH  = GameConfig.FIELD_WIDTH;
    private static final double FIELD_HEIGHT = GameConfig.FIELD_HEIGHT;

    // Inner margin between rounded border and play area
    private static final double BOARD_MARGIN = 40;

    // Game model
    private Ball ball;
    private Paddle paddle1;
    private Paddle paddle2;

    // UI nodes
    private Circle ballNode;
    private Rectangle leftPaddleNode;
    private Rectangle rightPaddleNode;
    private Label scoreALabel;
    private Label scoreBLabel;
    private Label winnerLabel;

    // Input state
    private boolean wPressed, sPressed, upPressed, downPressed;

    private AnimationTimer gameLoop;
    private boolean gameOver = false;

    private Stage primaryStage;
    private String player1 = "PLAYER A";
    private String player2 = "PLAYER B";

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        try {
            Image icon = new Image("icon.png");
            stage.getIcons().add(icon);
        } catch (Exception ignored) { }

        stage.setTitle("Ping Pong Game UI");

        Scene startScene = createStartScene();
        stage.setScene(startScene);
        stage.show();
    }

    /* ===================== START SCREEN ===================== */

    private Scene createStartScene() {
        StackPane root = new StackPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #020617;");

        BorderPane card = new BorderPane();
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #020617, #020617);" +
                        "-fx-background-radius: 26;" +
                        "-fx-border-radius: 26;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.7));
        shadow.setRadius(40);
        shadow.setOffsetY(10);
        card.setEffect(shadow);

        // Top bar, consistent with game screen
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 30, 18, 30));
        topBar.setStyle(
                "-fx-background-color: #0B1020;" +
                        "-fx-background-radius: 26 26 0 0;"
        );
        Label title = new Label("PING PONG GAME");
        title.setTextFill(Color.web("#E5E7EB"));
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        topBar.getChildren().add(title);
        card.setTop(topBar);

        // Center content: hero title, subtitle, name inputs, play button
        VBox center = new VBox(30);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40, 60, 60, 60));

        Label heroTitle = new Label("Neon Pong");
        heroTitle.setTextFill(Color.web("#E5E7EB"));
        heroTitle.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 42));

        Label heroSubtitle = new Label("Enter player names, then hit PLAY to start the match");
        heroSubtitle.setTextFill(Color.web("#6B7280"));
        heroSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 16));

        // Name inputs row
        HBox namesRow = new HBox(24);
        namesRow.setAlignment(Pos.CENTER);

        TextField leftNameField = new TextField();
        leftNameField.setPromptText("Left player name");
        styleNameField(leftNameField);

        TextField rightNameField = new TextField();
        rightNameField.setPromptText("Right player name");
        styleNameField(rightNameField);

        namesRow.getChildren().addAll(leftNameField, rightNameField);

        // Big circular play button with glow and triangle icon
        StackPane playButton = createPlayButton(() -> {
            String leftName  = leftNameField.getText().trim();
            String rightName = rightNameField.getText().trim();
            player1 = leftName.isEmpty()  ? "PLAYER A" : leftName.toUpperCase();
            player2 = rightName.isEmpty() ? "PLAYER B" : rightName.toUpperCase();
            showGameScene();
        });

        Label hint = new Label("Use W / S and ↑ / ↓ to move the paddles");
        hint.setTextFill(Color.web("#4B5563"));
        hint.setFont(Font.font("System", FontWeight.NORMAL, 13));

        center.getChildren().addAll(heroTitle, heroSubtitle, namesRow, playButton, hint);
        card.setCenter(center);

        root.getChildren().add(card);
        return new Scene(root, APP_WIDTH, APP_HEIGHT);
    }

    private void styleNameField(TextField field) {
        field.setPrefWidth(260);
        field.setFont(Font.font("System", FontWeight.NORMAL, 14));
        field.setStyle(
                "-fx-background-color: #020617;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-border-color: #1F2937;" +
                        "-fx-border-width: 1;" +
                        "-fx-text-fill: #E5E7EB;" +
                        "-fx-prompt-text-fill: #6B7280;" +
                        "-fx-padding: 10 16 10 16;"
        );
    }

    private StackPane createPlayButton(Runnable onClick) {
        double radius = 60;

        Circle base = new Circle(radius);
        base.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#22C55E")),
                new Stop(1, Color.web("#0EA5E9"))
        ));

        DropShadow glow = new DropShadow();
        glow.setColor(Color.color(0.2, 1, 0.7, 0.7));
        glow.setRadius(30);
        glow.setOffsetY(0);
        base.setEffect(glow);

        Polygon triangle = new Polygon(
                -15.0, -20.0,
                -15.0, 20.0,
                20.0, 0.0
        );
        triangle.setFill(Color.web("#F9FAFB"));

        StackPane button = new StackPane(base, triangle);
        button.setCursor(Cursor.HAND);

        // subtle hover / press feedback
        button.setOnMouseEntered(e -> button.setScaleX(1.05));
        button.setOnMouseEntered(e -> button.setScaleY(1.05));
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        button.setOnMousePressed(e -> button.setOpacity(0.8));
        button.setOnMouseReleased(e -> button.setOpacity(1.0));

        button.setOnMouseClicked(e -> onClick.run());

        return button;
    }

    /* ===================== GAME SCREEN ===================== */

    private void showGameScene() {
        StackPane root = new StackPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #020617;");

        BorderPane main = new BorderPane();
        main.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #020617, #020617);" +
                        "-fx-background-radius: 26;" +
                        "-fx-border-radius: 26;"
        );

        DropShadow mainShadow = new DropShadow();
        mainShadow.setColor(Color.color(0, 0, 0, 0.7));
        mainShadow.setRadius(40);
        mainShadow.setOffsetY(10);
        main.setEffect(mainShadow);

        buildCenter(main);
        root.getChildren().add(main);

        // Winner overlay
        winnerLabel = new Label();
        winnerLabel.setVisible(false);
        winnerLabel.setFont(Font.font("System", FontWeight.BOLD, 50));
        StackPane.setAlignment(winnerLabel, Pos.CENTER);
        root.getChildren().add(winnerLabel);

        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        main.prefWidthProperty().bind(scene.widthProperty());
        main.prefHeightProperty().bind(scene.heightProperty());

        setupGame(scene);

        primaryStage.setScene(scene);
        root.requestFocus();
    }

    /* ---------- UI building for game screen ---------- */

    private void buildCenter(BorderPane main) {
        VBox centerBox = new VBox(40);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(40, 40, 60, 40));
        centerBox.setStyle("-fx-background-color: transparent;");

        HBox scoreBoard = buildScoreBoard();
        StackPane playField = buildPlayField();

        centerBox.getChildren().addAll(scoreBoard, playField);
        main.setCenter(centerBox);
    }

    private HBox buildScoreBoard() {
        HBox scoreBoard = new HBox(120);
        scoreBoard.setAlignment(Pos.CENTER);

        // LEFT player
        VBox left = new VBox(8);
        left.setAlignment(Pos.CENTER);

        Label playerALabel = new Label(player1);
        playerALabel.setTextFill(Color.web("#9CA3AF"));
        playerALabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        scoreALabel = new Label("00");
        scoreALabel.setTextFill(Color.web("#60A5FA"));
        scoreALabel.setFont(Font.font("System", FontWeight.BOLD, 70));

        left.getChildren().addAll(playerALabel, scoreALabel);

        // divider
        Line divider = new Line(0, 0, 0, 90);
        divider.setStroke(Color.web("#374151"));
        divider.setStrokeWidth(1);

        // RIGHT player
        VBox right = new VBox(8);
        right.setAlignment(Pos.CENTER);

        Label playerBLabel = new Label(player2);
        playerBLabel.setTextFill(Color.web("#9CA3AF"));
        playerBLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        scoreBLabel = new Label("00");
        scoreBLabel.setTextFill(Color.web("#F472B6"));
        scoreBLabel.setFont(Font.font("System", FontWeight.BOLD, 70));

        right.getChildren().addAll(playerBLabel, scoreBLabel);

        scoreBoard.getChildren().addAll(left, divider, right);
        return scoreBoard;
    }

    private StackPane buildPlayField() {
        StackPane fieldContainer = new StackPane();
        fieldContainer.setAlignment(Pos.CENTER);

        double totalWidth  = FIELD_WIDTH  + 2 * BOARD_MARGIN;
        double totalHeight = FIELD_HEIGHT + 2 * BOARD_MARGIN;

        Rectangle fieldRect = new Rectangle(totalWidth, totalHeight);
        fieldRect.setArcWidth(28);
        fieldRect.setArcHeight(28);
        fieldRect.setFill(Color.web("#0F172A"));

        DropShadow ds = new DropShadow();
        ds.setColor(Color.color(0, 0, 0, 0.75));
        ds.setRadius(50);
        ds.setOffsetY(20);
        fieldRect.setEffect(ds);

        Pane field = new Pane();
        field.setPrefSize(totalWidth, totalHeight);
        field.setMaxSize(totalWidth, totalHeight);
        field.setMinSize(totalWidth, totalHeight);
        field.setBackground(new Background(
                new BackgroundFill(Color.web("#0F172A"), new CornerRadii(24), Insets.EMPTY))
        );

        Line midLine = new Line(
                BOARD_MARGIN + FIELD_WIDTH / 2, BOARD_MARGIN,
                BOARD_MARGIN + FIELD_WIDTH / 2, BOARD_MARGIN + FIELD_HEIGHT
        );
        midLine.setStroke(Color.web("#4B5563"));
        midLine.setStrokeWidth(2);
        midLine.getStrokeDashArray().addAll(10d, 18d);

        ballNode = new Circle(Ball.SIZE / 2.0);
        ballNode.setFill(Color.web("#E5E7EB"));

        leftPaddleNode = new Rectangle(Paddle.WIDTH, Paddle.HEIGHT);
        leftPaddleNode.setArcWidth(12);
        leftPaddleNode.setArcHeight(12);
        leftPaddleNode.setFill(Color.web("#60A5FA"));

        rightPaddleNode = new Rectangle(Paddle.WIDTH, Paddle.HEIGHT);
        rightPaddleNode.setArcWidth(12);
        rightPaddleNode.setArcHeight(12);
        rightPaddleNode.setFill(Color.web("#F472B6"));

        field.getChildren().addAll(midLine, ballNode, leftPaddleNode, rightPaddleNode);
        fieldContainer.getChildren().addAll(fieldRect, field);

        return fieldContainer;
    }

    /* ---------- Game wiring ---------- */

    private void setupGame(Scene scene) {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        ball = new Ball();
        paddle1 = new Paddle(true);   // left
        paddle2 = new Paddle(false);  // right
        gameOver = false;
        winnerLabel.setVisible(false);

        // keyboard input
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.W) {
                if (!wPressed) { wPressed = true; paddle1.switchDirection(-1); }
            } else if (code == KeyCode.S) {
                if (!sPressed) { sPressed = true; paddle1.switchDirection(1); }
            } else if (code == KeyCode.UP) {
                if (!upPressed) { upPressed = true; paddle2.switchDirection(-1); }
            } else if (code == KeyCode.DOWN) {
                if (!downPressed) { downPressed = true; paddle2.switchDirection(1); }
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.W)       wPressed = false;
            else if (code == KeyCode.S)  sPressed = false;
            else if (code == KeyCode.UP) upPressed = false;
            else if (code == KeyCode.DOWN) downPressed = false;

            if (!wPressed && !sPressed)     paddle1.stop();
            if (!upPressed && !downPressed) paddle2.stop();
        });

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
                renderGame();
            }
        };
        gameLoop.start();
    }

    private void updateGame() {
        if (gameOver) return;

        ball.update(paddle1, paddle2);
        paddle1.update(ball);
        paddle2.update(ball);

        if (paddle1.getScore() == 10) {
            showWinner(player1, Color.web("#60A5FA"));
        } else if (paddle2.getScore() == 10) {
            showWinner(player2, Color.web("#F472B6"));
        }
    }

    private void showWinner(String player, Color color) {
        gameOver = true;
        winnerLabel.setText(player.toUpperCase() + " WINS!");
        winnerLabel.setTextFill(color);
        winnerLabel.setVisible(true);
        gameLoop.stop();
    }

    private void renderGame() {
        ballNode.setCenterX(BOARD_MARGIN + ball.getX() + Ball.SIZE / 2.0);
        ballNode.setCenterY(BOARD_MARGIN + ball.getY() + Ball.SIZE / 2.0);

        leftPaddleNode.setX(BOARD_MARGIN + paddle1.getX());
        leftPaddleNode.setY(BOARD_MARGIN + paddle1.getY());

        rightPaddleNode.setX(BOARD_MARGIN + paddle2.getX());
        rightPaddleNode.setY(BOARD_MARGIN + paddle2.getY());

        scoreALabel.setText(String.format("%02d", paddle1.getScore()));
        scoreBLabel.setText(String.format("%02d", paddle2.getScore()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}