import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SudokuGrid {
	private static int[][] grid, backup;
	private int[][] original;

	private boolean[][] isFromFile;
	private GridPane gridPane;
	private StackPane stackPane;
	private static final int ROWSNUMBER = 9;
	private static final int COLNUMBER = 9;

	public SudokuGrid(int[][] grid, boolean[][] isFromFile) {
		SudokuGrid.grid = grid;
		SudokuGrid.backup = grid;
		
		this.original = new int[ROWSNUMBER][COLNUMBER];
		
		for (int row = 0; row < ROWSNUMBER; row++) {
			for (int col = 0; col < COLNUMBER; col++) {
				original[row][col] = grid[row][col];
			}
		}
		this.isFromFile = isFromFile;
		this.gridPane = new GridPane();
		this.stackPane = new StackPane();
		updateUI(grid);
	}

	public void updateUI(int grid[][]) {
		gridPane.getChildren().clear();
		int rectangleSize = 50;
		Text text = new Text();
		Rectangle rectangle = new Rectangle();
		for (int row = 0; row < ROWSNUMBER; row++) {
			for (int col = 0; col < COLNUMBER; col++) {
				rectangle = new Rectangle(rectangleSize, rectangleSize);
				Color color = getSubgridColor(row, col);

				rectangle.setFill(color);
				rectangle.setStroke(Color.BLACK);
				rectangle.setStrokeWidth(0.9);
				final int finalRow = row;
				final int finalCol = col;
				int value = grid[row][col];

				rectangle.setOnMouseClicked(e -> {
					handleClick(finalRow, finalCol);
				});
				stackPane = new StackPane();
				if (value == 0) {
					text = new Text("");
				} else {
					text = new Text(Integer.toString(value));
				}
				if (value != 0) {
					text.setFill(Color.DARKGOLDENROD);
					text.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

					if (isFromFile[row][col]) {
						text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
						text.setFill(Color.BLACK);

					}

				}
				stackPane.getChildren().addAll(rectangle, text);
				gridPane.add(stackPane, col, row);
			}
		}
	}

	public GridPane getGridPane() {
		return gridPane;
	}
	public void reset() {
		for (int row = 0; row < ROWSNUMBER; row++) {
			for (int col = 0; col < COLNUMBER; col++) {
				grid[row][col] = original[row][col];
			}
		}
		updateUI(grid);
		
	}
	private Color getSubgridColor(int row, int col) {
		int subgridRow = row / 3;
		int subgridCol = col / 3;
		if ((subgridRow + subgridCol) % 2 == 0) {
			return Color.WHITE;
		} else {
			return Color.LIGHTGREEN;
		}
	}

	private void handleClick(int row, int col) {
		if(isWinner()) return;
		if (!isFromFile[row][col]) {
			if (validNumber(selectedNumber)) {
				grid[row][col] = selectedNumber;
				updateUI(grid);

			}
		}
	}

	private boolean isAllPlaced() {
	    for (int row = 0; row < ROWSNUMBER; row++) {
	        for (int col = 0; col < COLNUMBER; col++) {
	            if (grid[row][col] == 0) {
	                return false; 
	            }
	        }
	    }
	    return true; 
	}
	public int checkSolution() {
		int counterMistakes = 0;
		for (int row = 0; row < ROWSNUMBER; row++) {
			for (int col = 0; col < COLNUMBER; col++) {
				if (isFromFile[row][col] || grid[row][col] == 0)
					continue;
				if (isSafe(row, col, grid[row][col], grid))
					continue;

				stackPane = (StackPane) gridPane.getChildren().get(row * 9 + col);
				Text text = null;
				for (javafx.scene.Node node : stackPane.getChildren()) {
					if (node instanceof Text) {
						text = (Text) node;
						break;
					}
				}
				if (text != null) {
					text.setFill(Color.RED);
					counterMistakes++;
				}
			}
		}
		
		return counterMistakes;
	}
	public boolean isWinner() {
		return isAllPlaced() && checkSolution() == 0;
	}
	private static int selectedNumber = 0;

	public  void setDefaultGrid() {
		SudokuGrid.grid = backup;
	}

	public static void setGrid(int[][] grid) {
		SudokuGrid.grid = grid;
	}

	public static void setSelectedNumber(int number) {
		selectedNumber = number;
	}

	public static boolean solution(int[][] grid) {
		solution(0, grid);
		setGrid(grid);

		return true;
	}

	public static boolean solution(int index, int[][] grid) {
		if (index == 81) {
			return true;
		}

		int row = getRow(index);
		int col = getColumn(index);

		if (grid[row][col] != 0) {
			return solution(index + 1, grid);
		}

		for (int num = 1; num <= 9; num++) {
			if (isValidPlacement(row, col, num, grid)) {
				grid[row][col] = num;
				if (solution(index + 1, grid)) {
					return true;
				}
				grid[row][col] = 0;
			}
		}

		return false;
	}

	public void printGrid() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(grid[i][j] + " ");
				if ((j + 1) % 3 == 0 && j != 8) {
					System.out.print("| ");
				}
			}
			System.out.println();
			if ((i + 1) % 3 == 0 && i != 8) {
				System.out.println("------+-------+------");
			}
		}
	}

	public static int getRow(int index) {
		return index / ROWSNUMBER;
	}

	public static int getColumn(int index) {
		return index % COLNUMBER;
	}

	public static int getIndex(int index, int grid[][]) {
		return grid[getRow(index)][getColumn(index)];
	}

	public static boolean isValidPlacement(int row, int col, int n, int[][] grid) {
		if (!validNumber(n)) {
			return false;
		}
		if (!isValidRow(row, n, grid)) {
			return false;
		}
		if (!isValidColumn(col, n, grid)) {
			return false;
		}
		if (!isValidSubgrid(row, col, n, grid)) {
			return false;
		}
		return true;
	}

	public static boolean validNumber(int n) {
		if (n < 1 || n > 9)
			return false;
		return true;
	}

	public static boolean isValidSubgrid(int row, int col, int n, int[][] grid) {
		int startRow = row - row % 3;
		int startCol = col - col % 3;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (grid[startRow + i][startCol + j] == n) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isValidRow(int row, int n, int grid[][]) {
		for (int col = 0; col < ROWSNUMBER; col++) {
			if (grid[row][col] == n) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidColumn(int col, int n, int grid[][]) {
		for (int row = 0; row < COLNUMBER; row++) {
			if (grid[row][col] == n) {
				return false;
			}
		}
		return true;
	}

	public static boolean isSafe(int row, int col, int n, int[][] grid) {
		if (!validNumber(n)) {
			return false;
		}
		if (!isValidRowForManulGame(row, col, n, grid)) {
			return false;
		}
		if (!isValidColumnForManulGame(row, col, n, grid)) {
			return false;
		}
		if (!isValidSubgridForManulGame(row, col, n, grid)) {
			return false;
		}
		return true;
	}

	public static boolean isValidRowForManulGame(int row, int column, int n, int grid[][]) {
		for (int col = 0; col < COLNUMBER; col++) {
			if (col == column)
				continue;
			if (grid[row][col] == n) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidColumnForManulGame(int r, int col, int n, int grid[][]) {
		for (int row = 0; row < ROWSNUMBER; row++) {
			if (row == r)
				continue;
			if (grid[row][col] == n) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidSubgridForManulGame(int row, int col, int n, int[][] grid) {
		int startRow = row - row % 3;
		int startCol = col - col % 3;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == row && j == col)
					continue;
				if (startRow + i == row && startCol + j == col)
					continue;
				if (grid[startRow + i][startCol + j] == n) {
					return false;
				}
			}
		}
		return true;
	}
}