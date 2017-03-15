package graphics.render;

import graphics.models.RawModel;
import graphics.resources.ResourceManager;
import graphics.shaders.GUIShader;
import graphics.textures.GUITexture;
import graphics.tools.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class GUIRenderer {

    private final RawModel quad;
    private GUIShader shader;

    public GUIRenderer(ResourceManager resourceManager) {
        float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
        quad = resourceManager.loadRawModelToVAO(positions, 2);
        shader = new GUIShader();
        shader.compile();
    }

    public void render(List<GUITexture> guis) {
        shader.startUsing();

        GL30.glBindVertexArray(quad.getVertexArrayObjectID());
        GL20.glEnableVertexAttribArray(0);

        for (GUITexture gui : guis) {
            shader.loadTransformationMatrix(Maths.createTransformationMatrix(gui.getPosition(), gui.getScale()));

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.stopUsing();
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
