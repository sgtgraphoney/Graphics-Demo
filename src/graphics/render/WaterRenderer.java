package graphics.render;

import graphics.entities.Camera;
import graphics.environment.Environment;
import graphics.environment.water.Water;
import graphics.materials.WaterMaterial;
import graphics.shaders.WaterShader;
import graphics.environment.water.WaterFrameBuffers;
import graphics.tools.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

public class WaterRenderer {

    private Vector2f moveFactor = new Vector2f();

    private WaterFrameBuffers buffers = new WaterFrameBuffers();

    public void render(Environment environment, MasterRenderer masterRenderer, Camera camera, float frameTimeSeconds) {

        for (Water water : environment.getWaters()) {

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            buffers.bindReflectionFrameBuffer();

            float distance = 2 * (camera.getPosition().y - water.getPosition().y);
            camera.getPosition().y -= distance;
            camera.invertPitch();
            masterRenderer.render(environment, camera, water.getClipPlane());
            camera.getPosition().y += distance;
            camera.invertPitch();

            buffers.bindRefractionFrameBuffer();
            masterRenderer.render(environment, camera, water.getInvertedClipPlane());

            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer();

            WaterMaterial material = water.getMaterial();
            WaterShader shader = material.getShader();

            shader.startUsing();

            shader.connectTextureUnits();

            moveFactor.x += material.getWaveSpeed().x * frameTimeSeconds;
            moveFactor.x %= 1;
            moveFactor.y += material.getWaveSpeed().y * frameTimeSeconds;
            moveFactor.y %= 1;
            shader.loadMoveFactor(moveFactor);

            shader.loadProjectionMatrix(camera.getProjectionMatrix());
            shader.loadViewMatrix(camera);
            shader.loadTransformationMatrix(Maths.createTransformationMatrix(water.getPosition(),
                    water.getRotation(), water.getScale()));
            material.loadParamsToShader();

            shader.loadLights(environment.getLights());

            GL30.glBindVertexArray(water.getRawModel().getVertexArrayObjectID());
            GL20.glEnableVertexAttribArray(0);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffers.getReflectionTexture());
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffers.getRefractionTexture());
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getDudvTexture());
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getNormalMap());
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffers.getRefractionDepthTexture());

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, water.getRawModel().getVertexCount());

            GL11.glDisable(GL11.GL_BLEND);
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
            shader.stopUsing();

        }

    }

    public void cleanUp(Environment environment) {
        buffers.cleanUp();
        for (Water water : environment.getWaters()) {
            water.getMaterial().getShader().cleanUp();
        }
    }

}
