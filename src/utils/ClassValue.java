package utils;

public class ClassValue<T> extends java.lang.ClassValue<T> {

	private static final ThreadLocal<Object> toSet = new ThreadLocal<>();
	
	@SuppressWarnings("unchecked")
	@Override
	protected T computeValue(Class<?> type) {
		return (T) toSet.get();
	}

	public void set(Class<?> type, T value) {
		remove(type);
		toSet.set(value);
		get(type);
		toSet.remove();
	}

}
