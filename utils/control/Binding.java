package utils.control;

public class Binding implements Updateable {

	protected double state;
	protected double delta;

	private final Key defaultKey;

	private Key key;

	public Binding(Key defaultKey) {
		this.defaultKey = defaultKey;
		this.key = defaultKey;
	}
	
	public void update() {
		double nstate = key.getState();
		delta = nstate - state;
		state = nstate;
	}

	public double getState() {
		return state;
	}

	public double getDelta() {
		return delta;
	}

	public boolean isPressed() {
		return state > 0.5;
	}

	public boolean isStartPressed() {
		return delta > 0.5;
	}

	public boolean isEndPressed() {
		return delta < -0.5;
	}

}