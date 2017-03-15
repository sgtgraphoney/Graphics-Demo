package graphics.render;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int FPS_CAP = 120;

    private static final DisplaySettings settings = new DisplaySettings();

    private static long lastFrameTime;
    private static float delta;

    public static void createDisplay() {
        ContextAttribs attribs = new ContextAttribs(3, 2);
        attribs.withForwardCompatible(true);
        attribs.withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(settings.getResolutionWidth(), settings.getResolutionHeight()));
            Display.create(new PixelFormat().withSamples(settings.getSamplesCount()), attribs);
            Display.setTitle("Graphics");
            GL11.glEnable(GL13.GL_MULTISAMPLE);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, settings.getResolutionWidth(), settings.getResolutionHeight());

        lastFrameTime = getCurrentTime();
    }

    public static void updateDisplay() {
        Display.sync(FPS_CAP);
        Display.update();

        long currentFrameTime = getCurrentTime();
        delta = (float) (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static void closeDisplay() {
        Display.destroy();
    }

    public static DisplaySettings getSettings() {
        return settings;
    }

    public static float getFrameTimeSeconds() {
        return  delta;
    }

    private static long getCurrentTime() {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }
}
