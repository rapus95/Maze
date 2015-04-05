package renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import math.vecmat.Mat;
import math.vecmat.Mat4;
import math.vecmat.Vec3;
import maze.Maze;
import maze.entities.Player;

import org.lwjgl.Sys;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class RenderMain {

	private GLFWErrorCallback errorCallback;

	private boolean shallClose = false;

	private Window[] windows = new Window[1];

	private PlayerData[] players = new PlayerData[1];

	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

		try {
			init();
			loop();

			for (Window window : windows) {
				window.dispose();
			}

		} finally {
			// Terminate GLFW and release the GLFWerrorfun
			GLFW.glfwTerminate();
			errorCallback.release();
		}
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFW.glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (GLFW.glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		int WIDTH = 800;
		int HEIGHT = 600;

		for (int i = 0; i < windows.length; i++) {
			windows[i] = new Window(WIDTH, HEIGHT, "Player1");
		}

		// Get the resolution of the primary monitor
		ByteBuffer vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		// Center our window
		GLFW.glfwSetWindowPos(windows[0].window, (GLFWvidmode.width(vidmode) - WIDTH) / 2 - WIDTH / 2 - 50, (GLFWvidmode.height(vidmode) - HEIGHT) / 4);

		if (windows.length > 1)
			GLFW.glfwSetWindowPos(windows[1].window, (GLFWvidmode.width(vidmode) - WIDTH) / 2 + WIDTH / 2 + 50, (GLFWvidmode.height(vidmode) - HEIGHT) / 4);

		players[0] = new PlayerData();
		players[0].window = windows[0];

		if (players.length == 2) {
			players[1] = new PlayerData();
			if (windows.length == 1) {
				players[1].window = windows[0];
				windows[0].gridWidth = 2;
				players[1].x = 1;
			} else {
				players[1].window = windows[1];
			}
		}

	}

	private void loop() {

		Maze m = new Maze(players.length);
		MazeRenderer mr = new MazeRenderer(m);
		players[0].player = m.currentPlayer();
		for(int i=1; i<players.length; i++){
			players[i].player = (Player) m.getEntities().get(i);
		}
		
		GLFWCursorPosCallback mouseCallBack = new GLFWCursorPosCallback() {

			Player p;

			public GLFWCursorPosCallback setMaze(Maze m) {
				this.p = m.currentPlayer();
				return this;
			}

			@Override
			public void invoke(long window, double xpos, double ypos) {
				double xd = xpos;
				double yd = ypos;
				p.rotate(xd * 0.1);
				p.uplook(yd * 0.1);
				GLFW.glfwSetCursorPos(window, 0, 0);
			}
		}.setMaze(m);

		GLFW.glfwSetCursorPosCallback(windows[0].window, mouseCallBack);

		for (Window window : windows) {
			setupWindow(window);
		}

		long lastNanoTime = System.nanoTime();
		while (!shallClose) {

			m.tick(-(lastNanoTime - (lastNanoTime = System.nanoTime())));

			for (Window window : windows) {
				window.activate();
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				for (PlayerData player : players) {
					if (player.window == window) {
						render(m, player, mr);
					}
				}
				window.draw();
				if(window.shouldClose()){
					shallClose = true;
				}
			}

			for (PlayerData player : players) {
				player.updateControlls();
			}

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			GLFW.glfwPollEvents();
		}

		// keyCallback.release();
		mouseCallBack.release();
	}

	private static void setupWindow(Window window) {
		window.activate();
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glMultMatrix(Mat.asTmpBufferGL(Mat.createPerspectiveMarix(60, 800.0 / 600, 0.1, 100)));
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	private static void render(Maze maze, PlayerData player, MazeRenderer mazeRenderer) {
		maze.currentPlayer = player.player;
		int[] size = new int[2];
		player.window.getSize(size);
		int gridWidth = player.window.gridWidth;
		int gridHeight = player.window.gridHeight;
		int x = size[0] * player.x / gridWidth;
		int y = size[1] * player.y / gridHeight;
		int width = size[0] * player.width / gridWidth;
		int height = size[1] * player.height / gridHeight;
		GL11.glViewport(x, y, width, height);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		Mat4 tmp = Mat.createPerspectiveMarix(player.fovy, (double) width / height, 0.1, 100);
		GL11.glMultMatrix(Mat.asTmpBufferGL(tmp));
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		Vec3 pos = player.player.getPos();
		tmp = Mat.createRotationMarix(player.player.uplook(), 1, 0, 0);
		GL11.glMultMatrix(Mat.asTmpBufferGL(tmp));
		tmp = Mat.createLookAtMatrix(pos, pos.add(player.player.getViewDirection()), player.player.getUp());
		GL11.glMultMatrix(Mat.asTmpBufferGL(tmp));
		mazeRenderer.render();
	}

	private boolean bombJoystick;

	public void handleJoystick(long window, Player p) {
		FloatBuffer b = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_1);
		p.rotate(b.get(0)*b.get(0)*b.get(0) * 4);
		p.uplook(b.get(1)*b.get(1)*b.get(1) * 4);
		
		ByteBuffer bb = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_1);
		p.setForwardSpeed((bb.get(5) == 1 ? 2 : 1) * (-b.get(3)*b.get(3)*b.get(3)));
		p.setSidewardSpeed((bb.get(5) == 1 ? 2 : 1) * b.get(4)*b.get(4)*b.get(4));

		if (bb.get(4) == 1) {
			if (!bombJoystick) {
				p.placeBomb();
				bombJoystick = true;
			}
		} else {
			bombJoystick = false;
		}
		if (isPressed(window, GLFW.GLFW_KEY_ESCAPE))
			shallClose = true;
	}

	private boolean space;

	public void handleKeyboard(long window, Player p) {
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

	private class PlayerData {
		Player player;
		Window window;
		int x;
		int y;
		int width = 1;
		int height = 1;
		double fovy = 60;

		public void updateControlls() {
			if (players[0] == this) {
				handleKeyboard(window.window, player);
			} else {
				handleJoystick(window.window, player);
			}
		}

	}

	private static class Window {

		private static Window current;

		private long window;

		int gridWidth = 1;
		int gridHeight = 1;

		public Window(int width, int height, String title) {
			GLFW.glfwDefaultWindowHints();
			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
			window = GLFW.glfwCreateWindow(width, height, title, 0, 0);
			if (window == 0) {
				throw new RuntimeException("Failed to create the GLFW window");
			}
			activate();
			GLContext.createFromCurrent();
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			GLFW.glfwShowWindow(window);
			GLFW.glfwSetCursorPos(window, 0, 0);
		}

		public boolean shouldClose() {
			return GLFW.glfwWindowShouldClose(window)!=0;
		}

		private static final IntBuffer xpos = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		private static final IntBuffer ypos = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();

		public void getSize(int[] buff) {
			if (buff.length != 2)
				throw new IllegalArgumentException();
			xpos.position(0);
			ypos.position(0);
			GLFW.glfwGetWindowSize(window, xpos, ypos);
			buff[0] = xpos.get(0);
			buff[1] = ypos.get(0);
		}

		public void draw() {
			GLFW.glfwSwapBuffers(window);
		}

		public void activate() {
			if (current != this) {
				current = this;
				GLFW.glfwMakeContextCurrent(window);
			}
		}

		public void dispose() {
			if (window != 0) {
				GLFW.glfwDestroyWindow(window);
				window = 0;
			}
		}

	}

}
