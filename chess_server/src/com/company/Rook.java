package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rook implements PieceType {
    @Override
    public List<Effect> move(Move mov, Board board) {
        if(mov.ady() > 0 && mov.adx() > 0) return null;
        return Collections.singletonList(new Effect(mov));
    }

    public List<Move> possible(Square s, Board b, int nextMoveID) {
        List<Square> res = new ArrayList<>();
        List<Square> p;
        p = b.path(new Move(-1, s, new Square(s.row + 8, s.col)));
        for (Square  sq : p) {
            res.add(sq);
        }
        p = b.path(new Move(-1, s, new Square(s.row - 8, s.col)));
        for (Square  sq : p) {
            res.add(sq);
        }
        p = b.path(new Move(-1, s, new Square(s.row, s.col + 8)));
        for (Square  sq : p) {
            res.add(sq);
        }
        p = b.path(new Move(-1, s, new Square(s.row, s.col - 8)));
        for (Square  sq : p) {
            res.add(sq);
        }
        return Move.generateMoves(s, res, nextMoveID);
    }

    @Override
    public String toString() {
        return "R";
    }

    @Override
    public boolean isRook() {
        return true;
    }

    double[][] position = new double[][]{
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5, },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5, },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5, },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5, },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5, },
            { 0.0, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0, },
    };

    public double score(Square s, int nextMoveID) {
        int row = s.piece.color() == Color.WHITE ? s.row : 7-s.row;
        return 50.0 + position[row][s.col];
    }
}
