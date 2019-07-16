package com.company;

public enum Color {
    WHITE, BLACK, GREY;
    public Color opposite() {
        switch(this) {
            case BLACK: return WHITE;
            case WHITE: return BLACK;
            default: return GREY;
        }
    }
}
