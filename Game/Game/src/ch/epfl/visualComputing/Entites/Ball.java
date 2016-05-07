package ch.epfl.visualComputing.Entites;


import processing.core.PApplet;
import processing.core.PVector;

public class Ball {

    private final PApplet ctx;
    private PVector position = new PVector();
    private final int size;
    private PVector velocity = new PVector(0,0);

    public Ball(PApplet context, int size) {
        this.ctx = context;
        this.size = size;
    }
    public void draw(int board) {
        ctx.noStroke();
        ctx.pushMatrix();
        ctx.translate(board - position.y, size/2, position.x);
        ctx.sphere(size/2f);
        ctx.popMatrix();
    }

    public void invertXVelocity() {
        this.velocity.x *= -1;
    }
    public void invertYVelocity() {
        this.velocity.y *= -1;
    }

    public PVector getVelocity() { return new PVector(velocity.x, velocity.y, velocity.z); }
    public void setVelocity(PVector v) { this.velocity = v; }

    public PVector getPosition() { return position.copy(); }
    public void setPosition(PVector p) { this.position = p; }
    public void setPosX(float x) { position.x = x; }
    public void setPosY(float y) { position.y = y; }

    public boolean intersects(Cylinder c) {
        PVector difference = c.getPosition().copy().sub(this.getPosition().copy());
        return difference.mag() - c.size/2 - this.size/2 <= 0;
    }

    public PVector bounceOn(Cylinder c) {
        PVector normal = this.getPosition().sub(c.getPosition()).normalize();
        float product = normal.dot(this.velocity) * 2f;
        return this.velocity.copy().sub(normal.mult(product));
    }
}