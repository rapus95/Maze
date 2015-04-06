package maze;

public enum Gravity {
	NONE, STATIC, DYNAMIC;
	
	public Gravity toggle(){
		switch(this){
			case DYNAMIC :
				return STATIC;
			case NONE :
				return NONE;
			case STATIC :
				return DYNAMIC;
			default :
				return NONE;
			
		}
	}
}
