class My2DPoint {
  float x;
  float y;
  My2DPoint(float x, float y) {
    this.x = x;
    this.y = y;
  }
}

class My3DPoint {
  float x, y, z;
  My3DPoint(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}

My2DPoint projectPoint(My3DPoint eye, My3DPoint p) {
   return new My2DPoint((p.x-eye.x)/(1-p.z/eye.z), (p.y - eye.y)/(1-p.z/eye.z));
}

class My2DBox {
  My2DPoint[] s;
  My2DBox(My2DPoint[] s) {
    this.s = s;
  }
  void render(){
    //front side
    stroke(255,0,0);
    line(s[0].x, s[0].y, s[3].x, s[3].y);
    line(s[0].x, s[0].y, s[1].x, s[1].y);
    line(s[1].x, s[1].y, s[2].x, s[2].y);
    line(s[2].x, s[2].y, s[3].x, s[3].y);
    //back side
    stroke(0,0, 255);
    line(s[4].x, s[4].y, s[7].x, s[7].y);
    line(s[7].x, s[7].y, s[6].x, s[6].y);
    line(s[6].x, s[6].y, s[5].x, s[5].y);
    line(s[5].x, s[5].y, s[4].x, s[4].y);
    //connecting edges
    stroke(0,255, 0);
    line(s[0].x, s[0].y, s[4].x, s[4].y);
    line(s[1].x, s[1].y, s[5].x, s[5].y);
    line(s[2].x, s[2].y, s[6].x, s[6].y);
    line(s[3].x, s[3].y, s[7].x, s[7].y);
  }
}

class My3DBox {
 My3DPoint[] p;
 My3DBox(My3DPoint origin, float dimX, float dimY, float dimZ) {
  float x = origin.x;
  float y = origin.y;
  float z = origin.z;
  this.p = new My3DPoint[]{new My3DPoint(x,y+dimY,z+dimZ),
                           new My3DPoint(x,y,z+dimZ),
                           new My3DPoint(x+dimX,y,z+dimZ),
                           new My3DPoint(x+dimX,y+dimY,z+dimZ),
                           new My3DPoint(x,y+dimY,z),
                           origin,
                           new My3DPoint(x+dimX,y,z),
                           new My3DPoint(x+dimX,y+dimY,z)
  };
 }
 My3DBox(My3DPoint[] p) {
  this.p = p; 
 }
}
import java.util.List;
import java.util.ArrayList;
My2DBox projectBox(My3DPoint eye, My3DBox box) {
  List<My2DPoint> points = new ArrayList<My2DPoint>();
  for (My3DPoint p : box.p) {
    points.add(projectPoint(eye, p));
  }
  return new My2DBox(points.toArray(new My2DPoint[points.size()]));
}

float[] homogeneous3DPoint (My3DPoint p) {
  float[] result = {p.x, p.y, p.z , 1};
  return result;
}

My3DPoint euclidian3DPoint (float[] a) {
  My3DPoint result = new My3DPoint(a[0]/a[3], a[1]/a[3], a[2]/a[3]);
  return result;
}

void settings() {
     size(400, 400, P2D);
}
    void setup() {
    }
void draw() {
My3DPoint eye = new My3DPoint(-100, -100, -5000);
My3DPoint origin = new My3DPoint(0, 0, 0); //The first vertex of your cuboid
My3DBox input3DBox = new My3DBox(origin, 100,150,300);
projectBox(eye, input3DBox).render();
}