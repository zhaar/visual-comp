package ch.epfl.visualComputing;


import ch.epfl.visualComputing.Entites.Ball;
import ch.epfl.visualComputing.Entites.Cylinder;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    public boolean editMode;
    private float angleX;
    private float angleY;
    private float angleZ;
    private float inputFactorSpeed = 1f;
    private static final float scaleFactor = 0.01f;
    public final int boardSize;
    final List<Cylinder> objects = new ArrayList<Cylinder>();
    private final Ball ball;

    public GameState(int s, PApplet ctx) {
        this.ball = new Ball(ctx, 20);
        ball.setPosition(new PVector(150, 150));
        this.boardSize = s;
    };

    void updateSpeedFactor(float speed) {
        float amount = speed /10;
        this.inputFactorSpeed = PhysicsEngine.clamp(inputFactorSpeed + amount, 0.2f, 1.5f);
    }

    float getAngleX() { return angleX; }
    void setAngleX(float x) { this.angleX = x; }
    float getAngleY() { return angleY; }
    void setAngleY(float y) { this.angleY = y; }
    float getAngleZ() { return angleZ; }
    void setAngleZ(float z) { this.angleZ = z; }
    float getSpeedFactor() { return inputFactorSpeed * scaleFactor; }

    void draw(PApplet ctx) {
        if (!editMode) {
            PhysicsEngine.applyForces(this, ball);
            PhysicsEngine.updateBallState(this, ball);
        }
        ctx.camera(ctx.width/2.0f, ctx.height / 2.0f, ctx.height/2.0f / PApplet.tan(PApplet.radians(30f)), ctx.width/2.0f, ctx.height/2.0f, 0, 0, -1, 0);
        ctx.directionalLight(50, 100, 125, 0.5f, -0.5f, 0);
        ctx.ambientLight(102, 102, 102);
        ctx.background(200);
        ctx.translate(ctx.width/2, ctx.height/2, 0);

        ctx.noStroke();
        if (!editMode) {
            ctx.rotateX(angleX);
            ctx.rotateZ(angleZ);
        } else {
            ctx.rotateX(PApplet.radians(90));
        }


        ctx.box(boardSize, 5, boardSize);
        ctx.pushMatrix();
        ctx.translate(-boardSize/2, 5, -boardSize/2);
        ball.draw(boardSize);

        for (Cylinder e : objects) {
            e.draw(boardSize);
        }
        ctx.popMatrix();
    }
}