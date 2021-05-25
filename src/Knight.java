import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Knight extends Piece {
	
	// each tile has each number from 0 to 63
	// knight moves two squares vertically and one square horizontally
	// moves 6, 10, 15, or 17 tiles before or after the tile
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -17, -15, -10, -6, 6, 10, 15, 17 };

	public Knight(final int position, final PieceColor color) {
		super(Type.KNIGHT, position, color, true);
	}

	public Knight(final int position, final PieceColor color, final boolean firstMove) {
		super(Type.KNIGHT, position, color, firstMove);
	}

	@Override
	public Collection<Move> calculateMoves(final Board board) {
		// use Set to avoid duplicates
		// duplicates cause multiple green dots, or legal moves to appear on a tile
		final Set<Move> legalMoves = new HashSet<>();
		for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			// exceptions
			if (exceptFirstCol(this.position, currentCandidateOffset)
					|| exceptSecondCol(this.position, currentCandidateOffset)
					|| exceptSeventhCol(this.position, currentCandidateOffset)
					|| exceptEighthCol(this.position, currentCandidateOffset)) {
				continue;
			}
			final int candidateDestinationCoordinate = this.position + currentCandidateOffset;
			// candidate coordinate is in bounds of the board
			if (isValidTileCoordinate(candidateDestinationCoordinate)) {
				final Tile destinationTile = board.getTile(candidateDestinationCoordinate);
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
		return legalMoves;
	}

	@Override
	public Knight movePiece(final Move move) {
		return new Knight(move.getDestination(), move.getMovedPiece().getColor());
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
	// secondCol: 1, 9, 17, 25, 33... are true
	public static final boolean[] firstCol = initialCol(0);
	public static final boolean[] secondCol = initialCol(1);
	public static final boolean[] seventhCol = initialCol(6);
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
		return firstCol[current]
				&& (candidateMove == -17 || candidateMove == -10 || candidateMove == 6 || candidateMove == 15);
	}

	public static boolean exceptSecondCol(final int current, final int candidateMove) {
		return secondCol[current] && (candidateMove == -10 || candidateMove == 6);
	}

	public static boolean exceptSeventhCol(final int current, final int candidateMove) {
		return seventhCol[current] && (candidateMove == -6 || candidateMove == 10);
	}

	public static boolean exceptEighthCol(final int current, final int candidateMove) {
		return eighthCol[current]
				&& (candidateMove == -15 || candidateMove == -6 || candidateMove == 10 || candidateMove == 17);
	}
}
