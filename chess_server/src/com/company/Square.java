package com.company;

public class Square {
    public int row;
    public int col;
    public Piece piece;
    public Square(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public void setPiece(Piece p) {
        piece = p;
    }
    public Color color() {
        return (row+col) % 2 == 0 ? Color.WHITE : Color.BLACK;
    }
    public String toString() {
        return "[" + (piece == null ? color() == Color.BLACK ? "#" : " " : piece.toString()) + "]";
    }
    public String positionString() {
        return ""+row+","+col;
    }
    public String an() {
        return Character.toString(Character.toChars(col+97)[0]) + Character.toString(Character.toChars(row+49)[0]);
    }
}
