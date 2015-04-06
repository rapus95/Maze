package utils.control2;

public class KeyBinding {

	private final Key defaultKey;
	
	private Key key;
	
	private double state;
	
	private double delta;
	
	KeyBinding(Key defaultKey){
		this.defaultKey = defaultKey;
		this.key = defaultKey;
	}
	
	void update(long window){
		double nstate = key.getState(window);
		delta = nstate-state;
		state = nstate;
	}
	
	public double getState(){
		return state;
	}
	
	public double getDelta(){
		return delta;
	}
	
	public boolean isPressed(){
		return state>0.5;
	}
	
	public boolean isStartPressed(){
		return state>0.5;
	}
	
	public boolean isEndPressed(){
		return state<-0.5;
	}
	
}
