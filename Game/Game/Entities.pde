
public class Ball {
  private PVector position = new PVector();
  private final int size;
  private PVector velocity = new PVector(0,0);
  
  public Ball(int size) {
    this.size = size;
  }
  void draw(int board) {
    //fill(255, 255, 255);
    noStroke();
    pushMatrix();
    translate(board - position.y, size/2, position.x);
    sphere(size/2f);  
    popMatrix();
  }  
  
  void invertXVelocity() {
    this.velocity.x *= -1; 
  }
  void invertYVelocity() {
    this.velocity.y *= -1; 
  }
  
  public PVector getVelocity() { return new PVector(velocity.x, velocity.y, velocity.z); }
  public void setVelocity(PVector v) { this.velocity = v; }
  
  public PVector getPosition() { return position.copy(); } 
  public void setPosition(PVector p) { this.position = p; } 
  public void setPosX(float x) { position.x = x; }
  public void setPosY(float y) { position.y = y; }
  
  boolean intersects(Cylinder c) {
    PVector difference = c.getPosition().copy().sub(this.getPosition().copy());
    return difference.mag() - c.size/2 - this.size/2 <= 0;
  }
  
  PVector bounceOn(Cylinder c) {
    PVector normal = this.getPosition().sub(c.getPosition()).normalize();
    float product = normal.dot(this.velocity) * 2f;
    return this.velocity.copy().sub(normal.mult(product));
  }
}

public class Cylinder {
  private static final int cylinderResolution = 40;
  private final PShape cylinder = createShape(GROUP);
  private final int cHeight = 40;
  private PVector position;
  public final float size;
  public Cylinder(float posX, float posY, float s) {
    this.size = s;
    position = new PVector(posX, posY);
    float angle;
    float[] x = new float[cylinderResolution + 1];
    float[] y = new float[cylinderResolution + 1];

    //get the x and y position on a circle for all the sides
    for (int i = 0; i < x.length; i++) {
      angle = (TWO_PI / cylinderResolution) * i;
      x[i] = sin(angle) * size/2;
      y[i] = cos(angle) * size/2;
    }

    PShape side = createShape();
    side.beginShape(QUAD_STRIP);
    //draw the border of the cylinder
    for (int i = 0; i < x.length; i++) {
      side.vertex(x[i], y[i], 0);
      side.vertex(x[i], y[i], cHeight);
    }
    side.endShape();

    PShape bottom = createShape();
    bottom.beginShape(TRIANGLE_FAN);
    bottom.vertex(0, 0, 0);
    for (int i = 0; i < x.length; i++) {
      bottom.vertex(x[i], y[i], 0);
    }
    bottom.endShape();

    PShape top = createShape();
    top.beginShape(TRIANGLE_FAN);
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

  
  void draw(int board) {
    //fill(255, 30, 30);
    //stroke(150);
    pushMatrix();
    translate(board - position.y, 0, position.x);
    rotateX(-PI/2);
    shape(cylinder);
    popMatrix();
    //noStroke();
  }
}