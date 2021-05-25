public class MoveTransition {

	private final Board nextBoard;
	// transition move
	private Move move;
	private final MoveStatus moveStatus;

	public MoveTransition(final Board nextBoard, final Move move, final MoveStatus moveStatus) {
		this.nextBoard = nextBoard;
		this.move = move;
		this.moveStatus = moveStatus;
	}

	public MoveStatus getMoveStatus() {
		return this.moveStatus;
	}

	public Board getNextBoard() {
		return this.nextBoard;
	}
}