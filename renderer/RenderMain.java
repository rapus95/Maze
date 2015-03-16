package renderer;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import math.matrix.Vec;
import maze.Maze;
import maze.entities.Player;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

public class RenderMain {

	private GLFWErrorCallback errorCallback;
	// private GLFWKeyCallback keyCallback;

	private boolean multiplayer = false;

	private boolean shallClose = false;

	// The window handle
	private long window;
	private long window2;

	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

		try {
			init();
			loop();

			// Release window and window callbacks
			glfwDestroyWindow(window);
			if (multiplayer)
				glfwDestroyWindow(window2);
			// keyCallback.release();
		} finally {
			// Terminate GLFW and release the GLFWerrorfun
			glfwTerminate();
			errorCallback.release();
		}
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden
												// after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

		int WIDTH = 800;
		int HEIGHT = 600;

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Player1", NULL, NULL);
		if (multiplayer)
			window2 = glfwCreateWindow(WIDTH, HEIGHT, "Player2", NULL, NULL);
		if (window == NULL || (multiplayer && window2 == NULL))
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.

		// Get the resolution of the primary monitor
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - WIDTH) / 2 - WIDTH / 2 - 50, (GLFWvidmode.height(vidmode) - HEIGHT) / 2);

		if (multiplayer)
			glfwSetWindowPos(window2, (GLFWvidmode.width(vidmode) - WIDTH) / 2 + WIDTH / 2 + 50, (GLFWvidmode.height(vidmode) - HEIGHT) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		if (multiplayer)
			glfwMakeContextCurrent(window2);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);

		if (multiplayer)
			glfwShowWindow(window2);
	}

	private void loop() {

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the ContextCapabilities instance and makes the OpenGL
		// bindings available for use.
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();

		if (multiplayer) {
			glfwMakeContextCurrent(window2);
			GLContext.createFromCurrent();
		}

		MazeRenderer mr = new MazeRenderer();
		Maze m = new Maze();
		// keyCallback = new GLFWKeyCallback() {
		//
		// Player p;
		//
		// public GLFWKeyCallback setMaze(Maze m) {
		// this.p = m.currentPlayer();
		// return this;
		// }
		//
		//
		// @Override
		// public void invoke(long window, int key, int scancode, int action,
		// int mods) {
		// System.out.println("shift:" + ((mods&GLFW.GLFW_MOD_SHIFT)!=0) + ":w:"
		// + (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS));
		// switch (key) {
		// case GLFW.GLFW_KEY_LEFT_SHIFT:
		// switch (action) {
		// case GLFW.GLFW_PRESS :
		// case GLFW.GLFW_REPEAT :
		// break;
		// case GLFW.GLFW_RELEASE :
		// break;
		// default :
		// break;
		// }
		// break;
		// case GLFW.GLFW_KEY_W :
		// switch (action) {
		// case GLFW.GLFW_PRESS :
		// case GLFW.GLFW_REPEAT :
		// p.setForwardSpeed((GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT)
		// == GLFW.GLFW_PRESS ? 2 : 1) * 1);
		// break;
		// case GLFW.GLFW_RELEASE :
		// p.setForwardSpeed(0);
		// break;
		// default :
		// break;
		// }
		// break;
		// case GLFW.GLFW_KEY_S :
		// switch (action) {
		// case GLFW.GLFW_PRESS :
		// case GLFW.GLFW_REPEAT :
		// p.setForwardSpeed((GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT)
		// == GLFW.GLFW_PRESS ? 2 : 1) * -1);
		// break;
		// case GLFW.GLFW_RELEASE :
		// p.setForwardSpeed(0);
		// break;
		// default :
		// break;
		// }
		// break;
		// case GLFW.GLFW_KEY_D :
		// switch (action) {
		// case GLFW.GLFW_PRESS :
		// p.setSidewardSpeed(1);
		// break;
		// case GLFW.GLFW_RELEASE :
		// p.setSidewardSpeed(0);
		// break;
		// default :
		// break;
		// }
		// break;
		// case GLFW.GLFW_KEY_A :
		// switch (action) {
		// case GLFW.GLFW_PRESS :
		// p.setSidewardSpeed(-1);
		// break;
		// case GLFW.GLFW_RELEASE :
		// p.setSidewardSpeed(0);
		// break;
		// default :
		// break;
		// }
		// break;
		// case GLFW.GLFW_KEY_ESCAPE :
		// shallClose = true;
		// break;
		// }
		// }
		// }.setMaze(m);
		// GLFW.glfwSetKeyCallback(window, keyCallback);

		GLFWCursorPosCallback mouseCallBack = new GLFWCursorPosCallback() {

			Player p;

			public GLFWCursorPosCallback setMaze(Maze m) {
				this.p = m.currentPlayer();
				return this;
			}

			@Override
			public void invoke(long window, double xpos, double ypos) {
				double xd = xpos - 400;
				double yd = ypos - 300;
				double rad = Math.atan2(p.getViewDirection().getComponent(1), p.getViewDirection().getComponent(0));
				double radUp = Math.atan2(p.getViewDirection().getComponent(1), p.getViewDirection().getComponent(0));
				rad += xd * 0.002;
				p.getViewDirection().setComponent(0, Math.cos(rad));
				p.getViewDirection().setComponent(1, Math.sin(rad));
				GLFW.glfwSetCursorPos(window, 800 / 2, 600 / 2);
			}
		}.setMaze(m);
		GLFW.glfwSetCursorPosCallback(window, mouseCallBack);

		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

		if (multiplayer)
			GLFW.glfwSetInputMode(window2, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		// Set the clear color

		glfwMakeContextCurrent(window);
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		glMatrixMode(GL11.GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(60, 800.0f / 600, 0.1f, 100);
		glMatrixMode(GL11.GL_MODELVIEW);
		glLoadIdentity();
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		if (multiplayer) {
			glfwMakeContextCurrent(window2);
			glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
			glMatrixMode(GL11.GL_PROJECTION);
			glLoadIdentity();
			GLU.gluPerspective(60, 800.0f / 600, 0.1f, 100);
			glMatrixMode(GL11.GL_MODELVIEW);
			glLoadIdentity();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		long lastNanoTime = System.nanoTime();
		GLFW.glfwSetCursorPos(window, 800 / 2, 600 / 2);
		while (!shallClose) {

			m.tick(-(lastNanoTime - (lastNanoTime = System.nanoTime())));

			glfwMakeContextCurrent(window);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																// framebuffer
			glLoadIdentity();
			// m.currentPlayer = (Player) m.getEntities()[0];
			Vec pos = m.currentPlayer().getPos();
			Vec viewTarget = m.currentPlayer().getViewDirection();
			GLU.gluLookAt(0, 0, 0, (float) viewTarget.getComponent(0), 0, (float) viewTarget.getComponent(1), 0, 1, 0);
			mr.render(m);
			glfwSwapBuffers(window); // swap the color buffers

			handleKeyboard(m.currentPlayer);

			if (multiplayer) {
				glfwMakeContextCurrent(window2);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
				// TODO m.currentPlayer = (Player) m.getEntities()[1];
				glLoadIdentity();
				pos = m.currentPlayer().getPos();
				viewTarget = m.currentPlayer().getViewDirection();
				GLU.gluLookAt(0, 0, 0, (float) viewTarget.getComponent(0), 0, (float) viewTarget.getComponent(1), 0, 1, 0);
				mr.render(m);
				glfwSwapBuffers(window2); // swap the color buffers

				handleJoystick(m.currentPlayer);
			}
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

		// keyCallback.release();
		mouseCallBack.release();
	}

	public void handleJoystick(Player p) {
		FloatBuffer b = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_1);
		float rot = b.get(4);
		double rad = Math.atan2(p.getViewDirection().getComponent(1), p.getViewDirection().getComponent(0));
		rad += rot * 0.1;
		p.getViewDirection().setComponent(0, Math.cos(rad));
		p.getViewDirection().setComponent(1, Math.sin(rad));
		ByteBuffer bb = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_1);
		p.setForwardSpeed((bb.get(5) == 1 ? 2 : 1) * (-b.get(3)));
		p.setSidewardSpeed(b.get(0));
	}

	private boolean space;

	public void handleKeyboard(Player p) {
		boolean shift = isPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) || isPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
		float speed = 0;
		speed += isPressed(window, GLFW.GLFW_KEY_W) ? 1 : 0;
		speed += isPressed(window, GLFW.GLFW_KEY_S) ? -1 : 0;
		speed *= shift ? 2 : 1;
		p.setForwardSpeed(speed);
		speed = 0;
		speed += isPressed(window, GLFW.GLFW_KEY_D) ? 1 : 0;
		speed += isPressed(window, GLFW.GLFW_KEY_A) ? -1 : 0;
		speed *= shift ? 2 : 1;
		p.setSidewardSpeed(speed);

		if (isPressed(window, GLFW.GLFW_KEY_ESCAPE))
			shallClose = true;

		if (isPressed(window, GLFW.GLFW_KEY_SPACE)) {
			if (!space) {
				p.placeBomb();
				space = true;
			}
		} else {
			space = false;
		}
	}

	private boolean isPressed(long window, int key) {
		return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
	}

	public static void main(String[] args) {
		new RenderMain().run();
	}

}
