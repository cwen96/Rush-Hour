package rushHour;

import java.util.ArrayList;

public class GraphNode {
    private ArrayList<String> board;
    private ArrayList<String> moves;

    public GraphNode(GraphNode origNode) {
        ArrayList<String> clonedBoard = new ArrayList<>();
        ArrayList<String> clonedMoves = new ArrayList<>();
        for(int i = 0; i < origNode.getBoard().size(); i++) {
            clonedBoard.add(origNode.getBoard().get(i));
        }
        for(int i = 0; i < origNode.getMoves().size(); i++) {
            clonedMoves.add(origNode.getMoves().get(i));
        }
        this.board = clonedBoard;
        this.moves = clonedMoves;
    }

    public GraphNode(ArrayList<String> board, ArrayList<String> prevMoves) {
        if (prevMoves == null) {
            ArrayList<String> clonedBoard = new ArrayList<>();
            for(int i = 0; i < board.size(); i++) {
                clonedBoard.add(board.get(i));
            }
            this.board = clonedBoard;
            this.moves = null;
        }
        else {
            this.moves.add(boardDiff(board, prevMoves));
        }

    }

    public void appendMoves(String move) {
        this.moves.add(move);
    }

    public String boardDiff(ArrayList<String> board1, ArrayList<String> board2) {
        //if board2 is null, then no moves; therefore, diff is null;
        String diff = null;
        char movedCar = ' ';
        char dir = ' ';
        boolean checkDode = false;
        if(board2 != null) {
            while(!checkDode)
                for (int i = 0; i < board1.size(); i++) {
                    char[] compare1 = board1.get(i).toCharArray();
                    char[] compare2 = board2.get(i).toCharArray();
                    for (int j = 0; j < board1.size(); j++) {
                        if (compare1[j] != compare2[j]) {
                            if (compare2[j] == '.') {
                                movedCar = compare1[j];
                                if(!checkDode) {
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
                                if(!checkDode) {
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
                        if(checkDode) {
                            break;
                        }
                    }
                }
            diff = movedCar + "" + dir;
        }
        return diff;
    }

    public ArrayList<String> getBoard() {
        return this.board;
    }

    public ArrayList<String> getMoves() {
        return this.moves;
    }




}
