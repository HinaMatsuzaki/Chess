
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Tile {

	protected final int coordinate; // tile numbers from 0 to 63

	private static final Map<Integer, EmptyTile> EMPTY_TILES = createEmptyTiles();

	// create all possible empty tiles in advance to avoid constructing them again
	private static Map<Integer, EmptyTile> createEmptyTiles() {
		final Map<Integer, EmptyTile> map = new HashMap<>();
		for (int i = 0; i < 64; i++) {
			map.put(i, new EmptyTile(i));
		}
		return Collections.unmodifiableMap(map);
	}

	// create new tiles
	// if you want an empty tile, get one of the empty tiles
	// otherwise it creates a new occupied tile
	public static Tile createTile(final int coordinate, final Piece piece) {
		if (piece != null) {
			return new OccupiedTile(coordinate, piece);
		} else {
			return EMPTY_TILES.get(coordinate);
		}
	}

	// constructor
	private Tile(final int coordinate) {
		this.coordinate = coordinate;
	}

	// tell you whether the tile is occupied or not
	public abstract boolean occupied();

	// if the tile is occupied, return the piece; if not, return null
	public abstract Piece getPiece();

	public int getTileCoordinate() {
		return this.coordinate;
	}

	// empty tile: a tile that has no piece on it
	public static final class EmptyTile extends Tile {
		private EmptyTile(final int coordinate) {
			super(coordinate);
		}

		@Override
		// empty tile shows up as a -
		public String toString() {
			return "-";
		}

		@Override
		public boolean occupied() {
			return false;
		}

		@Override
		public Piece getPiece() {
			return null;
		}
	}

	// occupied tile: a tile that has a piece on it
	public static final class OccupiedTile extends Tile {

		private final Piece p;

		private OccupiedTile(int coordinate, final Piece p) {
			super(coordinate);
			this.p = p;
		}

		@Override
		public String toString() {
			return getPiece().getColor().black() ? getPiece().toString().toLowerCase() : getPiece().toString();
		}

		@Override
		public boolean occupied() {
			return true;
		}

		@Override
		public Piece getPiece() {
			return this.p;
		}
	}
}