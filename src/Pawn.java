import java.util.ArrayList;
import java.util.Collection;
//import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pawn extends Piece {

	public Pawn(final int position, final PieceColor color) {
		super(Type.PAWN, position, color, true);
	}

	public Pawn(final int position, final PieceColor color, final boolean firstMove) {
		super(Type.PAWN, position, color, firstMove);
	}
	
	// each tile has each number from 0 to 63
	// pawn moves directly one or two squares (8, 16)
	// pawn moves diagonally forward when capturing an opponent's piece (7, 9)
	private final static int[] CANDIDATE_MOVE_COORDINATES = { 8, 16, 7, 9 };

	public Collection<Move> calculateMoves(Board board) {
		// use Set to avoid duplicates
		// duplicates cause multiple green dots, or legal moves to appear on a tile
		final Set<Move> legalMoves = new HashSet<>();
		for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			// pawn: direction of movement is important
			final int candidateCoordinate = this.position + (this.color.getDirection() * currentCandidateOffset);
			// candidate coordinate is in bounds of the board
			if (!isValidTileCoordinate(candidateCoordinate)) {
				continue;
			}
			if (currentCandidateOffset == 8 && !board.getTile(candidateCoordinate).occupied()) {
				legalMoves.add(new Move.PawnMove(board, this, candidateCoordinate));
				if (this.color.isPawnPromotionSquare(candidateCoordinate)) {
					legalMoves.add(new Move.PawnPromotion(new Move.PawnMove(board, this, candidateCoordinate)));
				} else {
					legalMoves.add(new Move.PawnMove(board, this, candidateCoordinate));
				}
			// pawn jump (16 tiles): the move should be the first
			// black player: second row, white player: seventh row
			} else if (currentCandidateOffset == 16 && this.firstMove()
					&& ((secondRow.contains(this.position) && this.getColor().black())
							|| (seventhRow.contains(this.position) && this.getColor().white()))) {
				// getDirection: white: -1, black: 1
				final int behindCandidateCoordinate = this.position + (this.color.getDirection() * 8);
				// if there is no piece on the two tiles in front of the piece 
				if (!board.getTile(behindCandidateCoordinate).occupied()
						&& !board.getTile(candidateCoordinate).occupied()) {
					// pawn jump is possible
					legalMoves.add(new Move.PawnJump(board, this, candidateCoordinate));
				}
			// if you are on the second (if you're white) or seven (if you're black) row
			// if there is an enemy piece on a tile that is one square diagonally forward to the right
			} else if (currentCandidateOffset == 7 && !((eighthCol.contains(this.position) && this.color.white()
					|| (firstCol.contains(this.position) && this.color.black())))) {
				// if there is no piece on the candidate tile
				if (board.getTile(candidateCoordinate).occupied()) {
					final Piece pieceOnCandidate = board.getTile(candidateCoordinate).getPiece();
					// if the piece is your enemy
					if (this.color != pieceOnCandidate.getColor()) {
						if (this.color.isPawnPromotionSquare(candidateCoordinate)) {
							// add the piece to the PawnPromotion
							legalMoves.add(new Move.PawnPromotion(
									new Move.PawnAttackMove(board, this, candidateCoordinate, pieceOnCandidate)));
						} else {
							// otherwise, add the piece to the PawnAttackMove
							legalMoves.add(new Move.PawnAttackMove(board, this, candidateCoordinate, pieceOnCandidate));
						}
					}
				} else if (board.getEnPassantPawn() != null) {
					// getOppositeDirection: white: 1, black: -1
					// when the EnPassantPawn is on your right
					if (board.getEnPassantPawn()
							.getPosition() == (this.position + (this.color.getOppositeDirection()))) {
						final Piece pieceOnCandidate = board.getEnPassantPawn();
						// it the piece is your enemy
						if (this.color != pieceOnCandidate.getColor()) {
							// add the piece to the PawnEnPassantAttackMove
							legalMoves.add(new Move.PawnEnPassantAttackMove(board, this, candidateCoordinate,
									pieceOnCandidate));
						}
					}
				}
			// if there is an enemy piece on a tile that is one square diagonally forward to the left
			} else if (currentCandidateOffset == 9 && !((firstCol.contains(this.position) && this.color.white()
					|| (eighthCol.contains(this.position) && this.color.black())))) {
				// if there is no piece on the candidate tile
				if (board.getTile(candidateCoordinate).occupied()) {
					final Piece pieceOnCandidate = board.getTile(candidateCoordinate).getPiece();
					// if the piece is your enemy
					if (this.color != pieceOnCandidate.getColor()) {
						if (this.color.isPawnPromotionSquare(candidateCoordinate)) {
							// add the piece to the PawnPromotion
							legalMoves.add(new Move.PawnPromotion(
									new Move.PawnAttackMove(board, this, candidateCoordinate, pieceOnCandidate)));
						} else {
							// otherwise, add the piece to the PawnAttackMove
							legalMoves.add(new Move.PawnAttackMove(board, this, candidateCoordinate, pieceOnCandidate));
						}
					}
				} else if (board.getEnPassantPawn() != null) {
					// getOppositeDirection: white: 1, black: -1
					// when the EnPassantPawn is on your left
					if (board.getEnPassantPawn()
							.getPosition() == (this.position - (this.color.getOppositeDirection()))) {
						final Piece pieceOnCandidate = board.getEnPassantPawn();
						// if the piece is your enemy
						if (this.color != pieceOnCandidate.getColor()) {
							// add the piece to the PawnEnPassantAttackMove
							legalMoves.add(new Move.PawnEnPassantAttackMove(board, this, candidateCoordinate,
									pieceOnCandidate));
						}
					}
				}
			}
		}
		return legalMoves;
	}

	// check if the coordinate is in bounds of the chess board
	private static boolean isValidTileCoordinate(final int coordinate) {
		return coordinate >= 0 && coordinate <= 63;
	}

	public final List<Integer> secondRow = initRow(8);
	public final List<Integer> seventhRow = initRow(48);

	public List<Integer> initRow(int start) {
		List<Integer> row = new ArrayList<>();
		for (int i = start; i < start + 8; i++) {
			row.add(i);
		}
		return row;
	}
	
	public final List<Integer> firstCol = initCol(0);
	public final List<Integer> eighthCol = initCol(7);

	public List<Integer> initCol(int mod) {
		List<Integer> row = new ArrayList<>();
		for (int i = 0; i < 64; i++) {
			if (i % 8 == mod)
				row.add(i);
		}
		return row;
	}

	@Override
	public Pawn movePiece(final Move move) {
		return new Pawn(move.getDestination(), move.getMovedPiece().getColor());
	}

	@Override
	public String toString() {
		return Type.PAWN.toString();
	}

	public Piece getPromotionPiece() {
		return new Queen(this.position, this.color, false);
	}
}