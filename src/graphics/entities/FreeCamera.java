package graphics.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FreeCamera extends Camera {

    private float mouseSensitivity = 0.12f;
    private float speed = 100;
    private float boost = 3;

    public FreeCamera(int fieldOfView) {
        super(fieldOfView);
    }

    public float getMouseSensitivity() {
        return mouseSensitivity;
    }

    public void setMouseSensitivity(float mouseSensitivity) {
        this.mouseSensitivity = mouseSensitivity;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBoost() {
        return boost;
    }

    public void setBoost(float boost) {
        this.boost = boost;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void move(float frameTimeSeconds) {

        float pitchCos = (float) Math.cos(Math.toRadians(pitch));
        float pitchSin = (float) Math.sin(Math.toRadians(pitch));
        float yawCos = (float) Math.cos(Math.toRadians(yaw));
        float yawSin = (float) Math.sin(Math.toRadians(yaw));

        if (Mouse.isButtonDown(1)) {

            float dPitch = Mouse.getDY() * mouseSensitivity;
            float dYaw = Mouse.getDX() * mouseSensitivity * (pitchCos / 2 + 0.5f);

            if (pitch + dPitch > 90) {
                dPitch = 90 - pitch;
            } else if (pitch + dPitch < -90) {
                dPitch = -90 - pitch;
            }

            pitch += dPitch;
            yaw -= dYaw;
        }

        float x = 0, z = 0, localSpeed = speed;

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            localSpeed *= boost;
        }

        localSpeed *= frameTimeSeconds;

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            z += localSpeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            z -= localSpeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            x -= localSpeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            x += localSpeed;
        }

        position.x += z * yawSin * pitchCos + x * yawCos;
        position.z += -z * yawCos * pitchCos + x * yawSin;
        position.y -= z * pitchSin;

    }

}
