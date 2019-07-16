package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class King implements PieceType {
   @Override
   public List<Effect> move(Move mov, Board board) {
      Square src = mov.src;
      if(mov.ady() > 1) return null;
      if(mov.adx() > 1) {
         if(src.piece.hasMoved()) return null;
         // castling
         List<Effect> fx = new ArrayList<>();
         fx.add(new Effect(mov));
         Square rs;
         if(mov.dx() == 2) {
            // king side
            rs = board.getSquare(src.row, src.col+3);
            Square s = board.getSquare(rs.row, rs.col-2);
            if(s != null) {
               Move m = new Move(-1, rs, s);
               fx.add(new Effect(m));
            }
         } else if(mov.dx() == -2) {
            // queen side
            rs = board.getSquare(src.row, src.col-4);
            Square s = board.getSquare(rs.row, rs.col+3);
            if(s != null) {
               Move m = new Move(-1, rs, s);
               fx.add(new Effect(m));
            }
         } else {
            return null;
         }
         if(rs.piece == null) return null;
         if(!rs.piece.isRook() || rs.piece.hasMoved()) return null;
         // ensure no pieces on path b/w king & rook
         List<Square> bwPath = board.path(new Move(-1, src, rs));
         for (Square target : bwPath) {
           if (target.piece != null) return null;
         }
         // check that king can move through entire planned path w/o going into check
         List<Square> path = board.path(mov);
         for (Square target : path) {
            List<Effect> cfx = board.applyMoveFx(new Move(-1, src, target));
            if (cfx != null) board.undoAll(cfx);
            else return null;
         }
         return fx;
      }
      return Collections.singletonList(new Effect(mov));
   }

   double[][] position = new double[][]{
           { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
           { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
           { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
           { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
           { -2.0, -3.0, -3.0, -4.0, -4.0, -3.0, -3.0, -2.0},
           { -1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0},
           {  2.0,  2.0,  0.0,  0.0,  0.0,  0.0,  2.0,  2.0 },
           {  2.0,  3.0,  1.0,  0.0,  0.0,  1.0,  3.0,  2.0 }
   };

   public double score(Square s, int nextMoveID) {
      int row = s.piece.color() == Color.WHITE ? s.row : 7-s.row;
      return 900.0 + position[row][s.col];
   }

   @Override
   public List<Move> possible(Square ks, Board b, int nextMoveID) {
      Square[] poss = {
              b.getSquare(ks.row,ks.col+1),
              b.getSquare(ks.row,ks.col-1),
              b.getSquare(ks.row+1,ks.col),
              b.getSquare(ks.row-1,ks.col),
              b.getSquare(ks.row+1,ks.col+1),
              b.getSquare(ks.row-1,ks.col-1),
              b.getSquare(ks.row+1,ks.col-1),
              b.getSquare(ks.row-1,ks.col+1),
      };
      List<Square> a =  new ArrayList<>();
      for(Square s : poss) {
         if (s != null) a.add(s);
      }
      return Move.generateMoves(ks, a, nextMoveID);
   }


   @Override
   public String toString() {
      return String.valueOf(Character.toChars(9812	));
   }

   @Override
   public boolean isKing() {
      return true;
   }
}
