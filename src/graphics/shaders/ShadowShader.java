package graphics.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

public class ShadowShader extends ShaderProgram {

    private static final String VERTEX_SHADER_FILE = "src/graphics/shaders/ShadowVertexShader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "src/graphics/shaders/ShadowFragmentShader.glsl";

    private int location_mvpMatrix;

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
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "in_textureCoordinates");
    }

    @Override
    protected void getAllUniformLocations() {
        location_mvpMatrix = super.getUniformLocation("mvpMatrix");

    }

    public void loadMvpMatrix(Matrix4f mvpMatrix){
        super.loadMatrix4f(location_mvpMatrix, mvpMatrix);
    }

}
