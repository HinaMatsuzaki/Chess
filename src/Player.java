import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Player {
	protected final Board board;
	// keep track of its king
	protected final King king;
	protected final Collection<Move> legalMoves;
	protected final boolean inCheck;

	public Player(final Board board, final Collection<Move> legalMoves, Collection<Move> opponentLegalMoves) {
		this.board = board;
		this.king = trackKing();
		legalMoves.addAll(calculateKingCastles(legalMoves, opponentLegalMoves));
		this.legalMoves = Collections.unmodifiableCollection(legalMoves);
		// check occurs when the current player's king is under threat of capture on their opponent's next turn
		// get all possible attacks the opponents can make on their next turn
		// the current player's king is in check if the king is on one of the target tiles
		this.inCheck = !Player.calculateAttacksOnTile(this.king.getPosition(), opponentLegalMoves).isEmpty();
	}

	protected static Collection<Move> calculateAttacksOnTile(int position, Collection<Move> moves) {
		Set<Move> attackMoves = new HashSet<>();
		// go through the Collection of candidate moves the opponent can make on their
		// next turn
		for (Move move : moves) {
			// if the current player's king is on one of the candidate moves
			if (position == move.getDestination())
				// put the move in the attackMoves list
				attackMoves.add(move);
		}
		return attackMoves;
	}

	public King getKing() {
		return this.king;
	}

	public Collection<Move> getLegalMoves() {
		return this.legalMoves;
	}

	private King trackKing() {
		// go through all of the active pieces
		for (final Piece piece : getActivePieces()) {
			// if you find the king, return it
			if (piece.getType().isKing()) {
				return (King) piece;
			}
		}
		// otherwise, return an exception
		throw new RuntimeException();
	}

	// calculate if the king can escape
	protected boolean canEscape() {
		for (final Move move : this.legalMoves) {
			final MoveTransition transition = makeMove(move);
			;
			// if the king successfully avoids the attack
			if (transition.getMoveStatus().done())
				return true;
		}
		// there was no possible move to make in order to avoid the attack
		return false;
	}

	// check if the move is contained in the legalMoves collection
	public boolean inLegalMoves(final Move move) {
		return this.legalMoves.contains(move);
	}

	// true when the king is under attack and threatened to be captured by another piece
	public boolean inCheck() {
		return this.inCheck;
	}

	// true when the player cannot move out of danger and away from check -> the game is over
	public boolean inCheckMate() {
		return this.inCheck && !canEscape();
	}

	// true when there is no legal moves to make
	public boolean inStaleMate() {
		return !this.inCheck && !canEscape();
	}

	// move two pieces King and rook under a special situation
	public boolean isCastled() {
		return false;
	}

	public MoveTransition makeMove(final Move move) {
		// if the move is illegal
		if (!inLegalMoves(move)) {
			// the transition is also illegal, so return the same board (this.board)
			return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
		}
		final Board transitionBoard = move.execute();
		final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
				transitionBoard.currentPlayer().getOpponent().getKing().getPosition(),
				transitionBoard.currentPlayer().getLegalMoves());
		if (!kingAttacks.isEmpty()) {
			// return the same board and leave the player in check
			return new MoveTransition(this.board, move, MoveStatus.LEAVE_PLAYER_IN_CHECK);
		}
		// return a new board we can transit to
		return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
	}

	public abstract Collection<Piece> getActivePieces();

	public abstract PieceColor getColor();

	public abstract Player getOpponent();

	protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
			Collection<Move> opponentLegals);
}