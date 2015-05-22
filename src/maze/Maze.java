package maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import math.vecmat.Vec3;
import maze.blocks.Air;
import maze.blocks.Wall;
import maze.entities.Player;

import static math.vecmat.Vec.*;

public class Maze {
	private final BlockData walls[][][] = new BlockData[50][50][10];
	private Map<UUID, Entity> entities = new HashMap<>();
	private final List<Entity> entityList = new ArrayList<>();
	public Player currentPlayer;
	private double nextRandomBomb = 5;

	public Maze(int players) {
		// entities[1] = new Player(this, tmp);
		Maze.fillMaze(walls, Wall.INSTANCE);
		// walls[5][5][0] = new BlockData(Wall.INSTANCE, Vec3(5, 5, 0));
		long seed = (long) ((-Math.random() + 0.5) * 2 * Long.MAX_VALUE);// =
																			// -1310318952414076928L;//
		Maze.genMaze(walls, seed);
		System.out.println(seed);
		Vec3 tmp = getNonWall((int) (Math.random() * walls.length * walls[0].length * walls[0][0].length));
		// System.out.println("here");
		spawnEntity(currentPlayer = new Player(this, tmp));
		for(int i=1; i<players; i++)
			spawnEntity(new Player(this, tmp));
		// System.out.println("here");
	}

	private Vec3 getNonWall(int start) {
		Vec3 tmp;
		int xm = walls.length;
		int ym = xm  * walls[0].length;
		int zm = ym  * walls[0][0].length;
		start %= zm;
		int c = start;
		do {
			int x,y,z;
			z = c/ym;
			y = (c-z*ym)/xm;
			x = c-z*ym-y*xm;
			if (!isWall(tmp = Vec3(x, y, z)))
				return tmp;
			c++;
			c %= zm;
		} while (c != start);
		return Vec3();
	}
	public boolean isWall(Vec3 pos) {
		return get(pos).block == Wall.INSTANCE;
	}

	public BlockData get(Vec3 pos) {
		int x = (int) Math.floor(pos.x() + 0.5), y = (int) Math.floor(pos.y() + 0.5), z = (int) Math.floor(pos.z() + 0.5);
		if (x < 0 || x >= walls.length)
			return new BlockData(Wall.INSTANCE, Vec3(x, y, z));
		if (y < 0 || y >= walls[x].length)
			return new BlockData(Wall.INSTANCE, Vec3(x, y, z));
		if (z < 0 || z >= walls[x][y].length)
			return new BlockData(Wall.INSTANCE, Vec3(x, y, z));
		return walls[x][y][z];
	}
	public Vec3 getDimensions() {
		return Vec3(walls.length, walls[0].length, walls[0][0].length);
	}

	public void spawnEntity(Entity entity) {
		Random r = new Random();
		do {
			entity.id = new UUID(r.nextLong(), r.nextLong());
		} while (entities.containsKey(entity.id));
		entityList.add(entity);
		entities.put(entity.id, entity);
	}

	private final List<Entity> unmod = Collections.unmodifiableList(entityList);
	public List<Entity> getEntities() {
		return unmod;
	}

	public void tick(long deltaTime) {
		nextRandomBomb -= deltaTime / 1000_000_000d;
		if (nextRandomBomb < 0) {
			// System.out.println("bombPlaced");
			nextRandomBomb = 5;
			//spawnEntity(new Bomb(this, getNonWall((int) (Math.random() * 100)), null, (int) (Math.random() * 10), (int) (Math.random() * 10) + 10));
		}
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			e.tick(deltaTime);
		}
		int size = entityList.size();
		//for (int i = 0; i < entityList.size(); i++) {
			//Entity e = entityList.get(i);
			//e.fall(deltaTime);
		//}
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			for (int j = i + 1; j < size; j++) {
				Entity e2 = entityList.get(j);
				if (e.getPos().distanceSmaller(e2.getPos(), e.getSize() + e2.getSize())) {
					e.onEntityCollideWith(e2);
				}
			}
			e.moveOutOfWall(deltaTime / 1000_000_000d);
		}
		Iterator<Entity> it = entityList.iterator();
		while (it.hasNext()) {
			Entity e = it.next();
			if (e.isDead()) {
				it.remove();
				entities.remove(e.id);
			}
		}
	}

	public Player currentPlayer() {
		return currentPlayer;
	}

	public static void fillMaze(BlockData[][][] maze, Block type) {
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[i].length; j++) {
				for (int k = 0; k < maze[i][j].length; k++) {
					maze[i][j][k] = new BlockData(type, Vec3(i, j, k));
				}
			}
		}
	}

	private static void genMaze(BlockData[][][] maze, long seed) {
		Random rand = new Random(seed);
		int x = rand.nextInt(maze.length);
		int y = rand.nextInt(maze[x].length);
		int z = rand.nextInt(maze[x][y].length);
		randomWay(x, y, z, maze);
		boolean again;
		do {
			again = false;
			for (int i = 0; i < maze.length; i++) {
				for (int j = 0; j < maze[i].length; j++) {
					for (int k = 0; k < maze[i][j].length; k++) {
						if (isBlocked(i, j, k, maze)) {
							if (!isFilled(i - 2, j, k, maze) && isFilled(i - 1, j + 1, k, maze) && isFilled(i - 1, j - 1, k, maze) && isFilled(i - 1, j, k + 1, maze) && isFilled(i - 1, j, k - 1, maze)) {
								maze[i - 1][j][k] = new BlockData(Air.INSTANCE, Vec3(i - 1, j, k));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i + 2, j, k, maze) && isFilled(i + 1, j + 1, k, maze) && isFilled(i + 1, j - 1, k, maze) && isFilled(i + 1, j, k + 1, maze) && isFilled(i + 1, j, k - 1, maze)) {
								maze[i + 1][j][k] = new BlockData(Air.INSTANCE, Vec3(i + 1, j, k));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i, j - 2, k, maze) && isFilled(i + 1, j - 1, k, maze) && isFilled(i - 1, j - 1, k, maze) && isFilled(i, j - 1, k + 1, maze) && isFilled(i, j - 1, k - 1, maze)) {
								maze[i][j - 1][k] = new BlockData(Air.INSTANCE, Vec3(i, j - 1, k));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i, j + 2, k, maze) && isFilled(i + 1, j + 1, k, maze) && isFilled(i - 1, j + 1, k, maze) && isFilled(i, j + 1, k + 1, maze) && isFilled(i, j + 1, k - 1, maze)) {
								maze[i][j + 1][k] = new BlockData(Air.INSTANCE, Vec3(i, j + 1, k));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i, j, k - 2, maze) && isFilled(i + 1, j, k - 1, maze) && isFilled(i - 1, j, k - 1, maze) && isFilled(i, j + 1, k - 1, maze) && isFilled(i, j - 1, k - 1, maze)) {
								maze[i][j][k - 1] = new BlockData(Air.INSTANCE, Vec3(i, j, k - 1));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i, j, k + 2, maze) && isFilled(i + 1, j, k + 1, maze) && isFilled(i - 1, j, k + 1, maze) && isFilled(i, j + 1, k + 1, maze) && isFilled(i, j - 1, k + 1, maze)) {
								maze[i][j][k + 1] = new BlockData(Air.INSTANCE, Vec3(i, j, k + 1));
								randomWay(i, j, k, maze);
								again = true;
							}
						}
					}
				}
			}
		} while (again);
	}

	private static void randomWay(int x, int y, int z, BlockData[][][] maze) {
		Random rand = new Random();
		maze[x][y][z] = new BlockData(Air.INSTANCE, Vec3(x, y, z));
		int steps = rand.nextInt(10);
		while (steps-- > 0) {
			int nx = x;
			int ny = y;
			int nz = z;
			switch (rand.nextInt(6)) {
				case 0 :
					nx++;
					break;
				case 1 :
					nx--;
					break;
				case 2 :
					ny++;
					break;
				case 3 :
					ny--;
					break;
				case 4 :
					nz++;
					break;
				case 5 :
					nz--;
					break;
			}
			if (nx >= 0 && ny >= 0 && nz >= 0 && nx < maze.length && ny < maze[nx].length && nz < maze[nx][ny].length && isFilled(nx, ny, nz, maze) && (isFilled(nx + 1, ny, nz, maze) || (nx + 1 == x && ny == y && nz == z)) && (isFilled(nx - 1, ny, nz, maze) || (nx - 1 == x && ny == y && nz == z))
					&& (isFilled(nx, ny + 1, nz, maze) || (nx == x && ny + 1 == y && nz == z)) && (isFilled(nx, ny - 1, nz, maze) || (nx == x && ny - 1 == y && nz == z)) && (isFilled(nx, ny, nz + 1, maze) || (nx == x && ny == y && nz + 1 == z))
					&& (isFilled(nx, ny, nz - 1, maze) || (nx == x && ny == y && nz - 1 == z))) {
				x = nx;
				y = ny;
				z = nz;
				maze[x][y][z] = new BlockData(Air.INSTANCE, Vec3(x, y, z));
			}
		}
	}

	public static boolean isFilled(int x, int y, int z, BlockData[][][] maze) {
		return x < 0 || x >= maze.length || y < 0 || y >= maze[x].length || z < 0 || z >= maze[x][y].length || maze[x][y][z].block == Wall.INSTANCE;
	}

	public static boolean isBlocked(int x, int y, int z, BlockData[][][] maze) {
		return isFilled(x, y, z, maze) && isFilled(x + 1, y, z, maze) && isFilled(x - 1, y, z, maze) && isFilled(x, y + 1, z, maze) && isFilled(x, y - 1, z, maze) && isFilled(x, y, z + 1, maze) && isFilled(x, y, z - 1, maze);
	}

	public Entity getEntity(UUID uuid) {
		return entities.get(uuid);
	}

}
