package engine.model;

import java.io.Serializable;

public class RawModel implements Serializable{
	private int vaoID;
	private int vertexCount;
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	public int getVaoID() {
		return vaoID;
	}
	public void setVaoID(int vaoID) {
		this.vaoID = vaoID;
	}
	public int getVertexCount() {
		return vertexCount;
	}
	public void setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}
}
