import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WhitePlayer extends Player {
	// constructor
	public WhitePlayer(final Board board, final Collection<Move> whiteLegalMoves,
			final Collection<Move> blackLegalMoves) {
		super(board, whiteLegalMoves, blackLegalMoves);
	}

	@Override
	public Collection<Piece> getActivePieces() {
		// getWhitePieces: return this.white
		// this.whtie: trackActivePieces(this.chessBoard, Color.WHITE);
		return this.board.getWhitePieces();
	}

	@Override
	public PieceColor getColor() {
		return PieceColor.WHITE;
	}

	@Override
	public Player getOpponent() {
		return this.board.blackPlayer();
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
			if (!this.board.getTile(61).occupied() && !this.board.getTile(62).occupied()) {
				final Tile rookKing = this.board.getTile(63);
				// the rook must be the first move
				// the squares that the king passes over must not be under attack
				if (rookKing.occupied() && rookKing.getPiece().firstMove()) {
					if (Player.calculateAttacksOnTile(61, opponentLegals).isEmpty()
							&& Player.calculateAttacksOnTile(62, opponentLegals).isEmpty()
							&& rookKing.getPiece().getType().isRook()) {
						kingCastles.add(new Move.KingSideCastleMove(this.board, this.king, 62,
								(Rook) rookKing.getPiece(), rookKing.getTileCoordinate(), 61));
					}
				}
			}

			// queen side castle (left side)
			if (!this.board.getTile(59).occupied() && !this.board.getTile(58).occupied()
					&& !this.board.getTile(57).occupied()) {
				final Tile rookQueen = this.board.getTile(56);
				if (rookQueen.occupied() && rookQueen.getPiece().firstMove()
						&& Player.calculateAttacksOnTile(58, opponentLegals).isEmpty()
						&& Player.calculateAttacksOnTile(59, opponentLegals).isEmpty()
						&& rookQueen.getPiece().getType().isRook()) {
					kingCastles.add(new Move.QueenSideCastleMove(this.board, this.king, 58, (Rook) rookQueen.getPiece(),
							rookQueen.getTileCoordinate(), 59));
				}
			}
		}
		return Collections.unmodifiableList(kingCastles);
	}

}