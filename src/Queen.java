import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Queen extends Piece {
	
	// each tile has each number from 0 to 63
	// bishop moves in any direction diagonally, vertically, and horizontally
	// moves 1, 7, 8, or 9 tiles before or after the tile
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };

	public Queen(final int position, final PieceColor color) {
		super(Type.QUEEN, position, color, true);
	}

	public Queen(final int position, final PieceColor color, final boolean firstMove) {
		super(Type.QUEEN, position, color, firstMove);
	}

	@Override
	public Collection<Move> calculateMoves(final Board board) {
		final Set<Move> legalMoves = new HashSet<>();
		for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			int candidateDestinationCoordinate = this.position;
			// exceptions
			while (isValidTileCoordinate(candidateDestinationCoordinate)) {
				if (exceptFirstCol(candidateDestinationCoordinate, currentCandidateOffset)
						|| exceptEighthCol(candidateDestinationCoordinate, currentCandidateOffset)) {
					break;
				}
				candidateDestinationCoordinate += currentCandidateOffset;
				// candidate coordinate is in bounds of the board
				if (isValidTileCoordinate(candidateDestinationCoordinate)) {
					final Tile destinationTile = board.getTile(candidateDestinationCoordinate);
					// if there is no piece on the tile that the piece is going to
					if (!destinationTile.occupied()) {
						// add the piece to NonAttackingMove
						legalMoves.add(new Move.NonAttackingMove(board, this, candidateDestinationCoordinate));
					// if there is a piece on the tile that the piece is going to
					} else {
						final PieceColor pieceAtDestinationColor = destinationTile.getPiece().getColor();
						// if the piece is your enemy
						if (this.color != pieceAtDestinationColor) {
							// add the piece to MajorAttackMove
							legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoordinate,
									destinationTile.getPiece()));
						}
						break;
					}
				}
			}
		}
		return legalMoves;
	}

	@Override
	public Queen movePiece(final Move move) {
		return new Queen(move.getDestination(), move.getMovedPiece().getColor());
	}

	@Override
	public String toString() {
		return this.type.toString();
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