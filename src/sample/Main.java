//Justin Marotta & Michael Coppola
//Fruit Basket
//04/04/2019

package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {

    private boolean moveRight = false;
    private boolean moveLeft = false;
    private boolean loseStock1 = false;
    private boolean loseStock2 = false;
    private boolean loseStock3 = false;
    private Group gameRoot = new Group();
    private ArrayList<Circle> cannonBalls = new ArrayList<Circle>();

    // Images
    private Image cannonsSprite = new Image(new FileInputStream("sprites/cannons.png"));
    private ImageView cannons = new ImageView(cannonsSprite);

    private Image blastSprite = new Image(new FileInputStream("sprites/blast.png")); // Location of pictures in quotes.
    private ImageView blast = new ImageView(blastSprite);

    private Image manSprite = new Image(new FileInputStream("sprites/man.png"));
    private ImageView man = new ImageView(manSprite);

    private ImageView stock1 = new ImageView(manSprite);
    private ImageView stock2 = new ImageView(manSprite);
    private ImageView stock3 = new ImageView(manSprite);

    // Constant Sizing Variables
    final Double MAN_SPRITE_WIDTH = manSprite.getWidth();
    final Double MAN_SPRITE_HEIGHT = manSprite.getHeight();

    // Audio
    File boomFile = new File("sounds/boom.wav");
    private AudioClip boom = new AudioClip(boomFile.toURI().toString());

    public Main() throws FileNotFoundException {
    }

    private void makeCannonBalls() {
        Random randCannon = new Random();
        Circle cannonBall = new Circle(98, Color.GRAY);
        cannonBalls.add(cannonBall);
        gameRoot.getChildren().add(cannonBall);
        int i = randCannon.nextInt(8);
        if (i > 0) {
            cannonBall.setCenterX((i * 240) + 125);
            blast.setX((i * 240) + 85);
        } else {
            cannonBall.setCenterX(120);
            blast.setX(80);
        }
        cannonBall.setCenterY(260);
        blast.setY(280);
        boom.play();
    }

    public void start(Stage stage) throws Exception {
        // Setting the Stage
        stage.setTitle("Raining Balls");
        stage.setFullScreen(true);
        
        // Game Scene
        Scene scene = new Scene(gameRoot, stage.getWidth(), stage.getHeight(), Color.LIGHTBLUE);
        stage.setScene(scene);
        stage.show();

        // Constant Sizing Variables
        final Double STAGE_HEIGHT = stage.getHeight();
        final Double STAGE_WIDTH = stage.getWidth();

        // Adding Images
        gameRoot.getChildren().add(cannons);

        gameRoot.getChildren().add(man);
        man.setX(STAGE_WIDTH - STAGE_WIDTH / 2);
        man.setY(STAGE_HEIGHT - MAN_SPRITE_HEIGHT * 2);

        gameRoot.getChildren().add(stock1);
        stock1.setX(0);
        stock1.setY(STAGE_HEIGHT - MAN_SPRITE_HEIGHT);

        gameRoot.getChildren().add(stock2);
        stock2.setX(MAN_SPRITE_WIDTH);
        stock2.setY(STAGE_HEIGHT - MAN_SPRITE_HEIGHT);

        gameRoot.getChildren().add(stock3);
        stock3.setX(MAN_SPRITE_WIDTH * 2);
        stock3.setY(STAGE_HEIGHT - MAN_SPRITE_HEIGHT);

        // Creating the Animation Timer
        new AnimationTimer() {
            public void handle(long now) {
                // Moving
                if (moveRight) {
                    if (man.getX() < STAGE_WIDTH - 40) {
                        man.setX(man.getX() + 15);
                    }
                }
                if (moveLeft) {
                    if (man.getX() > 0) {
                        man.setX(man.getX() - 15);
                    }
                }

                // Spawning Balls
                if (now % 10 == 0) {
                    makeCannonBalls();
                }

                // Balls Falling
                for (Circle cannonBall : cannonBalls) {
                    cannonBall.setCenterY(cannonBall.getCenterY() + 15);
                }

                // Ball Limits
                for (int i = 0; i < cannonBalls.size(); i++) {
                    if (cannonBalls.get(i).getCenterY() > STAGE_HEIGHT - 145) {
                        gameRoot.getChildren().remove(cannonBalls.get(i));
                        cannonBalls.remove(cannonBalls.get(i));
                    }
                }

                // Getting Hit
                for (int i = 0; i < cannonBalls.size(); i++) {
                    if (man.intersects(cannonBalls.get(i).getLayoutBounds())) {
                        man.setX(940);

                        // Lose Stock
                        if (!loseStock3) {
                            gameRoot.getChildren().remove(stock3);
                            loseStock3 = true;
                        } else if (!loseStock2) {
                            gameRoot.getChildren().remove(stock2);
                            loseStock2 = true;
                        } else if (!loseStock1) {
                            gameRoot.getChildren().remove(stock1);
                            loseStock1 = true;
                            stage.close();
                        }

                        // Remove Balls
                        for (Circle cannonBall : cannonBalls) {
                            gameRoot.getChildren().remove(cannonBall);
                        }
                        cannonBalls = new ArrayList<Circle>();
                    }
                }

                // Cannons Layer Fix
                gameRoot.getChildren().remove(cannons);
                gameRoot.getChildren().add(cannons);
            }

        }.start();

        // Key event booleans
        EventHandler<KeyEvent> manKeyDown = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.RIGHT) {
                    moveRight = true;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    moveLeft = true;
                }

            }
        };
        EventHandler<KeyEvent> manKeyUp = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.RIGHT) {
                    moveRight = false;
                }
                if (event.getCode() == KeyCode.LEFT) {
                    moveLeft = false;
                }
            }
        };
        scene.addEventHandler(KeyEvent.KEY_PRESSED, manKeyDown);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, manKeyUp);
    }

    public static void main(String[] args) {
        launch(args);
    }
}