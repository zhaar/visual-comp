import ch.epfl.visualComputing.ImageProcessing;

void settings() {
      size(600, 600, P3D);
}

ImageProcessing proc;
GameState state = new GameState(300);
Ball ball = new Ball(20);

void setup() {
  noStroke();
  proc = new ImageProcessing("/Users/zephyz/Projects/processing/videotest/data/testvideo.mp4");
  String[] args = { "Image processing window"};
  //PApplet.runSketch(args, proc);
  ball.setPosition(new PVector(150, 150));

}

import java.util.List;
import java.util.ArrayList;

interface Drawable {
  void draw();
}

boolean inInterval(float v, float min, float max) {
  return min <= v && v <= max;
}

final float gravityConstant = 1;

PVector computeSpeedFromAngle(float x, float y) {
  PVector gravityForce = new PVector();
  gravityForce.x = sin(radians(x)) * gravityConstant;
  gravityForce.y = sin(radians(y)) * gravityConstant; 
  return gravityForce;
}

PVector computeFriction(Ball b) {
  float normalForce = 1;
  float mu = 0.005;
  float frictionMagnitude = normalForce * mu;
  PVector friction = b.getVelocity();
  friction.mult(-1);
  friction.normalize();
  friction.mult(frictionMagnitude);
  return friction;
}

void applyForces(GameState s, Ball b) {
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

Ball updateBallState(GameState s, Ball b) {
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

void draw() {
  background(200);
  
  if (!state.editMode) {
    applyForces(state, ball);
    updateBallState(state, ball);
  }
  state.draw(ball);
  PVector angle = proc.getRotation();
  state.setAngleX(clampAngle(angle.x));
  state.setAngleY(clampAngle(angle.y));
}

void placeObject(GameState s, float x, float y) {
  s.objects.add(new Cylinder(x, y, 30));
}

int mouseDragY, mouseDragX;

void mousePressed() {
  if (!state.editMode) {
    mouseDragX = mouseDragY = 0;  // The drag starts here
  } else {
    //here we assume the board takes the entire frame
    //float s =  height/2.0 / tan(radians(30));
    //println("s : " + s);
    //float xCoord = (mouseX * width / state.boardSize) - (width/2);
    //float yCoord = (mouseY * height / state.boardSize) - (height/2);
    //if (inInterval(xCoord, 0, state.boardSize) && inInterval(yCoord, 0, state.boardSize)) {
    //  println("placing at x: " + xCoord);
    //  println("placing at y: " + yCoord);
    //  placeObject(state, xCoord/2, yCoord/2); 
    //} else {
    //  println(xCoord + ", " + yCoord + " not in interval");
    //}
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

float clamp(float value, float min, float max) {
  return value < min? min : (value > max ? max : value);
}

void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  state.updateSpeedFactor(e);
}

void keyPressed() {
  if (keyCode == SHIFT) {
    state.editMode = true;
  } else if (keyCode == BACKSPACE) {
    state = new GameState(300);
    ball = new Ball(20);
    ball.setPosition(new PVector(150, 150));
  }
}

void keyReleased() {
  if (keyCode == SHIFT) {
    state.editMode = false;
  }
}

//void mouseDragged() {
//  mouseDragX = (mouseX - pmouseX);
//  mouseDragY = (mouseY - pmouseY);
//  state.setAngleX(clampAngle(state.getAngleX() + mouseDragY * state.getSpeedFactor()));
//  state.setAngleZ(clampAngle(state.getAngleZ() + mouseDragX * state.getSpeedFactor()));
//}