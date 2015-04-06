package utils.control;

public class BindingPlus extends Binding {

	private final Key defaultKey;
	
	private Key key;
	
	BindingPlus(Key defaultKey){
		this.defaultKey = defaultKey;
		this.key = defaultKey;
	}
	
	@Override
	protected double queryState(long window) {
		return key.getState();
	}
	
}
