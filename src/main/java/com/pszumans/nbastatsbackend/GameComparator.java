package com.pszumans.nbastatsbackend;

import java.util.Comparator;

public class GameComparator implements Comparator<Game> {
    @Override
    public int compare(Game o1, Game o2) {
        return o1.isOnline() && !o2.isOnline() ? -1
                : !o1.isOnline() && o2.isOnline() ? 1
                : Long.valueOf(o1.getGameId()).compareTo(Long.valueOf(o2.getGameId()));
    }
}
