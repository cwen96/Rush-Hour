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

    }

    public String boardDiff(ArrayList<String> board1, ArrayList<String> board2) {
        String diff;
        char movedCar;
        for (int i = 0; i < board1.size(); i++) {
            char[] compare1 = board1.get(i).toCharArray();
            char[] compare2 = board2.get(i).toCharArray();
            for (int j = 0; j < board1.size(); j++) {
                if (compare1[j] != compare2[j]) {
                    movedCar = (compare1[j] == '.') ? compare2[j] : compare1[j];
                }

            }
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
