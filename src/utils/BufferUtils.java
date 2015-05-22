package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BufferUtils {

	public static ByteBuffer allocate(int cap){
		return ByteBuffer.allocateDirect(cap).order(ByteOrder.nativeOrder());
	}
	
}
