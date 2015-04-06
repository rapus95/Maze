package utils.control;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

public class KeySet {

	private final Map<KeyType, Key> keys = new HashMap<>();

	public static final KeySet WASD_DEFAULT_KEYSET = new KeySet(new Object[]{
			KeyType.SPEEDMULTIPLIER, GLFW.GLFW_KEY_LEFT_SHIFT,
			KeyType.FORWARD, GLFW.GLFW_KEY_W,
			KeyType.BACKWARD, GLFW.GLFW_KEY_S,
			KeyType.LEFT, GLFW.GLFW_KEY_A,
			KeyType.RIGHT, GLFW.GLFW_KEY_D,
			KeyType.UPWARD, GLFW.GLFW_KEY_SPACE,
			KeyType.DOWNWARD, 0,
			KeyType.TOGGLE_GRAVITY_TYPE, GLFW.GLFW_KEY_E,
			KeyType.PLACE_BOMB, GLFW.GLFW_KEY_LEFT_CONTROL,
			KeyType.QUIT, GLFW.GLFW_KEY_ESCAPE});

	public static final KeySet ARROWS_DEFAULT_KEYSET = new KeySet(new Object[]{
			KeyType.SPEEDMULTIPLIER, GLFW.GLFW_KEY_RIGHT_SHIFT,
			KeyType.FORWARD, GLFW.GLFW_KEY_UP,
			KeyType.BACKWARD, GLFW.GLFW_KEY_DOWN,
			KeyType.LEFT, GLFW.GLFW_KEY_LEFT,
			KeyType.RIGHT, GLFW.GLFW_KEY_RIGHT,
			KeyType.UPWARD, GLFW.GLFW_KEY_ENTER,
			KeyType.DOWNWARD, 0,
			KeyType.TOGGLE_GRAVITY_TYPE, GLFW.GLFW_KEY_MINUS,
			KeyType.PLACE_BOMB, GLFW.GLFW_KEY_RIGHT_CONTROL,
			KeyType.QUIT, GLFW.GLFW_KEY_ESCAPE});

	private KeySet(Object[] mapping) {
		for (int i = 0; i < mapping.length-1; ) {
			KeyType kt = (KeyType)mapping[i++];
			Key k = new Key(kt.singleActivationPerPress, (int)mapping[i++]);
			keys.put(kt, k);
		}
	}

	public double getKeyState(long window, KeyType type) {
		return keys.get(type).isPressed(window)?1:0;
	}

	public static enum KeyType {
		SPEEDMULTIPLIER, FORWARD, BACKWARD, LEFT, RIGHT, UPWARD(true), DOWNWARD(true), TOGGLE_GRAVITY_TYPE(true), PLACE_BOMB(true), QUIT;

		private final boolean singleActivationPerPress;
		private KeyType() {
			this(false);
		}
		private KeyType(boolean singleActivationPerPress) {
			this.singleActivationPerPress = singleActivationPerPress;
		}
	}

	private static class Key {
		private final boolean repeat;
		private final int keyCode;
		public Key(boolean singleActivation, int keyCode_GLFW) {
			this.repeat = !singleActivation;
			this.keyCode = keyCode_GLFW;
		}

		private boolean isDown;
		public boolean isPressed(long window) {
			if(keyCode==0)
				return false;
			boolean currentState = isPressed(window, keyCode);
			if(repeat)
				return currentState;
			if (currentState){
				if (!isDown) {
					isDown = true;
					return true;
				}
			} else {
				isDown = false;
			}
			return false;
		}

		private static boolean isPressed(long window, int key) {
			return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
		}
	}
}
