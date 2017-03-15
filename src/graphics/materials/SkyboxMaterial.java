package graphics.materials;

import graphics.shaders.SkyboxShader;

public class SkyboxMaterial {

    SkyboxShader shader = new SkyboxShader();
    private int cubeMapID;

    public SkyboxMaterial(int cubeMapID) {
        this.cubeMapID = cubeMapID;
    }

    public SkyboxShader getShader() {
        return shader;
    }

    public void setShader(SkyboxShader shader) {
        this.shader = shader;
    }

    public int getCubeMapID() {
        return cubeMapID;
    }

    public void setCubeMapID(int cubeMapID) {
        this.cubeMapID = cubeMapID;
    }
}
