
public class Ball {
  private PVector position = new PVector();
  private final int size;
  private PVector forces = new PVector();
  private float frictionFactor;
  private PVector velocity = new PVector(0,0);
  
  public Ball(int size) {
    this.size = size;
  }
  void draw() {
    fill(255, 255, 255);
    ellipse(position.x, position.y, size, size);
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

public class Cylinder implements Drawable {
  private PVector position;
  public final float size;
  public Cylinder(float x, float y, float s) {
    this.size = s;
    position = new PVector(x, y);
  }
  
  PVector getPosition() {
    return new PVector(position.x, position.y, position.z);
  }
  
  void draw() {
    fill(255, 30, 30);
    ellipse(position.x, position.y, size, size);  
  }