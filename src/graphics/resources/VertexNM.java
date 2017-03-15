package graphics.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class VertexNM {

    private static final int NO_INDEX = -1;

    private Vector3f position;
    private int textureIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private VertexNM duplicateVertex = null;
    private int index;
    private float length;
    private List<Vector3f> tangents = new ArrayList<Vector3f>();
    private ArrayList<Vector2f> textures;
    private ArrayList<Vector3f> normals;

    public VertexNM(int index, Vector3f position, ArrayList<Vector2f> textures, ArrayList<Vector3f> normals) {
        this.index = index;
        this.position = position;
        this.length = position.length();
        this.textures = textures;
        this.normals = normals;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        VertexNM vertex = (VertexNM) obj;
        return position.equals(vertex.position) && textureIndex == vertex.textureIndex
                && normalIndex == vertex.normalIndex;
    }

    @Override
    public int hashCode() {
        int sum = 0;
        if (textures.size() > textureIndex) {
            sum += textures.get(textureIndex).hashCode();
        }
        if (normals.size() > normalIndex) {
            sum += normals.get(normalIndex).hashCode();
        }
        return position.hashCode() + sum;
    }

    public void addTangent(Vector3f tangent) {
        Vector3f copy = new Vector3f(tangent);
        copy.normalise();
        tangents.add(copy);
    }

    public VertexNM duplicate(int newIndex){
        VertexNM vertex = new VertexNM(newIndex, position, textures, normals);
        vertex.tangents = this.tangents;
        return vertex;
    }

    public Vector3f getAverageTangent() {
        Vector3f averagedTangent = new Vector3f(0, 0, 0);
        for (Vector3f tangent : tangents) {
            Vector3f.add(averagedTangent, tangent, averagedTangent);
        }
        averagedTangent.normalise();
        return averagedTangent;
    }

    public void setAveragedTangent(Vector3f averagedTangent) {
        tangents.clear();
        tangents.add(averagedTangent);
    }

    public int getIndex(){
        return index;
    }

    public float getLength(){
        return length;
    }

    public boolean isSet(){
        return textureIndex!=NO_INDEX && normalIndex!=NO_INDEX;
    }

    public boolean hasSameTextureAndNormal(int textureIndexOther,int normalIndexOther){
        return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
    }

    public void setTextureIndex(int textureIndex){
        this.textureIndex = textureIndex;
    }

    public void setNormalIndex(int normalIndex){
        this.normalIndex = normalIndex;
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public int getNormalIndex() {
        return normalIndex;
    }

    public VertexNM getDuplicateVertex() {
        return duplicateVertex;
    }

    public void setDuplicateVertex(VertexNM duplicateVertex) {
        this.duplicateVertex = duplicateVertex;
    }

}