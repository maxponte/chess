package com.company;

import java.util.ArrayList;
import java.util.List;

public class Move {
    Square src;
    Square dst;
    int id;
    public Move(int id, Square src, Square dst) {
        this.id = id;
        this.src = src;
        this.dst = dst;
    }
    public int dx() {
        return dst.col - src.col;
    }
    public int adx() {
        return Math.abs(dst.col - src.col);
    }
    public int dy() {
        return dst.row - src.row;
    }
    public int ady() {
        return Math.abs(dst.row - src.row);
    }
    public String an() {
        return src.an() + "," + dst.an();
    }
    public static List<Move> generateMoves(Square src, List<Square> dests, int id) {
        List<Move> p = new ArrayList<>();
        for (Square sq : dests) {
            p.add(new Move(id, src, sq));
        }
        return p;

    }
}
