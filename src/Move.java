import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Move {

	protected final Board board;
	// moved piece
	protected final Piece p;
	// coordinate to which the piece was moved
	protected final int destination;
	// the move is the first time
	protected final boolean firstMove;

	public static final Move NULL_MOVE = new NullMove();

	// constructor
	private Move(final Board board, final Piece p, final int destination) {
		this.board = board;
		this.p = p;
		this.destination = destination;
		this.firstMove = p.firstMove();
	}

	private Move(final Board board, final int destination) {
		this.board = board;
		this.p = null;
		this.destination = destination;
		this.firstMove = false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.destination;
		result = prime * result + this.p.hashCode();
		result = prime * result + this.p.getPosition();
		return result;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Move))
			return false;
		final Move otherMove = (Move) other;
		return getCurrentCoordinate() == otherMove.getCurrentCoordinate()
				&& getDestination() == otherMove.getDestination() && getMovedPiece().equals(otherMove.getMovedPiece());
	}

	public Board getBoard() {
		return this.board;
	}

	public int getCurrentCoordinate() {
		return this.p.getPosition();
	}

	// get destination coordinate
	public int getDestination() {
		return this.destination;
	}

	// get the moved piece
	public Piece getMovedPiece() {
		return this.p;
	}

	public boolean isAttack() {
		return false;
	}

	public boolean isCastlingMove() {
		return false;
	}

	public Piece getAttackedPiece() {
		return null;
	}

	public Board execute() {
		final Board.Builder builder = new Board.Builder();
		for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
			if (!this.p.equals(piece)) {
				builder.setPiece(piece);
			}
		}
		for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
			builder.setPiece(piece);
		}
		builder.setPiece(this.p.movePiece(this));
		builder.setTurn(this.board.currentPlayer().getOpponent().getColor());
		builder.setMoveTransition(this);
		return builder.build();
	}

	public Board undo() {
		final Board.Builder builder = new Board.Builder();
		this.board.getAllPieces().forEach(builder::setPiece);
		builder.setTurn(this.board.currentPlayer().getColor());
		return builder.build();
	}

	private static List<String> initializeAlgebraicNotation() {
		return Collections.unmodifiableList(Arrays.asList(
				"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", 
				"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
				"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
				"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", 
				"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", 
				"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
				"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", 
				"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
	}

	String disambiguationFile() {
		for (final Move move : this.board.currentPlayer().getLegalMoves()) {
			if (move.getDestination() == this.destination && !this.equals(move)
					&& this.p.getType().equals(move.getMovedPiece().getType())) {
				return initializeAlgebraicNotation().get(this.p.getPosition()).substring(0, 1);
			}
		}
		return "";
	}

	public static class MajorAttackMove extends AttackingMove {
		public MajorAttackMove(final Board board, final Piece p, final int destination, final Piece attackedPiece) {
			super(board, p, destination, attackedPiece);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof MajorAttackMove && super.equals(other);
		}

		@Override
		public String toString() {
			return p.getType() + initializeAlgebraicNotation().get(this.destination);
		}
	}

	// move to an empty tile
	public static final class NonAttackingMove extends Move {
		public NonAttackingMove(final Board board, final Piece p, final int destination) {
			super(board, p, destination);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof NonAttackingMove && super.equals(other);
		}

		@Override
		public String toString() {
			return p.getType().toString() + // disambiguationFile() +
					initializeAlgebraicNotation().get(this.destination);
		}
	}

	// move to a tile that has an opponent's piece
	public static class AttackingMove extends Move {
		final Piece attackedPiece;

		public AttackingMove(final Board board, final Piece p, final int destination, final Piece attackedPiece) {
			super(board, p, destination);
			this.attackedPiece = attackedPiece;
		}

		@Override
		public int hashCode() {
			return this.attackedPiece.hashCode() + super.hashCode();
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other)
				return true;
			if (!(other instanceof AttackingMove))
				return false;
			final AttackingMove otherAttackingMove = (AttackingMove) other;
			return super.equals(otherAttackingMove) && getAttackedPiece().equals(otherAttackingMove.getAttackedPiece());
		}

		@Override
		public boolean isAttack() {
			return true;
		}

		@Override
		public Piece getAttackedPiece() {
			return this.attackedPiece;
		}
	}

	public static class PawnMove extends Move {
		public PawnMove(final Board board, final Piece p, final int destination) {
			super(board, p, destination);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnMove && super.equals(other);
		}

		@Override
		public String toString() {
			return initializeAlgebraicNotation().get(this.destination);
		}

	}

	public static class PawnAttackMove extends AttackingMove {
		public PawnAttackMove(final Board board, final Piece p, final int destination, final Piece attackedPiece) {
			super(board, p, destination, attackedPiece);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnAttackMove && super.equals(other);
		}

		@Override
		public String toString() {
			return initializeAlgebraicNotation().get(this.p.getPosition()).substring(0, 1) + "x"
					+ initializeAlgebraicNotation().get(this.destination);
		}

	}

	public static final class PawnEnPassantAttackMove extends PawnAttackMove {
		public PawnEnPassantAttackMove(final Board board, final Piece p, final int destination,
				final Piece attackedPiece) {
			super(board, p, destination, attackedPiece);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
		}

		@Override
		public Board execute() {
			final Board.Builder builder = new Board.Builder();
			for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
				if (!this.p.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
				if (!piece.equals(this.getAttackedPiece())) {
					builder.setPiece(piece);
				}
			}
			builder.setPiece(this.p.movePiece(this));
			builder.setTurn(this.board.currentPlayer().getOpponent().getColor());
			builder.setMoveTransition(this);
			return builder.build();
		}
	}

	public static class PawnPromotion extends Move {
		final Move decoratedMove;
		final Pawn promotedPawn;

		public PawnPromotion(final Move decoratedMove) { // , final Piece promotionPiece) {
			super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestination());
			this.decoratedMove = decoratedMove;
			this.promotedPawn = new Pawn(decoratedMove.getDestination(), decoratedMove.getMovedPiece().getColor(),
					true);
			// this.promotionPiece = promotionPiece;
		}

		@Override
		public int hashCode() {
			return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnPromotion && (super.equals(other));
		}

		@Override
		public Board execute() {
			System.out.println("Pawn promotion executed");
			final Board pawnMovedBoard = this.decoratedMove.execute();
			final Board.Builder builder = new Board.Builder();
			for (final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()) {
				if (!this.promotedPawn.equals(piece)) {
					builder.setPiece(piece);
				} else {
					Pawn thisPiece = (Pawn) piece;
					builder.setPiece(thisPiece.getPromotionPiece());
				}
			}
			for (final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()) {
				builder.setPiece(piece);
			}
			builder.setTurn(pawnMovedBoard.currentPlayer().getColor());
			builder.setMoveTransition(this);
			return builder.build();
		}

		@Override
		public boolean isAttack() {
			return this.decoratedMove.isAttack();
		}

		@Override
		public Piece getAttackedPiece() {
			return this.decoratedMove.getAttackedPiece();
		}

		@Override
		public String toString() {
			return initializeAlgebraicNotation().get(this.p.getPosition()) + "-"
					+ initializeAlgebraicNotation().get(this.destination); // + "=" + this.promotionPiece.getType();
		}
	}

	public static final class PawnEnPassantAttack extends PawnAttackMove {
		public PawnEnPassantAttack(final Board board, final Piece p, final int destination, final Piece attackedPiece) {
			super(board, p, destination, attackedPiece);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnEnPassantAttack && super.equals(other);
		}

		@Override
		public Board execute() {
			final Board.Builder builder = new Board.Builder();
			for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
				if (!this.p.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
				if (!piece.equals(this.getAttackedPiece())) {
					builder.setPiece(piece);
				}
			}
			builder.setPiece(this.p.movePiece(this));
			builder.setTurn(this.board.currentPlayer().getOpponent().getColor());
			return builder.build();
		}

		@Override
		public Board undo() {
			final Board.Builder builder = new Board.Builder();
			this.board.getAllPieces().forEach(builder::setPiece);
			builder.setEnPassantPawn((Pawn) this.getAttackedPiece());
			builder.setTurn(this.board.currentPlayer().getColor());
			return builder.build();
		}
	}

	public static final class PawnJump extends Move {
		public PawnJump(final Board board, final Piece p, final int destination) {
			super(board, p, destination);
		}

		public Board execute() {
			final Board.Builder builder = new Board.Builder();
			for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
				if (!this.p.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(piece);
			}
			final Pawn movedPawn = (Pawn) this.p.movePiece(this);
			builder.setPiece(movedPawn);
			builder.setEnPassantPawn(movedPawn);
			builder.setTurn(this.board.currentPlayer().getOpponent().getColor());

			builder.setMoveTransition(this);
			return builder.build();
		}

		@Override
		public String toString() {
			return initializeAlgebraicNotation().get(this.destination);
		}
	}

	static abstract class CastleMove extends Move {

		protected final Rook castleRook;
		protected final int castleRookStart;
		protected final int castleRookDestination;

		public CastleMove(final Board board, final Piece p, final int destination, final Rook castleRook,
				final int castleRookStart, final int castleRookDestination) {
			super(board, p, destination);
			this.castleRook = castleRook;
			this.castleRookStart = castleRookStart;
			this.castleRookDestination = castleRookDestination;
		}

		public Rook getCastleRook() {
			return this.castleRook;
		}

		@Override
		public boolean isCastlingMove() {
			return true;
		}

		@Override
		public Board execute() {
			final Board.Builder builder = new Board.Builder();
			for (final Piece piece : this.board.getAllPieces()) {
				if (!this.p.equals(piece) && !this.castleRook.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(piece);
			}
			// move the king to its destination location
			builder.setPiece(this.p.movePiece(this));
			// create a new rook that is on the castle side
			builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getColor(), false));
			builder.setTurn(this.board.currentPlayer().getOpponent().getColor());
			builder.setMoveTransition(this);
			return builder.build();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + this.castleRook.hashCode();
			result = prime * result + this.castleRookDestination;
			return result;
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof CastleMove)) {
				return false;
			}
			final CastleMove otherCastleMove = (CastleMove) other;
			return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
		}

	}

	public static final class KingSideCastleMove extends CastleMove {
		public KingSideCastleMove(final Board board, final Piece p, final int destination, final Rook castleRook,
				final int castleRookStart, final int castleRookDestination) {
			super(board, p, destination, castleRook, castleRookStart, castleRookDestination);
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof KingSideCastleMove)) {
				return false;
			}
			final KingSideCastleMove otherKingSideCastleMove = (KingSideCastleMove) other;
			return super.equals(otherKingSideCastleMove)
					&& this.castleRook.equals(otherKingSideCastleMove.getCastleRook());
		}

		@Override
		// PGN convention
		public String toString() {
			return "O-O";
		}
	}

	public static final class QueenSideCastleMove extends CastleMove {
		public QueenSideCastleMove(final Board board, final Piece p, final int destination, final Rook castleRook,
				final int castleRookStart, final int castleRookDestination) {
			super(board, p, destination, castleRook, castleRookStart, castleRookDestination);
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof QueenSideCastleMove)) {
				return false;
			}
			final QueenSideCastleMove otherQueenSideCastleMove = (QueenSideCastleMove) other;
			return super.equals(otherQueenSideCastleMove)
					&& this.castleRook.equals(otherQueenSideCastleMove.getCastleRook());
		}

		@Override
		// PGN convention
		public String toString() {
			return "O-O-O";
		}
	}

	public static final class NullMove extends Move {
		public NullMove() {
			super(null, 65);
		}

		@Override
		public int getCurrentCoordinate() {
			return -1;
		}

		@Override
		public Board execute() {
			throw new RuntimeException();
		}
	}

	public static class MoveFactory {

		private MoveFactory() {
			throw new RuntimeException();
		}

		public static Move getNullMove() {
			return NULL_MOVE;
		}

		public static Move createMove(final Board board, final int currentCoordinate, final int destination) {
			for (final Move move : board.getAllLegalMoves()) {
				if (move.getCurrentCoordinate() == currentCoordinate && move.getDestination() == destination)
					return move;
			}
			return NULL_MOVE;
		}
	}
}