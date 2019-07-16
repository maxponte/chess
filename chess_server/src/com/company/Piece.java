package com.company;

import java.util.ArrayList;
import java.util.List;

// bad to have abstract class sometimes
// could have a "HistoryPiece(piece) = piece"
// team member writes new shitty chess piece
// modifying the history via the new piece
// how much does person need to know?
public class Piece {
    public PieceType pt;
    List<Integer> history = new ArrayList<>();
    Color color;
    public Piece(Color c, PieceType pt) {
        this.pt = pt;
        color = c;
    }

    public List<Effect> move(Move mov, Board board) {
        return pt.move(mov, board);
    }

    public List<Move> possible(Square s, Board b, int nextMoveID) {
        return pt.possible(s, b, nextMoveID);
    }

    public Color color() {
        return color;
    }

    public void commitMove(Move mov, int id) {
        mov.id = id;
        history.add(id);
    }

    public void rollbackMove() {
        history.remove(history.size()-1);
    }

    public Integer lastMoveID() {
        return history.get(history.size()-1);
    }

    public boolean hasMoved() {
        return history.size() > 0;
    }

    public boolean isKnight() {
        return pt.isKnight();
    }

    public boolean isPawn() {
        return pt.isPawn();
    }

    public boolean isKing() {
        return pt.isKing();
    }

    public boolean isRook() {
        return pt.isRook();
    }

    public double score(Square s, int nextMoveID) {
        return pt.score(s, nextMoveID);
    }

    @Override
    public String toString() {
        return pt.toString();
    }
}

