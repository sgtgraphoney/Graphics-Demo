package graphics.environment.terrain;

import graphics.materials.MultiTexturedTerrainMaterial;
import graphics.materials.TerrainMaterial;
import graphics.models.RawModel;
import graphics.resources.ResourceLoader;
import graphics.resources.ResourceManager;
import graphics.resources.VertexNM;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.geom.FlatteningPathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Terrain {

    private static final int MAX_PIXEL_COLOR = 255 * 255 * 255;

    private Vector3f position = new Vector3f(0, 0, 0);

    private int resolution;
    private int size;
    private int maxHeight;

    private RawModel rawModel;
    private MultiTexturedTerrainMaterial material;

    float[][] heights;

    public Terrain(ResourceManager manager, String heightMap, int resolution, int size, int maxHeight,
                   MultiTexturedTerrainMaterial material) {
        this.resolution = resolution;
        this.size = size;
        this.maxHeight = maxHeight;
        this.material = material;
        this.rawModel = generateTerrain(manager, heightMap);
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getResolution() {
        return resolution;
    }

    public int getSize() {
        return size;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public MultiTexturedTerrainMaterial getMaterial() {
        return material;
    }

    private RawModel generateTerrain(ResourceManager manager, String heightMap) {

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("res/textures/" + heightMap + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        heights = new float[resolution][resolution];

        int vertexCount = resolution * resolution;

        ArrayList<VertexNM> vertices = new ArrayList<>();
        ArrayList<Vector2f> textureCoordinates = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();

        int scaleOffset = (int) ((float) image.getHeight() / resolution);
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {

                float x = (float) j / (float) (resolution - 1) * size;
                float height = getHeight(j * scaleOffset, i * scaleOffset, image);
                heights[j][i] = height;
                float z = (float) i / (float) (resolution - 1) * size;
                vertices.add(new VertexNM(vertices.size(), new Vector3f(x, height, z), textureCoordinates,
                        normals));

                float u = (float) j / (float) (resolution - 1);
                float v = (float) i / (float) (resolution - 1);
                textureCoordinates.add(new Vector2f(u, v));

            }
        }

        ArrayList<Vector3f> tangents = new ArrayList<>();
        smooth(3, vertices, normals, tangents, textureCoordinates);

        int[] indicesArray = new int[resolution * (resolution - 1) * 6];

        int indexPointer = 0;
        for (int z = 0; z < resolution - 1; z++) {
            for (int x = 0; x < resolution - 1; x++) {
                int topLeft = (z * resolution) + x;
                int topRight = topLeft + 1;
                int bottomLeft = ((z + 1) * resolution) + x;
                int bottomRight = bottomLeft + 1;

                indicesArray[indexPointer++] = topLeft;
                indicesArray[indexPointer++] = bottomLeft;
                indicesArray[indexPointer++] = topRight;
                indicesArray[indexPointer++] = topRight;
                indicesArray[indexPointer++] = bottomLeft;
                indicesArray[indexPointer++] = bottomRight;
            }
        }

        float[] verticesArray = new float[vertexCount * 3];
        float[] normalsArray = new float[vertexCount * 3];
        float[] tangentsArray = new float[vertexCount * 3];
        float[] texturesArray = new float[vertexCount * 2];

        storeBuffersInArray(vertices, normals, tangents, textureCoordinates, verticesArray, normalsArray,
                tangentsArray, texturesArray);

        return manager.loadRawModelToVAO(new ResourceLoader.RawModelArrays(verticesArray, indicesArray, normalsArray,
                tangentsArray, texturesArray));
    }

    private void smooth(int radius, List<VertexNM> vertices, List<Vector3f> normals, List<Vector3f> tangents,
                        List<Vector2f> textureCoordinates) {
        normals.clear();
        tangents.clear();

        for (int i = 1; i < resolution - 1; i++) {

            int currentRadius;
            if (i < radius || resolution - radius - 1 < i) {
                currentRadius = Math.min(i, resolution - 1 - i);
            } else {
                currentRadius = radius;
            }

            float left = 0, right = 0, top = 0, bottom = 0;
            for (int j = i - currentRadius; j <= i + currentRadius; j++) {
                if (j != i) {
                    left += heights[0][j];
                    right += heights[resolution - 1][j];
                    top += heights[j][0];
                    bottom += heights[j][resolution - 1];
                }
            }

            float count = currentRadius * 2;
            left /= count;
            right /= count;
            top /= count;
            bottom /= count;

            heights[0][i] = left;
            heights[resolution - 1][i] = right;
            heights[i][0] = top;
            heights[i][resolution - 1] = bottom;

            vertices.get(resolution * i).getPosition().y = left;
            vertices.get(resolution * i + resolution - 1).getPosition().y = right;
            vertices.get(i).getPosition().y = top;
            vertices.get(resolution * (resolution - 1) + i).getPosition().y = bottom;

        }

        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                int currentRadius;
                if (i < radius || j < radius || resolution - radius - 1 < i
                        || resolution - radius - 1 < j) {
                    currentRadius = Math.min(Math.min(i, j), Math.min(resolution - 1 - i, resolution - 1 - j));
                    if (currentRadius == 0) {
                        continue;
                    }
                } else {
                    currentRadius = radius;
                }

                float heightSum = 0;
                for (int k = i - currentRadius; k <= i + currentRadius; k++) {
                    for (int l = j - currentRadius; l <= j + currentRadius; l++) {
                        if (k != i || l != j) {
                            heightSum += heights[l][k];
                        }
                    }
                }
                heightSum /= (currentRadius * 2 + 1) * (currentRadius * 2 + 1) - 1;

                heights[j][i] = heightSum;
                vertices.get(resolution * i + j).getPosition().y = heightSum;
            }
        }

        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                Vector3f normal = calculateNormal(j, i, resolution);
                normals.add(normal);
            }
        }

        for (int z = 0; z < resolution - 1; z++) {
            for (int x = 0; x < resolution - 1; x++) {
                int topLeft = (z * resolution) + x;
                int topRight = topLeft + 1;
                int bottomLeft = ((z + 1) * resolution) + x;
                int bottomRight = bottomLeft + 1;

                Vector3f pos1 = vertices.get(topLeft).getPosition();
                Vector3f pos2 = vertices.get(bottomLeft).getPosition();
                Vector3f pos3 = vertices.get(topRight).getPosition();
                Vector3f pos4 = vertices.get(bottomRight).getPosition();

                Vector2f uv1 = textureCoordinates.get(topLeft);
                Vector2f uv2 = textureCoordinates.get(bottomLeft);
                Vector2f uv3 = textureCoordinates.get(topRight);
                Vector2f uv4 = textureCoordinates.get(bottomRight);

                Vector3f tangent1 = calculateTangent(pos1, pos2, pos3, uv1, uv2, uv3);
                Vector3f tangent2 = calculateTangent(pos3, pos2, pos4, uv3, uv2, uv4);

                vertices.get(topLeft).addTangent(tangent1);
                vertices.get(bottomLeft).addTangent(tangent1);
                vertices.get(topRight).addTangent(tangent1);
                vertices.get(topRight).addTangent(tangent2);
                vertices.get(bottomLeft).addTangent(tangent2);
                vertices.get(bottomRight).addTangent(tangent2);
            }
        }

        for (int i = 0; i < vertices.size(); i++) {
            VertexNM vertex = vertices.get(i);
            vertex.getAverageTangent().normalise();
            tangents.add(vertex.getAverageTangent());
        }
    }

    private static void storeBuffersInArray(List<VertexNM> vertices, List<Vector3f> normals, List<Vector3f> tangents,
                                            List<Vector2f> textureCoordinates, float[] verticesArray,
                                            float[] normalsArray, float[] tangentsArray, float[] texturesArray) {
        for (int i = 0; i < vertices.size(); i++) {
            Vector3f vertex = vertices.get(i).getPosition();
            verticesArray[i * 3] = vertex.x;
            verticesArray[i * 3 + 1] = vertex.y;
            verticesArray[i * 3 + 2] = vertex.z;
            Vector3f normal = normals.get(i);
            normalsArray[i * 3] = normal.x;
            normalsArray[i * 3 + 1] = normal.y;
            normalsArray[i * 3 + 2] = normal.z;
            Vector3f tangent = tangents.get(i);
            tangentsArray[i * 3] = tangent.x;
            tangentsArray[i * 3 + 1] = tangent.y;
            tangentsArray[i * 3 + 2] = tangent.z;
            Vector2f texCoords = textureCoordinates.get(i);
            texturesArray[i * 2] = texCoords.x;
            texturesArray[i * 2 + 1] = texCoords.y;
        }
    }

    private static Vector3f calculateTangent(Vector3f pos1, Vector3f pos2, Vector3f pos3, Vector2f uv1, Vector2f uv2,
                                             Vector2f uv3) {
        Vector3f edge1 = Vector3f.sub(pos2, pos1, null);
        Vector3f edge2 = Vector3f.sub(pos3, pos1, null);
        Vector2f deltaUV1 = Vector2f.sub(uv2, uv1, null);
        Vector2f deltaUV2 = Vector2f.sub(uv3, uv1, null);

        float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

        Vector3f tangent = new Vector3f();
        tangent.x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x);
        tangent.y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y);
        tangent.z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z);
        tangent.normalise();

        return tangent;
    }

    private Vector3f calculateNormal(int x, int z, int resolution) {
        int dx = 1, dz = 1;
        if (x == 0 || x == resolution - 1) {
            dx = 0;
        }
        if (z == 0 || z == resolution - 1) {
            dz = 0;
        }
        float heightL = heights[x - dx][z];
        float heightR = heights[x + dx][z];
        float heightD = heights[x][z - dz];
        float heightU = heights[x][z + dz];

        float unit = (float) size / (float) (resolution);
        Vector3f normal = Vector3f.cross(new Vector3f(0, heightD - heightU, unit),
                new Vector3f(unit, heightL - heightR, 0), null);
        normal.normalise();
        return normal;
    }

    private float getHeight(int x, int z, BufferedImage image) {

        if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
            return 0;
        }

        float height = image.getRGB(x, z);
        height += MAX_PIXEL_COLOR / 2f;
        height /= MAX_PIXEL_COLOR / 2f;
        height *= maxHeight / 2f;

        return height + maxHeight / 2f;
    }

}
