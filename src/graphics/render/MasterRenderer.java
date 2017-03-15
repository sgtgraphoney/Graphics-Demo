package graphics.render;

import graphics.entities.Camera;
import graphics.entities.Entity;
import graphics.environment.Environment;
import graphics.environment.terrain.Terrain;
import graphics.models.BasicModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.ARBSeamlessCubemapPerTexture;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector4f;

public class MasterRenderer {

    private static final float RED = 0.01f;
    private static final float GREEN = 0.02f;
    private static final float BLUE = 0.03f;

    private EntityRenderer entityRenderer = new EntityRenderer();
    private MultiTexturedTerrainRenderer mtTerrainRenderer = new MultiTexturedTerrainRenderer();
    private SkyboxRenderer skyboxRenderer = new SkyboxRenderer();
    private ShadowMapMasterRenderer shadowMapRenderer;

    public MasterRenderer(Camera camera) {
        shadowMapRenderer = new ShadowMapMasterRenderer(camera);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(ARBSeamlessCubemapPerTexture.GL_TEXTURE_CUBE_MAP_SEAMLESS);
        GL11.glClearColor(RED, GREEN, BLUE, 1);
    }

    public void render(Environment environment, Camera camera, Vector4f clipPlane) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());

        entityRenderer.render(environment, camera, clipPlane, shadowMapRenderer.getToShadowMapSpaceMatrix());
        mtTerrainRenderer.render(environment, camera, clipPlane, shadowMapRenderer.getToShadowMapSpaceMatrix());
        skyboxRenderer.render(environment.getSkybox(), camera);
    }

    public void renderShadowMap(Environment environment) {
        shadowMapRenderer.render(environment, environment.getLights().get(0));
    }

    public int getShadowMapTexture() {
        return shadowMapRenderer.getShadowMap();
    }

    public void cleanUp(Environment environment) {
        for (BasicModel model : environment.getEntities().keySet()) {
            model.getMaterial().getShader().cleanUp();
        }
        for (Terrain terrain : environment.getTerrains()) {
            terrain.getMaterial().getShader().cleanUp();
        }
        environment.getSkybox().getMaterial().getShader().cleanUp();
        shadowMapRenderer.cleanUp();
    }

}
