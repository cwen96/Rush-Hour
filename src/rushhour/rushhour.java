package rushhour;
import java.io.*;
import java.util.*;

public class rushhour {
    static final int maxLength = 6;
    static final int maxHeight = 6;
    //x coordinate of goal state
    static final int xCordGoal = 2;
    //y coordinate of goal state
    static final int yCordGoal = 5;
    //original board
    static String init = "";
    //Collection of horizontal cars
    static String hor = "";
    //Collection of vertical cars
    static String ver = "";
    //Collection of size 3 cars
    static String sizeThree = "";
    //Collection of size 2 cars
    static String sizeTwo = "";
    //Used to store a path to the goal
    static ArrayList<String> route = new ArrayList<>();
    // Used for BFS
    static Queue<String> queue = new LinkedList<>();
    // Used as edges between states
    static Map<String,String> pred = new HashMap<>();
    //represents a letter of the target car
    static final char target = 'X';
    //represents an unoccupied space
    static final char unoccupied = '.';
    //represents out of bound
    static final char outOfBound = '@';

    //rushhour constructor: Add cars to hor, ver, sizeThree, and sizeTwo collections
    /**
     * @param fileName creates a rushhour game from fileName's board (i.e. "A00.txt")
     * @throws IOException if the file cannot be read by the reader. (caught inside function)
     */
    public rushhour(String fileName) {
        try {

            //Read the text file provided
            String line = "";
            File f = new File(fileName);
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            while((line = br.readLine())!=null)
            {
                init += line;
            }

            //Create an initial board from the text file provided
            char car = ' ';
            Scanner scan = new Scanner(f);
            char[][] board = new char [6][6];
            String boardcontent;
            for (int i = 0; i < 6; i++) {
                boardcontent = scan.nextLine();
                for (int j = 0; j < 6; j++) {
                    board[i][j] = boardcontent.charAt(j);
                }
            }

            //Here, checks if car is horizontal or vertical and if size 3 or size 2
            for(int i = 0; i < board.length; i++){
                for(int j = 0; j < board[i].length; j++){
                    String s = String.valueOf(board[i][j]);
                    if(!hor.contains(s) && !ver.contains(s) && board[i][j] != '.'){
                        car = board[i][j];
                        if(j != 5 && board[i][j+1] == car){
                            hor += car;
                            if(j != 4 && board[i][j+2] != car) {
                                sizeTwo += car;
                            }
                            if(j != 4 && board[i][j+2] == car) {
                                sizeThree += car;
                            }
                            if(j == 4 && board[i][j+1] == car) {
                                sizeTwo += car;
                            }
                        }
                        if(i != 5 && board[i+1][j] == car) {
                            ver += car;
                            if(i != 4 && board[i+2][j] != car) {
                                sizeTwo += car;
                            }
                            if(i != 4 && board[i+2][j] == car) {
                                sizeThree += car;
                            }
                            if(i == 4 && board[i+1][j] == car) {
                                sizeTwo += car;
                            }
                        }
                    }
                }
            }

            scan.close();

        } catch (IOException e) {
            System.out.println("Error reading file.");
            e.printStackTrace();
        }
    }

    static int rowTrans(int row, int column) {
        return row * maxLength + column;
    }

    //Verifies the car size
    static boolean checkOrientation(char entity, String type) {
        return type.indexOf(entity) != -1;
    }

    //Calculates the size of a car
    static int findLength(char car) {
        return checkOrientation(car, sizeThree) ? 3 : checkOrientation(car, sizeTwo) ? 2 : 0/0;
    }

    //Get a car that is residing at the provided coordinates in the provided state
    static char getCar(String state, int row, int column) {
        return (checkBoundaries(row, maxHeight) && checkBoundaries(column, maxLength)) ? state.charAt(rowTrans(row, column)) : outOfBound;
    }

    static boolean checkBoundaries(int v, int maximum) {
        return (v >= 0) && (v < maximum);
    }

    // checks if a given state is a goal state
    static boolean isGoal(String state) {
        return getCar(state, xCordGoal, yCordGoal) == target;
    }

    // in a given state, starting from given coordinate, toward the given direction,
    // counts how many empty spaces there are (origin inclusive)
    static int countSpaces(String state, int rowNum, int columnNum, int dRow, int dColumn) {
        int k = 0;
        while (getCar(state, rowNum + k * dRow, columnNum + k * dColumn) == unoccupied) {
            k++;
        }
        return k;
    }

    //Check if the connection between next and previous was made before
    //If not, connect them together
    /**
     * @param next next state of the board
     * @param previous previous state of the board
     */
    static void seen(String next, String previous) {
        if (!pred.containsKey(next)) {
            pred.put(next, previous);
            queue.add(next);
        }
    }

    //Recursively traces the original board from the goal state
    //By doing this, a path to the goal state can be generated
    /**
     * @param current current state of the board
     * @return car movement (i.e. AU1, PD1, etc)
     */
    static String numMoves(String current) {
        String previous = pred.get(current);
        String step = (previous == null) ? "" : numMoves(previous) + boardDiff(previous, current);
        route.add(step);
        step = "";
        return step;
    }

    //Checks which car was moved in which direction from the previous state
    /**
     * @param prev first board that is going to be compared
     * @param current second board that is going to be compared
     * @return which car moved and in which direction (i.e. AU1, PD1, etc)
     */
    static String boardDiff(String prev, String current) {
        String diff = null;

        if(prev != null) {

            String prevOne = prev.substring(0, 6);
            String prevTwo = prev.substring(6, 12);
            String prevThree = prev.substring(12, 18);
            String prevFour = prev.substring(18, 24);
            String prevFive = prev.substring(24, 30);
            String prevSix = prev.substring(30, 36);

            String currOne = current.substring(0, 6);
            String currTwo = current.substring(6, 12);
            String currThree = current.substring(12, 18);
            String currFour = current.substring(18, 24);
            String currFive = current.substring(24, 30);
            String currSix = current.substring(30, 36);

            ArrayList<String> board1 = new ArrayList<>();
            board1.add(prevOne);
            board1.add(prevTwo);
            board1.add(prevThree);
            board1.add(prevFour);
            board1.add(prevFive);
            board1.add(prevSix);

            ArrayList<String> board2 = new ArrayList<>();
            board2.add(currOne);
            board2.add(currTwo);
            board2.add(currThree);
            board2.add(currFour);
            board2.add(currFive);
            board2.add(currSix);

            char movedCar = ' ';
            char dir = ' ';
            boolean checkDode = false;

            while (!checkDode) {
                for (int i = 0; i < board1.size(); i++) {
                    char[] compare1 = board1.get(i).toCharArray();
                    char[] compare2 = board2.get(i).toCharArray();
                    for (int j = 0; j < board1.size(); j++) {
                        if (compare1[j] != compare2[j]) {
                            if (compare2[j] == '.') {
                                movedCar = compare1[j];
                                if (!checkDode) {
                                    if (j != 5 && compare2[j + 1] == movedCar) {
                                        dir = 'R';
                                        checkDode = true;
                                        break;
                                    }
                                    if (compare1[j] == movedCar) {
                                        dir = 'D';
                                        checkDode = true;
                                    }
                                }
                            }
                            if (compare2[j] != '.') {
                                movedCar = compare2[j];
                                if (!checkDode) {
                                    if (j != 5 && compare1[j + 1] == movedCar) {
                                        dir = 'L';
                                        checkDode = true;
                                        break;
                                    }
                                    if (compare1[j] != movedCar) {
                                        dir = 'U';
                                        checkDode = true;
                                    }
                                }
                            }
                        }
                        if (checkDode) {
                            break;
                        }
                    }
                }
            }
            diff = movedCar + "" + dir + "1";
        }
        return diff;
    }

    //Take in the current state, and move a car for one space
    /**
     * @param current current state of the board
     * @param row number of rows to move
     * @param column number of columns to move
     * @param orientation orientation of the car (hor (horizontal) or ver (vertical))
     * @param distance distance to move the car
     * @param rowDirection y-value to move: +1 for down, -1 for up
     * @param columnDirection x-value to move: +1 for right, -1 for left
     * @param n number of board states to generate
     */
    static void makeMoves(String current, int row, int column, String orientation, int distance, int rowDirection, int columnDirection, int n) {
        row += distance * rowDirection;
        column += distance * columnDirection;
        char car = getCar(current, row, column);
        if (!checkOrientation(car, orientation)) {
            return;
        }
        final int length = findLength(car);
        StringBuilder sb = new StringBuilder(current);
        for (int i = 0; i < n; i++) {
            row -= rowDirection;
            column -= columnDirection;
            sb.setCharAt(rowTrans(row, column), car);
            sb.setCharAt(rowTrans(row + length * rowDirection, column + length * columnDirection), unoccupied);
            seen(sb.toString(), current);
            current = sb.toString();
        }
    }

    //Create a new state from the current state
    /**
     * @param current current state of the board
     */
    static void generateNewNodes(String current) {
        for (int row = 0; row < maxHeight; row++) {
            for (int column = 0; column < maxLength; column++) {
                if (getCar(current, row, column) != unoccupied) {
                    continue;
                }
                int upSpaces = countSpaces(current, row, column, -1, 0);
                int downSpaces = countSpaces(current, row, column, +1, 0);
                int leftSpaces = countSpaces(current, row, column, 0, -1);
                int rightSpaces = countSpaces(current, row, column, 0, +1);
                makeMoves(current, row, column, ver, upSpaces, -1, 0, upSpaces + downSpaces - 1);
                makeMoves(current, row, column, ver, downSpaces, +1, 0, upSpaces + downSpaces - 1);
                makeMoves(current, row, column, hor, leftSpaces, 0, -1, leftSpaces + rightSpaces - 1);
                makeMoves(current, row, column, hor, rightSpaces, 0, +1, leftSpaces + rightSpaces - 1);
            }
        }
    }


    //Breadth first search algorithm to find a solution to the board.
    public static void BFS() {
        seen(init, null);
        boolean done = false;
        while (!queue.isEmpty()) {
            String current = queue.remove();
            if (isGoal(current) && !done) {
                numMoves(current);
                break;
            }
            generateNewNodes(current);
        }
    }

    //Retrieve an arraylist containing a path to the goal
    public static ArrayList<String> getRoute() {
        return route;
    }

}