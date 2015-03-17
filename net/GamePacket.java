package net;

import intercom.Packet;
import intercom.PacketHandler;
import maze.Maze;

public abstract class GamePacket extends Packet {

	@Override
	public void handle(PacketHandler handler) {
		handle(handler, ((GameData)handler.getData()).getMaze());
	}
	
	public abstract void handle(PacketHandler handler, Maze maze);

}
