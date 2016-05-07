package ch.epfl.visualComputing;

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
        state.setAngleX(PhysicsEngine.clampAngle(state.getAngleX() + mouseDragY * state.getSpeedFactor()));
        state.setAngleZ(PhysicsEngine.clampAngle(state.getAngleZ() + mouseDragX * state.getSpeedFactor()));
    }
}
