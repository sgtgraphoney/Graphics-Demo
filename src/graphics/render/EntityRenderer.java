package graphics.render;

import com.sun.istack.internal.Nullable;
import graphics.entities.Camera;
import graphics.entities.DirectLight;
import graphics.entities.Entity;
import graphics.environment.Environment;
import graphics.materials.BasicObjectMaterial;
import graphics.models.BasicModel;
import graphics.models.RawModel;
import graphics.resources.ResourceManager;
import graphics.shaders.BasicShader;
import graphics.tools.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static graphics.resources.ResourceLoader.SMOOTHING_GROUPS_COUNT;

public class EntityRenderer {

    public void render(Environment environment, Camera camera, Vector4f clipPlane, Matrix4f toShadowSpace) {
        for (BasicModel model : environment.getEntities().keySet()) {

            BasicObjectMaterial material = model.getMaterial();
            BasicShader shader = material.getShader();
            shader.startUsing();

            shader.loadProjectionMatrix(camera.getProjectionMatrix());
            shader.loadViewMatrix(camera.getViewMatrix());
            shader.loadClipPlane(clipPlane);
            shader.loadToShadowSpaceMatrix(toShadowSpace);

            shader.connectTextureUnits();

            RawModel rawModel = model.getRawModel();
            GL30.glBindVertexArray(rawModel.getVertexArrayObjectID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(3);

            int diffuse = material.getDiffuseMapID();
            int normal = material.getNormalMapID();
            int specular = material.getSpecularMapID();
            int displacement = material.getDisplacementMapID();

            boolean hasTextureCoordinates = diffuse != 0 || normal != 0 || specular != 0 || displacement != 0;
            if (hasTextureCoordinates) {
                GL20.glEnableVertexAttribArray(2);
            }

            if (diffuse != 0) {
                GL13.glActiveTexture(GL13.GL_TEXTURE1);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, diffuse);
            }
            if (normal != 0) {
                GL13.glActiveTexture(GL13.GL_TEXTURE2);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal);
            }
            if (specular != 0) {
                GL13.glActiveTexture(GL13.GL_TEXTURE3);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, specular);
            }
            if (displacement != 0) {
                GL13.glActiveTexture(GL13.GL_TEXTURE4);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, displacement);
            }

            if (material.isTwoSized()) {
                GL11.glDisable(GL11.GL_CULL_FACE);
            }

            material.loadParamsToShader();
            shader.loadLights(environment.getLights());

            List<Entity> batch = environment.getEntities().get(model);
            for (Entity entity : batch) {
                shader.loadTransformationMatrix(Maths.createTransformationMatrix(entity.getPosition(),
                        entity.getRotation(), entity.getScale()));

                int[] offsets = rawModel.getSgOffsets();
                int globalOffset = 0;
                for (int offset : offsets) {
                    if (offset != 0) {
                        GL11.glDrawElements(GL11.GL_TRIANGLES, offset, GL11.GL_UNSIGNED_INT, globalOffset * 4);
                        globalOffset += offset;
                    }
                }

            }

            if (material.isTwoSized()) {
                GL11.glEnable(GL11.GL_CULL_FACE);
            }

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            if (hasTextureCoordinates) {
                GL20.glDisableVertexAttribArray(2);
            }
            GL20.glDisableVertexAttribArray(3);

            GL30.glBindVertexArray(0);

            shader.stopUsing();

        }
    }

}
