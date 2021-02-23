package basicCube;

import java.awt.Color;

public enum FieldColor {
	RED, ORANGE, GREEN, BLUE, WHITE, YELLOW;
	private FieldColor opposite;
	public static final Color MYORANGE = new Color(255, 102, 0);
	public static final Color MYGREEN = new Color(0, 102, 0);
	
	public void setOpposite(FieldColor opposite) {
		this.opposite = opposite;
		opposite.opposite = this;
	}

	public FieldColor getOpposite() {
		return opposite;
	}

	public static char getCharRepresentation(FieldColor color) {
		if (color == null)
			return ' ';
		switch (color) {
		case BLUE:
			return 'b';
		case GREEN:
			return 'g';
		case ORANGE:
			return 'o';
		case RED:
			return 'r';
		case WHITE:
			return 'w';
		case YELLOW:
			return 'y';
		default:
			return ' ';
		}
	}

	@Override
	public String toString() {
		return "" + getCharRepresentation(this);
	}

	public Color getColor() {
		switch (this) {
		case BLUE:
			return Color.BLUE;
		case GREEN:
			return MYGREEN;
		case ORANGE:
			return MYORANGE;
		case RED:
			return Color.RED;
		case WHITE:
			return Color.WHITE;
		case YELLOW:
			return Color.YELLOW;
		default:
			return Color.GRAY;

		}
	}
}
