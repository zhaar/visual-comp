void settings() {
      size(500, 500, P3D);
}
    void setup() {
      noStroke();
}

float angleX = PI/8;
float angleY = PI/8;
float angleZ = PI/8;

void draw() {
      background(200);
      camera(width/2, height/2, 350, 250, 250, 250, 0, 1, 0);
      directionalLight(50, 100, 125, 0, 1, 0);
      ambientLight(102, 102, 102);
      translate(width/2, height/2, 0);
      rotateX(angleX);
      rotateY(angleY);
      rotateZ(angleZ);
      box(100, 30, 100);
      //translate(100, 0, 0);
      //sphere(50);
}

int mouseDragY, mouseDragX;

void mousePressed() {
  mouseDragX = mouseDragY = 0;  // The drag starts here
}

float clampAngle(float value) {
  if (value < -PI/3) {
    return -PI/3;
  } else if (value > PI/3) {
    return PI/3;
  } else {
    return value;
  }
}

void mouseDragged() {
  mouseDragX = (mouseX - pmouseX);
  mouseDragY = (mouseY - pmouseY);
  angleY = clampAngle(angleY + mouseDragX * 0.01f);
  angleZ = clampAngle(angleZ + mouseDragY * 0.01f);
}