package net;

import intercom.Packet;
import intercom.PacketHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import maze.Entity;
import maze.Maze;

public class PacketEntityData extends GamePacket {

	public static final PacketFactory FACTORY = new PacketFactory() {

		@Override
		public Packet create(DataInputStream input) throws IOException {
			return new PacketEntityData(input);
		}

	};

	private UUID uuid;
	private String entityID;
	private byte[] data;

	public PacketEntityData(Entity entity) {
		uuid = entity.id;
		entityID = entity.getClass().getName();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeSync(new DataOutputStream(out));
		data = out.toByteArray();
	}

	public PacketEntityData(DataInputStream input) throws IOException {
		uuid = Utils.readUUID(input);
		entityID = input.readUTF();
		int size = input.readInt();
		data = new byte[size];
		input.readFully(data);
	}

	@Override
	public PacketFactory getFactory() {
		return FACTORY;
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		Utils.writeUUID(out, uuid);
		out.writeUTF(entityID);
		out.writeInt(data.length);
		out.write(data);
	}

	@Override
	public void handle(PacketHandler handler, Maze maze) {
		Entity e = maze.getEntity(uuid);
		if (e == null || !e.getClass().getName().equals(entityID)) {
			try {
				e = ((Entity) Class.forName(entityID).getConstructor(Maze.class, DataInputStream.class)
						.newInstance(maze, new DataInputStream(new ByteArrayInputStream(data))));
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
			e.id = uuid;
			maze.spawnEntity(e);
		} else {
			try {
				e.readSync(new DataInputStream(new ByteArrayInputStream(data)));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
