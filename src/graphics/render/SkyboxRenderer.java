package graphics.render;

import graphics.entities.Camera;
import graphics.environment.skybox.Skybox;
import graphics.shaders.SkyboxShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class SkyboxRenderer {

    public void render(Skybox skybox, Camera camera) {
        SkyboxShader shader = skybox.getMaterial().getShader();
        shader.startUsing();

        shader.loadProjectionMatrix(camera.getProjectionMatrix());
        shader.loadViewMatrix(camera);

        GL30.glBindVertexArray(skybox.getRawModel().getVertexArrayObjectID());
        GL20.glEnableVertexAttribArray(0);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skybox.getMaterial().getCubeMapID());

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, skybox.getRawModel().getVertexCount());

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stopUsing();
    }

}
