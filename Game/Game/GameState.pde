
public class GameState {
  public boolean editMode;
  private float angleX;
  private float angleY;
  private float angleZ;
  private float inputFactorSpeed = 1f;
  private static final float scaleFactor = 0.01f;
  public final int boardSize;
  final List<Cylinder> objects = new ArrayList<Cylinder>();
  
  public GameState(int s) {
    this.boardSize = s;
  };
 
  void updateSpeedFactor(float speed) {
    float amount = speed /10;
    this.inputFactorSpeed = clamp(inputFactorSpeed + amount, 0.2, 1.5);
  }
  
  float getAngleX() { return angleX; }
  void setAngleX(float x) { this.angleX = x; }
  float getAngleY() { return angleY; }
  void setAngleY(float y) { this.angleY = y; }
  float getAngleZ() { return angleZ; }
  void setAngleZ(float z) { this.angleZ = z; }
  float getSpeedFactor() { return inputFactorSpeed * scaleFactor; }
  
  void draw(Ball b) {
    if (editMode) {
      camera(width/2.0, height / 2.0, height/2.0 / tan(radians(90)), width/2.0, height/2.0, 0, 0, -1, 0);
    } else {
      camera(width/2.0, height / 2.0, height/2.0 / tan(radians(30)), width/2.0, height/2.0, 0, 0, -1, 0);
    }
    directionalLight(50, 100, 125, 0.5, -0.5, 0);
    ambientLight(102, 102, 102);
    pushMatrix();
    translate(width/2, height/2, 0);
    rotateX(angleX);
    rotateY(angleY);
    rotateZ(angleZ);
    box(boardSize, 5, boardSize); 
    translate(-boardSize/2, 5, -boardSize/2);
    if (!editMode) {
      b.draw(boardSize);     
    }
    for (Cylinder e : objects) {
      e.draw(); 
    }
    popMatrix();
  }
}