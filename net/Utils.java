package net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import math.matrix.IVec;

public class Utils {

	public static void writeUUID(DataOutputStream out, UUID uuid) throws IOException {
		out.writeLong(uuid.getMostSignificantBits());
		out.writeLong(uuid.getLeastSignificantBits());
	}

	public static UUID readUUID(DataInputStream in) throws IOException {
		return new UUID(in.readLong(), in.readLong());
	}

	public static void writeVec(DataOutputStream out, IVec vec) throws IOException {
		for (int i = 0; i < vec.getDimensionCount(); i++)
			out.writeDouble(vec.get(i));
	}

	public static <T extends IVec> T readVec(DataInputStream dataInputStream, T vec) throws IOException {
		for (int i = 0; i < vec.getDimensionCount(); i++)
			vec.set(i, dataInputStream.readDouble());
		return vec;
	}

}
