package ch.epfl.visualComputing.Entites;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

public class Cylinder {
    private static final int cylinderResolution = 40;

    private final PApplet ctx;
    private final PShape cylinder;
    private PVector position;
    public final float size;

    public Cylinder(PApplet context, float posX, float posY, float s) {
        ctx = context;
        this.cylinder = context.createShape(PConstants.GROUP);
        this.size = s;
        position = new PVector(posX, posY);
        float angle;
        float[] x = new float[cylinderResolution + 1];
        float[] y = new float[cylinderResolution + 1];

        //get the x and y position on a circle for all the sides
        for (int i = 0; i < x.length; i++) {
            angle = (ctx.TWO_PI / cylinderResolution) * i;
            x[i] = ctx.sin(angle) * size/2;
            y[i] = ctx.cos(angle) * size/2;
        }

        PShape side = ctx.createShape();
        side.beginShape(PConstants.QUAD_STRIP);
        //draw the border of the cylinder
        int cHeight = 40;
        for (int i = 0; i < x.length; i++) {
            side.vertex(x[i], y[i], 0);
            side.vertex(x[i], y[i], cHeight);
        }
        side.endShape();

        PShape bottom = ctx.createShape();
        bottom.beginShape(PConstants.TRIANGLE_FAN);
        bottom.vertex(0, 0, 0);
        for (int i = 0; i < x.length; i++) {
            bottom.vertex(x[i], y[i], 0);
        }
        bottom.endShape();

        PShape top = ctx.createShape();
        top.beginShape(PConstants.TRIANGLE_FAN);
        top.vertex(0, 0, cHeight);
        for (int i = 0; i < x.length; i++) {
            top.vertex(x[i], y[i], cHeight);
        }
        top.endShape();

        cylinder.addChild(side);
        cylinder.addChild(top);
        cylinder.addChild(bottom);
    }

    PVector getPosition() {
        return new PVector(position.x, position.y, position.z);
    }

    public void draw(int board) {
        ctx.pushMatrix();
        ctx.translate(board - position.y, 0, position.x);
        ctx.rotateX(-PConstants.PI/2);
        ctx.shape(cylinder);
        ctx.popMatrix();
    }
}