package RushHour2;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Solver {
    public static void solveFromFile(String inputPath, String outputPath) {
        try {
            rushhour game = new rushhour(inputPath);
            game.BFS();
            System.out.println(game.getRoute());
            createFile(outputPath);
            for (int i = 1; i < game.getRoute().size(); i++) {
                writeMoveToFile(outputPath, game.getRoute().get(i));
            }
        }
        catch (Exception e) {
            System.out.println("Error solving from file.");
            e.printStackTrace();
        }

    }

    /**
     * @param fileName create a solution file (i.e. "A00Sol.txt")
     * @throws IOException if the move is illegal
     */
    public static void createFile(String fileName) throws IOException {
        try {
            File solution = new File(fileName);
            if (solution.createNewFile()) {
                System.out.println("New solution file created in current directory:" + fileName);
            }
        }
        catch (IOException e) {
            System.out.println("Unable to create new file.");
            e.printStackTrace();
        }
    }

    public static void writeMoveToFile(String fileName, String contents) throws IOException {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(contents + "\n");
            writer.close();
        }
        catch(IOException e) {
            System.out.println("Error writing to file: " + fileName);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        rushhour initial = new rushhour("A00.txt");
        // typical queue-based breadth first search implementation
//        propose(INITIAL, null);
        solveFromFile("A00.txt", "A00Sol.txt");
    }


}
