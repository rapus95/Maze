package utils.control;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class KeyboardKey implements Key {

	private static final Map<Integer, KeyboardKey> KEYS = new HashMap<Integer, KeyboardKey>();
	
	public static final GLFWKeyCallback KEY_CALLBACK = new GLFWKeyCallback(){

		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			KeyboardKey kk = KEYS.get(key);
			if(kk!=null){
				kk.state = (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT);
			}
		}
		
	};
	
	public static KeyboardKey getKey(int key){
		KeyboardKey kk = KEYS.get(key);
		if(kk==null){
			kk = new KeyboardKey();
			KEYS.put(key, kk);
		}
		return kk;
	}
	
	private boolean state;
	
	private KeyboardKey(){
	}
	
	@Override
	public double getState() {
		return state?1:0;
	}

}
