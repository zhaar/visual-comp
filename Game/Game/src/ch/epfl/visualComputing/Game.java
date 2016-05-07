package ch.epfl.visualComputing;

import ch.epfl.visualComputing.Entites.Ball;
import ch.epfl.visualComputing.Entites.Cylinder;
import processing.core.*;
import processing.event.MouseEvent;

public class Game extends PApplet {

    public void settings() {
        size(600, 600, P3D);
    }

    GameState state = new GameState(300, this);

    public void setup() {
        noStroke();
    }

    boolean inInterval(float v, float min, float max) {
        return min <= v && v <= max;
    }

    public static final float gravityConstant = 1;

    public static PVector computeSpeedFromAngle(float x, float y) {
        PVector gravityForce = new PVector();
        gravityForce.x = sin(radians(x)) * gravityConstant;
        gravityForce.y = sin(radians(y)) * gravityConstant;
        return gravityForce;
    }

    public static PVector computeFriction(Ball b) {
        float normalForce = 1;
        float mu = 0.005f;
        float frictionMagnitude = normalForce * mu;
        PVector friction = b.getVelocity();
        friction.mult(-1);
        friction.normalize();
        friction.mult(frictionMagnitude);
        return friction;
    }

    public static void applyForces(GameState s, Ball b) {
        PVector friction = computeFriction(b);
        b.setVelocity(b.getVelocity()
                .add(computeSpeedFromAngle(s.getAngleX(), s.getAngleZ()))
                .add(friction));
        for (Cylinder e : s.objects) {
            if (b.intersects(e)) {
                b.setVelocity(b.bounceOn(e));
            }
        }
    }

    public static Ball updateBallState(GameState s, Ball b) {
        if ( b.getPosition().x > s.boardSize) {
            b.setPosX(s.boardSize);
            b.invertXVelocity();
        } else if (b.getPosition().x < 0) {
            b.setPosX(0);
            b.invertXVelocity();
        } else if (b.getPosition().y > s.boardSize) {
            b.setPosY(s.boardSize);
            b.invertYVelocity();
        } else if (b.getPosition().y < 0) {
            b.setPosY(0);
            b.invertYVelocity();
        }
        b.setPosition(b.getPosition().add(b.getVelocity()));
        return b;
    }

    public void draw() {
        background(200);
        state.draw(this);
    }

    public void placeObject(GameState s, float x, float y) {
        s.objects.add(new Cylinder(this, x, y, 30));
    }

    int mouseDragY, mouseDragX;

    public void mousePressed() {
        if (!state.editMode) {
            mouseDragX = mouseDragY = 0;  // The drag starts here
        } else {
            float yCoord = 2 * ((float)mouseX - (float)width/4f)/width;
            float xCoord = 2 * ((float)mouseY - (float)height/4f)/height;
            println("x: " + xCoord);
            if (inInterval(xCoord, 0f, 1f) && inInterval(yCoord, 0, 1f)) {
                placeObject(state, xCoord * state.boardSize, yCoord * state.boardSize);
            }

        }
    }

    float clampAngle(float value) {
        return clamp(value, -PI/3, PI/3);
    }

    public static float clamp(float value, float min, float max) {
        return value < min? min : (value > max ? max : value);
    }

    public void mouseWheel(MouseEvent event) {
        float e = event.getCount();
        state.updateSpeedFactor(e);
    }

    public void keyPressed() {
        if (keyCode == SHIFT) {
            state.editMode = true;
        } else if (keyCode == BACKSPACE) {
            state = new GameState(300, this);
        }
    }

    public void keyReleased() {
        if (keyCode == SHIFT) {
            state.editMode = false;
        }
    }

    public void mouseDragged() {
        mouseDragX = (mouseX - pmouseX);
        mouseDragY = (mouseY - pmouseY);
        state.setAngleX(clampAngle(state.getAngleX() + mouseDragY * state.getSpeedFactor()));
        state.setAngleZ(clampAngle(state.getAngleZ() + mouseDragX * state.getSpeedFactor()));
    }
}
