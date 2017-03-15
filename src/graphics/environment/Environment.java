package graphics.environment;

import graphics.entities.Entity;
import graphics.entities.SpotLight;
import graphics.environment.skybox.Skybox;
import graphics.environment.terrain.Terrain;
import graphics.environment.water.Water;
import graphics.models.BasicModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Environment {

    private EnvironmentSettings settings;

    private Map<BasicModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();
    private List<SpotLight> lights = new ArrayList<>();
    private List<Water> waters = new ArrayList<>();
    private Skybox skybox;

    public Map<BasicModel, List<Entity>> getEntities() {
        return entities;
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public List<SpotLight> getLights() {
        return lights;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public List<Water> getWaters() {
        return waters;
    }

    public void setWaters(List<Water> waters) {
        this.waters = waters;
    }
}
