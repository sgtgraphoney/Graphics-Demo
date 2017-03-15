package tester;

import graphics.entities.Entity;
import graphics.entities.FreeCamera;
import graphics.entities.SpotLight;
import graphics.environment.Environment;
import graphics.environment.skybox.Skybox;
import graphics.environment.terrain.Terrain;
import graphics.environment.water.Water;
import graphics.materials.BasicObjectMaterial;
import graphics.materials.MultiTexturedTerrainMaterial;
import graphics.materials.WaterMaterial;
import graphics.models.BasicModel;
import graphics.render.DisplayManager;
import graphics.render.GUIRenderer;
import graphics.render.MasterRenderer;
import graphics.render.WaterRenderer;
import graphics.resources.ResourceManager;
import graphics.textures.GUITexture;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();

        ResourceManager resourceManager = new ResourceManager();

        FreeCamera camera = new FreeCamera(60);
        camera.setPosition(new Vector3f(256, 120, 256));

        MasterRenderer renderer = new MasterRenderer(camera);
        WaterRenderer waterRenderer = new WaterRenderer();
        GUIRenderer guiRenderer = new GUIRenderer(resourceManager);

        Environment environment = new Environment();

        BasicObjectMaterial material2 = new BasicObjectMaterial();
        material2.setDiffuseMapID(resourceManager.loadTexture("sand_wet"));
       // material2.setNormalMapID(resourceManager.loadTexture("sand_wet_NRM"));
        //material2.setDiffuseColor(new Vector3f(0.25f, 0.3f, 0.4f));
        material2.setShineDamper(10);
        //material2.setReflectivity(0.4f);
        material2.getShader().compile();
        BasicModel model2 = new BasicModel(resourceManager.loadRawModelToVAO("robot"), material2);

        Entity entity2 = new Entity(model2, new Vector3f(256, 100, 150), new Vector3f(0, 0, 0), 0.02f);
        ArrayList<Entity> list2 = new ArrayList<>();
        list2.add(entity2);
        environment.getEntities().put(model2, list2);

        int blendMap = resourceManager.loadTexture("blendmap");
        int[] diffuseMaps = new int[5];
        diffuseMaps[0] = resourceManager.loadTexture("grass");
        diffuseMaps[1] = resourceManager.loadTexture("sand_wet");
        diffuseMaps[2] = resourceManager.loadTexture("sand_dry");
        diffuseMaps[3] = resourceManager.loadTexture("coral");
        MultiTexturedTerrainMaterial terrainMaterial = new MultiTexturedTerrainMaterial(diffuseMaps, blendMap);
        terrainMaterial.getConfigs()[2].setNormalMapID(resourceManager.loadTexture("sand_dry_NRM"));
        terrainMaterial.getConfigs()[3].setDiffuseColor(new Vector3f(0.25f, 0.25f, 0.25f));
        terrainMaterial.getConfigs()[3].setMapScale(0.4f);
        terrainMaterial.getConfigs()[2].setMapScale(0.6f);
        terrainMaterial.getConfigs()[1].setMapScale(0.6f);
        terrainMaterial.getConfigs()[1].setShineDamper(35);
        terrainMaterial.getConfigs()[1].setReflectivity(0.7f);
        terrainMaterial.getConfigs()[1].setSpecularColor(new Vector3f(0.7f, 0.7f, 0.7f));
        terrainMaterial.getConfigs()[1].setSpecularMapID(resourceManager.loadTexture("sand_wet_SPEC"));
        terrainMaterial.getShader().compile();
        Terrain terrain = new Terrain(resourceManager, "heightmap", 256, 1024, 120, terrainMaterial);
        environment.getTerrains().add(terrain);

        SpotLight light = new SpotLight(new Vector3f(100000, 1000000, 100000), new Vector3f(1.4f, 1.4f, 1.4f),
                new Vector3f(1, 0, 0));
        environment.getLights().add(light);

        Skybox skybox = new Skybox(resourceManager, new String[]{"right", "left", "top",
                "bottom", "back", "front"});
        skybox.getMaterial().getShader().compile();
        environment.setSkybox(skybox);

        WaterMaterial waterMaterial = new WaterMaterial();
        waterMaterial.setDudvTexture(resourceManager.loadTexture("waterDUDV"));
        waterMaterial.setNormalMap(resourceManager.loadTexture("normal"));
        Water water = new Water(resourceManager, waterMaterial, new Vector3f(512, 90, 512), new Vector3f(0, 0, 0),
                512);
        water.getMaterial().setDiffuseColor(new Vector3f(0, 0.2f, 0.2f));
        water.getMaterial().getShader().compile();

        environment.getWaters().add(water);

        ArrayList<GUITexture> guis = new ArrayList<>();
        guis.add(new GUITexture(0, new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f)));

        while (!Display.isCloseRequested()) {

            boolean underWater = false;

            camera.move(DisplayManager.getFrameTimeSeconds());

            renderer.renderShadowMap(environment);
            guis.get(0).setTexture(renderer.getShadowMapTexture());

            renderer.render(environment, camera, new Vector4f());

            for (Water w : environment.getWaters()) {
                if (camera.getPosition().y < water.getPosition().y) {
                    underWater = true;
                    break;
                }
            }

            if (underWater) {

            } else {
                waterRenderer.render(environment, renderer, camera, DisplayManager.getFrameTimeSeconds());
            }

            //guiRenderer.render(guis);

            DisplayManager.updateDisplay();
        }

        renderer.cleanUp(environment);
        waterRenderer.cleanUp(environment);
        guiRenderer.cleanUp();

        resourceManager.cleanUp();
        DisplayManager.closeDisplay();

    }

}
