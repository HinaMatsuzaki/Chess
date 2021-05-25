import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BlackPlayer extends Player {
	// constructor
	public BlackPlayer(final Board board, final Collection<Move> whiteLegalMoves,
			final Collection<Move> blackLegalMoves) {
		super(board, blackLegalMoves, whiteLegalMoves);

	}

	@Override
	public Collection<Piece> getActivePieces() {
		// getBlackPieces: return this.black
		// this.black: trackActivePieces(this.chessBoard, Color.BLACK);
		return this.board.getBlackPieces();
	}

	@Override
	public PieceColor getColor() {
		return PieceColor.BLACK;
	}

	@Override
	public Player getOpponent() {
		return this.board.whitePlayer();
	}

	@Override
	public String toString() {
		return PieceColor.BLACK.toString();
	}

	@Override
	protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
			final Collection<Move> opponentLegals) {
		final List<Move> kingCastles = new ArrayList<>();
		// must move the king first, and then the rook in order to castle
		// the king must be the first move
		// the king must not be in check
		if (this.king.firstMove() && !this.inCheck()) {
			// king side castle (right side)
			// all spaces between the king and the rook must be empty
			if (!this.board.getTile(5).occupied() && !this.board.getTile(6).occupied()) {
				final Tile rookKing = this.board.getTile(7);
				// the rook must be the first move
				// the squares that the king passes over must not be under attack
				if (rookKing.occupied() && rookKing.getPiece().firstMove()) {
					if (Player.calculateAttacksOnTile(5, opponentLegals).isEmpty()
							&& Player.calculateAttacksOnTile(6, opponentLegals).isEmpty()
							&& rookKing.getPiece().getType().isRook()) {
						kingCastles.add(new Move.KingSideCastleMove(this.board, this.king, 6,
								(Rook) rookKing.getPiece(), rookKing.getTileCoordinate(), 5));
					}
				}
			}
			// queen side castle (left side)
			if (!this.board.getTile(1).occupied() && !this.board.getTile(2).occupied()
					&& !this.board.getTile(3).occupied()) {
				final Tile rookQueen = this.board.getTile(0);
				if (rookQueen.occupied() && rookQueen.getPiece().firstMove()
						&& Player.calculateAttacksOnTile(2, opponentLegals).isEmpty()
						&& Player.calculateAttacksOnTile(3, opponentLegals).isEmpty()
						&& rookQueen.getPiece().getType().isRook()) {
					kingCastles.add(new Move.QueenSideCastleMove(this.board, this.king, 2, (Rook) rookQueen.getPiece(),
							rookQueen.getTileCoordinate(), 3));
				}
			}
		}
		return Collections.unmodifiableList(kingCastles);
	}
}
