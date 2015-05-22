package renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import math.vecmat.Mat;
import math.vecmat.Mat4;
import math.vecmat.Vec3;
import maze.Maze;
import maze.entities.Player;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;

import utils.GameController;
import window.Viewport;
import window.WindowManager;
import control.Binding;
import control.CursorKey;
import control.Gamepad;
import control.KeyboardKey;

public class RenderMain {

	private GLFWErrorCallback errorCallback;

	private boolean shallClose = false;

	private WindowManager manager = new WindowManager(1);

	private PlayerData[] players = new PlayerData[2];

	public void run() {
		try {
			init();
			loop();


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

		for (int i = 0; i < manager.getWindowCount(); i++) {
			manager.createWindow(i, WIDTH, HEIGHT, true, "Player"+(i+1));
		}

		// Get the resolution of the primary monitor
		ByteBuffer vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		// Center our window
		manager.setPosition(0, (GLFWvidmode.width(vidmode) - WIDTH) / 2 - WIDTH / 2 - 50, (GLFWvidmode.height(vidmode) - HEIGHT) / 4);
		manager.setCenter(0);
		
		if (manager.getWindowCount() > 1)
			manager.setPosition(1, (GLFWvidmode.width(vidmode) - WIDTH) / 2 + WIDTH / 2 + 50, (GLFWvidmode.height(vidmode) - HEIGHT) / 4);

		players[0] = new PlayerData(null, manager.createViewport(0, 0, 0), new GameController());

		if (players.length == 2) {
			if (manager.getWindowCount() == 1) {
				manager.getWindow(0).setGridWidth(2);
				players[1] = new PlayerData(null, manager.createViewport(0, 1, 0), null);
			} else {
				players[1] = new PlayerData(null, manager.createViewport(1, 0, 0), null);
			}
		}

	}

	private void loop() {

		Maze m = new Maze(players.length);
		MazeRenderer mr = new MazeRenderer(m);
		players[0].setPlayer(m.currentPlayer());
		for (int i = 1; i < players.length; i++) {
			players[i].setPlayer((Player) m.getEntities().get(i));
		}

//		GLFWCursorPosCallback mouseCallBack = new GLFWCursorPosCallback() {
//
//			Player p;
//
//			public GLFWCursorPosCallback setMaze(Maze m) {
//				this.p = m.currentPlayer();
//				return this;
//			}
//
//			@Override
//			public void invoke(long window, double xpos, double ypos) {
//				double xd = xpos;
//				double yd = ypos;
//				p.rotate(xd * 0.1);
//				p.uplook(yd * 0.1);
//				GLFW.glfwSetCursorPos(window, 0, 0);
//			}
//		}.setMaze(m);
//
//		GLFW.glfwSetCursorPosCallback(windows[0].window, mouseCallBack);

		manager.setupWindow(-1, Mat.asTmpBufferGL(Mat.createPerspectiveMarix(60, 800.0 / 600, 0.1, 100)));

		CursorKey.setCatched(true);
		
		long lastNanoTime = System.nanoTime();
		while (!shallClose && exit.getState() == 0) {

			m.tick(-(lastNanoTime - (lastNanoTime = System.nanoTime())));

			for (int i=0; i<manager.getWindowCount(); i++) {
				manager.activateWindow(i).clearWindow(i);
				for (PlayerData player : players) {
					if (player.vp.getIndex() == i) {
						render(m, player, mr);
					}
				}
				manager.drawWindow(i);
				if (manager.shallCloseWindow(i)) {
					shallClose = true;
				}
			}

			GLFW.glfwPollEvents();

			CursorKey.update();
			Gamepad.update();
			
			exit.update();
			for (PlayerData player : players) {
				player.updateControls();
			}

		}

		KeyboardKey.KEY_CALLBACK.release();
		CursorKey.CURSOR_CALLBACK.release();
	}

	private static void render(Maze maze, PlayerData player, MazeRenderer mazeRenderer) {
		maze.currentPlayer = player.player;
		Viewport v = player.vp;
		v.setPerspective(Mat.asTmpBufferGL(Mat.createPerspectiveMarix(60, (double) v.getWidth() / v.getHeight(), 0.1, 100)));
		v.activate();
		Vec3 pos = player.player.getPos();
		Mat4 tmp;
		tmp = Mat.createRotationMarix(player.player.uplook(), 1, 0, 0).mul(Mat.createLookAtMatrix(pos, pos.add(player.player.getViewDirection()), player.player.getUp()));
		GL11.glMultMatrixd(Mat.asTmpBufferGL(tmp));
		mazeRenderer.render();
	}

	private boolean bombJoystick;

	public void handleJoystick(long window, Player p) {
		FloatBuffer b = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_1);

		p.rotate(b.get(0) * b.get(0) * b.get(0) * 4);
		p.uplook(b.get(1) * b.get(1) * b.get(1) * 4);

		ByteBuffer bb = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_1);
		p.setForwardSpeed((bb.get(5) == 1 ? 2 : 1) * (-b.get(3) * b.get(3) * b.get(3)));
		p.setSidewardSpeed((bb.get(5) == 1 ? 2 : 1) * b.get(4) * b.get(4) * b.get(4));

		if (bb.get(4) == 1) {
			if (!bombJoystick) {
				p.placeBomb();
				bombJoystick = true;
			}
		} else {
			bombJoystick = false;
		}
		// if (isPressed(window, GLFW.GLFW_KEY_ESCAPE))
		// shallClose = true;
	}

	public final Binding exit = Binding.createAndDefault(KeyboardKey.getKey(GLFW.GLFW_KEY_ESCAPE));

	private class PlayerData {
		private Player player;
		private final GameController controller;
		private final Viewport vp;

		private PlayerData(Player p, Viewport vp, GameController c) {
			this.player = p;
			this.controller = c;
			this.vp = vp;
		}
		
		private void setPlayer(Player p) {
			if (this.player == null)
				this.player = p;
		}

		public void updateControls() {
			if(controller==null)
				return;
			controller.update();
			player.rotate(controller.rotateRight.getState()-controller.rotateLeft.getState());
			player.uplook(controller.rotateUp.getState()-controller.rotateDown.getState());
			player.setForwardSpeed(controller.forward.getState()-controller.backward.getState());
			player.setSidewardSpeed(controller.right.getState()-controller.left.getState());
			if(controller.jump.isStartPressed()){
				player.setUpSpeed(1);
			}
			if(controller.shoot.isStartPressed()){
				player.placeBomb();
			}
			if(controller.cheat_changeGravity.isStartPressed()){
				player.toggleGravityMode();
			}
			// double tmp;
			// if (controller instanceof Keyboard) {
			// ((Keyboard) controller).setWindow(this.window.window);
			// }
			// if (controller.getB(ControlType.QUIT))
			// shallClose = true;
			// player.setForwardSpeed(controller.getD(ControlType.FORWARD));
			// player.setSidewardSpeed(controller.getD(ControlType.SIDEWARD));
			// if ((tmp = controller.getD(ControlType.UPWARD)) != 0)
			// player.setUpSpeed(tmp);
			// if (controller.getB(ControlType.BOMB))
			// player.placeBomb();
			// if (controller.getB(ControlType.GRAVITY_CHANGE))
			// player.toggleGravityMode();
		}
	}

}
