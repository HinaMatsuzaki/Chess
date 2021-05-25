import java.util.ArrayList;
import java.util.List;

public enum PieceColor {

	WHITE {
		@Override
		public boolean white() {
			return true;
		}

		@Override
		public boolean black() {
			return false;
		}

		@Override
		public int getDirection() {
			return -1;
		}

		@Override
		public int getOppositeDirection() {
			return 1;
		}

		@Override
		public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return whitePlayer;
		}

		@Override
		public boolean isPawnPromotionSquare(int position) {
			return firstRow.contains(position);
		}
	},
	BLACK {
		@Override
		public boolean white() {
			return false;
		}

		@Override
		public boolean black() {
			return true;
		}

		@Override
		public int getDirection() {
			return 1;
		}

		@Override
		public int getOppositeDirection() {
			return -1;
		}

		@Override
		public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return blackPlayer;
		}

		@Override
		public boolean isPawnPromotionSquare(int position) {
			return eighthRow.contains(position);
		}
	};

	// check if the piece is white
	public abstract boolean white();

	// check if the piece is black
	public abstract boolean black();

	// need to know the direction of the movement for Pawn
	// white moves from down to up
	// black moves from up to down
	public abstract int getDirection();

	public abstract int getOppositeDirection();

	public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);

	public abstract boolean isPawnPromotionSquare(int position);

	public final List<Integer> firstRow = initRow(0);
	public final List<Integer> eighthRow = initRow(56);

	public List<Integer> initRow(int start) {
		List<Integer> row = new ArrayList<>();
		for (int i = start; i < start + 8; i++) {
			row.add(i);
		}
		return row;
	}
}