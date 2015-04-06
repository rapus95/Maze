package utils.control;

public class BindingPlusMinusAxis extends Binding{
	
	private final Key defaultBaseKey, defaultSubKey;
	
	private Key baseKey, subKey;
	
	BindingPlusMinusAxis(Key defaultBaseKey, Key defaultSubKey){
		this.defaultBaseKey = defaultBaseKey;
		this.defaultSubKey = defaultSubKey;
		this.baseKey = defaultBaseKey;
		this.subKey = defaultSubKey;
	}

	@Override
	protected double queryState(long window) {
		return baseKey.getState()-subKey.getState();
	}

}
