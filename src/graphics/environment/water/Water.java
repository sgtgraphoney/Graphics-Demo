package graphics.environment.water;

import graphics.materials.WaterMaterial;
import graphics.models.RawModel;
import graphics.resources.ResourceManager;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Water {

    private static final float[] VERTICES = {
            -1, 0, -1,
            -1, 0, 1,
            1, 0, 1,
            -1, 0, -1,
            1, 0, 1,
            1, 0, -1
    };

    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    private Vector4f clipPlane;

    RawModel rawModel;
    WaterMaterial material;

    public Water(ResourceManager resourceManager, WaterMaterial material, Vector3f position, Vector3f rotation,
                 float scale) {
        this.material = material;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        rawModel = resourceManager.loadRawModelToVAO(VERTICES, 3);
        clipPlane = new Vector4f(0, 1, 0, -position.y);
    }

    public Vector4f getClipPlane() {
        Vector4f plane = new Vector4f(clipPlane);
        plane.w += 5;
        return plane;
    }

    public Vector4f getInvertedClipPlane() {
        Vector4f inverted = new Vector4f();
        clipPlane.negate(inverted);
        inverted.w += 1;
        return inverted;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public WaterMaterial getMaterial() {
        return material;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
