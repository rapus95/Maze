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

import math.matrix.PolarVec;
import math.matrix.Vec;
import maze.Maze;
import maze.entities.Player;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

public class RenderMain {

	private GLFWErrorCallback errorCallback;

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
		glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - WIDTH) / 2 - WIDTH / 2 - 50, (GLFWvidmode.height(vidmode) - HEIGHT) / 4);

		if (multiplayer)
			glfwSetWindowPos(window2, (GLFWvidmode.width(vidmode) - WIDTH) / 2 + WIDTH / 2 + 50, (GLFWvidmode.height(vidmode) - HEIGHT) / 4);

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
				PolarVec t = p.getViewDirection();
				t.setComponent(1, t.getComponent(1) + xd * 0.002);
				t.setComponent(2, Math.max(-Math.PI / 2, Math.min(Math.PI / 2, t.getComponent(2) + yd * 0.002)));
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


			glfwMakeContextCurrent(window);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																// framebuffer
			glLoadIdentity();
			PolarVec viewTarget = m.currentPlayer().getViewDirection();
			GL11.glRotated(Math.toDegrees(viewTarget.getComponent(2)), 1, 0, 0);
			GL11.glRotated(Math.toDegrees(viewTarget.getComponent(1)), 0, 1, 0);
			m.currentPlayer = (Player) m.getEntities().get(0);
			
			
			Vec pos = m.currentPlayer().getPos();
			double posX = pos.getComponent(0), posY = pos.getComponent(1), posZ = pos.getComponent(2);
			GL11.glPushMatrix();
			GL11.glTranslated(-posX, -posZ, -posY);
			m.tick(-(lastNanoTime - (lastNanoTime = System.nanoTime())));
			GL11.glPopMatrix();
			mr.render(m);
			glfwSwapBuffers(window); // swap the color buffers

			handleKeyboard(m.currentPlayer);

			if (multiplayer) {
				glfwMakeContextCurrent(window2);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
				m.currentPlayer = (Player) m.getEntities().get(1);
				glLoadIdentity();
				viewTarget = m.currentPlayer().getViewDirection();
				GL11.glRotated(Math.toDegrees(viewTarget.getComponent(2)), 1, 0, 0);
				GL11.glRotated(Math.toDegrees(viewTarget.getComponent(1)), 0, 1, 0);
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

	private boolean bombJoystick;

	public void handleJoystick(Player p) {
		FloatBuffer b = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_1);
		PolarVec t = p.getViewDirection();
		t.setComponent(1, t.getComponent(1) + b.get(4) * 0.1);
		//t.setComponent(2, Math.max(-Math.PI / 2, Math.min(Math.PI / 2, t.getComponent(2) + b.get(0) * 0.1)));
		ByteBuffer bb = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_1);
		p.setForwardSpeed((bb.get(5) == 1 ? 2 : 1) * (-b.get(3)));
		p.setSidewardSpeed((bb.get(5) == 1 ? 2 : 1) * b.get(0));

		if (bb.get(4) == 1) {
			if (!bombJoystick) {
				p.placeBomb();
				bombJoystick = true;
			}
		} else {
			bombJoystick = false;
		}
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

}
