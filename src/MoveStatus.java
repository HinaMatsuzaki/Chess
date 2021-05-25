public enum MoveStatus {
	// the move is successfully done
	DONE {
		@Override
		public boolean done() {
			return true;
		}
	},

	// the move is illegal
	ILLEGAL_MOVE {
		@Override
		public boolean done() {
			// the move is illegal, the move is not done
			return false;
		}
	},

	// the player is still in check
	LEAVE_PLAYER_IN_CHECK {
		@Override
		public boolean done() {
			return false;
		}
	};

	public abstract boolean done();
}