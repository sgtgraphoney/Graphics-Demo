package graphics.shaders;

import graphics.entities.SpotLight;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;

import static graphics.render.DisplaySettings.SUPPORTED_LIGHTS_COUNT;

public class BasicShader extends ShaderProgram {

    private static final String VERTEX_SHADER_FILE = "src/graphics/shaders/BasicVertexShader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "src/graphics/shaders/BasicFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_diffuseColor;
    private int location_specularColor;
    private int location_hasTransparency;
    private int location_useFakeLighting;
    private int location_reflectivity;
    private int location_shineDamper;
    private int location_diffuseMap;
    private int location_normalMap;
    private int location_specularMap;
    private int location_displacementMap;
    private int location_plane;
    private int location_toShadowMapSpace;
    private int location_shadowMap;

    private boolean useDiffuseMap;
    private boolean useNormalMap;
    private boolean useSpecularMap;
    private boolean useDisplacementMap;

    protected int loadShader(int type) {
        String fileName = null;
        if (type == GL20.GL_VERTEX_SHADER) {
            fileName = VERTEX_SHADER_FILE;
        } else if (type == GL20.GL_FRAGMENT_SHADER) {
            fileName = FRAGMENT_SHADER_FILE;
        }

        StringBuilder shaderSource = loadShaderSource(fileName);

        if (useDiffuseMap || useNormalMap || useSpecularMap || useDisplacementMap) {
            shaderSource.insert(DEFINE_OFFSET, "#define FLAG_TEXTURE\n");
        }
        if (useDiffuseMap) {
            shaderSource.insert(DEFINE_OFFSET, "#define FLAG_DIFFUSE_MAP\n");
        }
        if (useNormalMap) {
            shaderSource.insert(DEFINE_OFFSET, "#define FLAG_NORMAL_MAP\n");
        }
        if (useSpecularMap) {
            shaderSource.insert(DEFINE_OFFSET, "#define FLAG_SPECULAR_MAP\n");
        }
        if (useDisplacementMap) {
            shaderSource.insert(DEFINE_OFFSET, "#define FLAG_DISPLACEMENT_MAP\n");
        }

        return configureShader(shaderSource.toString(), type);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "normal");
        if (useDiffuseMap || useNormalMap || useSpecularMap || useDisplacementMap) {
            super.bindAttribute(2, "textureCoordinates");
        }
        if (useNormalMap) {
            super.bindAttribute(3, "tangent");
        }
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_diffuseColor = super.getUniformLocation("diffuseColor");
        location_specularColor = super.getUniformLocation("specularColor");
        location_hasTransparency = super.getUniformLocation("hasTransparency");
        location_useFakeLighting = super.getUniformLocation("useFakeLightning");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_plane = super.getUniformLocation("plane");
        if (useDiffuseMap) {
            location_diffuseMap = super.getUniformLocation("diffuseMap");
        }
        if (useNormalMap) {
            location_normalMap = super.getUniformLocation("normalMap");
        }
        if (useSpecularMap) {
            location_specularMap = super.getUniformLocation("specularMap");
        }
        if (useDisplacementMap) {
            location_displacementMap = super.getUniformLocation("displacementMap");
        }
        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        location_shadowMap = super.getUniformLocation("shadowMap");
    }

    public void connectTextureUnits() {
        super.loadInt(location_shadowMap, 0);
        if (useDiffuseMap) {
            super.loadInt(location_diffuseMap, 1);
        }
        if (useNormalMap) {
            super.loadInt(location_normalMap, 2);
        }
        if (useSpecularMap) {
            super.loadInt(location_specularMap, 3);
        }
        if (useDisplacementMap) {
            super.loadInt(location_displacementMap, 4);
        }
    }

    public void setUseDiffuseMap(boolean useDiffuseMap) {
        this.useDiffuseMap = useDiffuseMap;
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

    public void loadTransparencyVariable(boolean hasTransparency) {
        super.loadBoolean(location_hasTransparency, hasTransparency);
    }

    public void loadFakeLightningVariable(boolean useFakeLightning) {
        super.loadBoolean(location_useFakeLighting, useFakeLightning);
    }

    public void loadShineVariables(float shineDamper, float reflectivity) {
        super.loadFloat(location_shineDamper, shineDamper);
        super.loadFloat(location_reflectivity, reflectivity);
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
