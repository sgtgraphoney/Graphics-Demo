package graphics.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

public class GUIShader extends ShaderProgram {

    private static final String VERTEX_SHADER_FILE = "src/graphics/shaders/GUIVertexShader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "src/graphics/shaders/GUIFragmentShader.glsl";

    private int location_transformationMatrix;

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix4f(location_transformationMatrix, matrix);
    }

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
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }


}
