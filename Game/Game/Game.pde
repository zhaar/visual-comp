void settings() {
      size(500, 500, P3D);
}

GameState state;

void setup() {
  state = new GameState();
  noStroke();
}

import java.util.List;
import java.util.ArrayList;

interface Physical {
  
}

interface Drawable {
  void draw();
}

abstract class Entity implements Physical, Drawable { }
public class Ball extends Entity {
  float x, y, z;
  void draw() {
    
  }
  void setPositions(float x, float y, float z) {
     
  }
  
}

public class GameState implements Drawable{
  private float angleX;
  private float angleY;
  private float angleZ;
  private float inputFactorSpeed = 1f;
  private List<Entity> entities = new ArrayList<Entity>();
  private static final float scaleFactor = 0.01f;
  
  public GameState() {};
 
  void updateSpeedFactor(float speed) {
    float amount = speed /10;
    this.inputFactorSpeed = clamp(inputFactorSpeed + amount, 0.2, 1.5);
  }
  
  List<Entity> getEntities() { return entities; }
  float getAngleX() { return angleX; }
  void setAngleX(float x) { this.angleX = x; }
  float getAngleY() { return angleY; }
  void setAngleY(float y) { this.angleY = y; }
  float getAngleZ() { return angleZ; }
  void setAngleZ(float z) { this.angleZ = z; }
  float getSpeedFactor() { return inputFactorSpeed * scaleFactor; }
      
  void draw() {
    camera(width/2.0, height / 2.0, height/2.0 / tan(radians(30)), width/2.0, height/2.0, 0, 0, -1, 0);
    directionalLight(50, 100, 125, 0.5, -0.5, 0);
    ambientLight(102, 102, 102);
    background(200);
    translate(width/2, height/2, 0);
    rotateX(angleX);
    rotateY(angleY);
    rotateZ(angleZ);
    box(300, 5, 300); 
    translate(-width/2, -height/2, 0);
     textSize(30); 
    fill(255, 0, 0); 
    text("input speed " + inputFactorSpeed, 40, 40, -10);
  }
}

GameState handleInput(GameState prevState) {
  return prevState;
}

GameState handlePhysics(GameState prevState) {
  
  return prevState;  
}

void drawGame(GameState state) {
  state.draw();
  for (Entity e: state.getEntities()) {
    e.draw();
  }
}


void draw() {
  drawGame(handlePhysics(state));
}

int mouseDragY, mouseDragX;

void mousePressed() {
  mouseDragX = mouseDragY = 0;  // The drag starts here
}

float clampAngle(float value) {
  return clamp(value, -PI/3, PI/3);
}

float clamp(float value, float min, float max) {
  return value < min? min : (value > max ? max : value);
}

void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  state.updateSpeedFactor(e);
}

void mouseDragged() {
  mouseDragX = (mouseX - pmouseX);
  mouseDragY = (mouseY - pmouseY);
  state.setAngleX(clampAngle(state.getAngleX() + mouseDragY * state.getSpeedFactor()));
  state.setAngleZ(clampAngle(state.getAngleZ() + mouseDragX * state.getSpeedFactor()));
}