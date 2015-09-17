package engine.renderEngine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;

import engine.toolbox.Input;
import engine.toolbox.MouseInput;

public class DisplayManager {

	public static long window;

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	private static long lastFrameTime;
	private static float delta;

	private static double newX = 400;
	private static double newY = 300;

	private static boolean mouseLocked = false;
	
	private static float mouseSpeed = 0.1f;

	public static Input input;
	public static MouseInput mouseWheel;

	public static float deltaX, deltaY;
	public static boolean buffering = false;

	public static void createDisplay() {
		if (glfwInit() == GL_FALSE)
			throw new IllegalStateException();
		glfwWindowHint(GLFW_SAMPLES, 4);
		glfwWindowHint(GLFW_VERSION_MAJOR, 4);

		window = glfwCreateWindow(WIDTH, HEIGHT, "TEST", NULL, NULL);

		mouseWheel = new MouseInput();
		input = new Input();
		glfwSetKeyCallback(window, input);
		glfwSetScrollCallback(window, mouseWheel);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwShowWindow(window);
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();
		glfwSwapInterval(1);
		glEnable(GL_MULTISAMPLE);
		glPatchParameteri(GL_PATCHES, 3);
		lastFrameTime = getCurrentTime();
	}
	public static void updateDisplay() {
		glfwPollEvents();
		updateMouse();
		glfwSwapBuffers(window);
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;
	}
	public static float getFrameTimeSeconds(){
		return delta;
	}
	private static void updateJoyStick(){
		FloatBuffer delta = glfwGetJoystickAxes(0);
		deltaX = delta.get(2) * 5;
		deltaY = -delta.get(3) * 5;
	}
	private static void updateMouse() {
		if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
			glfwSetCursorPos(window, WIDTH / 2, HEIGHT / 2);

			mouseLocked = true;
		}

		if (mouseLocked) {
			DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

			glfwGetCursorPos(window, x, y);
			
			x.rewind();
			y.rewind();

			newX = x.get();
			newY = y.get();

			deltaX = (float) (newX - (WIDTH / 2)) * mouseSpeed;
			deltaY = (float) (newY - (HEIGHT / 2)) * mouseSpeed;

			glfwSetCursorPos(window, WIDTH / 2, HEIGHT / 2);
		}
	}

	public static void closeDisplay() {
		glfwDestroyWindow(window);
		glfwTerminate();
	}
	private static long getCurrentTime(){
		return System.currentTimeMillis();
	}

}
