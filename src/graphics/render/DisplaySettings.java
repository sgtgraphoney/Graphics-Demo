package graphics.render;

import org.lwjgl.opengl.GL11;

public class DisplaySettings {

    public static final int SUPPORTED_LIGHTS_COUNT = 8;

    private  int resolutionWidth = 1280;
    private int resolutionHeight = 720;
    private int textureFilteringMode = GL11.GL_LINEAR_MIPMAP_LINEAR;
    private boolean anisotropicFilteringOn = true;
    private int samplesCount = 8;

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public int getTextureFilteringMode() {
        return textureFilteringMode;
    }

    public void setTextureFilteringMode(int textureFilteringMode) {
        this.textureFilteringMode = textureFilteringMode;
    }

    public boolean isAnisotropicFilteringOn() {
        return anisotropicFilteringOn;
    }

    public void setAnisotropicFilteringOn(boolean anisotropicFilteringOn) {
        this.anisotropicFilteringOn = anisotropicFilteringOn;
    }

    public int getSamplesCount() {
        return samplesCount;
    }

    public void setSamplesCount(int samplesCount) {
        this.samplesCount = samplesCount;
    }
}
