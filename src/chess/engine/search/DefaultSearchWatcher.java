package chess.engine.search;

import chess.engine.model.Move;

public class DefaultSearchWatcher implements SearchWatcher {
    @Override
    public void searchInfo(SearchStats stats, Move[] pv) {
        System.err.println(new StringBuilder(stats.toString()));
        System.err.println(new StringBuilder("Best: ").append(Move.toString(pv)));
        System.err.println("Score: " + stats.score);
    }
}
