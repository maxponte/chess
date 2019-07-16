package com.company;

public class BoardTextView {
    Board board;
    static int n = 8;
    public BoardTextView(Board board) {
        this.board = board;
    }
    public void print() {
        for (int i = 0; i < n; i++) {
            System.out.print(""+(i+1)+" ");
            for (int j = 0; j < n; j++) {
                System.out.print(board.squares[i][j].toString() + " ");
            }
            System.out.print("\n");
        }
        System.out.print("   ");
        for (int j = 0; j < n; j++) {
            System.out.print(Character.toString((char)(97+j)) + "   ");
        }
        System.out.print("\n\n");
    }
    public void printRev() {
        for (int i = n-1; i >= 0; i--) {
            System.out.print(""+(i+1)+" ");
            for (int j = n-1; j >= 0; j--) {
                System.out.print(board.squares[i][j].toString() + " ");
            }
            System.out.print("\n");
        }
        System.out.print("   ");
        for (int j = n-1; j >= 0; j--) {
            System.out.print(Character.toString((char)(97+j)) + "   ");
        }
        System.out.print("\n\n");
    }
}
