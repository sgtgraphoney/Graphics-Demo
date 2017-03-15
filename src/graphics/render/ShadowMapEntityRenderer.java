package graphics.render;

import graphics.entities.Camera;
import graphics.entities.Entity;
import graphics.environment.Environment;
import graphics.materials.BasicObjectMaterial;
import graphics.models.BasicModel;
import graphics.models.RawModel;
import graphics.shaders.BasicShader;
import graphics.shaders.ShadowShader;
import graphics.tools.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;

public class ShadowMapEntityRenderer {

    private ShadowShader shader;
    private Matrix4f projectionViewMatrix;

    public ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
        this.shader = shader;
        this.projectionViewMatrix = projectionViewMatrix;
    }

    public void render(Environment environment) {

        shader.startUsing();

        for (BasicModel model : environment.getEntities().keySet()) {

            RawModel rawModel = model.getRawModel();
            BasicObjectMaterial material = model.getMaterial();
            GL30.glBindVertexArray(rawModel.getVertexArrayObjectID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);

            List<Entity> batch = environment.getEntities().get(model);
            for (Entity entity : batch) {

                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getDiffuseMapID());

                if (material.hasTransparency()) {
                    GL11.glDisable(GL11.GL_CULL_FACE);
                }

                Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, Maths.createTransformationMatrix(entity.getPosition(),
                        entity.getRotation(), entity.getScale()), null);
                shader.loadMvpMatrix(mvpMatrix);

                int[] offsets = rawModel.getSgOffsets();
                int globalOffset = 0;
                for (int offset : offsets) {
                    if (offset != 0) {
                        GL11.glDrawElements(GL11.GL_TRIANGLES, offset, GL11.GL_UNSIGNED_INT, globalOffset * 4);
                        globalOffset += offset;
                    }
                }

                if (material.hasTransparency()) {
                    GL11.glEnable(GL11.GL_CULL_FACE);
                }

            }

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
        }

        GL30.glBindVertexArray(0);

        shader.stopUsing();
    }

}
