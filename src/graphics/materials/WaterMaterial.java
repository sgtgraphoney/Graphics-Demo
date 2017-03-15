package graphics.materials;

import graphics.shaders.WaterShader;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class WaterMaterial {

    private WaterShader shader = new WaterShader();

    private Vector3f diffuseColor = new Vector3f(0, 0.2f, 0.3f);
    private Vector3f specularColor = new Vector3f(1, 1, 1);

    private int dudvTexture;
    private int normalMap;

    private int shineDamper = 20;
    private int reflectivity = 1;
    private float transparency = 2f;

    private Vector2f waveSpeed = new Vector2f(0.05f, 0.05f);

    public void loadParamsToShader() {
        shader.loadColors(diffuseColor, specularColor);
        shader.loadShineVariables(shineDamper, reflectivity);
        shader.loadTransparency(transparency);
    }

    public WaterShader getShader() {
        return shader;
    }

    public Vector3f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector3f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vector3f getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(Vector3f specularColor) {
        this.specularColor = specularColor;
    }

    public int getDudvTexture() {
        return dudvTexture;
    }

    public void setDudvTexture(int dudvTexture) {
        this.dudvTexture = dudvTexture;
    }

    public int getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(int normalMap) {
        this.normalMap = normalMap;
    }

    public int getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(int shineDamper) {
        this.shineDamper = shineDamper;
    }

    public int getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(int reflectivity) {
        this.reflectivity = reflectivity;
    }

    public Vector2f getWaveSpeed() {
        return waveSpeed;
    }

    public void setWaveSpeed(Vector2f waveSpeed) {
        this.waveSpeed = waveSpeed;
    }

    public float getTransparency() {
        return transparency;
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }
}
