package com.company;

import java.util.List;

public interface PieceType {
    public List<Effect> move(Move mov, Board board);
    public List<Move> possible(Square s, Board board, int nextMoveID);
    public String toString();
    default public boolean isKnight() {
        return false;
    }
    default public boolean isPawn() { return false; }
    default public boolean isKing() { return false; }
    default public boolean isRook() { return false; }
    public double score(Square s, int nextMoveID);
}
