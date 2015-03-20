package utils;

import java.io.IOException;

import renderer.RenderMain;

public class Main {

	public static void main(String[] args) throws IOException {

		// ServerPacketHandler server = new ServerPacketHandler(65535);
		// ClientPacketHandler client = new ClientPacketHandler("127.0.0.1",
		// 65535);
		// System.out.println("Send");
		// server.ensureRegistrated(TestPacket.FACTORY);
		// System.out.println("Try");
		// client.sendPacket(new TestPacket());
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		//
		// }
		// System.out.println("Try2");
		// client.close();
		// server.close();
		new RenderMain().run();
	}
}


