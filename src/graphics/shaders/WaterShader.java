package graphics.shaders;

import graphics.entities.Camera;
import graphics.entities.SpotLight;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

import static graphics.render.DisplaySettings.SUPPORTED_LIGHTS_COUNT;

public class WaterShader extends ShaderProgram {

    private static final String VERTEX_SHADER_FILE = "src/graphics/shaders/WaterVertexShader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "src/graphics/shaders/WaterFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_reflectionTexture;
    private int location_refractionTexture;
    private int location_dudvMap;
    private int location_normalMap;
    private int location_depthMap;
    private int location_moveFactor;
    private int location_cameraPosition;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_diffuseColor;
    private int location_specularColor;
    private int location_transparency;

    @Override
    protected int loadShader(int type) {
        String fileName = null;
        if (type == GL20.GL_VERTEX_SHADER) {
            fileName = VERTEX_SHADER_FILE;
        } else if (type == GL20.GL_FRAGMENT_SHADER) {
            fileName = FRAGMENT_SHADER_FILE;
        }

        StringBuilder shaderSource = loadShaderSource(fileName);
        return configureShader(shaderSource.toString(), type);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_reflectionTexture = super.getUniformLocation("reflectionTexture");
        location_refractionTexture = super.getUniformLocation("refractionTexture");
        location_dudvMap = super.getUniformLocation("dudvMap");
        location_normalMap = super.getUniformLocation("normalMap");
        location_depthMap = super.getUniformLocation("depthMap");
        location_moveFactor = super.getUniformLocation("moveFactor");
        location_cameraPosition = super.getUniformLocation("cameraPosition");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_diffuseColor = super.getUniformLocation("diffuseColor");
        location_specularColor = super.getUniformLocation("specularColor");
        location_transparency = super.getUniformLocation("transparency");
    }

    public void connectTextureUnits() {
        super.loadInt(location_reflectionTexture, 0);
        super.loadInt(location_refractionTexture, 1);
        super.loadInt(location_dudvMap, 2);
        super.loadInt(location_normalMap, 3);
        super.loadInt(location_depthMap, 4);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix4f(location_projectionMatrix, matrix);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix4f(location_transformationMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        super.loadMatrix4f(location_viewMatrix, camera.getViewMatrix());
        super.loadVector3f(location_cameraPosition, camera.getPosition());
    }

    public void loadMoveFactor(Vector2f factor) {
        super.loadVector2f(location_moveFactor, factor);
    }

    public void loadLights(List<SpotLight> lights) {
        int location = 0;
        for (int i = 0; i < SUPPORTED_LIGHTS_COUNT; i++) {
            if (i < lights.size()) {
                SpotLight light = lights.get(i);
                super.loadVector3f(location++, light.getPosition());
                super.loadVector3f(location++, light.getColor());
                super.loadVector3f(location++, light.getAttenuation());
            } else {
                super.loadVector3f(location++, new Vector3f(0, 0, 0));
                super.loadVector3f(location++, new Vector3f(0, 0, 0));
                super.loadVector3f(location++, new Vector3f(1, 0, 0));
            }
        }
    }

    public void loadShineVariables(float shineDamper, float reflectivity) {
        super.loadFloat(location_shineDamper, shineDamper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadColors(Vector3f diffuseColor, Vector3f specularColor) {
        super.loadVector3f(location_diffuseColor, diffuseColor);
        super.loadVector3f(location_specularColor, specularColor);
    }

    public void loadTransparency(float transparency) {
        super.loadFloat(location_transparency, transparency);
    }
}
