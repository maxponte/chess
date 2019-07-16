package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Knight implements PieceType {
    @Override
    public List<Effect> move(Move mov, Board board) {
        if(mov.adx() == 2 && mov.ady() != 1) return null;
        if(mov.ady() == 2 && mov.adx() != 1) return null;
        if(mov.ady() != 2 && mov.adx() != 2) return null;
        return Collections.singletonList(new Effect(mov));
    }

    @Override
    public List<Move> possible(Square s, Board b, int nextMoveID) {
        List<Square> res = new ArrayList<>();
        Square m;
        m = b.getSquare(s.row + 2, s.col - 1);
        if(m != null) res.add(m);
        m = b.getSquare(s.row + 2, s.col + 1);
        if(m != null) res.add(m);
        m = b.getSquare(s.row + 1, s.col + 2);
        if(m != null) res.add(m);
        m = b.getSquare(s.row + 1, s.col - 2);
        if(m != null) res.add(m);
        m = b.getSquare(s.row - 1, s.col + 2);
        if(m != null) res.add(m);
        m = b.getSquare(s.row - 1, s.col - 2);
        if(m != null) res.add(m);
        m = b.getSquare(s.row - 2, s.col + 1);
        if(m != null) res.add(m);
        m = b.getSquare(s.row - 2, s.col - 1);
        if(m != null) res.add(m);
        return Move.generateMoves(s, res, nextMoveID);
    }


    double[][] position = new double[][]{
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
            {-4.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -4.0},
            {-3.0,  0.0,  1.0,  1.5,  1.5,  1.0,  0.0, -3.0},
            {-3.0,  0.5,  1.5,  2.0,  2.0,  1.5,  0.5, -3.0},
            {-3.0,  0.0,  1.5,  2.0,  2.0,  1.5,  0.0, -3.0},
            {-3.0,  0.5,  1.0,  1.5,  1.5,  1.0,  0.5, -3.0},
            {-4.0, -2.0,  0.0,  0.5,  0.5,  0.0, -2.0, -4.0},
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0}
    };

    public double score(Square s, int nextMoveID) {
        return 30 + position[s.row][s.col];
    }

    @Override
    public String toString() {
        return "K";
    }

    @Override
    public boolean isKnight() {
        return true;
    }
}
