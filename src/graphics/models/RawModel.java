package graphics.models;

public class RawModel {

    private int vertexArrayObjectID;
    private int vertexCount;
    private int[] sgOffsets;

    public RawModel(int vertexArrayObjectID, int vertexCount) {
        this.vertexArrayObjectID = vertexArrayObjectID;
        this.vertexCount = vertexCount;
    }

    public RawModel(int vertexArrayObjectID, int vertexCount, int[] sgOffsets) {
        this.vertexArrayObjectID = vertexArrayObjectID;
        this.vertexCount = vertexCount;
        this.sgOffsets = sgOffsets;
    }

    public int getVertexArrayObjectID() {
        return vertexArrayObjectID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int[] getSgOffsets() {
        return sgOffsets;
    }
}
