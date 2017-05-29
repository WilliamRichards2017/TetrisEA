import java.util.*;
import java.io.*;

public class Board {
    private int width;
    private int height;
    private int[][] board;
    private ArrayList<BoardState> board_states;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        init_board();
    }

    public int[][] getBoard(){
        return this.board;
    }
    
    public ArrayList<BoardState> getBoardStates(){
        return this.board_states;
    }
    
    public ArrayList<BoardState> simulateAllConfigurations(ArrayList<int[][]> current, ArrayList<int[][]> lookahead){
        this.board_states = new ArrayList<BoardState>();
        for(int[][] c : current){
            for(int[][] l : lookahead){
                for(int i = 0; i < 10; i++){
                    for(int j = 0; j < 10; j++){
                        simulateDrop(c, l, i, j);
                    }
                }
            }
        }
        return this.board_states;
    }
    
    public void simulateDrop(int[][] current, int[][] lookahead, int col1, int col2){
        int[][] copy = copyBoard(board);
        int drop_result1 = drop_piece(current, col1);
        int drop_result2 = drop_piece(lookahead, col2);
        
        if(drop_result1 == 1 && drop_result2 == 1){
            board_states.add(new BoardState(board, current, col1, width, height));
        }
        
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = copy[i][j];
            }
        }
    }
    
    private int[][] copyBoard(int[][] b){
        int[][] cpy = new int[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                cpy[i][j] = b[i][j];
            }
        }
        return cpy;
    }
    
    // 1 -> valid drop, -1 -> game over, 0 -> invalid move
    public int drop_piece(int[][] piece, int column) {
        int flag;
        if (is_valid_move(piece, 0, column)) {
            return add_piece(piece, column);
        }
        else{
            return 0;
        }
    }
    
    // print out array flipped accrose x axis so it looks like a tetris board
    public void print_tetris(int[][] arr){
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr[i].length; j++){
                System.out.print(arr[i][j] + " ");
            }System.out.println();
        }
    }
    
    // clear complete rows in the board and return the number of rows cleared
    public int clear_rows(){
        int cleared = 0;
        int row_index = 0;
        while(row_index < height){
            int[] row = board[row_index];
            if(isComplete(row)){
                cleared++;
                for(int i = row_index; i > 0; i--){
                    board[i] = board[i-1];
                }
            }
            row_index++;
        }
        return cleared;
    }
    
    private boolean isComplete(int[] row){
        for(int i = 0; i < row.length; i++){
            if(row[i] == 0){
                return false;
            }
        }return true;
    }
    
    private void init_board(){
        this.board = new int[height][width];
    }
    
    private int add_piece(int[][] piece, int col){
        int piece_width = piece[0].length;
        int piece_height = piece.length;
        int row = drop_height(piece, col);

        if(row<1){
            return -1;
        }
        while(collision_height(piece,row,col)==1){
            row-=1;
            if(row-piece_height<1){
                return -1;
            }
        }
        for(int i = 0; i < piece_height; i++){
            for(int j = 0; j < piece_width; j++ ) {
                board[row+i][col+j] += piece[i][j];
            }
        }
        return 1;
    }

    private int collision_height (int[][] piece, int row, int col){
        int piece_width = piece[0].length;
        int piece_height = piece.length;
        int flag = 0;

        for(int i = 0; i < piece_height; i++ ){
            for(int j = 0; j < piece_width; j++ ) {

                if(board[row+i][col+j] + piece[i][j] > 1){
                    flag = 1;
                }
            }
        }
        return flag;
    }

    private int max(int x, int y) {
        return (x > y) ? x : y;
    }

    private int drop_height(int[][] piece, int column){
        int piece_width = piece[0].length;
        int piece_height = piece.length;
        int best_height = 22-piece_height;
        for(int j = this.height-1; j > 0; j--) {
            if (board[j][column] >= 1) {
                best_height = j-piece_height;
            }
        }
        //System.out.println("drop height");
        //System.out.println(best_height);
        return best_height;
    }


    //returns if a move is legal,
    private boolean is_valid_move(int[][] piece, int x, int y){
        boolean bool = true;

        //invalid move
        if (piece[0].length + y > 10) {
            bool = false;
        }
        return bool;
    }
}
