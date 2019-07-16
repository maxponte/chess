package com.company;

import com.google.gson.Gson;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    private Board board;
    public int nextMoveID = 0;
    public Color winner;
    Broker b;
    Gson gson;
    PrintStream out;
    public Game(Gson gson, Broker b) {
        this.b = b;
        this.gson = gson;
        out = System.out;
        reset();
    }
    public void reset() {
        nextMoveID = 0;
        winner = null;
        board = new Board();
        showBoard();
    }
    public Move toMove(String srcS, String dstS) {
        if(srcS.equals(dstS)) return null;
        Color color = nextMoveID % 2 == 0 ? Color.WHITE : Color.BLACK;
        Square src = board.squareAt(srcS);
        Piece piece = src.piece;

        // ensure they're moving their own piece
        if (piece == null || piece.color() != color) {
            return null;
        }

        Square dst = board.squareAt(dstS);
        Move mov = new Move(nextMoveID, src, dst);
        return mov;
    }
    public void showBoard() {
        b.publish(new Message(1, gson.toJson(board)));
    }
    public void initBoard() {
        b.publish(new Message(3, gson.toJson(board)));
    }
    public void simpleMessage(String s) {
        b.publish(new Message(2, s));
    }
    public boolean autoMove() {
        Color color = nextMoveID % 2 == 0 ? Color.WHITE : Color.BLACK;
        List<Move> allPossible = board.allPossibleMoves(color, nextMoveID);
        int n = allPossible.size();
        if(n == 0) {
            return false;
        }
        Random rand = new Random();
        Move chosen = allPossible.get(rand.nextInt(allPossible.size()));
        moveH(chosen);
        return true;
    }
    public boolean autoMinimaxMove() {
        Color color = nextMoveID % 2 == 0 ? Color.WHITE : Color.BLACK;
        ScoredMove m = minimaxMove(5, color, Integer.MIN_VALUE, Integer.MAX_VALUE, null, null, false, 0);
        Move chosen = m.move;
        if (chosen == null) return false;
        moveH(chosen);
        return true;
    }
    class ScoredMove {
        double score;
        Move move;
        double alpha;
        double beta;
        public ScoredMove(double score, Move move, double alpha, double beta) {
            this.move = move;
            this.score = score;
            this.alpha = alpha;
            this.beta = beta;
        }
    }
    // captures first
    public void sortMoves(List<Move> m) {
        int j = 0;
        for (int i = 0; i < m.size(); i++) {
            Move k = m.get(i);
            if(k.dst.piece != null) Collections.swap(m, i, j++);
        }
    }
    public ScoredMove minimaxMove(int maxDepth, Color color, double alpha, double beta, List<Move> pw, List<Move> pb, boolean qui, int absDepth) {
        absDepth++;
        if (maxDepth == 0 || absDepth > 8) {
            return new ScoredMove(board.score(color, nextMoveID, absDepth), null, alpha, beta);
        }
        List<Move> allPossible = board.allPossibleMoves(color, nextMoveID);
        if(qui) {
            List<Move> prev = color == Color.WHITE ? pw : pb;
            allPossible.removeAll(prev);
        }
        if (allPossible.size() == 0) {
            return new ScoredMove(board.score(color, nextMoveID, absDepth), null, alpha, beta);
        }
        double max = Integer.MIN_VALUE;
        Move maxMove = null;
        double min = Integer.MAX_VALUE;
        Move minMove = null;
        sortMoves(allPossible);
        for (Move m : allPossible) {
            double delta = 0;
            if(maxDepth == 1) {
                // qui
                delta = board.score(color, nextMoveID, absDepth);
            }
            List<Effect> fx = board.applyMoveFx(m);
            if(maxDepth == 1) {
                // qui
                delta = board.score(color, nextMoveID, absDepth) - delta;
                if(delta >= 30) {
                    maxDepth++;
                    qui = true;
                }
            }
            nextMoveID++;
            List<Move> nxtPw = color == Color.WHITE ? allPossible : pw;
            List<Move> nxtPb = color == Color.BLACK ? allPossible : pb;
            ScoredMove further = minimaxMove(maxDepth - 1, color.opposite(), alpha, beta, nxtPw, nxtPb, qui, absDepth);
            board.undoAll(fx);
            nextMoveID--;
            double score = further.score;
            if (score < min) {
                min = score;
                minMove = m;
                if (color == Color.BLACK && score < beta) {
                    beta = score;
                    if (beta <= alpha) break;
                }
            }
            if (score > max) {
                max = score;
                maxMove = m;
                if (color == Color.WHITE && score > alpha) {
                    alpha = score;
                    if (alpha >= beta) break;
                }
            }
        }
        Move chosen = color == Color.WHITE ? maxMove : minMove;
        double result = color == Color.WHITE ? max : min;
        return new ScoredMove(result, chosen, alpha, beta);
    }
    private void moveH(Move mov) {
        if (winner != null) {
            simpleMessage("game over already");
            return;
        }
        if (mov == null) {
            simpleMessage("invalid move");
            showBoard();
            return;
        }
        int code = board.move(mov, nextMoveID);
        if (code > 0) {
            nextMoveID++;
            if (code == 2) {
                winner = nextMoveID % 2 == 0 ? Color.WHITE : Color.BLACK;
                System.out.println("check mate happened");
                simpleMessage("check mate");
            } else {
                simpleMessage("moved " + mov.dst.piece + ": " + mov.src.an() + " to " + mov.dst.an());
            }
            showBoard();
        } else {
            simpleMessage("invalid move");
            showBoard();
        }
    }
    public int nPieces() {
        return board.nPieces();
    }
    public void move(String srcS, String dstS) {
        Move mov = toMove(srcS, dstS);
        moveH(mov);
    }
}
