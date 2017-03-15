package graphics.resources;

import com.jumi.image.TGADecoder;
import graphics.render.DisplayManager;
import graphics.textures.TextureData;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class ResourceLoader {

    public static final int SMOOTHING_GROUPS_COUNT = 32;

    private static final String TEXTURE_LOCATION_PATH = "res/textures/";
    private static final String TEXTURE_FILE_EXTENSION = ".png";

    public static int loadTexture(String fileName, boolean anisotropicFilteringOn) {

        Texture texture;
        int filteringMode = DisplayManager.getSettings().getTextureFilteringMode();

        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream(TEXTURE_LOCATION_PATH + fileName
                    + TEXTURE_FILE_EXTENSION));
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filteringMode);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filteringMode);

            float bias;
            if (anisotropicFilteringOn) {
                bias = 0;
            } else {
                bias = -0.4f;
            }

            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, bias);

            if (anisotropicFilteringOn) {
                if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                    float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic
                            .GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                            amount);
                } else {
                    System.out.println("Anisotropic filtering is not supported");
                }
            }

        } catch (IOException e) {
            System.err.println("Could not load texture from file: " + TEXTURE_LOCATION_PATH + fileName
                    + TEXTURE_FILE_EXTENSION);
            e.printStackTrace();
            return 0;
        }

        return texture.getTextureID();
    }

    public static class RawModelArrays {
        public float[] vertices;
        public int[] indices;
        public float[] normals;
        public float[] tangents;
        public float[] textures;
        public int[] sgOffset;
        public boolean useSmoothingGroups;

        public RawModelArrays(float[] vertices, int[] indices, float[] normals, float[] tangents, float[] textures) {
            this.vertices = vertices;
            this.indices = indices;
            this.normals = normals;
            this.tangents = tangents;
            this.textures = textures;
        }

        public RawModelArrays(float[] vertices, int[] indices, float[] normals, float[] tangents, float[] textures,
                              int[] sgOffset) {
            this.vertices = vertices;
            this.indices = indices;
            this.normals = normals;
            this.tangents = tangents;
            this.textures = textures;
            this.sgOffset = sgOffset;
            useSmoothingGroups = true;
        }
    }

    public static RawModelArrays loadRawModel(String filename) {
        FileReader fileReader = null;

        try {
            fileReader = new FileReader("res/models/" + filename + ".obj");
        } catch (FileNotFoundException e) {
            System.err.println("Could not read file!");
            e.printStackTrace();
            System.exit(-1);
        }

        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Vector2f> textures = new ArrayList<>();
        LinkedHashSet<VertexNM>[] smoothingGroups = new LinkedHashSet[SMOOTHING_GROUPS_COUNT];
        ArrayList<Integer>[] sgIndices = new ArrayList[SMOOTHING_GROUPS_COUNT];

        try {
            while (true) {
                line = reader.readLine();
                String[] currentLine = line.split(" +");
                if (line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if (line.startsWith("vt ")) {
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("s ")) {
                    break;
                }
            }

            int sgIndex = 0;
            while (line != null) {
                if (!line.startsWith("f ")) {
                    if (line.startsWith("s ")) {
                        sgIndex = Integer.parseInt(line.substring(2)) - 1;
                        if (smoothingGroups[sgIndex] == null) {
                            smoothingGroups[sgIndex] = new LinkedHashSet<>();
                            sgIndices[sgIndex] = new ArrayList<>();
                        }
                    }
                    line = reader.readLine();
                    continue;
                }

                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                VertexNM v0 = processVertex(vertex1, vertices, smoothingGroups[sgIndex], sgIndices[sgIndex],
                        textures, normals);
                VertexNM v1 = processVertex(vertex2, vertices, smoothingGroups[sgIndex], sgIndices[sgIndex],
                        textures, normals);
                VertexNM v2 = processVertex(vertex3, vertices, smoothingGroups[sgIndex], sgIndices[sgIndex],
                        textures, normals);
                calculateTangents(v0, v1, v2, textures);
                line = reader.readLine();

            }

            for (int i = 0; i < SMOOTHING_GROUPS_COUNT; i++) {

                if (smoothingGroups[i] == null) {
                    continue;
                }

                for (VertexNM vertex : smoothingGroups[i]) {
                    Vector3f normal = normals.get(vertex.getNormalIndex());
                    Vector3f tangent = makeTangentOrthogonal(normal, vertex.getAverageTangent());
                    vertex.setAveragedTangent(tangent);
                }

            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Could not read OBJ file");
            e.printStackTrace();
        }

        return createActualArrays(normals, textures, smoothingGroups, sgIndices);
    }

    private static Vector3f makeTangentOrthogonal(Vector3f normal, Vector3f tangent) {
        float angle = (float) Math.toDegrees(Vector3f.angle(normal, tangent));
        if (angle != 90.0f) {
            Vector3f bitangent = Vector3f.cross(normal, tangent, null);
            Vector3f result = Vector3f.cross(bitangent, normal, null);
            result.normalise();
            return result;
        } else {
            return tangent;
        }
    }

    private static void calculateTangents(VertexNM v0, VertexNM v1, VertexNM v2,
                                          List<Vector2f> textures) {
        Vector3f delatPos1 = Vector3f.sub(v1.getPosition(), v0.getPosition(), null);
        Vector3f delatPos2 = Vector3f.sub(v2.getPosition(), v0.getPosition(), null);
        Vector2f uv0 = textures.get(v0.getTextureIndex());
        Vector2f uv1 = textures.get(v1.getTextureIndex());
        Vector2f uv2 = textures.get(v2.getTextureIndex());
        Vector2f deltaUv1 = Vector2f.sub(uv1, uv0, null);
        Vector2f deltaUv2 = Vector2f.sub(uv2, uv0, null);

        float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
        delatPos1.scale(deltaUv2.y);
        delatPos2.scale(deltaUv1.y);
        Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
        tangent.scale(r);

        v0.addTangent(tangent);
        v1.addTangent(tangent);
        v2.addTangent(tangent);
    }

    private static RawModelArrays createActualArrays(List<Vector3f> normals, List<Vector2f> textureCoordinates,
                                                     LinkedHashSet<VertexNM>[] smoothingGroups,
                                                     ArrayList<Integer>[] smoothingGroupIndices) {

        int[] offsets = new int[SMOOTHING_GROUPS_COUNT];

        ArrayList<Vector3f> actualVertices = new ArrayList<>();
        ArrayList<Vector3f> actualNormals = new ArrayList<>();
        ArrayList<Vector3f> actualTangents = new ArrayList<>();
        ArrayList<Vector2f> actualTextureCoordinates = new ArrayList<>();
        ArrayList<Integer> actualIndices = new ArrayList<>();

        int addedVerticesCount = 0;
        for (int i = 0; i < SMOOTHING_GROUPS_COUNT; i++) {

            if (smoothingGroups[i] == null) {
                continue;
            }

            for (VertexNM vertex : smoothingGroups[i]) {
                actualVertices.add(vertex.getPosition());
                actualNormals.add(normals.get(vertex.getNormalIndex()));
                actualTangents.add(vertex.getAverageTangent());
                actualTextureCoordinates.add(textureCoordinates.get(vertex.getTextureIndex()));
            }

            offsets[i] = smoothingGroupIndices[i].size();
            for (int index : smoothingGroupIndices[i]) {
                actualIndices.add(addedVerticesCount + index);
            }

            addedVerticesCount += smoothingGroups[i].size();
        }

        float[] verticesArray = new float[addedVerticesCount * 3];
        float[] texturesArray = new float[addedVerticesCount * 2];
        float[] normalsArray = new float[addedVerticesCount * 3];
        float[] tangentsArray = new float[addedVerticesCount * 3];

        for (int i = 0; i < addedVerticesCount; i++) {
            Vector3f vertex = actualVertices.get(i);
            verticesArray[i * 3] = vertex.x;
            verticesArray[i * 3 + 1] = vertex.y;
            verticesArray[i * 3 + 2] = vertex.z;

            Vector3f normal = actualNormals.get(i);
            normalsArray[i * 3] = normal.x;
            normalsArray[i * 3 + 1] = normal.y;
            normalsArray[i * 3 + 2] = normal.z;

            Vector3f tangent = actualTangents.get(i);
            tangentsArray[i * 3] = tangent.x;
            tangentsArray[i * 3 + 1] = tangent.y;
            tangentsArray[i * 3 + 2] = tangent.z;

            Vector2f textureCoords = actualTextureCoordinates.get(i);
            texturesArray[i * 2] = textureCoords.x;
            texturesArray[i * 2 + 1] = textureCoords.y;
        }

        int[] indicesArray = new int[actualIndices.size()];

        for (int i = 0; i < actualIndices.size(); i++) {
            indicesArray[i] = actualIndices.get(i);
        }

        return new RawModelArrays(verticesArray, indicesArray, normalsArray, tangentsArray, texturesArray, offsets);
    }

    private static VertexNM processVertex(String[] vertexLine, List<Vector3f> vertices,
                                          LinkedHashSet<VertexNM> sgVertices, List<Integer> indices,
                                          ArrayList<Vector2f> textureCoordinates, ArrayList<Vector3f> normals) {

        Vector3f currentVertex = vertices.get(Integer.parseInt(vertexLine[0]) - 1);
        VertexNM vertex = new VertexNM(sgVertices.size(), currentVertex, textureCoordinates, normals);
        vertex.setTextureIndex(Integer.parseInt(vertexLine[1]) - 1);
        vertex.setNormalIndex(Integer.parseInt(vertexLine[2]) - 1);

        if (sgVertices.add(vertex)) {

            indices.add(sgVertices.size() - 1);

        } else {

            for (VertexNM v : sgVertices) {
                if (v.equals(vertex)) {
                    indices.add(v.getIndex());
                }
            }

        }

        return vertex;
    }

}
