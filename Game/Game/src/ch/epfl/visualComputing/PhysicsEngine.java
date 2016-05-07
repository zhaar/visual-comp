package ch.epfl.visualComputing;

import ch.epfl.visualComputing.Entites.Ball;
import ch.epfl.visualComputing.Entites.Cylinder;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class PhysicsEngine {

    public static final float gravityConstant = 1;

    public static float clampAngle(float value) {
        return clamp(value, -PConstants.PI/3f, PConstants.PI/3f);
    }

    public static float clamp(float value, float min, float max) {
        return value < min? min : (value > max ? max : value);
    }

    public static void applyForces(GameState s, Ball b) {
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

    public static PVector computeFriction(Ball b) {
        float normalForce = 1;
        float mu = 0.005f;
        float frictionMagnitude = normalForce * mu;
        PVector friction = b.getVelocity();
        friction.mult(-1);
        friction.normalize();
        friction.mult(frictionMagnitude);
        return friction;
    }

    public static PVector computeSpeedFromAngle(float x, float y) {
        PVector gravityForce = new PVector();
        gravityForce.x = PApplet.sin(PApplet.radians(x)) * gravityConstant;
        gravityForce.y = PApplet.sin(PApplet.radians(y)) * gravityConstant;
        return gravityForce;
    }

    public static Ball updateBallState(GameState s, Ball b) {
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
}
