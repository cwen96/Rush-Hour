package rushHour;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class RushHour {
    private static final int MAX_LENGTH = 6;
    private static final int MAX_HEIGHT = 6;
    private static final int SOLVED_X_COORD = 5;
    private static final int SOLVED_Y_COORD = 2;
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    // Complete board. Each element of the board is a row of the board.
    private ArrayList<String> board = new ArrayList<>(6);
    // Maps a car to its orientation (horizontal or vertical)
    private HashMap<Character, Integer> orientation = new HashMap<>();
    // Maps the current state to the previous state
    private HashMap<GraphNode, GraphNode> previousState = new HashMap<>();
    // Queue used for BFS
    private Queue<GraphNode> queue = new LinkedList<>();


    public RushHour (String fileName) {
        readInFile(fileName);
    }

    /**
     * @param fileName reads in a file (i.e. "A00.txt")
     */
    // Populates the board variable with the original board.
    // Each row of the board is an element inside the ArrayList.
    // Tested and working as expected.
    public void readInFile(String fileName) {
        // Reads in a board.
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);
            // While there is a next row of the board.
            while (reader.hasNext()) {
                // Append the current line to the ArrayList.
                this.board.add(reader.next());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
    }


    /**
     * @param board Original board state
     */
    // Sets the orientation of each car
    // Finished and untested. Need implementation for findOrientation.
    public void setOrientations(ArrayList<String> board) {
        for (int i = 0; i < board.size(); i++) {
            for(int j = 0; j < MAX_LENGTH; j++) {
                char currCar = board.get(i).charAt(j);
                if (currCar != '.' && !orientation.containsKey(currCar)) {
                    orientation.put(currCar, findOrientation(currCar));
                }
                else {
                    continue;
                }
            }
        }
    }


    // Finds the orientation of c.
    // Returns 0 for horizontal orientation and 1 for vertical orientation.
    // Possible implementation: Horizontal orientation if two characters are consecutive, vertical otherwise.
    public int findOrientation(char c) {
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < 5; j++) {
                if (this.board.get(i).toCharArray()[j] == this.board.get(i).toCharArray()[j+1] && this.board.get(i).toCharArray()[j] == c) {
                    return 0;
                }
            }
        }
        return 1;
    }


    //Finds the length of c.
    // Returns 2 or 3 (I think 3 is the maximum length)?
    // Possible implementation: Count the number of times the character occurs in the
    //    entire board ArrayList.
    public int findLength(char c) {
        int count = 0;
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < 6; j++) {
                if (this.board.get(i).toCharArray()[j] == c) {
                    count += 1;
                }
            }
        }
        return count;
    }


    public void seen(GraphNode next, GraphNode prev) {
        if (!previousState.containsKey(next)) {
            previousState.put(next, prev);
            queue.add(next);
        }
        if (prev == null) {

        }
        else {
            next.appendMoves(next.boardDiff(next.getBoard(), prev.getBoard()));
        }
    }

    public int numMoves(GraphNode current) {
        GraphNode prev = previousState.get(current);
        int moves;
        if (prev == null) {
            moves = 0;
        }
        else {
            moves = numMoves(prev) + 1;
        }
        return moves;
    }

    public boolean checkBoundaries(int val) {
        if (val >= 0 && val < 6) {
            return true;
        }
        else {
            return false;
        }
    }

    // in given state, returns the entity at a given coordinate, possibly out of bound
    public char getCar(GraphNode boardState, int row, int column) {
        if (checkBoundaries(row) && checkBoundaries(column)) {
            return boardState.getBoard().get(row).charAt(column);
        }
        else {
            return '~';
        }
    }

    public void makeMove(GraphNode current, int row, int column, int orientation, int distance, int rowDirection, int columnDirection, int n) {
        GraphNode newBoard = new GraphNode(current);
        row += distance * rowDirection;
        column += distance * columnDirection;
        char car = getCar(current, row, column);

        if (findOrientation(car) != orientation) {
            return;
        }

        int length = findLength(car);
        for (int i = 0; i < n; i++) {
            row -= rowDirection;
            column -= columnDirection;
            char[] tmp = newBoard.getBoard().get(row).toCharArray();
            tmp[column] = car;
            newBoard.getBoard().set(row, String.valueOf(tmp));
            tmp = newBoard.getBoard().get(row + length * rowDirection).toCharArray();
            tmp[column + length * columnDirection] = '~';
            newBoard.getBoard().set(row + length * rowDirection, String.valueOf(tmp));
            seen(newBoard, current);
            current = newBoard; // comment to combo as one step
        }
    }

    public int countSpaces(GraphNode state, int rowNum, int columnNum, int dRow, int dColumn) {
        int k = 0;
        while (getCar(state, rowNum + k * dRow, columnNum + k * dColumn) == '.') {
            k++;
        }
        return k;
    }

    public void generateNewNodes(GraphNode current) {
        for (int rowNum = 0; rowNum < MAX_HEIGHT; rowNum++) {
            for (int columnNum = 0; columnNum < MAX_LENGTH; columnNum++) {
                if (getCar(current, rowNum, columnNum) != '.') {
                    continue;
                }
                int upSpaces = countSpaces(current, rowNum, columnNum, -1, 0);
                int downSpaces = countSpaces(current, rowNum, columnNum, +1, 0);
                int leftSpaces = countSpaces(current, rowNum, columnNum, 0, -1);
                int rightSpaces = countSpaces(current, rowNum, columnNum, 0, +1);
                makeMove(current, rowNum, columnNum, 1, upSpaces, -1, 0, upSpaces + downSpaces - 1);
                makeMove(current, rowNum, columnNum, 1, downSpaces, +1, 0, upSpaces + downSpaces - 1);
                makeMove(current, rowNum, columnNum, 0, leftSpaces, 0, -1, leftSpaces + rightSpaces - 1);
                makeMove(current, rowNum, columnNum, 0, rightSpaces, 0, +1, leftSpaces + rightSpaces - 1);
            }
        }
    }

    // Chec
    public boolean isDone(GraphNode currState) {
        if (getCar(currState, SOLVED_X_COORD, SOLVED_Y_COORD) == 'X') {
            return true;
        }
        else {
            return false;
        }
    }

    public void BFS() {
        GraphNode initialBoard = new GraphNode(board, null);
        seen(initialBoard, null);
        boolean done = false;

        while (!queue.isEmpty()) {
            GraphNode current = queue.remove();
            if (isDone(current) && !done) {
                done = true;
                numMoves(current);
            }
            generateNewNodes(current);
            break;
        }
        System.out.println(previousState.size() + " explored");
    }



    // Test functions here.
    public static void main(String[] args) throws FileNotFoundException{
        RushHour game = new RushHour("A00.txt");
        //System.out.println(game.board);
        game.BFS();

//        ArrayList<String> board1 = new ArrayList<>(6);
//        ArrayList<String> board2 = new ArrayList<>(6);
//
//        File file1 = new File("GoalTest copy.txt");
//        Scanner reader1 = new Scanner(file1);
//        while (reader1.hasNext()) {
//            board1.add(reader1.next());
//        }
//        reader1.close();
//
//        File file2 = new File("GoalTest copy 2.txt");
//        Scanner reader2 = new Scanner(file2);
//        while (reader2.hasNext()) {
//            board2.add(reader2.next());
//        }
//        reader2.close();

        //GraphNode graphNode = new GraphNode();
        //System.out.println(graphNode.boardDiff(board1, board2));

    }



}