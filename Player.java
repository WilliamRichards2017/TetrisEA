import java.util.*;
import java.io.*;

public class Player{
    private int max_fitness;
    private int fitness;
    private double[] genome;
    private int[][] current_piece;
    private int[][] look_ahead;

    public Player(int m){
        this.max_fitness = m;
        initGenome();
    }

    // get genome
    public double[] getGenome(){
        return this.genome;
    }

    // get fitness
    public double getFitness(){
        return this.fitness;
    }

    public void setFitness(int f){
        this.fitness = f;
    }

    // set genome
    public void setGenome(double[] g){
        for(int i = 0; i < g.length; i++){
            this.genome[i] = g[i];
        }
    }

    // simulate a player's genome until it dies
    public int simulate(String visualize){
        int width = 10;
        int height = 22;
        Board board = new Board(width, height);
        return simulatePlayer(board, visualize);
    }

    private int simulatePlayer(Board board, String visualize){
        // initialize fitness to 0
        this.fitness = 0;

        // get a random current piece
        ArrayList<int[][]> current_piece = getRandomPiece();
        ArrayList<int[][]> next_piece = null;

        // while the game isn't over
        int gameOver = -1;
        while(gameOver != 1){

            // get the next piece
            next_piece = getRandomPiece();

            // simulate all configurations
            ArrayList<BoardState> bs = board.simulateAllConfigurations(current_piece, next_piece);

            if(bs.isEmpty()){
                gameOver = 1;
                System.out.println("no simulated best move found");
                continue;
            }

            // find the best move among simulated board states
            BoardState best_board_state = getBestBoardState(bs);

            // drop the piece according to the best simulated board state
            int[][] best_piece = best_board_state.getPiece();
            int best_col = best_board_state.getCol();
            board.drop_piece(best_piece, best_col);

            // update rows deleted
            this.fitness += board.clear_rows();
            if(this.fitness >= this.max_fitness){
                gameOver = 1;
                System.out.println("fitness exceeded max fitness");
                continue;
            }

            // update the current piece
            current_piece = next_piece;

          //  board.print_tetris(board.getBoard());
          //  System.out.println("----------------------------");
        }
        board.print_tetris(board.getBoard());
        System.out.println("----------------------------");

      //  System.out.println(this.fitness);
        return this.fitness;
    }

    private BoardState getBestBoardState(ArrayList<BoardState> bs){
        TreeMap<Double, BoardState> tree = new TreeMap<Double, BoardState>();
        for(BoardState b : bs){
            double score = eval(b.getBoard());
            tree.put(score, b);
        }
        return tree.get(tree.lastKey());
    }

    // get a random tetramino
    public ArrayList<int[][]> getRandomPiece(){
        Piece pieces = new Piece();
        Random random = new Random();
        int piece_index = random.nextInt(7);
        return pieces.getPiece(piece_index);
    }

    private void print(int[][] p){
        for(int i = 0; i < p.length; i++){
            for(int j = 0; j < p[i].length; j++){
                System.out.print(p[i][j] + " ");
            }System.out.println();
        }
    }

    // pick one random component in a vector and scale it by +- mutation_range
    public void mutate(double mutation_range){
        double random = Math.random();

        // randomly choose sign (+ or -)
        int sign = 1;
        if(random > 0.5){
            sign = -1;
        }
        double mutation_factor = sign * mutation_range;

        // find a random index
        Random rand = new Random();
        int randIndex = rand.nextInt(4);

        // mutate a randomly selected component
        this.genome[randIndex] = this.genome[randIndex] * (1 + mutation_factor);
        normalize();
    }

    // randomly decide how many components to swap and swap them
    public void crossover(Player p){

        // randomly decide how many components to swap
        Random random = new Random();
        int n = random.nextInt(4) + 1;

        // shuffle index array
        int[] index_arr = {0,1,2,3};
        int index, temp;
        for(int i = index_arr.length-1; i > 0; i--){
            index = random.nextInt(i+1);
            temp = index_arr[index];
            index_arr[index] = index_arr[i];
            index_arr[i] = temp;
        }

        // pick the first n component indices from the shuffled list and swap
        double[] p2_genome = p.getGenome();
        for(int i = 0; i < n; i++){
            int crossover_index = index_arr[i];
            double temp2 = this.genome[crossover_index];
            this.genome[crossover_index] = p2_genome[crossover_index];
            p2_genome[crossover_index] = temp2;
        }
        p.setGenome(p2_genome);
        normalize();
        p.normalize();
    }

    // normalize the genome vector
    public void normalize(){
        double len = 0;
        for(int i = 0; i < 4; i++){
            len += Math.pow(genome[i],2);
        }
        len = Math.sqrt(len);
        for(int i = 0; i < 4; i++){
            this.genome[i] = this.genome[i]/len;
        }
    }

    // randomly initialize chromosomes
    private void initGenome(){
        this.genome = new double[4];
        Random rand = new Random();

        // random double between -1 and 1
        for(int i = 0; i < 4; i++){
            this.genome[i] = rand.nextDouble() * 2 - 1;
        }
    }

    private double eval(int[][] b){
        return -genome[0]*aggregateHeight(b) + genome[1]*completeRows(b) - genome[2]*holes(b) - genome[3]*bumpiness(b);
    }

    // compute the sum of heights for all columns in the board
    private int aggregateHeight(int[][] board){
        int aggregate = 0;
        int[] heights = getHeights(board);
        for(int i = 0; i < heights.length; i++){
            aggregate += heights[i];
            //System.out.print(heights[i] + " ");
        }
        //System.out.println();
        return aggregate;
    }

    // find the number of complete rows on the board
    private int completeRows(int[][] board){
        int completeRows = 0;
        for(int i = 0; i < board.length; i++){
            boolean complete = true;
            for(int j = 0; j < board[i].length; j++){
                if(board[i][j] != 1){
                    complete = false;
                }
                if(!complete){
                    break;
                }
            }
            if(complete){
                completeRows++;
            }
        }
        return completeRows;
    }

    // find the number of holes on the board
    private int holes(int[][] board){
        int holes = 0;
        int cols = board[0].length;
        int rows = board.length;
        for(int i = 0; i < cols; i++){
            boolean holes_counting = false;
            for(int j = 0; j < rows; j++){
                if(holes_counting == false && board[j][i] == 1){
                    holes_counting = true;
                }
                if(holes_counting == true && board[j][i] == 0){
                    holes++;
                }
            }
        }
        return holes;
    }

    // aggregate the different in height for all adjacent columns
    private int bumpiness(int[][] board){
        int bump = 0;
        int[] heights = getHeights(board);
        for(int i = 0; i < heights.length-1; i++){
            bump += Math.abs(heights[i]-heights[i+1]);
        }
        return bump;
    }

    private int[] getHeights(int[][] board){
        int[] heights = new int[board[0].length];
        int cols = board[0].length;
        int rows = board.length;
        int max_height = board[0].length;
        for(int i = 0 ; i < cols; i++){
            int current_height = max_height;
            for(int j = 0; j < rows; j++){
                if(board[j][i] == 1){
                    heights[i] = current_height;
                    break;
                }
                current_height--;
            }
        }
        return heights;
    }

    public static void main(String[] args){
        Player p = new Player(50000);
        int f = p.simulate("off");
    }
}
