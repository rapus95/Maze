package utils.control;


public class GamepadKey implements Key {

	private final Gamepad g;
	private final int key;

	public GamepadKey(Gamepad g, int key) {
		this.g = g;
		this.key = key;
	}

	@Override
	public double getState() {
		return g.getState(key);
	}

}
