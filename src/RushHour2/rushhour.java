package RushHour2;
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

    static int rowTrans(int r, int c) {
        return r * maxLength + c;
    }

    //Verifies the car size
    static boolean isType(char entity, String type) {
        return type.indexOf(entity) != -1;
    }

    //Calculates the size of a car
    static int findLength(char car) {
        return isType(car, sizeThree) ? 3 : isType(car, sizeTwo) ? 2 : 0/0;
    }

    //Get a car that is residing at the provided coordinates in the provided state
    static char at(String state, int row, int column) {
        return (inBound(row, maxHeight) && inBound(column, maxLength)) ? state.charAt(rowTrans(row, column)) : outOfBound;
    }

    static boolean inBound(int v, int maximum) {
        return (v >= 0) && (v < maximum);
    }

    // checks if a given state is a goal state
    static boolean isGoal(String state) {
        return at(state, xCordGoal, yCordGoal) == target;
    }

    // in a given state, starting from given coordinate, toward the given direction,
    // counts how many empty spaces there are (origin inclusive)
    static int countSpaces(String state, int rowNum, int columnNum, int dRow, int dColumn) {
        int k = 0;
        while (at(state, rowNum + k * dRow, columnNum + k * dColumn) == unoccupied) {
            k++;
        }
        return k;
    }

    //Check if the connection between next and previous was made before
    //If not, connect them together
    static void seen(String next, String previous) {
        if (!pred.containsKey(next)) {
            pred.put(next, previous);
            queue.add(next);
        }
    }

    //Trace the original board from the goal state
    //By doing this, a path to the goal state can be generated
    static String numMoves(String current) {
        String previous = pred.get(current);
        String step = (previous == null) ? "" : numMoves(previous) + boardDiff(previous, current);
        System.out.println(step);
        route.add(step);
        System.out.println(route);
        step = "";
        return step;
    }

    //Checks which car was moved in which direction from the previous state
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
    static void makeMove(String current, int row, int column, String type, int distance, int rowDirection, int columnDirection, int n) {
        row += distance * rowDirection;
        column += distance * columnDirection;
        char car = at(current, row, column);
        if (!isType(car, type)) return;
        final int length = findLength(car);
        StringBuilder sb = new StringBuilder(current);
        for (int i = 0; i < n; i++) {
            row -= rowDirection;
            column -= columnDirection;
            sb.setCharAt(rowTrans(row, column), car);
            sb.setCharAt(rowTrans(row + length * rowDirection, column + length * columnDirection), unoccupied);
            seen(sb.toString(), current);
            current = sb.toString(); // comment to combo as one step
        }
    }

    //Create a new state from the current state
    static void generateNewNodes(String current) {
        for (int r = 0; r < maxHeight; r++) {
            for (int c = 0; c < maxLength; c++) {
                if (at(current, r, c) != unoccupied) continue;
                int upSpaces = countSpaces(current, r, c, -1, 0);
                int downSpaces = countSpaces(current, r, c, +1, 0);
                int leftSpaces = countSpaces(current, r, c, 0, -1);
                int rightSpaces = countSpaces(current, r, c, 0, +1);
                makeMove(current, r, c, ver, upSpaces, -1, 0, upSpaces + downSpaces - 1);
                makeMove(current, r, c, ver, downSpaces, +1, 0, upSpaces + downSpaces - 1);
                makeMove(current, r, c, hor, leftSpaces, 0, -1, leftSpaces + rightSpaces - 1);
                makeMove(current, r, c, hor, rightSpaces, 0, +1, leftSpaces + rightSpaces - 1);
            }
        }
    }

    //Add cars to hor, ver, sizeThree, and sizeTwo collections
    public rushhour(String fileName) throws Exception {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void BFS() throws Exception {
        seen(init, null);
        boolean done = false;
        while (!queue.isEmpty()) {
            String current = queue.remove();
            if (isGoal(current) && !done) {
                done = true;
                numMoves(current);
                break;
            }
            generateNewNodes(current);
        }
        System.out.println(pred.size() + " explored");
    }

    //Retrieve an arraylist containing a path to the goal
    public static ArrayList<String> getRoute() {
        return route;
    }

}