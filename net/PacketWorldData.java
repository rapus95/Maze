package net;

import intercom.Packet;
import intercom.PacketHandler;
import intercom.Packet.PacketFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import maze.BlockData;
import maze.Maze;

public class PacketWorldData extends GamePacket {

	public static final PacketFactory FACTORY = new PacketFactory() {

		@Override
		public Packet create(DataInputStream input) throws IOException {
			return new PacketEntityData(input);
		}

	};

	private BlockData[][] map;
	
	@Override
	public void handle(PacketHandler handler, Maze maze) {
		// TODO Auto-generated method stub

	}

	@Override
	public PacketFactory getFactory() {
		return FACTORY;
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		// TODO Auto-generated method stub

	}

}
