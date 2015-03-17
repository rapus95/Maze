package maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import math.matrix.Vec;
import maze.blocks.Air;
import maze.blocks.Wall;
import maze.entities.Bomb;
import maze.entities.Player;

public class Maze {
	private BlockData walls[][][] = new BlockData[50][50][1];
	private Map<UUID, Entity> entities = new HashMap<>();
	private final List<Entity> entityList = new ArrayList<>();
	public Player currentPlayer;
	private double nextRandomBomb = 5;
	private boolean fixedGravity = true;

	public Maze() {
		// entities[1] = new Player(this, tmp);
		Maze.fillMaze(walls, Air.INSTANCE);
		walls[5][5][0] = new BlockData(Wall.INSTANCE, Vec.fromList(5, 5, 0));
//		Maze.genMaze(walls);
		Vec tmp = getNonWall((int) (Math.random() * 100));
//		System.out.println("here");
		spawnEntity(currentPlayer = new Player(this, tmp));
//		spawnEntity(new Player(this, tmp));
//		System.out.println("here");
	}

	private Vec getNonWall(int start) {
		Vec tmp;
		start %= walls.length * walls[0].length * walls[0][0].length;
		for (int i = start / walls.length; i < walls.length; i++) {
			for (int j = start / walls.length / walls[0].length; j < i && j < walls[i].length; j++) {
				for (int k = start % walls[0][0].length; k < i && k < walls[i].length && k < j && k < walls[i][j].length; k++) {
					if (!isWall(tmp = Vec.fromList(i, j, k)))
						return tmp;
				}
			}
		}
		return new Vec(3);
	}
	public boolean isWall(Vec pos) {
		return get(pos).block == Wall.INSTANCE;
	}

	public BlockData get(Vec pos) {
		int x = (int) Math.floor(pos.getComponent(0) + 0.5), y = (int) Math.floor(pos.getComponent(1) + 0.5), z = (int) Math.floor(pos.getComponent(2) + 0.5);
		if (x < 0 || x >= walls.length)
			return new BlockData(Wall.INSTANCE, Vec.fromList(x, y, z));
		if (y < 0 || y >= walls[x].length)
			return new BlockData(Wall.INSTANCE, Vec.fromList(x, y, z));
		if (z < 0 || z >= walls[x][y].length)
			return new BlockData(Wall.INSTANCE, Vec.fromList(x, y, z));
		return walls[x][y][z];
	}
	public Vec getDimensions() {
		return Vec.fromList(walls.length, walls[0].length, walls[0][0].length);
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
//			System.out.println("bombPlaced");
			nextRandomBomb = 5;
			//spawnEntity(new Bomb(this, getNonWall((int) (Math.random() * 100)), null, (int) (Math.random() * 10), (int) (Math.random() * 10) + 10));
		}
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			e.tick(deltaTime);
		}
		int size = entityList.size();
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			e.fall();
		}
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			for (int j = i + 1; j < size; j++) {
				Entity e2 = entityList.get(j);
				if (e.getPos().distanceToSmaller(e2.getPos(), e.getSize() + e2.getSize())) {
					e.onEntityCollideWith(e2);
				}
			}
			e.moveOutOfWall();
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
					maze[i][j][k] = new BlockData(type, Vec.fromList(i, j, k));
				}
			}
		}
	}

	private static void genMaze(BlockData[][][] maze) {
		Random rand = new Random();
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
								maze[i - 1][j][k] = new BlockData(Air.INSTANCE, Vec.fromList(i - 1, j, k));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i + 2, j, k, maze) && isFilled(i + 1, j + 1, k, maze) && isFilled(i + 1, j - 1, k, maze) && isFilled(i + 1, j, k + 1, maze) && isFilled(i + 1, j, k - 1, maze)) {
								maze[i + 1][j][k] = new BlockData(Air.INSTANCE, Vec.fromList(i + 1, j, k));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i, j - 2, k, maze) && isFilled(i + 1, j - 1, k, maze) && isFilled(i - 1, j - 1, k, maze) && isFilled(i, j - 1, k + 1, maze) && isFilled(i, j - 1, k - 1, maze)) {
								maze[i][j - 1][k] = new BlockData(Air.INSTANCE, Vec.fromList(i, j - 1, k));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i, j + 2, k, maze) && isFilled(i + 1, j + 1, k, maze) && isFilled(i - 1, j + 1, k, maze) && isFilled(i, j + 1, k + 1, maze) && isFilled(i, j + 1, k - 1, maze)) {
								maze[i][j + 1][k] = new BlockData(Air.INSTANCE, Vec.fromList(i, j + 1, k));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i, j, k - 2, maze) && isFilled(i + 1, j, k - 1, maze) && isFilled(i - 1, j, k - 1, maze) && isFilled(i, j + 1, k - 1, maze) && isFilled(i, j - 1, k - 1, maze)) {
								maze[i][j][k - 1] = new BlockData(Air.INSTANCE, Vec.fromList(i, j, k - 1));
								randomWay(i, j, k, maze);
								again = true;
							} else if (!isFilled(i, j, k + 2, maze) && isFilled(i + 1, j, k + 1, maze) && isFilled(i - 1, j, k + 1, maze) && isFilled(i, j + 1, k + 1, maze) && isFilled(i, j - 1, k + 1, maze)) {
								maze[i][j][k + 1] = new BlockData(Air.INSTANCE, Vec.fromList(i, j, k + 1));
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
		maze[x][y][z] = new BlockData(Air.INSTANCE, Vec.fromList(x, y, z));
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
				maze[x][y][z] = new BlockData(Air.INSTANCE, Vec.fromList(x, y, z));
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
	
	public boolean isGravityFixed(){
		return fixedGravity;
	}

}
