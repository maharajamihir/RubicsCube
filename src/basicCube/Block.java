package basicCube;

public class Block {

	private FieldColor top;
	private FieldColor bottom;
	private FieldColor right;
	private FieldColor left;
	private FieldColor front;
	private FieldColor back;

	public Block(FieldColor top, FieldColor right, FieldColor bottom, FieldColor left, FieldColor front,
			FieldColor back) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
		this.front = front;
		this.back = back;
	}

	public Block() {
	}

	public Block turnRight() {
		FieldColor tmp = right;
		right = front;
		front = left;
		left = back;
		back = tmp;
		return this;
	}

	public Block turnLeft() {
		FieldColor tmp = left;
		left = front;
		front = right;
		right = back;
		back = tmp;
		return this;
	}

	public Block turnUp() {
		FieldColor tmp = top;
		top = front;
		front = bottom;
		bottom = back;
		back = tmp;
		return this;
	}

	public Block turnDown() {
		FieldColor tmp = bottom;
		bottom = front;
		front = top;
		top = back;
		back = tmp;
		return this;
	}

	// getters and setters
	public FieldColor getBack() {
		return back;
	}

	public FieldColor getBottom() {
		return bottom;
	}

	public FieldColor getFront() {
		return front;
	}

	public FieldColor getLeft() {
		return left;
	}

	public FieldColor getRight() {
		return right;
	}

	public FieldColor getTop() {
		return top;
	}

	public void setBack(FieldColor back) {
		this.back = back;
	}

	public void setBottom(FieldColor bottom) {
		this.bottom = bottom;
	}

	public void setFront(FieldColor front) {
		this.front = front;
	}

	public void setLeft(FieldColor left) {
		this.left = left;
	}

	public void setRight(FieldColor right) {
		this.right = right;
	}

	public void setTop(FieldColor top) {
		this.top = top;
	}

	public Block createCopy() {
		return new Block(top, right, bottom, left, front, back);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("  ________" + System.lineSeparator());
		s.append(" /   " + FieldColor.getCharRepresentation(top) + "   /|" + System.lineSeparator());
		s.append("/_______/ |" + System.lineSeparator());
		s.append("|       | |" + System.lineSeparator());
		s.append("|   " + FieldColor.getCharRepresentation(front) + "   |" + FieldColor.getCharRepresentation(right)
				+ "/" + System.lineSeparator());
		s.append("|_______|/" + System.lineSeparator());
		return s.toString();
	}
}
