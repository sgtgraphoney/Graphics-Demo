package graphics.materials;

import graphics.shaders.BasicShader;
import org.lwjgl.util.vector.Vector3f;

public class BasicObjectMaterial {

    private BasicShader shader = new BasicShader();

    private Vector3f diffuseColor = new Vector3f(0.5f, 0.5f, 0.5f);
    private Vector3f specularColor = new Vector3f(1, 1, 1);

    private int diffuseMapID;
    private int normalMapID;
    private int specularMapID;
    private int displacementMapID;

    private boolean twoSized;
    private boolean hasTransparency;
    private boolean useFakeLighting;

    private float shineDamper = 1;
    private float reflectivity = 0;

    public void loadParamsToShader() {
        shader.loadColors(diffuseColor, specularColor);
        shader.loadTransparencyVariable(hasTransparency);
        shader.loadFakeLightningVariable(useFakeLighting);
        shader.loadShineVariables(shineDamper, reflectivity);
    }

    public Vector3f getDiffuseColor() {
        return diffuseColor;
    }

    public BasicShader getShader() {
        return shader;
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

    public int getDiffuseMapID() {
        return diffuseMapID;
    }

    public void setDiffuseMapID(int diffuseMapID) {
        this.diffuseMapID = diffuseMapID;
        shader.setUseDiffuseMap(diffuseMapID != 0);
    }

    public int getNormalMapID() {
        return normalMapID;
    }

    public void setNormalMapID(int normalMapID) {
        this.normalMapID = normalMapID;
        shader.setUseNormalMap(normalMapID != 0);
    }

    public int getSpecularMapID() {
        return specularMapID;
    }

    public void setSpecularMapID(int specularMapID) {
        this.specularMapID = specularMapID;
        shader.setUseSpecularMap(normalMapID != 0);
    }

    public int getDisplacementMapID() {
        return displacementMapID;
    }

    public void setDisplacementMapID(int displacementMapID) {
        this.displacementMapID = displacementMapID;
        shader.setUseDisplacementMap(displacementMapID != 0);
    }

    public boolean isTwoSized() {
        return twoSized;
    }

    public void setTwoSized(boolean twoSized) {
        this.twoSized = twoSized;
    }

    public boolean hasTransparency() {
        return hasTransparency;
    }

    public void setTransparency(boolean transparency) {
        this.hasTransparency = transparency;
    }

    public boolean useFakeLighting() {
        return useFakeLighting;
    }

    public void setFakeLighting(boolean fakeLighting) {
        this.useFakeLighting = fakeLighting;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }
}
