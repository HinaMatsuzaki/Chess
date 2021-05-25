import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class King extends Piece {

	// each tile has each number from 0 to 63
	// king moves one square in any direction 
	// moves 1, 7, 8 or 9 tiles before or after the tile
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };
	private final boolean isCastled;
	private final boolean kingSideCastleCapable;
	private final boolean queenSideCastleCapable;

	public King(final int position, final PieceColor color, final boolean kingSideCastleCapable,
			final boolean queenSideCastleCapable) {
		super(Type.KING, position, color, true);
		this.isCastled = false;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}

	public King(final int position, final PieceColor color, final boolean firstMove, final boolean isCastled,
			final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
		super(Type.KING, position, color, firstMove);
		this.isCastled = isCastled;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}

	public boolean isCastled() {
		return this.isCastled;
	}
	
	// king side
	public boolean isKingSideCastleCapable() {
		return this.kingSideCastleCapable;
	}

	// queen side
	public boolean isQueenSideCastleCapable() {
		return this.queenSideCastleCapable;
	}

	@Override
	public Collection<Move> calculateMoves(final Board board) {
		// use Set to avoid duplicates
		// duplicates cause multiple green dots, or legal moves to appear on a tile
		final Set<Move> legalMoves = new HashSet<>();
		for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			// exceptions
			if (exceptFirstCol(this.position, currentCandidateOffset)
					|| exceptEighthCol(this.position, currentCandidateOffset)) {
				continue;
			}
			final int candidateDestinationCoordinate = this.position + currentCandidateOffset;
			// candidate coordinate is in bounds of the board
			if (isValidTileCoordinate(candidateDestinationCoordinate)) {
				Tile destinationTile = board.getTile(candidateDestinationCoordinate);
				if (true) {
					// if there is no piece on the tile that the piece is going to
					if (!destinationTile.occupied()) {
						// add the piece to NonAttackingMove
						legalMoves.add(new Move.NonAttackingMove(board, this, candidateDestinationCoordinate));
					} else {
						final PieceColor pieceAtDestinationColor = destinationTile.getPiece().getColor();
						// if the piece is your enemy
						if (this.color != pieceAtDestinationColor) {
							// add the piece to MajorAttackMove
							legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoordinate,
									destinationTile.getPiece()));
						}
					}
				}
			}
		}
		return legalMoves;
	}

	@Override
	public String toString() {
		return this.type.toString();
	}

	@Override
	public King movePiece(final Move move) {
		return new King(move.getDestination(), this.color, false, move.isCastlingMove(), false, false);
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof King)) {
			return false;
		}
		if (!super.equals(other)) {
			return false;
		}
		final King king = (King) other;
		return isCastled == king.isCastled;
	}

	@Override
	public int hashCode() {
		return (31 * super.hashCode()) + (isCastled ? 1 : 0);
	}

	// check if the coordinate is in bounds of the chess board
	private static boolean isValidTileCoordinate(final int coordinate) {
		return coordinate >= 0 && coordinate <= 63;
	}

	// boolean array of size 64
	// all of the values are false except for the values that corresponds to the column
	// firstCol: 0, 8, 16, 24, 32... are true
	public static final boolean[] firstCol = initialCol(0);
	public static final boolean[] eighthCol = initialCol(7);

	private static boolean[] initialCol(int colNum) {
		final boolean[] col = new boolean[64];
		for (int i = 0; i < col.length; i++) {
			col[i] = false;
		}
		do {
			col[colNum] = true;
			colNum += 8;
		} while (colNum < 64);
		return col;
	}

	// exceptions
	// int current: current position
	// int candidateMove: values in the possibleMoves list
	public static boolean exceptFirstCol(final int current, final int candidateMove) {
		return firstCol[current] && (candidateMove == -9 || candidateMove == -1 || candidateMove == 7);
	}

	public static boolean exceptEighthCol(final int current, final int candidateMove) {
		return eighthCol[current] && (candidateMove == -7 || candidateMove == 1 || candidateMove == 9);
	}
}