package engine.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderSource;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL40.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import engine.math.Matrix4f;
import engine.math.Vector2f;
import engine.math.Vector3f;

public abstract class ShaderProgram {
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	private int tessControlShaderID;
	private int tessEvaluationID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public ShaderProgram(String vertexFile, String fragmentFile, String tessControlFile, String tessEvaluationFile){
		vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);		
		tessControlShaderID = loadShader(tessControlFile, GL_TESS_CONTROL_SHADER);
		tessEvaluationID = loadShader(tessEvaluationFile, GL_TESS_EVALUATION_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		glAttachShader(programID, tessControlShaderID);
		glAttachShader(programID, tessEvaluationID);
		bindAttributes();
		glLinkProgram(programID);
		System.out.println("In Program "+programID+"\n"+glGetProgramInfoLog(programID)+"\nEnde Log");
		glValidateProgram(programID);
	}
	public ShaderProgram(String vertexFile, String fragmentFile){
		vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		glLinkProgram(programID);
		glValidateProgram(programID);
	}
	
	protected abstract void bindAttributes();

	protected void bindAttribute(int attribute, String variableName){
		glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected abstract void getAllUniformLocations();
	
	protected int getUniformLocation(String uniformName){
		int location = glGetUniformLocation(programID, uniformName);
		if(location == -1){
			System.out.println("Fehler beim finden von "+uniformName+" in Programm "+programID);
		}
		return location;
	}
	
	public void start(){
		glUseProgram(programID);
	}
	
	public void stop(){
		glUseProgram(0);
	}
	
	public void cleanUp(){
		stop();
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDetachShader(programID, tessControlShaderID);
		glDetachShader(programID, tessEvaluationID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		glDeleteShader(tessControlShaderID);
		glDeleteShader(tessEvaluationID);
		glDeleteProgram(programID);
	}
	
	protected void loadMatrix(int location, Matrix4f matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4(location, false, matrixBuffer);
	}
	protected void loadInt(int location, int value){
		glUniform1i(location, value);
	}
	
	protected void loadFloat(int location, float value){
		glUniform1f(location, value);
	}
	protected void loadBoolean(int location, boolean value){
		float toLoad = 0;
		if(value){
			toLoad = 1;
		}
		glUniform1f(location, toLoad);
	}
	protected void loadVector(int location, Vector3f vector){
		glUniform3f(location, vector.x, vector.y, vector.z);
	}
	protected void loadVector(int location, float x, float y) {
		glUniform2f(location, x, y);
	}
	protected void loadVector(int location, Vector2f vector){
		glUniform2f(location, vector.x, vector.y);
	}
	private static int loadShader(String file, int type){
		StringBuilder shaderSource = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine())!= null){
				shaderSource.append(line).append("\n");
			}
			reader.close();
		}catch(IOException e){
			System.err.println("LOLZ NO FILE "+ file);
		}
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE){
			System.out.println("Fehler in "+file+"\n");
			System.out.println(glGetShaderInfoLog(shaderID));
		}
		return shaderID;
	}

	
	
}
