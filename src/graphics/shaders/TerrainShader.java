package graphics.shaders;

import graphics.entities.SpotLight;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;

import static graphics.render.DisplaySettings.SUPPORTED_LIGHTS_COUNT;

public class TerrainShader extends ShaderProgram {

    private static final String VERTEX_SHADER_FILE = "src/graphics/shaders/TerrainVertexShader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "src/graphics/shaders/TerrainFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_diffuseColor;
    private int location_specularColor;
    private int location_reflectivity;
    private int location_shineDamper;
    private int location_mapScale;
    private int location_diffuseMap;
    private int location_normalMap;
    private int location_specularMap;
    private int location_displacementMap;
    private int location_plane;

    private boolean useNormalMap;
    private boolean useSpecularMap;
    private boolean useDisplacementMap;

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

    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_diffuseColor = super.getUniformLocation("diffuseColor");
        location_specularColor = super.getUniformLocation("specularColor");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_mapScale = super.getUniformLocation("mapScale");
        location_plane = super.getUniformLocation("plane");
        location_diffuseMap = super.getUniformLocation("diffuseMap");
        if (useNormalMap) {
            location_normalMap = super.getUniformLocation("normalMap");
        }
        if (useSpecularMap) {
            location_specularMap = super.getUniformLocation("specularMap");
        }
        if (useDisplacementMap) {
            location_displacementMap = super.getUniformLocation("displacementMap");
        }
    }

    public void connectTextureUnits() {
        super.loadInt(location_diffuseMap, 0);
        if (useNormalMap) {
            super.loadInt(location_normalMap, 1);
        }
        if (useSpecularMap) {
            super.loadInt(location_specularMap, 2);
        }
        if (useDisplacementMap) {
            super.loadInt(location_displacementMap, 3);
        }
    }

    public void setUseNormalMap(boolean useNormalMap) {
        this.useNormalMap = useNormalMap;
    }

    public void setUseSpecularMap(boolean useSpecularMap) {
        this.useSpecularMap = useSpecularMap;
    }

    public void setUseDisplacementMap(boolean useDisplacementMap) {
        this.useDisplacementMap = useDisplacementMap;
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix4f(location_projectionMatrix, matrix);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix4f(location_transformationMatrix, matrix);
    }

    public void loadViewMatrix(Matrix4f matrix) {
        super.loadMatrix4f(location_viewMatrix, matrix);
    }

    public void loadColors(Vector3f diffuseColor, Vector3f specularColor) {
        super.loadVector3f(location_diffuseColor, diffuseColor);
        super.loadVector3f(location_specularColor, specularColor);
    }

    public void loadShineVariables(float shineDamper, float reflectivity) {
        super.loadFloat(location_shineDamper, shineDamper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadMapScale(float mapScale) {
        super.loadFloat(location_mapScale, mapScale);
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

    public void loadClipPlane(Vector4f plane) {
        super.loadVector4f(location_plane, plane);
    }
}
