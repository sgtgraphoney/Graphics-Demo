package graphics.materials;

import graphics.shaders.TerrainShader;
import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;

public class TerrainMaterial {

    private static final float UNITS_PER_TEXTURE = 10;

    private TerrainShader shader = new TerrainShader();

    private Vector3f diffuseColor = new Vector3f(0.5f, 0.5f, 0.5f);
    private Vector3f specularColor = new Vector3f(1, 1, 1);

    private int diffuseMapID;
    private int normalMapID;
    private int specularMapID;
    private int displacementMapID;

    private float mapScale = 1;
    private float shineDamper = 1;
    private float reflectivity = 0;

    public void loadParamsToShader(int terrainSize) {
        shader.loadColors(diffuseColor, specularColor);
        shader.loadShineVariables(shineDamper, reflectivity);
        shader.loadMapScale(mapScale * (float) terrainSize / UNITS_PER_TEXTURE);
    }

    public TerrainShader getShader() {
        return shader;
    }

    public void setShader(TerrainShader shader) {
        this.shader = shader;
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

    public int getDiffuseMapID() {
        return diffuseMapID;
    }

    public void setDiffuseMapID(int diffuseMapID) {
        this.diffuseMapID = diffuseMapID;
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

    public float getMapScale() {
        return mapScale;
    }

    public void setMapScale(float mapScale) {
        this.mapScale = mapScale;
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
