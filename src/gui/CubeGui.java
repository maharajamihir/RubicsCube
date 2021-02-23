package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import basicCube.*;

@SuppressWarnings("serial")
public class CubeGui extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	public static CubeGui c3dtest;

	public static void main(String[] args) {
		Cube cube = new Cube(3);
		c3dtest = new CubeGui(cube);
		c3dtest.setVisible(true);
	}

	public int width = 600;
	public int height = 300;
	public JPanel panel;
	public float distance;
	public float angle;
	Point prevMove = new Point();
	public Cubelet cube;
	public Cube currentCube;

	public CubeGui(Cube currentCube) {
		super("Rubics Cube");
		this.currentCube = currentCube;
		/* view setup */
		angle = (float) Math.toRadians(40);
		distance = (width / 2) / (float) (Math.tan(angle / 2));
		System.out.println("distance: " + distance);
		panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				cube.project(g);
			}
		};
		panel.setBackground(Color.DARK_GRAY);
		panel.setPreferredSize(new Dimension(300, 300));
		add(panel);
		pack();
		setExtendedState(MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		new Thread(moveHandler).start();
		this.addKeyListener(this);

		cube = new Cubelet();

	}

	class Cubelet {

		public int size = 200; // kantelänge
		Vector3D ulf, urf, llf, lrf; // upper left front, upper right front, usw..
		Vector3D ulb, urb, llb, lrb; // upper left back ,...
		Point[][][] innerCorners = new Point[4][4][4];

		public Cubelet() {
			init();
		}

		private void init() {
			// würfel soll zentriert sein, die koordinaten für den oberen linken
			// (front-)punkt also:
			int startx = width / 2 - size / 2;
			int starty = height / 2 - size / 2;
			// alle 8 würfel-punkte:
			ulf = new Vector3D(startx, starty, -size);
			urf = new Vector3D(startx + size, starty, -size);
			llf = new Vector3D(startx, starty + size, -size);
			lrf = new Vector3D(startx + size, starty + size, -size);

			ulb = new Vector3D(startx, starty, 0);
			urb = new Vector3D(startx + size, starty, 0);
			llb = new Vector3D(startx, starty + size, 0);
			lrb = new Vector3D(startx + size, starty + size, 0);
			float size = this.size;
			float step = size / 3;
			for (int x = 0; x < 4; x++) {
				for (int y = 0; y < 4; y++) {
					for (int z = 0; z < 4; z++) {
						innerCorners[x][y][z] = new Vector3D(ulb.x + x * step, ulf.y + y * step, ulf.z + z * step)
								.to2D();
					}
				}
			}
		}

		private void move(int dx, int dy) {
			width += dx;
			ulf.x += dx;
			urf.x += dx;
			llf.x += dx;
			lrf.x += dx;
			ulb.x += dx;
			urb.x += dx;
			llb.x += dx;
			lrb.x += dx;

			height += dy;
			ulf.y += dy;
			urf.y += dy;
			llf.y += dy;
			lrf.y += dy;
			ulb.y += dy;
			urb.y += dy;
			llb.y += dy;
			lrb.y += dy;

			Arrays.stream(innerCorners)
					.forEach(arr -> Arrays.stream(arr).forEach(arr1 -> Arrays.stream(arr1).forEach(el -> {
						el.x += dx;
						el.y += dy;
					})));
			init();
		}

		private void paintTop(Graphics g) {
			for (int x = 0; x < 3; x++) {
				for (int z = 0; z < 3; z++) {
					Color color = currentCube.cube[x][0][z].getTop() == null ? Color.BLACK
							: currentCube.cube[x][0][z].getTop().getColor();
					g.setColor(color);
					g.fillPolygon(
							new int[] { innerCorners[x][0][z].x, innerCorners[x + 1][0][z].x,
									innerCorners[x + 1][0][z + 1].x, innerCorners[x][0][z + 1].x },
							new int[] { innerCorners[x][0][z].y, innerCorners[x + 1][0][z].y,
									innerCorners[x + 1][0][z + 1].y, innerCorners[x][0][z + 1].y },
							4);
					// paint borders
					g.setColor(Color.BLACK);
					g.drawLine(innerCorners[x][0][z].x, innerCorners[x][0][z].y, innerCorners[x + 1][0][z].x,
							innerCorners[x + 1][0][z].y);
					g.drawLine(innerCorners[x][0][z].x, innerCorners[x][0][z].y, innerCorners[x][0][z + 1].x,
							innerCorners[x][0][z + 1].y);
					g.drawLine(innerCorners[x + 1][0][z + 1].x, innerCorners[x + 1][0][z + 1].y,
							innerCorners[x + 1][0][z].x, innerCorners[x + 1][0][z].y);
					g.drawLine(innerCorners[x + 1][0][z + 1].x, innerCorners[x + 1][0][z + 1].y,
							innerCorners[x][0][z + 1].x, innerCorners[x][0][z + 1].y);

				}
			}
		}

		private void paintFront(Graphics g) {
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					Color color = currentCube.cube[x][y][0].getFront() == null ? Color.BLACK
							: currentCube.cube[x][y][0].getFront().getColor();

					g.setColor(color);
					g.fillPolygon(
							new int[] { innerCorners[x][y][0].x, innerCorners[x + 1][y][0].x,
									innerCorners[x + 1][y + 1][0].x, innerCorners[x][y + 1][0].x, },
							new int[] { innerCorners[x][y][0].y, innerCorners[x + 1][y][0].y,
									innerCorners[x + 1][y + 1][0].y, innerCorners[x][y + 1][0].y },
							4);

					// paint borders
					g.setColor(Color.BLACK);
					g.drawLine(innerCorners[x][y][0].x, innerCorners[x][y][0].y, innerCorners[x + 1][y][0].x,
							innerCorners[x + 1][y][0].y);
					g.drawLine(innerCorners[x][y][0].x, innerCorners[x][y][0].y, innerCorners[x][y + 1][0].x,
							innerCorners[x][y + 1][0].y);
					g.drawLine(innerCorners[x + 1][y + 1][0].x, innerCorners[x + 1][y + 1][0].y,
							innerCorners[x + 1][y][0].x, innerCorners[x + 1][y][0].y);
					g.drawLine(innerCorners[x + 1][y + 1][0].x, innerCorners[x + 1][y + 1][0].y,
							innerCorners[x][y + 1][0].x, innerCorners[x][y + 1][0].y);

				}
			}
		}

		private void paintLeft(Graphics g) {
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					Color color = currentCube.cube[0][x][y].getLeft() == null ? Color.BLACK
							: currentCube.cube[0][x][y].getLeft().getColor();
					// get color of block and paint it
					g.setColor(color);
					g.fillPolygon(
							new int[] { innerCorners[0][x][y].x, innerCorners[0][x + 1][y].x,
									innerCorners[0][x + 1][y + 1].x, innerCorners[0][x][y + 1].x },
							new int[] { innerCorners[0][x][y].y, innerCorners[0][x + 1][y].y,
									innerCorners[0][x + 1][y + 1].y, innerCorners[0][x][y + 1].y },
							4);
					// paint borders
					g.setColor(Color.BLACK);
					g.drawLine(innerCorners[0][x][y].x, innerCorners[0][x][y].y, innerCorners[0][x + 1][y].x,
							innerCorners[0][x + 1][y].y);
					g.drawLine(innerCorners[0][x][y].x, innerCorners[0][x][y].y, innerCorners[0][x][y + 1].x,
							innerCorners[0][x][y + 1].y);
					g.drawLine(innerCorners[0][x + 1][y + 1].x, innerCorners[0][x + 1][y + 1].y,
							innerCorners[0][x + 1][y].x, innerCorners[0][x + 1][y].y);
					g.drawLine(innerCorners[0][x + 1][y + 1].x, innerCorners[0][x + 1][y + 1].y,
							innerCorners[0][x][y + 1].x, innerCorners[0][x][y + 1].y);
				}
			}
		}

		public void project(Graphics g) {
			paintLeft(g);
			paintTop(g);
			paintFront(g);
		}

		float zoomFactor = 5;

		public void further() {
			size -= zoomFactor;
			init();
		}

		public void closer() {
			size += zoomFactor;
			init();
		}

	}

	private float rotX, rotY, rotZ = 0f;

	class Vector3D {
		public float x, y, z;

		public Vector3D(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public String toString() {
			return "(" + x + "," + y + "," + z + ")";
		}

		public Point to2D() {

			Vector3D v = new Vector3D(x, y, z);
			rotateVector(v, rotY, -rotX, rotZ);

			Point p;
			/* 3d -> 2d */
			float Z = distance + v.z;
			p = new Point((int) (distance * v.x / Z), (int) (distance * v.y / Z));

			p.x += width / 2;
			p.y += height / 2;

//         System.out.println(this + " -> (" + p.x + "," + p.y + ")");
			return p;
		}
	}

	@FunctionalInterface
	interface Move {
		void execute();
	}

	/// Internal Attributes
	private Map<Integer, Boolean> isKeyPressed = new HashMap<>();
	private List<Cube> states = new ArrayList<>() {
		@Override
		public boolean add(Cube e) {
			if (size() > 15)
				remove(0);
			return super.add(e);
		}
	};
	private Queue<Move> moves = new ArrayDeque<>() {
		@Override
		public boolean add(Move e) {
			if (this.size() < 5)
				return super.add(e);
			return false;
		}
	};
	private Runnable moveHandler = () -> {
		while (true) {
			try {
				Thread.sleep(5);
				if (!moves.isEmpty()) {
					states.add(currentCube.createCopy());
					moves.remove().execute();
				}
			} catch (InterruptedException ignore) {
				System.err.println("Thread was interrupted");
			}
		}
	};

	private synchronized void addRandomMove() {
		switch (new Random().nextInt(12)) {
		case 0 -> moves.add(() -> turnUp(2));
		case 1 -> moves.add(() -> turnDown(2));
		case 2 -> moves.add(() -> turnLeft(2));
		case 3 -> moves.add(() -> turnRight(2));
		case 4 -> moves.add(() -> turnUp(0));
		case 5 -> moves.add(() -> turnDown(0));
		case 6 -> moves.add(() -> turnLeft(0));
		case 7 -> moves.add(() -> turnRight(0));
		case 8 -> moves.add(() -> turnCube(x -> turnUp(x)));
		case 9 -> moves.add(() -> turnCube((x) -> turnDown(x)));
		case 10 -> moves.add(() -> turnCube((x) -> turnLeft(x)));
		case 11 -> moves.add(() -> turnCube((x) -> turnRight(x)));
		}
	}

	private synchronized void turnUp(int x) {
		currentCube.turnUp(x);
		cube.init();
		panel.repaint();
	}

	private synchronized void turnDown(int x) {
		currentCube.turnUp(x);
		currentCube.turnUp(x);
		currentCube.turnUp(x);
		cube.init();
		panel.repaint();
	}

	private synchronized void turnRight(int y) {
		currentCube.turnRight(y);
		cube.init();
		panel.repaint();
	}

	private synchronized void turnLeft(int y) {
		currentCube.turnRight(y);
		currentCube.turnRight(y);
		currentCube.turnRight(y);
		cube.init();
		panel.repaint();
	}

	private synchronized void turnCube(Consumer<Integer> c) {
		IntStream.range(0, 3).forEach(t -> c.accept(t));
	}

	private synchronized void back() {
		if (!states.isEmpty()) {
			this.currentCube = states.remove(states.size() - 1);
			cube.init();
			panel.repaint();
		}
	}

	/// Status Getters: Use these to check mouse and keyboard status
	public boolean isKeyPressed(int vkCode) {
		return isKeyPressed.getOrDefault(vkCode, false);
	}

	/// Event Handlers: The GUI uses these methods to handle user input and remember
	/// the states
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isShiftDown()) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP -> moves.add(() -> turnCube(x -> turnUp(x)));
			case KeyEvent.VK_DOWN -> moves.add(() -> turnCube((x) -> turnDown(x)));
			case KeyEvent.VK_LEFT -> moves.add(() -> turnCube((x) -> turnLeft(x)));
			case KeyEvent.VK_RIGHT -> moves.add(() -> turnCube((x) -> turnRight(x)));
			}
			return;
		}
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP -> moves.add(() -> turnUp(2));
		case KeyEvent.VK_DOWN -> moves.add(() -> turnDown(2));
		case KeyEvent.VK_LEFT -> moves.add(() -> turnLeft(2));
		case KeyEvent.VK_RIGHT -> moves.add(() -> turnRight(2));
		case KeyEvent.VK_W -> moves.add(() -> turnUp(0));
		case KeyEvent.VK_S -> moves.add(() -> turnDown(0));
		case KeyEvent.VK_A -> moves.add(() -> turnLeft(0));
		case KeyEvent.VK_D -> moves.add(() -> turnRight(0));
		case KeyEvent.VK_SPACE -> addRandomMove();
		case KeyEvent.VK_Z -> back();
		}
		isKeyPressed.put(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		isKeyPressed.put(e.getKeyCode(), false);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			// zoom in
			cube.closer();
			panel.repaint();
		} else {
			// zoom out
			cube.further();
			panel.repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		prevMove = e.getPoint();

	}

	float ROT_FACTOR = 100;

	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - prevMove.x;
		int dy = e.getY() - prevMove.y;

		// move
		cube.move(dx, dy);

		panel.repaint();
		prevMove = e.getPoint();
	}

	public void rotateVector(Vector3D p, float thetaX, float thetaY, float thetaZ) {
		float aX, aY, aZ; // temp point

		float camX = 0;
		float camY = 0;
		float camZ = 0;

		aX = p.x;
		aY = p.y;
		aZ = p.z;

		// 3D -> 2D transformation matrix calculation with rotation
		// and camera coordinate parameters
		aY = p.y;
		aZ = p.z;
		// Rotation um x-Achse
		// p[i][x] = px;
		p.y = (float) ((aY - camY) * Math.cos(thetaX) - (aZ - camZ) * Math.sin(thetaX));
		p.z = (float) ((aY - camY) * Math.sin(thetaX) + (aZ - camZ) * Math.cos(thetaX));

		aX = p.x;
		aZ = p.z;
		// Rotation um y-Achse
		p.x = (float) ((aX - camX) * Math.cos(thetaY) + (aZ - camZ) * Math.sin(thetaY));
		// p[i][y]= py;
		p.z = (float) (-(aX - camX) * Math.sin(thetaY) + (aZ - camZ) * Math.cos(thetaY));

		aY = p.y;
		aX = p.x;
		// Rotation um z-Achse
		p.x = (float) ((aX - camX) * Math.cos(thetaZ) - (aY - camY) * Math.sin(thetaZ));
		p.y = (float) ((aY - camY) * Math.cos(thetaZ) + (aX - camX) * Math.sin(thetaZ));

	}
}