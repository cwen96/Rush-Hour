package RushHour2;
import java.io.*;
import java.util.*;

public class rushhour {
    static final int N = 6;
    static final int M = 6;
    static final int xCordGoal = 2;
    static final int yCordGoal = 5;
    static String init = "";
    static String hor = "";
    static String ver = "";
    static String longs = "";
    static String shorts = "";
    static ArrayList<String> route = new ArrayList<>();
    static final char target = 'X';
    static final char unoccupied = '.';
    static final char outOfBound = '@';

    // conventional row major 2D-1D index transformation
    static int rc2i(int r, int c) {
        return r * N + c;
    }

    // checks if an entity is of a given type
    static boolean isType(char entity, String type) {
        return type.indexOf(entity) != -1;
    }

    // finds the length of a car
    static int length(char car) {
        return isType(car, longs) ? 3 : isType(car, shorts) ? 2 : 0/0;
    }

    // in given state, returns the entity at a given coordinate, possibly out of bound
    static char at(String state, int r, int c) {
        return (inBound(r, M) && inBound(c, N)) ? state.charAt(rc2i(r, c)) : outOfBound;
    }
    static boolean inBound(int v, int max) {
        return (v >= 0) && (v < max);
    }

    // checks if a given state is a goal state
    static boolean isGoal(String state) {
        return at(state, xCordGoal, yCordGoal) == target;
    }

    // in a given state, starting from given coordinate, toward the given direction,
    // counts how many empty spaces there are (origin inclusive)
    static int countSpaces(String state, int r, int c, int dr, int dc) {
        int k = 0;
        while (at(state, r + k * dr, c + k * dc) == unoccupied) {
            k++;
        }
        return k;
    }

    // the predecessor map, maps currentState => previousState
    static Map<String,String> pred = new HashMap<String,String>();
    // the breadth first search queue
    static Queue<String> queue = new LinkedList<String>();
    // the breadth first search proposal method: if we haven't reached it yet,
    // (i.e. it has no predecessor), we map the given state and add to queue
    static void propose(String next, String prev) {
        if (!pred.containsKey(next)) {
            pred.put(next, prev);
            queue.add(next);
        }
    }

    // the predecessor tracing method, implemented using recursion for brevity;
    // guaranteed no infinite recursion, but may throw StackOverflowError on
    // really long shortest-path trace (which is infeasible in standard Rush Hour)
    static String trace(String current) {
        String prev = pred.get(current);
        String step = (prev == null) ? "" : trace(prev) + boardDiff(prev, current);
        System.out.println(step);
        route.add(step);
        System.out.println(route);
        step = "";
        return step;
    }

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

    static void slide(String current, int r, int c, String type, int distance, int dr, int dc, int n) {
        r += distance * dr;
        c += distance * dc;
        char car = at(current, r, c);
        if (!isType(car, type)) return;
        final int L = length(car);
        StringBuilder sb = new StringBuilder(current);
        for (int i = 0; i < n; i++) {
            r -= dr;
            c -= dc;
            sb.setCharAt(rc2i(r, c), car);
            sb.setCharAt(rc2i(r + L * dr, c + L * dc), unoccupied);
            propose(sb.toString(), current);
            current = sb.toString(); // comment to combo as one step
        }
    }

    static void explore(String current) {
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                if (at(current, r, c) != unoccupied) continue;
                int nU = countSpaces(current, r, c, -1, 0);
                int nD = countSpaces(current, r, c, +1, 0);
                int nL = countSpaces(current, r, c, 0, -1);
                int nR = countSpaces(current, r, c, 0, +1);
                slide(current, r, c, ver, nU, -1, 0, nU + nD - 1);
                slide(current, r, c, ver, nD, +1, 0, nU + nD - 1);
                slide(current, r, c, hor, nL, 0, -1, nL + nR - 1);
                slide(current, r, c, hor, nR, 0, +1, nL + nR - 1);
            }
        }
    }

    public rushhour(String fileName) throws Exception {
        try {
            String line = "";
            File f = new File(fileName);
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            while((line = br.readLine())!=null)
            {
                init += line;
            }

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


            for(int i = 0; i < board.length; i++){
                for(int j = 0; j < board[i].length; j++){
                    String s = String.valueOf(board[i][j]);
                    if(!hor.contains(s) && !ver.contains(s) && board[i][j] != '.'){
                        car = board[i][j];
                        if(j != 5 && board[i][j+1] == car){
                            hor += car;
                            if(j != 4 && board[i][j+2] != car) {
                                shorts += car;
                            }
                            if(j != 4 && board[i][j+2] == car) {
                                longs += car;
                            }
                            if(j == 4 && board[i][j+1] == car) {
                                shorts += car;
                            }
                        }
                        if(i != 5 && board[i+1][j] == car) {
                            ver += car;
                            if(i != 4 && board[i+2][j] != car) {
                                shorts += car;
                            }
                            if(i != 4 && board[i+2][j] == car) {
                                longs += car;
                            }
                            if(i == 4 && board[i+1][j] == car) {
                                shorts += car;
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
        propose(init, null);
        boolean solved = false;
        while (!queue.isEmpty()) {
            String current = queue.remove();
            if (isGoal(current) && !solved) {
                solved = true;
                trace(current);
                break; // comment to continue exploring entire space
            }
            explore(current);
        }
        System.out.println(pred.size() + " explored");
    }

    public static ArrayList<String> getRoute() {
        return route;
    }

}