package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Queen implements PieceType {
    @Override
    public List<Effect> move(Move mov, Board board) {
        if(mov.ady() > 0 && mov.adx() > 0 && mov.ady() != mov.adx()) return null;
        return Collections.singletonList(new Effect(mov));
    }

    @Override
    public List<Move> possible(Square s, Board b, int nextMoveID) {
        Bishop bi = new Bishop();
        Rook r = new Rook();
        List<Move> res = new ArrayList<>();
        for (Move m : r.possible(s, b, nextMoveID)) {
            res.add(m);
        }
        for (Move m : bi.possible(s, b, nextMoveID)) {
            res.add(m);
        }
        return res;
    }

    double[][] position = new double[][]{
            { -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0 },
            { -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0 },
            { -1.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0 },
            { -0.5, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5 },
            { 0.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5 },
            { -1.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0 },
            { -1.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, -1.0 },
            { -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0 },
    };

    public double score(Square s, int nextMoveID) {
        return 90 + position[s.row][s.col];
    }

    @Override
    public String toString() {
        return "Q";
    }
}
