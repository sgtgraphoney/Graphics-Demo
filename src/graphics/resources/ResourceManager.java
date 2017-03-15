package graphics.resources;

import de.matthiasmann.twl.utils.PNGDecoder;
import graphics.models.RawModel;
import graphics.render.DisplayManager;
import graphics.textures.TextureData;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class ResourceManager {

    private ArrayList<Integer> vertexArrayObjects = new ArrayList<>();
    private ArrayList<Integer> vertexBufferObjects = new ArrayList<>();
    private ArrayList<Integer> textures = new ArrayList<>();

    public int loadTexture(String fileName) {
        int textureID = ResourceLoader.loadTexture(fileName, DisplayManager.getSettings().isAnisotropicFilteringOn());
        textures.add(textureID);
        return textureID;
    }

    public int loadCubeMap(String[] textureFiles) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        for (int i = 0; i < textureFiles.length; i++) {
            TextureData data = decodeTextureFile("res/textures/" + textureFiles[i] + ".png");
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(),
                    data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        textures.add(texID);
        return texID;
    }

    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            FileInputStream in = new FileInputStream(fileName);
            PNGDecoder decoder = new PNGDecoder(in);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ", didn't work");
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
    }

    public void changeTextureFilteringMode(int filteringMode) {
        for(int textureID : textures) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filteringMode);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filteringMode);
        }
    }

    public RawModel loadRawModelToVAO(String fileName) {
        ResourceLoader.RawModelArrays arrays = ResourceLoader.loadRawModel(fileName);
        return loadRawModelToVAO(arrays);
    }

    public RawModel loadRawModelToVAO(ResourceLoader.RawModelArrays arrays) {
        int vaoID = createVAO();

        bindIndicesBuffer(arrays.indices);

        storeDataInAttributeList(0, 3, arrays.vertices);
        storeDataInAttributeList(1, 3, arrays.normals);
        storeDataInAttributeList(2, 2, arrays.textures);
        storeDataInAttributeList(3, 3, arrays.tangents);

        GL30.glBindVertexArray(0);

        RawModel model;
        if (arrays.useSmoothingGroups) {
            model = new RawModel(vaoID, arrays.indices.length, arrays.sgOffset);
        } else {
            model = new RawModel(vaoID, arrays.indices.length);
        }

        return model;
    }

    public RawModel loadRawModelToVAO(float[] vertices, int dimensions) {
        int vaoID = createVAO();

        storeDataInAttributeList(0, dimensions, vertices);

        GL30.glBindVertexArray(0);

        return new RawModel(vaoID, vertices.length / dimensions);
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vertexArrayObjects.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vertexBufferObjects.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    public void cleanUp() {
        textures.forEach(GL11::glDeleteTextures);
        vertexArrayObjects.forEach(GL30::glDeleteVertexArrays);
        vertexBufferObjects.forEach(GL15::glDeleteBuffers);
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vertexBufferObjects.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

}
