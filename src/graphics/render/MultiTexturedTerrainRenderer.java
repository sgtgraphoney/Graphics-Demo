package graphics.render;

import graphics.entities.Camera;
import graphics.environment.Environment;
import graphics.environment.terrain.Terrain;
import graphics.materials.MultiTexturedTerrainMaterial;
import graphics.models.RawModel;
import graphics.shaders.MultiTexturedTerrainShader;
import graphics.tools.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import static graphics.materials.MultiTexturedTerrainMaterial.MATERIALS_COUNT;

public class MultiTexturedTerrainRenderer {

    public void render(Environment environment, Camera camera, Vector4f clipPlane, Matrix4f toShadowSpace) {
        for (Terrain terrain : environment.getTerrains()) {

            MultiTexturedTerrainMaterial material = terrain.getMaterial();
            MultiTexturedTerrainShader shader = material.getShader();
            shader.startUsing();

            shader.loadProjectionMatrix(camera.getProjectionMatrix());
            shader.loadViewMatrix(camera.getViewMatrix());
            shader.loadTransformationMatrix(Maths.createTransformationMatrix(terrain.getPosition(),
                    new Vector3f(0, 0, 0), 1));
            shader.loadToShadowSpaceMatrix(toShadowSpace);
            shader.loadClipPlane(clipPlane);

            shader.connectTextureUnits();

            RawModel rawModel = terrain.getRawModel();
            GL30.glBindVertexArray(rawModel.getVertexArrayObjectID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);
            GL20.glEnableVertexAttribArray(3);

            MultiTexturedTerrainMaterial.MaterialConfiguration[] configs = material.getConfigs();

            int unit = GL13.GL_TEXTURE1;
            GL13.glActiveTexture(unit++);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getBlendMapID());

            for (int i = 0; i < MATERIALS_COUNT; i++) {
                GL13.glActiveTexture(unit++);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, configs[i].getDiffuseMapID());
                GL13.glActiveTexture(unit++);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, configs[i].getNormalMapID());
                GL13.glActiveTexture(unit++);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, configs[i].getSpecularMapID());
                GL13.glActiveTexture(unit++);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, configs[i].getDisplacementMapID());
            }

            material.loadParamsToShader(terrain.getSize());
            shader.loadLights(environment.getLights());

            GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(3);
            GL30.glBindVertexArray(0);

            shader.stopUsing();
        }
    }

}
