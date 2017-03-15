package graphics.shaders;

import graphics.entities.Camera;
import graphics.tools.Maths;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

public class SkyboxShader extends ShaderProgram {

    private static final String VERTEX_SHADER_FILE = "src/graphics/shaders/SkyboxVertexShader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "src/graphics/shaders/SkyboxFragmentShader.glsl";

    private int location_projectionMatrix;
    private int location_viewMatrix;

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
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix4f(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = camera.getViewMatrix();
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        super.loadMatrix4f(location_viewMatrix, matrix);
    }
}
