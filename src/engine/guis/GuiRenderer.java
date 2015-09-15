package engine.guis;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;

import engine.math.Matrix4f;
import engine.model.RawModel;
import engine.renderEngine.Loader;
import engine.toolbox.Maths;

public class GuiRenderer {
	private final RawModel quad;
	private GuiShader shader;
	public GuiRenderer(Loader loader){
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadToVAO(positions, 2);
		shader = new GuiShader();
	}
	public void render(List<GuiTexture> guis){
		shader.start();
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		for (GuiTexture guiTexture : guis) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, guiTexture.getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(guiTexture.getPosition(), guiTexture.getScale());
			shader.loadTransformation(matrix);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shader.stop();
	}
	public void cleanUp(){
		shader.cleanUp();
	}
}
