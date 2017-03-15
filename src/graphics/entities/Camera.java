package graphics.entities;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public abstract class Camera {

    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 10000f;

    protected Vector3f position = new Vector3f(0, 0, 0);
    protected float pitch;
    protected float yaw;
    protected float roll;
    protected int fieldOfView;
    protected Matrix4f projectionMatrix;

    public Camera(int fieldOfView) {
        this.fieldOfView = fieldOfView;
        createProjectionMatrix();
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public int getFieldOfView() {
        return fieldOfView;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float yScale = (float) (1f / Math.tan(Math.toRadians(fieldOfView / 2f)));
        float xScale = yScale / aspectRatio;
        float frustumLength = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength);
        projectionMatrix.m33 = 0;
    }

    public Matrix4f getViewMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.translate(position.negate(null), matrix, matrix);
        return matrix;
    }

    public void invertPitch() {
        pitch = -pitch;
    }
}
