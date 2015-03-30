package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import math.vecmat.PolarVec;
import math.vecmat.Vec;

public class Utils {

	public static void writeUUID(DataOutputStream out, UUID uuid) throws IOException {
		out.writeLong(uuid.getMostSignificantBits());
		out.writeLong(uuid.getLeastSignificantBits());
	}

	public static UUID readUUID(DataInputStream in) throws IOException {
		return new UUID(in.readLong(), in.readLong());
	}

	public static void writeVec(DataOutputStream out, Vec<?> vec) throws IOException {
		for (int i = 0; i < vec.dim(); i++)
			out.writeDouble(vec.get(i));
	}

	public static <T extends Vec<T>> T readVec(DataInputStream dataInputStream, T vec) throws IOException {
		for (int i = 0; i < vec.dim(); i++)
			vec.set(i, dataInputStream.readDouble());
		return vec;
	}
	
	public static void writeVec(DataOutputStream out, PolarVec vec) throws IOException {
		for (int i = 0; i < vec.dim(); i++)
			out.writeDouble(vec.get(i));
	}

	public static PolarVec readVec(DataInputStream dataInputStream, PolarVec vec) throws IOException {
		for (int i = 0; i < vec.dim(); i++)
			vec.set(i, dataInputStream.readDouble());
		return vec;
	}

}
