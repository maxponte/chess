package com.company;

public class Effect {
    Square src;
    Square dst;
    Piece piece;
    Piece dstPiece;
    public Effect(Square src, Square dst, Piece piece, Piece dstPiece) {
        this.src = src;
        this.dst = dst;
        this.piece = piece;
        this.dstPiece = dstPiece;
    }
    public Effect(Move mov) {
        this.src = mov.src;
        this.dst = mov.dst;
        this.piece = mov.src.piece;
        this.dstPiece = mov.dst.piece;
    }
}
