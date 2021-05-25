import java.util.Collection;


public abstract class Piece {
	// piece type
	final Type type;
	// piece position
	final int position;
	// piece is either white or black
	final PieceColor color;
	private final boolean firstMove;
	private final int cachedHashCode;

	// constructor
	public Piece(final Type type, final int position, final PieceColor color, boolean firstMove) {
		this.type = type;
		this.position = position;
		this.color = color;
		this.firstMove = firstMove;
		this.cachedHashCode = computeHashCode();
	}

	@Override
	// we want object equality
	// object equality (equals()): two separate objects have the same values/state <- we want this
	// reference equality (==): two object references point to the same object
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		// instanceof: check if the reference variable is containing a given type of
		// object reference or not
		if (!(other instanceof Piece))
			return false;
		final Piece otherPiece = (Piece) other;

		return (this.type == otherPiece.getType() && this.position == otherPiece.getPosition()
				&& this.color == otherPiece.getColor() && this.firstMove == otherPiece.firstMove());
	}

	// use 31 because it's an odd prime
	public int computeHashCode() {
		int result = type.hashCode();
		result = 31 * result + this.color.hashCode();
		result = 31 * result + this.position;
		result = 31 * result + (this.firstMove ? 1 : 0);
		return result;
	}

	// Override
	public int hashCode() {
		return this.cachedHashCode;
	}

	// get the type of the piece
	public Type getType() {
		return this.type;
	}

	// get the position of the piece
	public int getPosition() {
		return this.position;
	}

	// get the color of the piece
	public PieceColor getColor() {
		return this.color;
	}

	// check if the move is the first time
	public boolean firstMove() {
		return this.firstMove;
	}

	// calculate legal chess moves
	public abstract Collection<Move> calculateMoves(final Board board);

	// return the moved piece with an updated piece position
	public abstract Piece movePiece(Move move);

	public enum Type {
		PAWN(100, "P") {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}
		},
		KNIGHT(300, "N") {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}
		},
		BISHOP(300, "B") {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}
		},
		ROOK(500, "R") {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return true;
			}
		},
		QUEEN(900, "Q") {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}
		},
		KING(10000, "K") {
			@Override
			public boolean isKing() {
				return true;
			}

			@Override
			public boolean isRook() {
				return false;
			}
		};

		private String pieceName;
		private int pieceValue;

		// constructor
		private Type(final int pieceValue, final String pieceName) {
			this.pieceValue = pieceValue;
			this.pieceName = pieceName;
		}

		// return the alphabet as a name of the piece
		@Override
		public String toString() {
			return this.pieceName;
		}

		public int getPieceValue() {
			return this.pieceValue;
		}

		// check if the piece is king
		public abstract boolean isKing();

		public abstract boolean isRook();
	}
}