import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Table {
	private final JFrame gameFrame;
	private final GameHistoryPanel gameHistoryPanel;
	private final TakenPiecesPanel takenPiecesPanel;
	private final BoardPanel boardPanel;
	private final MoveLog moveLog;
	private Board chessBoard;
	private Tile sourceTile;
	private Tile destinationTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;
	private boolean highlightLegalMoves;

	private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
	private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
	private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
	private static String defaultPieceImagesPath = "pieces/chessPieces/";
	private Color lightTileColor = Color.decode("#98AFC7");
	private Color darkTileColor = Color.decode("#E5E4E2");

	public Table() {
		this.gameFrame = new JFrame("Chess");
		this.gameFrame.setLayout(new BorderLayout());
		final JMenuBar tableMenuBar = createTableMenuBar();
		this.gameFrame.setJMenuBar(tableMenuBar);
		this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
		this.moveLog = new MoveLog();
		this.chessBoard = Board.createStandardBoard();
		this.gameHistoryPanel = new GameHistoryPanel();
		this.takenPiecesPanel = new TakenPiecesPanel();
		this.boardPanel = new BoardPanel();
		this.boardDirection = BoardDirection.NORMAL;
		this.highlightLegalMoves = true;
		this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
		this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
		this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
		this.gameFrame.setVisible(true);
	}

	private JMenuBar createTableMenuBar() {
		final JMenuBar tableMenuBar = new JMenuBar();
		tableMenuBar.add(createFileMenu());
		tableMenuBar.add(createPreferencesMenu());
		return tableMenuBar;
	}

	private JMenu createFileMenu() {
		final JMenu fileMenu = new JMenu("File");
		final JMenuItem openPGN = new JMenuItem("Load PGN File");
		openPGN.addActionListener((e) -> {
			System.out.println("open up that pgn file!");
		});
		fileMenu.add(openPGN);

		final JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);

		return fileMenu;
	}

	private JMenu createPreferencesMenu() {
		final JMenu preferencesMenu = new JMenu("Preferences");
		final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
		flipBoardMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				boardDirection = boardDirection.opposite();
				boardPanel.drawBoard(chessBoard);
			}
		});
		preferencesMenu.add(flipBoardMenuItem);
		preferencesMenu.addSeparator();

		final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
		legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
			}
		});
		preferencesMenu.add(legalMoveHighlighterCheckbox);

		return preferencesMenu;
	}

	public enum BoardDirection {
		NORMAL {
			@Override
			List<TilePanel> traverse(final List<TilePanel> boardTiles) {
				return boardTiles;
			}

			@Override
			BoardDirection opposite() {
				return FLIPPED;
			}
		},
		FLIPPED {
			@Override
			List<TilePanel> traverse(final List<TilePanel> boardTiles) {
				Collections.reverse(boardTiles);
				return boardTiles;
			}

			@Override
			BoardDirection opposite() {
				return NORMAL;
			}
		};

		abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

		abstract BoardDirection opposite();

	}

	private class BoardPanel extends JPanel {
		final List<TilePanel> boardTiles;

		BoardPanel() {
			super(new GridLayout(8, 8));
			this.boardTiles = new ArrayList<>();
			for (int i = 0; i < 64; i++) {
				final TilePanel tilePanel = new TilePanel(this, i);
				this.boardTiles.add(tilePanel);
				add(tilePanel);
			}
			setPreferredSize(BOARD_PANEL_DIMENSION);
			validate();
		}

		public void drawBoard(final Board board) {
			removeAll();
			for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
				tilePanel.drawTile(board);
				add(tilePanel);
			}
			validate();
			repaint();
		}
	}

	public static class MoveLog {
		private final List<Move> moves;

		MoveLog() {
			this.moves = new ArrayList<>();
		}

		public List<Move> getMoves() {
			return this.moves;
		}

		public void addMove(final Move move) {
			this.moves.add(move);
		}

		public int size() {
			return this.moves.size();
		}

		public void clear() {
			this.moves.clear();
		}

		public Move removeMove(int index) {
			return this.moves.remove(index);
		}

		public boolean removeMove(final Move move) {
			return this.moves.remove(move);
		}
	}

	private class TilePanel extends JPanel {
		private final int tileId;

		TilePanel(final BoardPanel boardPanel, final int tileId) {
			super(new GridBagLayout());
			this.tileId = tileId;
			setPreferredSize(TILE_PANEL_DIMENSION);
			assignTileColor();
			assignTilePieceIcon(chessBoard);
			addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(final MouseEvent event) {
					if (isRightMouseButton(event)) {
						sourceTile = null;
						destinationTile = null;
						humanMovedPiece = null;
					} else if (isLeftMouseButton(event)) {
						if (sourceTile == null) {
							sourceTile = chessBoard.getTile(tileId);
							humanMovedPiece = sourceTile.getPiece();
							if (humanMovedPiece == null) {
								sourceTile = null;
							}
						} else {
							destinationTile = chessBoard.getTile(tileId);
							final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(),
									destinationTile.getTileCoordinate());
							final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
							if (transition.getMoveStatus().done()) {
								chessBoard = transition.getNextBoard();
								moveLog.addMove(move);
							}
							sourceTile = null;
							destinationTile = null;
							humanMovedPiece = null;
						}
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								gameHistoryPanel.redo(chessBoard, moveLog);
								takenPiecesPanel.redo(moveLog);
								boardPanel.drawBoard(chessBoard);
							}
						});
					}
				}

				private boolean isRightMouseButton(MouseEvent event) {
					return ((event.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0
							|| event.getButton() == MouseEvent.BUTTON3);
				}

				private boolean isLeftMouseButton(MouseEvent event) {
					return ((event.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0
							|| event.getButton() == MouseEvent.BUTTON1);
				}

				@Override
				public void mousePressed(final MouseEvent e) {

				}

				@Override
				public void mouseReleased(final MouseEvent e) {

				}

				@Override
				public void mouseEntered(final MouseEvent e) {

				}

				@Override
				public void mouseExited(final MouseEvent e) {

				}
			});

			validate();

		}

		public void drawTile(final Board board) {
			assignTileColor();
			assignTilePieceIcon(board);
			highlightLegals(board);
			validate();
			repaint();
		}

		private void assignTilePieceIcon(final Board board) {
			this.removeAll();
			if (board.getTile(this.tileId).occupied()) {
				try {
					final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath
							+ board.getTile(this.tileId).getPiece().getColor().toString().substring(0, 1) + ""
							+ board.getTile(this.tileId).getPiece().toString() + ".gif"));
					add(new JLabel(new ImageIcon(image)));
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void highlightLegals(final Board board) {
			if (highlightLegalMoves) {
				for (final Move move : pieceLegalMoves(board)) {
					if (move.getDestination() == this.tileId) {
						try {
							add(new JLabel(
									new ImageIcon(ImageIO.read(new File("legalMoves/legalMoves/green_dot.png")))));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		private boolean attackOnSquare(Move move, Collection<Move> enemyMoves) {
			return enemyMoves.stream().anyMatch(m -> m.destination == move.destination);
		}

		private Collection<Move> pieceLegalMoves(final Board board) {
			if (humanMovedPiece != null && humanMovedPiece.getColor() == board.currentPlayer().getColor()) {
				Set<Move> moves = humanMovedPiece.calculateMoves(board).stream().collect(Collectors.toSet());
				if (humanMovedPiece.type == Piece.Type.KING) {
					moves.addAll(board.currentPlayer().calculateKingCastles(board.currentPlayer().legalMoves,
							board.currentPlayer().getOpponent().legalMoves));
					moves = moves.stream()
							.filter(m -> !attackOnSquare(m, board.currentPlayer().getOpponent().getLegalMoves()))
							.collect(Collectors.toSet());
				}
				return moves;
			}
			return Collections.emptyList();
		}

		private void assignTileColor() {
			if (firstRow.contains(this.tileId) || thirdRow.contains(this.tileId) || fifthRow.contains(this.tileId)
					|| seventhRow.contains(this.tileId)) {
				if (this.tileId % 2 == 0) {
					setBackground(lightTileColor);
				} else {
					setBackground(darkTileColor);
				}
			} else if (secondRow.contains(this.tileId) || fourthRow.contains(this.tileId)
					|| sixthRow.contains(this.tileId) || eighthRow.contains(this.tileId)) {
				if (this.tileId % 2 == 0) {
					setBackground(darkTileColor);
				} else {
					setBackground(lightTileColor);
				}
			}
		}

		public final List<Integer> firstRow = initRow(0);
		public final List<Integer> secondRow = initRow(8);
		public final List<Integer> thirdRow = initRow(16);
		public final List<Integer> fourthRow = initRow(24);
		public final List<Integer> fifthRow = initRow(32);
		public final List<Integer> sixthRow = initRow(40);
		public final List<Integer> seventhRow = initRow(48);
		public final List<Integer> eighthRow = initRow(56);

		public List<Integer> initRow(int start) {
			List<Integer> row = new ArrayList<>();
			for (int i = start; i < start + 8; i++) {
				row.add(i);
			}
			return row;
		}
	}
}