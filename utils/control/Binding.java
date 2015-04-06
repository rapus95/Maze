package utils.control;

public abstract class Binding implements Updateable {

	protected double state;
	protected double delta;

	protected abstract double queryState(long window);

	@Override
	public void update(long window) {
		double nstate = queryState(window);
		delta = nstate-state;
		state = nstate;
	}

	public double getState() {
		return state;
	}

	public double getDelta() {
		return delta;
	}

	public boolean isPressed() {
		return state>0.5;
	}

	public boolean isStartPressed() {
		return delta>0.5;
	}

	public boolean isEndPressed() {
		return delta<-0.5;
	}

}