import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {
	private final List<Tile> gameBoard;
	private final Collection<Piece> white;
	private final Collection<Piece> black;

	private final WhitePlayer whitePlayer;
	private final BlackPlayer blackPlayer;
	private final Player currentPlayer;
	private final Pawn enPassantPawn;
	private final Move transitionMove;

	private Board(final Builder builder) {
		this.gameBoard = createGameBoard(builder);
		this.white = trackActivePieces(this.gameBoard, PieceColor.WHITE);
		this.black = trackActivePieces(this.gameBoard, PieceColor.BLACK);
		this.enPassantPawn = builder.enPassantPawn;
		final Collection<Move> whiteLegalMoves = calculateLegalMoves(this.white);
		final Collection<Move> blackLegalMoves = calculateLegalMoves(this.black);

		this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
		this.blackPlayer = new BlackPlayer(this, whiteLegalMoves, blackLegalMoves);
		this.currentPlayer = builder.turn.choosePlayer(this.whitePlayer, this.blackPlayer);
		this.transitionMove = builder.transitionMove != null ? builder.transitionMove : Move.MoveFactory.getNullMove();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 64; i++) {
			final String tileText = this.gameBoard.get(i).toString();
			builder.append(String.format("%3s", tileText));
			if ((i + 1) % 8 == 0) {
				builder.append("\n");
			}
		}
		return builder.toString();
	}

	public WhitePlayer whitePlayer() {
		return this.whitePlayer;
	}

	public BlackPlayer blackPlayer() {
		return this.blackPlayer;
	}

	public Player currentPlayer() {
		return this.currentPlayer;
	}

	public Pawn getEnPassantPawn() {
		return this.enPassantPawn;
	}

	public Move getTransitionMove() {
		return this.transitionMove;
	}

	public Collection<Piece> getBlackPieces() {
		return this.black;
	}

	public Collection<Piece> getWhitePieces() {
		return this.white;
	}

	public Collection<Piece> getAllPieces() {
		return Stream.concat(this.white.stream(), this.black.stream()).collect(Collectors.toList());
	}

	public Iterable<Move> getAllLegalMoves() {
		List<Move> allLegalMoves = new ArrayList<>();
		allLegalMoves.addAll(this.whitePlayer.getLegalMoves());
		allLegalMoves.addAll(this.blackPlayer.getLegalMoves());
		return Collections.unmodifiableList(allLegalMoves);
	}

	private Collection<Move> calculateLegalMoves(final Collection<Piece> p) {
		final List<Move> legalMove = new ArrayList<>();
		for (final Piece piece : p) {
			legalMove.addAll(piece.calculateMoves(this));
		}
		return legalMove;
	}

	private static Collection<Piece> trackActivePieces(final List<Tile> gameBoard, final PieceColor color) {
		final List<Piece> activePieces = new ArrayList<>();
		for (final Tile tile : gameBoard) {
			if (tile.occupied()) {
				final Piece piece = tile.getPiece();
				if (piece.getColor() == color) {
					activePieces.add(piece);
				}
			}
		}
		return activePieces;
	}

	public Tile getTile(final int coordinate) {
		return gameBoard.get(coordinate);
	}

	private static List<Tile> createGameBoard(final Builder builder) {
		final Tile[] tiles = new Tile[64];
		for (int i = 0; i < 64; i++) {
			tiles[i] = Tile.createTile(i, builder.setUpBoard.get(i));
		}
		return Collections.unmodifiableList(Arrays.asList(tiles));
	}

	public static Board createStandardBoard() {
		final Builder builder = new Builder();
		// black pieces
		builder.setPiece(new Rook(0, PieceColor.BLACK));
		builder.setPiece(new Knight(1, PieceColor.BLACK));
		builder.setPiece(new Bishop(2, PieceColor.BLACK));
		builder.setPiece(new Queen(3, PieceColor.BLACK));
		builder.setPiece(new King(4, PieceColor.BLACK, true, true));
		builder.setPiece(new Bishop(5, PieceColor.BLACK));
		builder.setPiece(new Knight(6, PieceColor.BLACK));
		builder.setPiece(new Rook(7, PieceColor.BLACK));
		builder.setPiece(new Pawn(8, PieceColor.BLACK));
		builder.setPiece(new Pawn(9, PieceColor.BLACK));
		builder.setPiece(new Pawn(10, PieceColor.BLACK));
		builder.setPiece(new Pawn(11, PieceColor.BLACK));
		builder.setPiece(new Pawn(12, PieceColor.BLACK));
		builder.setPiece(new Pawn(13, PieceColor.BLACK));
		builder.setPiece(new Pawn(14, PieceColor.BLACK));
		builder.setPiece(new Pawn(15, PieceColor.BLACK));
		// white pieces
		builder.setPiece(new Pawn(48, PieceColor.WHITE));
		builder.setPiece(new Pawn(49, PieceColor.WHITE));
		builder.setPiece(new Pawn(50, PieceColor.WHITE));
		builder.setPiece(new Pawn(51, PieceColor.WHITE));
		builder.setPiece(new Pawn(52, PieceColor.WHITE));
		builder.setPiece(new Pawn(53, PieceColor.WHITE));
		builder.setPiece(new Pawn(54, PieceColor.WHITE));
		builder.setPiece(new Pawn(55, PieceColor.WHITE));
		builder.setPiece(new Rook(56, PieceColor.WHITE));
		builder.setPiece(new Knight(57, PieceColor.WHITE));
		builder.setPiece(new Bishop(58, PieceColor.WHITE));
		builder.setPiece(new Queen(59, PieceColor.WHITE));
		builder.setPiece(new King(60, PieceColor.WHITE, true, true));
		builder.setPiece(new Bishop(61, PieceColor.WHITE));
		builder.setPiece(new Knight(62, PieceColor.WHITE));
		builder.setPiece(new Rook(63, PieceColor.WHITE));
		// white to move
		builder.setTurn(PieceColor.WHITE);
		// build the board
		return builder.build();
	}

	public static class Builder {
		// key: tile coordinate
		// value: piece on the tile coordinate
		Map<Integer, Piece> setUpBoard;
		// whose turn
		PieceColor turn;
		Pawn enPassantPawn;
		Move transitionMove;

		// constructor
		public Builder() {
			this.setUpBoard = new HashMap<>();
		}

		public Builder setPiece(final Piece p) {
			this.setUpBoard.put(p.getPosition(), p);
			return this;
		}

		public Builder setTurn(final PieceColor turn) {
			this.turn = turn;
			return this;
		}

		// create an immutable board based on the Builder
		public Board build() {
			return new Board(this);
		}

		public Builder setMoveTransition(final Move transitionMove) {
			this.transitionMove = transitionMove;
			return this;
		}

		public void setEnPassantPawn(Pawn enPassantPawn) {
			this.enPassantPawn = enPassantPawn;

		}
	}
}