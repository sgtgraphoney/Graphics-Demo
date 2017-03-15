package graphics.models;

import graphics.materials.BasicObjectMaterial;

public class BasicModel {

    private RawModel rawModel;
    private BasicObjectMaterial material;

    public BasicModel(RawModel model, BasicObjectMaterial material) {
        this.rawModel = model;
        this.material = material;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public BasicObjectMaterial getMaterial() {
        return material;
    }
}
