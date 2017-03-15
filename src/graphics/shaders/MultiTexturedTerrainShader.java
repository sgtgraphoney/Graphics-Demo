package graphics.shaders;

import graphics.entities.SpotLight;
import graphics.materials.MultiTexturedTerrainMaterial;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.FloatBuffer;
import java.util.List;

import static graphics.materials.MultiTexturedTerrainMaterial.MATERIALS_COUNT;
import static graphics.materials.MultiTexturedTerrainMaterial.UNITS_PER_TEXTURE;
import static graphics.render.DisplaySettings.SUPPORTED_LIGHTS_COUNT;

public class MultiTexturedTerrainShader extends ShaderProgram {

    private static final String VERTEX_SHADER_FILE = "src/graphics/shaders/MTTerrainVertexShader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "src/graphics/shaders/MTTerrainFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_blendMap;
    private int location_plane;
    private int[] location_diffuseMap;
    private int[] location_normalMap;
    private int[] location_specularMap;
    private int[] location_displacementMap;
    private int location_toShadowMapSpace;
    private int location_shadowMap;

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
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_plane = super.getUniformLocation("plane");
        location_blendMap = super.getUniformLocation("blendMap");
        location_diffuseMap = new int[MATERIALS_COUNT];
        location_normalMap = new int[MATERIALS_COUNT];
        location_specularMap = new int[MATERIALS_COUNT];
        location_displacementMap = new int[MATERIALS_COUNT];
        for (int i = 0; i < MATERIALS_COUNT; i++) {
            location_diffuseMap[i] = super.getUniformLocation("diffuseMap[" + i + "]");
            location_normalMap[i] = super.getUniformLocation("normalMap[" + i + "]");
            location_specularMap[i] = super.getUniformLocation("specularMap[" + i + "]");
            location_displacementMap[i] = super.getUniformLocation("displacementMap[" + i + "]");
        }
        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        location_shadowMap = super.getUniformLocation("shadowMap");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "normal");
        super.bindAttribute(2, "textureCoordinates");
        super.bindAttribute(3, "tangent");
    }

    public void connectTextureUnits() {
        int unit = 0;
        super.loadInt(location_shadowMap, unit++);
        super.loadInt(location_blendMap, unit++);
        for (int i = 0; i < MATERIALS_COUNT; i++) {
            super.loadInt(location_diffuseMap[i], unit++);
            super.loadInt(location_normalMap[i], unit++);
            super.loadInt(location_specularMap[i], unit++);
            super.loadInt(location_displacementMap[i], unit++);
        }
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

    public void loadMaterialConfigurations(MultiTexturedTerrainMaterial.MaterialConfiguration[] configurations,
                                           int terrainSize) {
        int location = 24;
        for (int i = 0; i < MATERIALS_COUNT; i++) {
            super.loadVector3f(location++, configurations[i].getDiffuseColor());
            super.loadVector3f(location++, configurations[i].getSpecularColor());
            super.loadFloat(location++, configurations[i].getMapScale() * (float) terrainSize / UNITS_PER_TEXTURE);
            super.loadFloat(location++, configurations[i].getShineDamper());
            super.loadFloat(location++, configurations[i].getReflectivity());
            super.loadBoolean(location++, configurations[i].isUseNormalMap());
            super.loadBoolean(location++, configurations[i].isUseSpecularMap());
            super.loadBoolean(location++, configurations[i].isUseDisplacementMap());
        }
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

    public void loadToShadowSpaceMatrix(Matrix4f matrix) {
        super.loadMatrix4f(location_toShadowMapSpace, matrix);
    }
}
