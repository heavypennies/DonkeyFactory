package chess.engine.search;

import chess.engine.model.Move;

public interface SearchWatcher {
    public void searchInfo(SearchStats stats, Move[] pv);
}
