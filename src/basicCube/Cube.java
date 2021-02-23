package basicCube;

import static basicCube.FieldColor.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Cube {
	private final int size;
	public final Block[][][] cube;

	public Cube(int size) {
		size = 3;
		if (size < 2)
			throw new IllegalArgumentException("Size must be at least 2");
		this.size = size;
		cube = new Block[size][size][size];
		RED.setOpposite(ORANGE);
		GREEN.setOpposite(BLUE);
		WHITE.setOpposite(YELLOW);
		createCube();
	}

	private Cube(Block[][][] cube) {
		this.cube = cube;
		this.size = cube.length;
	}

	public void turnUp(int xCoordinate) {
		if (xCoordinate >= size)
			throw new IllegalArgumentException("You are trying to turn a side, which isnt in the cube!");
		try {
			Map<Coordinate, Coordinate> turnMap = Files.lines(Path.of("src/basicCube/turnMap.csv"))
					.dropWhile(line -> !line.contains("UP")).skip(1).takeWhile(line -> !line.contains("done"))
					.map(line -> line.replaceAll("c", "" + xCoordinate).replaceAll("s", "" + (size - 1)))
					.map(line -> line.split(","))
					.collect(Collectors.toMap(left -> new Coordinate(left[0]), li -> new Coordinate(li[1])));

			List<Pair<Block, Coordinate>> blockList = IntStream.range(0, size)
					.mapToObj(y -> IntStream.range(0, size)
							.mapToObj(z -> new Pair<Block, Coordinate>(cube[xCoordinate][y][z],
									new Coordinate(xCoordinate, y, z))))
					.reduce(Stream.empty(), (s1, s2) -> Stream.concat(s1, s2)).collect(Collectors.toList());
			blockList.stream().forEach(pair -> {
				Block block = pair.t;
				block.turnUp();
				if (turnMap.containsKey(pair.u)) {
					Coordinate turnTo = turnMap.get(pair.u);
					cube[turnTo.getX()][turnTo.getY()][turnTo.getZ()] = block;
				}
			});
		} catch (IOException ignore) {
		}

	}

	public void turnRight(int yCoordinate) {
		if (yCoordinate >= size)
			throw new IllegalArgumentException("You are trying to turn a side, which isnt in the cube!");
		try {
			Map<Coordinate, Coordinate> turnMap = Files.lines(Path.of("src/basicCube/turnMap.csv"))
					.dropWhile(line -> !line.contains("RIGHT")).skip(1).takeWhile(line -> !line.contains("done"))
					.map(line -> line.replaceAll("c", "" + yCoordinate).replaceAll("s", "" + (size - 1)))
					.map(line -> line.split(","))
					.collect(Collectors.toMap(left -> new Coordinate(left[0]), li -> new Coordinate(li[1])));

			List<Pair<Block, Coordinate>> blockList = IntStream.range(0, size)
					.mapToObj(x -> IntStream.range(0, size)
							.mapToObj(z -> new Pair<Block, Coordinate>(cube[x][yCoordinate][z],
									new Coordinate(x, yCoordinate, z))))
					.reduce(Stream.empty(), (s1, s2) -> Stream.concat(s1, s2)).collect(Collectors.toList());
			blockList.stream().forEach(pair -> {
				Block block = pair.t;
				block.turnRight();
				if (turnMap.containsKey(pair.u)) {
					Coordinate turnTo = turnMap.get(pair.u);
					cube[turnTo.getX()][turnTo.getY()][turnTo.getZ()] = block;
				}
			});
		} catch (IOException ignore) {
		}

	}

	private void turnCubeRight() {
		IntStream.range(0, 3).forEach(y -> turnRight(y));
	}

	// methods that create cube
	private void createTopLayer() {
		for (int x = 0; x < cube.length; x++) {
			for (int z = 0; z < cube.length; z++) {
				cube[x][0][z].setTop(WHITE);
			}
		}
	}

	private void createBottomLayer() {
		for (int x = 0; x < cube.length; x++) {
			for (int z = 0; z < cube.length; z++) {
				cube[x][size - 1][z].setBottom(YELLOW);
			}
		}
	}

	private void createFrontLayer(FieldColor color) {
		for (int x = 0; x < cube.length; x++) {
			for (int y = 0; y < cube.length; y++) {
				cube[x][y][0].setFront(color);
			}
		}
	}

	private void createCube() {
		for (int x = 0; x < cube.length; x++) {
			for (int y = 0; y < cube.length; y++) {
				for (int z = 0; z < cube.length; z++) {
					cube[x][y][z] = new Block();
				}
			}
		}
		createTopLayer();
		createBottomLayer();
		createFrontLayer(BLUE);
		turnCubeRight();
		createFrontLayer(RED);
		turnCubeRight();
		createFrontLayer(GREEN);
		turnCubeRight();
		createFrontLayer(ORANGE);
		turnCubeRight();

//		createBackLayer();
//		createLeftLayer();
//		createRightLayer();
	}

	public Cube createCopy() {
		Block[][][] copy = new Block[size][size][size];
		IntStream.range(0, size)
				.forEach(x -> IntStream.range(0, size).forEach(y -> IntStream.range(0, size).forEach(z -> {
					copy[x][y][z] = cube[x][y][z].createCopy();
				})));
		return new Cube(copy);
	}

	public static void main(String args[]) {
		Cube cube = new Cube(3);
		System.out.println(cube);
		cube.turnUp(2);
		System.out.println(cube);
		System.out.println(cube.cube[0][0][0].getTop());
	}

	class Coordinate {
		private final int x;
		private final int y;
		private final int z;

		public Coordinate(String coordinateString) {
			String[] arr = coordinateString.split(";");
			this.x = Integer.parseInt(arr[0]);
			this.y = Integer.parseInt(arr[1]);
			this.z = Integer.parseInt(arr[2]);
		}

		public Coordinate(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + "," + z + ")";
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Coordinate))
				return false;
			Coordinate other = (Coordinate) obj;
			return other.x == x && other.y == y && other.z == z;
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(x) + 31 * Integer.hashCode(y) + 37 * Integer.hashCode(z);
		}
	}

	class Pair<T, U> {
		private final T t;
		private final U u;

		public Pair(T t, U u) {
			this.t = t;
			this.u = u;
		}

		public T getFirst() {
			return t;
		}

		public U getSecond() {
			return u;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Pair))
				return false;
			try {
				Pair<T, U> pair = (Pair<T, U>) obj;
				if (pair.t == t && pair.u == u)
					return true;
				if (pair.t != null && pair.t.equals(t) && pair.u == u)
					return true;
				if (pair.u != null && pair.u.equals(u) && pair.t == t)
					return true;
				else
					return pair.t.equals(t) && pair.u.equals(u);

			} catch (ClassCastException e) {
				return false;
			} catch (NullPointerException e) {
				return false;
			}
		}

		@Override
		public String toString() {
			return "(" + t.toString() + "," + u.toString() + ")";
		}
	}
}
