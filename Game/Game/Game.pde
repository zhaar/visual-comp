void settings() {
      size(600, 600, P3D);
}

GameState state = new GameState(300);
Ball ball = new Ball(20);

void setup() {
  noStroke();
  ball.setPosition(new PVector(150, 150));

}

import java.util.List;
import java.util.ArrayList;

interface Drawable {
  void draw();
}
//  void draw() {
//    camera(width/2.0, height / 2.0, height/2.0 / tan(radians(30)), width/2.0, height/2.0, 0, 0, -1, 0);
//    directionalLight(50, 100, 125, 0.5, -0.5, 0);
//    ambientLight(102, 102, 102);
//    background(200);
//    translate(width/2, height/2, 0);
//    rotateX(angleX);
//    rotateY(angleY);
//    rotateZ(angleZ);
//    box(300, 5, 300); 
//    translate(-width/2, -height/2, 0);

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
      println("original velocity: " + b.getVelocity());
      println("bounced velocity: " + b.bounceOn(e));
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
    //b.draw();
  }
  state.draw(ball);
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

void keyPressed() {
  if (keyCode == SHIFT) {
    state.editMode = true;
  }
}

void keyReleased() {
  if (keyCode == SHIFT) {
    state.editMode = false;
  }
}

void mouseDragged() {
  mouseDragX = (mouseX - pmouseX);
  mouseDragY = (mouseY - pmouseY);
  state.setAngleX(clampAngle(state.getAngleX() + mouseDragY * state.getSpeedFactor()));
  state.setAngleZ(clampAngle(state.getAngleZ() + mouseDragX * state.getSpeedFactor()));
}