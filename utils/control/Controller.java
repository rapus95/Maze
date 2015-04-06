package utils.control;

public interface Controller {

	public double getD(ControlType type);
	public boolean getB(ControlType type);
	
	public static enum ControlType{
		FORWARD, SIDEWARD, UPWARD, BOMB, GRAVITY_CHANGE, SPEED_MULTIPLIER, QUIT;
	}
}
