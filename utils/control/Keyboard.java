package utils.control;

import utils.control.KeySet.KeyType;

public class Keyboard implements Controller {

	private final KeySet keys;
	private long window;

	public Keyboard(KeySet keyset) {
		this.keys = keyset;
	}
	public void setWindow(long window) {
		this.window = window;
	}


	@Override
	public double getD(ControlType type) {
		double d = 0;
		switch (type) {
			case BOMB :
				return keys.getKeyState(window, KeyType.PLACE_BOMB);
			case FORWARD :
				d = keys.getKeyState(window, KeyType.FORWARD);
				d -= keys.getKeyState(window, KeyType.BACKWARD);
				d *= getD(ControlType.SPEED_MULTIPLIER);
				return d;
			case GRAVITY_CHANGE :
				return keys.getKeyState(window, KeyType.TOGGLE_GRAVITY_TYPE);
			case SIDEWARD :
				d = keys.getKeyState(window, KeyType.RIGHT);
				d -= keys.getKeyState(window, KeyType.LEFT);
				d *= getD(ControlType.SPEED_MULTIPLIER);
				return d;
			case SPEED_MULTIPLIER :
				return keys.getKeyState(window, KeyType.SPEEDMULTIPLIER) + 1;
			case UPWARD :
				d = keys.getKeyState(window, KeyType.UPWARD);
				d += keys.getKeyState(window, KeyType.DOWNWARD);
				d *= getD(ControlType.SPEED_MULTIPLIER);
				return d;
			case QUIT :
				return keys.getKeyState(window, KeyType.QUIT);
			default :
				break;

		}
		return 0;
	}

	@Override
	public boolean getB(ControlType type) {
		return getD(type)!=0;
	}

}
