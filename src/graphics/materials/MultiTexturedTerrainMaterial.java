package graphics.materials;

import graphics.shaders.MultiTexturedTerrainShader;
import org.lwjgl.util.vector.Vector3f;

public class MultiTexturedTerrainMaterial {

    public static final int MATERIALS_COUNT = 4;
    public static final float UNITS_PER_TEXTURE = 10;

    private MultiTexturedTerrainShader shader = new MultiTexturedTerrainShader();
    private int blendMapID;

    public static class MaterialConfiguration {

        private Vector3f diffuseColor = new Vector3f(0.5f, 0.5f, 0.5f);
        private Vector3f specularColor = new Vector3f(1, 1, 1);

        private int diffuseMapID;
        private int normalMapID;
        private int specularMapID;
        private int displacementMapID;

        private float mapScale = 1;
        private float shineDamper = 1;
        private float reflectivity = 0;

        private boolean useNormalMap;
        private boolean useSpecularMap;
        private boolean useDisplacementMap;

        public MaterialConfiguration(int diffuseMapID) {
            this.diffuseMapID = diffuseMapID;
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
            useNormalMap = normalMapID > 0;
        }

        public int getSpecularMapID() {
            return specularMapID;
        }

        public void setSpecularMapID(int specularMapID) {
            this.specularMapID = specularMapID;
            useSpecularMap = specularMapID > 0;
        }

        public int getDisplacementMapID() {
            return displacementMapID;
        }

        public void setDisplacementMapID(int displacementMapID) {
            this.displacementMapID = displacementMapID;
            useDisplacementMap = displacementMapID > 0;
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

        public boolean isUseNormalMap() {
            return useNormalMap;
        }

        public boolean isUseSpecularMap() {
            return useSpecularMap;
        }

        public boolean isUseDisplacementMap() {
            return useDisplacementMap;
        }
    }

    private MaterialConfiguration[] configs = new MaterialConfiguration[MATERIALS_COUNT];

    public MultiTexturedTerrainMaterial(int[] diffuseMaps, int blendMapID) {
        this.blendMapID = blendMapID;
        for (int i = 0; i < MATERIALS_COUNT; i++) {
            configs[i] = new MaterialConfiguration(diffuseMaps[i]);
        }
    }

    public MultiTexturedTerrainShader getShader() {
        return shader;
    }

    public int getBlendMapID() {
        return blendMapID;
    }

    public void setBlendMapID(int blendMapID) {
        this.blendMapID = blendMapID;
    }

    public MaterialConfiguration[] getConfigs() {
        return configs;
    }

    public void setConfigs(MaterialConfiguration[] configs) {
        this.configs = configs;
    }

    public void loadParamsToShader(int terrainSize) {
        shader.loadMaterialConfigurations(configs, terrainSize);
    }
}
