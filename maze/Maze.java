package maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import math.matrix.IVec;
import math.matrix.Vec;
import maze.blocks.Air;
import maze.blocks.Wall;
import maze.entities.Bomb;
import maze.entities.Player;

public class Maze {
	private BlockData walls[][] = new BlockData[50][50];
	private Map<UUID, Entity> entities = new HashMap<>();
	private final List<Entity> entityList = new ArrayList<>();
	public Player currentPlayer;
	private double nextRandomBomb=5;

	public Maze() {
		// entities[1] = new Player(this, tmp);
		Maze.fillMaze(walls);
		Maze.genMaze(walls);
		Vec tmp = getNonWall((int)(Math.random()*100));
		System.out.println("here");
		spawnEntity(currentPlayer = new Player(this, tmp));
		spawnEntity(new Player(this, tmp));
		System.out.println("here");
	}

	private Vec getNonWall(int start) {
		Vec tmp;
		start%=walls.length*walls[0].length;
		for (int i = start/walls.length; i < walls.length; i++) {
			for (int j = start%walls[0].length; j < i && j < walls[i].length; j++) {
				if (!isWall(tmp = new Vec((double) i, j)))
					return tmp;
			}
		}
		return new Vec(2);
	}

	public boolean isWall(IVec pos) {
		return get(pos).block == Wall.INSTANCE;
	}

	public BlockData get(IVec pos) {
		int x = (int) Math.floor(pos.getComponent(0) + 0.5), z = (int) Math.floor(pos.getComponent(1) + 0.5);
		if (x < 0 || x >= walls.length)
			return new BlockData(Wall.INSTANCE, new Vec((double) x, z));
		if (z < 0 || z >= walls[0].length)
			return new BlockData(Wall.INSTANCE, new Vec((double) x, z));
		return walls[x][z];
	}
	public IVec getDimensions() {
		return new Vec((double) walls.length, walls[0].length);
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
		nextRandomBomb-=deltaTime/1000_000_000d;
		if(nextRandomBomb<0){
			System.out.println("bombPlaced");
			nextRandomBomb = 5;
			spawnEntity(new Bomb(this, getNonWall((int)(Math.random()*100)), null, (int)(Math.random()*10), (int)(Math.random()*10)+10));
		}
		for (int i = 0; i < entityList.size(); i++) {
			Entity e = entityList.get(i);
			e.tick(deltaTime);
		}
		int size = entityList.size();
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

	public static void fillMaze(BlockData[][] maze) {
		for (int i = 0; i < maze.length; i++) {
			for (int j = 0; j < maze[i].length; j++) {
				maze[i][j] = new BlockData(Wall.INSTANCE, new Vec((double) i, j));
			}
		}
	}

	private static void genMaze(BlockData[][] maze) {
		Random rand = new Random();
		int x = rand.nextInt(maze.length);
		int y = rand.nextInt(maze[x].length);
		randomWay(x, y, maze);
		boolean again;
		do {
			again = false;
			for (int i = 0; i < maze.length; i++) {
				for (int j = 0; j < maze[i].length; j++) {
					if (isBlocked(i, j, maze)) {
						if (!isFilled(i - 2, j, maze) && isFilled(i - 1, j + 1, maze) && isFilled(i - 1, j - 1, maze)) {
							maze[i - 1][j] = new BlockData(Air.INSTANCE, new Vec(i - 1.0, j));
							randomWay(i, j, maze);
							again = true;
						} else if (!isFilled(i + 2, j, maze) && isFilled(i + 1, j + 1, maze) && isFilled(i + 1, j - 1, maze)) {
							maze[i + 1][j] = new BlockData(Air.INSTANCE, new Vec(i + 1.0, j));
							randomWay(i, j, maze);
							again = true;
						} else if (!isFilled(i, j - 2, maze) && isFilled(i + 1, j - 1, maze) && isFilled(i - 1, j - 1, maze)) {
							maze[i][j - 1] = new BlockData(Air.INSTANCE, new Vec((double) i, j - 1));
							randomWay(i, j, maze);
							again = true;
						} else if (!isFilled(i, j + 2, maze) && isFilled(i + 1, j + 1, maze) && isFilled(i - 1, j + 1, maze)) {
							maze[i][j + 1] = new BlockData(Air.INSTANCE, new Vec((double) i, j + 1));
							randomWay(i, j, maze);
							again = true;
						}
					}
				}
			}
		} while (again);
	}

	private static void randomWay(int x, int y, BlockData[][] maze) {
		Random rand = new Random();
		maze[x][y] = new BlockData(Air.INSTANCE, new Vec((double) x, y));
		int steps = rand.nextInt(10);
		while (steps-- > 0) {
			int nx = x;
			int ny = y;
			switch (rand.nextInt(4)) {
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
			}
			if (nx >= 0 && ny >= 0 && nx < maze.length && ny < maze[nx].length && isFilled(nx, ny, maze) && (isFilled(nx + 1, ny, maze) || (nx + 1 == x && ny == y)) && (isFilled(nx - 1, ny, maze) || (nx - 1 == x && ny == y)) && (isFilled(nx, ny + 1, maze) || (nx == x && ny + 1 == y))
					&& (isFilled(nx, ny - 1, maze) || (nx == x && ny - 1 == y))) {
				x = nx;
				y = ny;
				maze[x][y] = new BlockData(Air.INSTANCE, new Vec((double) x, y));
			}
		}
	}

	public static boolean isFilled(int x, int y, BlockData[][] maze) {
		return x < 0 || x >= maze.length || y < 0 || y >= maze[x].length || maze[x][y].block == Wall.INSTANCE;
	}

	public static boolean isBlocked(int x, int y, BlockData[][] maze) {
		return isFilled(x, y, maze) && isFilled(x + 1, y, maze) && isFilled(x - 1, y, maze) && isFilled(x, y + 1, maze) && isFilled(x, y - 1, maze);
	}

	public Entity getEntity(UUID uuid) {
		return entities.get(uuid);
	}

}
