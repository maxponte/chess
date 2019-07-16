package com.company;

import java.util.ArrayList;
import java.util.List;

public class Pawn implements PieceType {

    // IF ANY OF THESE CHANGE, UPDATE PieceTypeSerializer.java
    private int velocity;
    Color color;

    public Pawn(Color color) {
       this.velocity = color == Color.WHITE ? 1 : -1;
       this.color = color;
    }

    @Override
    public boolean isPawn() {
        return true;
    }

    @Override
    public List<Effect> move(Move mov, Board board) {
        Square src = mov.src;
        Square dst = mov.dst;
        // moving too fast

        if(mov.dy() != velocity && mov.dy() != 2*velocity) return null;
        // no double hops if not in starting position
        if(mov.dy() == 2 && src.row != 1) return null;
        if(mov.dy() == -2 && src.row != 6) return null;
        List<Effect> effects = new ArrayList<>();
        if(mov.dx() != 0) { // diagonal move
            if(mov.adx() > 1) return null;
            if (dst.piece == null) {
                // check for en passant
                Square enp = board.squares[dst.row - velocity][dst.col];
                if (enp.piece != null && enp.piece.isPawn()) {
                    Piece victim = enp.piece;
                    // must be done the next move after the target pawn moves 2 squares
                    if (victim.color() != color && victim.lastMoveID().intValue() == mov.id-1) {
                        effects.add(new Effect(enp, null, enp.piece, null));
                    } else {
                        return null;
                    }
                }
            }
        } else if(dst.piece != null) return null; // can't move forward into a piece
        effects.add(new Effect(src, dst, src.piece, dst.piece));
        return effects;
    }

    public List<Move> possible(Square s, Board b, int nextMoveID)  {
        List<Square> sl = new ArrayList<>();
        if (s.row == 1 || s.row == 6) {
            Square v = b.getSquare(s.row + velocity*2, s.col);
            if(v != null) sl.add(v);
        }
        Square v = b.getSquare(s.row + velocity, s.col);
        if(v != null) sl.add(v);
        if ((v = b.getSquare(s.row + velocity, s.col + 1)) != null) {
            if (v.piece != null) {
                sl.add(v);
            } else {
                // check for en passant
                Square ep = b.getSquare(s.row, s.col + 1);
                if (ep != null && (s.row == 3 || s.row == 4) && ep.piece != null && ep.piece.color() != color && ep.piece.lastMoveID() == nextMoveID - 1) {
                    sl.add(b.getSquare(s.row + velocity, s.col + 1));
                }
            }
        }
        if ((v = b.getSquare(s.row + velocity, s.col - 1)) != null && v.piece != null) {
            sl.add(v);
            if (v.piece != null) {
                sl.add(v);
            } else {
                // check for en passant
                Square ep = b.getSquare(s.row, s.col - 1);
                if (ep != null && (s.row == 3 || s.row == 4) && ep.piece != null && ep.piece.color() != color && ep.piece.lastMoveID() == nextMoveID - 1) {
                    sl.add(b.getSquare(s.row + velocity, s.col - 1));
                }
            }
        }
        return Move.generateMoves(s, sl, nextMoveID);
    }

    @Override
    public String toString() {
       return "P";
    }

    double[][] position = new double[][]{
            {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
            {5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0},
            {1.0,  1.0,  2.0,  3.0,  3.0,  2.0,  1.0,  1.0},
            {0.5,  0.5,  1.0,  2.5,  2.5,  1.0,  0.5,  0.5},
            {0.0,  0.0,  0.0,  2.0,  2.0,  0.0,  0.0,  0.0},
            {0.5, -0.5, -1.0,  0.0,  0.0, -1.0, -0.5,  0.5},
            {0.5,  1.0, 1.0,  -2.0, -2.0,  1.0,  1.0,  0.5},
            {0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0}
    };

    public double score(Square s, int nextMoveID) {
        int row = s.piece.color() == Color.WHITE ? s.row : 7-s.row;
        double bonus = nextMoveID < 0 ? 100.0 : 1.0;
        return 10.0 + (bonus*position[row][s.col]);
    }
}
