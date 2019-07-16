package com.company;

import java.io.PrintStream;
import java.util.*;

public class Board {
    public Square[][] squares;
    static final int n = 8;
    public Board() {
        squares = new Square[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                squares[i][j] = new Square(i, j);
            }
        }
        for (int i = 0; i < n; i++) {
            squares[6][i].setPiece(new Piece(Color.BLACK, new Pawn(Color.BLACK)));
        }
        for (int i = 0; i < n; i++) {
            squares[1][i].setPiece(new Piece(Color.WHITE, new Pawn(Color.WHITE)));
        }
        squares[7][4].setPiece(new Piece(Color.BLACK, new King()));
        squares[0][4].setPiece(new Piece(Color.WHITE, new King()));
        squares[7][3].setPiece(new Piece(Color.BLACK, new Queen()));
        squares[0][3].setPiece(new Piece(Color.WHITE, new Queen()));
        squares[0][2].setPiece(new Piece(Color.WHITE, new Bishop()));
        squares[0][5].setPiece(new Piece(Color.WHITE, new Bishop()));
        squares[7][2].setPiece(new Piece(Color.BLACK, new Bishop()));
        squares[7][5].setPiece(new Piece(Color.BLACK, new Bishop()));
        squares[0][1].setPiece(new Piece(Color.WHITE, new Knight()));
        squares[0][6].setPiece(new Piece(Color.WHITE, new Knight()));
        squares[7][1].setPiece(new Piece(Color.BLACK, new Knight()));
        squares[7][6].setPiece(new Piece(Color.BLACK, new Knight()));
        squares[0][0].setPiece(new Piece(Color.WHITE, new Rook()));
        squares[0][7].setPiece(new Piece(Color.WHITE, new Rook()));
        squares[7][0].setPiece(new Piece(Color.BLACK, new Rook()));
        squares[7][7].setPiece(new Piece(Color.BLACK, new Rook()));
    }
    public int colFromAN(int codePoint) {
        return codePoint - 97;
    }
    public Square squareAt(String an) {
        int col = colFromAN(Character.codePointAt(an, 0));
        int row = Character.codePointAt(an, 1) - 49; // '1' -> 0, etc.
        return squares[row][col];
    }
    public Square getKingSquare(Color color) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Square s = squares[i][j];
                if (s.piece != null && s.piece.color() == color && s.piece.isKing()) {
                    return s;
                }
            }
        }
        return null;
    }
    // ret all moves that would lead to piece on ks being eaten
    // can make it obey pins or not
    // incorrect for en-passant threatened pawns - TODO
    // ^ why? assumes the only the move dst will be killed as an effect, in enp this is not true
    public Map<Move, List<Effect>> threats(Square ks, boolean avoidCheck) {
        HashMap<Move, List<Effect>> ts = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Square s = squares[i][j];
                if (s.piece != null && s.piece.color() != ks.piece.color()) {
                    Move m = new Move(-1, s, ks);
                    List<Effect> mvs;
                    if (avoidCheck) {
                        mvs = applyMoveFx(m);
                        undoAll(mvs);
                    } else mvs = moveFx(m);
                    if (mvs != null && mvs.size() > 0) ts.put(m, mvs);
                }
            }
        }
        return ts;
    }
    public Map<Move, List<Effect>> checkThreats(Color color) {
        Square ks = getKingSquare(color);
        return threats(ks, false);
    }
    public boolean inCheck(Color color) {
        return checkThreats(color).size() > 0;
    }
    public List<Square> path(Move mov) {
        int dx = mov.dx() == 0 ? 0 : mov.dx() < 0 ? -1 : 1;
        int dy = mov.dy() == 0 ? 0 : mov.dy() < 0 ? -1 : 1;
        ArrayList<Square> cs = new ArrayList<>();
        Square c = mov.src;
        int row = c.row;
        int col = c.col;
        while(row >= 0 && row < 8 && col >= 0 && col < 8 && (row != mov.dst.row || col != mov.dst.col)) {
            c = squares[row][col];
            if (c != mov.src) cs.add(c);
            row += dy;
            col += dx;
        }
        return cs;
    }
    public boolean moveBlocked(Move mov) {
        List<Square> path = path(mov);
        for (Square c : path) {
            // we hit a piece on the path from src to dst
            if (c.piece != null) return true;
        }
        return false;
    }
    // effects of a move. ignores check, meaning pinned pieces will return fx, filtered out in applyMoveFx
    public List<Effect> moveFx(Move mov) {
        Piece piece = mov.src.piece;

        // ensure the move isn't obstructed
        if (!piece.isKnight() && moveBlocked(mov)) return null;

        List<Effect> fx = piece.move(mov, this);
        if (fx == null) return null;

        // ensure we aren't trying to eat a same color piece
        for(Effect m : fx) {
            if (m.dstPiece != null && m.dstPiece.color() == m.piece.color()) return null;
        }

        return fx;
    }
    public List<Effect> applyMoveFx(Move mov) {
        List<Effect> fx = moveFx(mov);
        if (fx == null) return null;
        Piece p = mov.src.piece;

        applyAll(fx, mov.id);

        // ensure they aren't in check after the move
        if(inCheck(p.color())) {
            // if they were, undo the move
            undoAll(fx);
            return null;
        }
        return fx;
    }
    public int move(Move mov, int nextMoveID) {
        List<Effect> fx = applyMoveFx(mov);
        if (fx != null) {
//            mov.dst.piece.commitMove(mov, nextMoveID);
            if(wins(mov.dst.piece.color())) {
                return 2;
            }
        }
        return fx != null ? 1 : 0;
    }
    public Square getSquare(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) return squares[row][col];
        return null;
    }
    public int nPieces() {
        int np = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Square s = squares[i][j];
                if(s.piece != null) np++;
            }
        }
        return np;
    }
    // is ~c in check mate?
    public boolean wins(Color c) {
        Color other = c.opposite();
        // they're in check
        if(!inCheck(other)) {
            return false;
        }

        // try moving their king around
        Square ks = getKingSquare(other);
        Square[] poss = {
                getSquare(ks.row,ks.col+1),
                getSquare(ks.row,ks.col-1),
                getSquare(ks.row+1,ks.col),
                getSquare(ks.row-1,ks.col),
                getSquare(ks.row+1,ks.col+1),
                getSquare(ks.row-1,ks.col-1),
                getSquare(ks.row+1,ks.col-1),
                getSquare(ks.row-1,ks.col+1),
        };
        for (Square p : poss) {
            if (p == null) continue;
            List<Effect> fx = applyMoveFx(new Move(-1, ks, p));
            // king is out of check on Square p
            Map<Move, List<Effect>> ct = checkThreats(other);
            if (fx != null) undoAll(fx);
            if (ct == null || ct.isEmpty()) {
                return false;
            }
        }
        // king cannot move out of check

        // try to eat or block the threatening pieces
        Map<Move, List<Effect>> mfx = checkThreats(other);
        for (Move m : mfx.keySet()) {
            Square threatSrc = m.src;
            List<Square> movePath = path(m);
            movePath.add(threatSrc);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    Square s = squares[i][j];
                    if (s.piece != null && s.piece.color() == other) {
                        for (Square t : movePath) {
                            List<Effect> mvs = applyMoveFx(new Move(-1, s, t));
                            if (mvs != null) {
                                // this move got us out of check
                                Effect x = mvs.get(0);
//                                out.println("can do move "+x.src.positionString()+" to "+x.dst.positionString());
                                undoAll(mvs);
//                                out.println("from threat "+m.src.positionString());
//                                out.println("piece is "+m.src.piece);
                                return false;
                            }
                        }
                    }
                }
            }
            movePath.remove(threatSrc);
        }

//         explainCheckMate(poss, other, ks, System.out);
//        System.out.println("true, check mate.");
        // check mate
        return true;
    }
    public void explainCheckMate(Square[] poss, Color other, Square ks, PrintStream out) {
        out.println("check by");
        for(Move m : checkThreats(other).keySet()) {
            out.println(m.an());
        }
        for (Square p : poss) {
            if (p == null) continue;
            List<Effect> fx = moveFx(new Move(-1, ks, p));
            if(fx == null) continue;
            applyAll(fx, -1);
            out.println("king on square " + p.an());
            print(out);
            // king is out of check on Square p
            Map<Move, List<Effect>> ct = checkThreats(other);
            if(ct != null) {
                out.println("king threatened on square " + p.an() + " by");
                for(Move m : ct.keySet()) {
                    out.println(m.an());
                }
                print(out);
            }
            if (fx != null) undoAll(fx);
        }
    }
    public void apply(Effect mov, int id) {
        if(mov.dst != null) mov.dst.piece = mov.piece;
        mov.src.piece = null;
        mov.piece.history.add(id);
    }
    public void applyAll(List<Effect> moves, int id) {
        for(Effect mov : moves) {
            apply(mov, id);
        }
    }
    public void undo(Effect mov) {
        mov.src.piece = mov.piece;
        if(mov.dst != null) mov.dst.piece = mov.dstPiece;
        mov.piece.rollbackMove();
    }
    public void undoAll(List<Effect> moves) {
        Collections.reverse(moves);
        for(Effect mov : moves) {
            undo(mov);
        }
        Collections.reverse(moves);
    }
    public void print(PrintStream out) {
        for (int i = 0; i < n; i++) {
            out.print(""+(i+1)+" ");
            for (int j = 0; j < n; j++) {
                out.print(squares[i][j].toString() + " ");
            }
            out.print("\n");
        }
        out.print("   ");
        for (int j = 0; j < n; j++) {
            out.print(Character.toString((char)(97+j)) + "   ");
        }
        out.print("\n\n");
    }
    public void printRev(PrintStream out) {
        for (int i = n-1; i >= 0; i--) {
            out.print(""+(i+1)+" ");
            for (int j = n-1; j >= 0; j--) {
                out.print(squares[i][j].toString() + " ");
            }
            out.print("\n");
        }
        out.print("   ");
        for (int j = n-1; j >= 0; j--) {
            out.print(Character.toString((char)(97+j)) + "   ");
        }
        out.print("\n\n");
    }
    public List<Move> allPossibleMoves(Color color, int nextMoveID) {
        List<Move> allPossible = new ArrayList<Move>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square s = getSquare(i, j);
                if (s.piece != null && s.piece.color() == color) {
                    List<Move> p = s.piece.possible(s, this, nextMoveID);
                    for(Move m : p) {
                        List<Effect> fx = applyMoveFx(m);
                        if(fx != null) {
                            allPossible.add(m);
                            undoAll(fx);
                        }
                    }
                }
            }
        }
        return allPossible;
    }
    static double discountFactor = 0.9;
    public double score(Color c, int nextMoveID, int searchDepth) {
        double result = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square s = getSquare(i, j);
                if (s.piece != null) {
                    int sign = s.piece.color() == Color.WHITE ? 1 : -1;
                    result += sign * s.piece.score(s, nextMoveID);
                }
            }
        }
        Color winner = c.opposite();
        double weight = c == Color.WHITE ? -9000 : 9000;
        if(wins(winner)) result = weight;
        return Math.pow(discountFactor, searchDepth-1) * result;
    }
}
