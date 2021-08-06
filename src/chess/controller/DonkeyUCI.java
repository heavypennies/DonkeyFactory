package chess.controller;

import chess.engine.model.*;
import chess.engine.search.*;
import chess.engine.utils.MoveGeneration;
import free.chessclub.bot.TwitterManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import twitter4j.TwitterException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static chess.engine.search.Searcher.INFINITY;

public class DonkeyUCI implements SearchWatcher {
    private static final String DONKEY_VERSION = "0.1.1";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss aaa");

    private int tweetMax = 1;
    private int tweetSoft = 3;
    private String twitterUser;

    public static final String UCI_OPTION_TWEET_SOFT = "tweetSoft";
    public static final String UCI_OPTION_TWEET_MAX = "tweetMax";
    public static final String UCI_OPTION_TWITTER_USER = "twitterUser";

    public static String UCI_UCI = "uci";
    public static String UCI_DEBUG = "debug";
    public static String UCI_IS_READY = "isready";
    public static String UCI_SET_OPTION = "setoption";
    public static String UCI_REGISTER = "register";
    public static String UCI_NEW_GAME = "ucinewgame";
    public static String UCI_POSITION = "position";
    public static String UCI_POSITION_START_POSITION = "startpos";
    public static String UCI_POSITION_FEN = "fen";
    public static String UCI_POSITION_MOVES = "moves";
    public static String UCI_PONDER_HIT = "ponderhit";
    public static String UCI_GO = "go";
    public static String UCI_GO_SEARCH_MOVES = "searchmoves";
    public static String UCI_GO_PONDER = "ponder";
    public static String UCI_GO_WHITE_TIME = "wtime";
    public static String UCI_GO_BLACK_TIME = "btime";
    public static String UCI_GO_MOVES_TO_GO = "movestogo";
    public static String UCI_GO_DEPTH = "depth";
    public static String UCI_GO_NODES = "nodes";
    public static String UCI_GO_MATE = "mate";
    public static String UCI_GO_MOVE_TIME = "movetime";
    public static String UCI_GO_INFINITE = "infinite";
    public static String UCI_STOP = "stop";
    public static String UCI_EVAL = "eval";
    public static String UCI_QUIT = "quit";

    private TwitterManager twitter;

    private MoveGeneration moveGeneration = new MoveGeneration();
    private BoardEvaluator eval = new SimpleEvaluator(moveGeneration);
    private ABSearch search = new ABSearch(moveGeneration, eval);
    private Move[] availableMoves = Move.createMoves(100);
    private IterativeSearch iterativeSearch;
    private Thread searchThread;
    private Thread timerThread;
    private int lastScore = 0;

    private PrintWriter logger;

    private Board gameBoard;
    private String fen;

    private boolean pondering = false;
    private boolean talkedMate = false;
    private boolean spoken = false;

    public DonkeyUCI() {
        iterativeSearch = new IterativeSearch(search, moveGeneration, eval, this);
        try {
            logger = new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File("donkey.log"))), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        logger.println("DonkeyFactory v" + DONKEY_VERSION);
    }

    @Override
    public void searchInfo(SearchStats stats, Move[] pv) {
        logger.println(stats.toString());
        logger.println(Move.toString(pv));
        if(!pondering) {
            if(!iterativeSearch.isDone()) {
                System.out.println("info " + stats.toInfoString() + " pv " + toAlgebraicLine(pv));

                lastScore = stats.score;

                if (stats.score > lastScore + 10000) {
                    System.out.println("chatLine Hee-Haw (" + stats.formatScore(stats.score) + ") - " + Move.toString(pv));
                    spoken = true;
                } else if (stats.score > lastScore + 900) {
                    System.out.println("chatLine Zoiks! (" + stats.formatScore(stats.score) + ") - " + Move.toString(pv));
                    spoken = true;
                } else if (stats.score > lastScore + 300) {
                    System.out.println("chatLine Ruh roh... (" + stats.formatScore(stats.score) + ") - " + Move.toString(pv));
                    spoken = true;
                }
                else if (stats.score < lastScore - 10000) {
                    System.out.println("chatLine You got me! (" + stats.formatScore(stats.score) + ") - " + Move.toString(pv));
                    spoken = true;
                } else if (stats.score < lastScore - 900) {
                    System.out.println("chatLine Eek - I need help (" + stats.formatScore(stats.score) + ") - " + Move.toString(pv));
                    spoken = true;
                } else if (stats.score < lastScore - 300) {
                    System.out.println("chatLine Dang, you are good! (" + stats.formatScore(stats.score) + ") - " + Move.toString(pv));
                    spoken = true;
                }
            }
            if(Math.abs(stats.score) > Searcher.MATE - 300) {
                iterativeSearch.stop();
                maybeTweetMatePuzzle(stats, pv);
            }
        }
    }

    private void maybeTweetMatePuzzle(SearchStats stats, Move[] pv) {
        if (!talkedMate) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int movesToMate = 1;
                    int color = pv[0].moved.color;
                    int movedType = pv[0].moved.type;
                    if(pv[1].moved == null) {
                        return;
                    }
                    int movedType2 = pv[1].moved.type;
                    int complexity = -Math.abs(eval.scorePosition(gameBoard, 0, 0)) / 600;
                    logger.println("MatePV: " + Move.toString(pv));

                    int material = 0;
                    for (int i = 0; i < 100 && pv[i].moved != null; i++) {
                        if(pv[i].taken != null) {
                            material += (pv[i].taken.color == color ? -1 : 1) * Math.abs(pv[i].taken.getMaterialValue());
                        }
                        if (pv[i].moved.color == color) {
                            if (pv[i].promoteTo == -1) {
                                if (pv[i].moved.type != Piece.QUEEN && pv[i].moved.type != Piece.ROOK) {
                                    if (movedType != pv[i].moved.type) {
                                        complexity += 1;
                                        movedType = pv[i].moved.type;
                                    }
                                } else if (pv[i].taken == null) {
                                    complexity += 1;
                                } else if (!pv[i].check) {
                                    complexity += 1;
                                }
                            } else if (pv[i].promoteTo == Piece.QUEEN) {
                                complexity -= 1;
                            }
                        } else {
                            if (pv[i].taken != null) {
                                complexity += 1;
                            } else if (movedType2 != pv[i].moved.type) {
                                complexity += 1;
                                movedType2 = pv[i].moved.type;
                            }
                        }
                        movesToMate++;
                    }
                    complexity -= material / 3;
                    movesToMate /= 2;
                    complexity += (movesToMate / 2) - 1;

                    int endIndex = fen.indexOf(" ");
                    String boardURL = "http://www.fen-to-image.com/image/36/double/" + fen.substring(0, endIndex > -1 ? endIndex : fen.length());
                    String tinyUrl = getTinyUrl(boardURL);

                    if (tinyUrl != null && !tinyUrl.equals("Error") && complexity + tweetSoft > tweetMax && movesToMate > 1) {
                        talkedMate = true;
                        // New DonkeyFactory Chess position
                        // Tweet
                        final String tweet = (complexity < tweetMax ? "@heavypennies " : "" ) + (color == 1 ? "White" : "Black") + " mates in " + movesToMate + " (DF-" + complexity + ") -> " + tinyUrl + " - solution in next tweet!  #donkeyfactory #chess";
                        final String mateLine = Move.toString(pv);
                        logger.println("Tiny URL: " + tinyUrl);
                        logger.println("Full URL: " + boardURL);
                        logger.println("Mate Line: " + mateLine);
                        logger.println(tweet);
                        tweet(tweet);


                        final int colorX = color;
                        final int movesToMateX = movesToMate;
                        final int complexityX = complexity;
                        final String tinyUrlX = tinyUrl;

                        try {
                            Thread.sleep(1000 * 60 * 5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        final String solutionTweet = (complexityX < tweetMax ? "@heavypennies " : "" ) + "M" + movesToMateX + ": " + tinyUrlX + "\n Solution:" + mateLine;
                        tweet(solutionTweet);
                        logger.println("Tweeted solution: " + solutionTweet);
                        System.out.println("chatLine Thank You! We created a new DonkeyFactory chess puzzle for this Mate in " + movesToMate + ", Estimated Complexity (DonkeyScale): " + complexity + "");
                        System.out.println("chatLine http://twitter.com/donkeyfactory");
                    }
                    else {
                        logger.println("(Difficulty " + complexity + ")-[" + dateFormat.format(new Date()) + "] I found Mate in " + movesToMate + " -> " + boardURL + " #donkeyfactory #chess");
                    }
                }
            }).start();
        }
    }

    public void tweet(String tweet) {
        if (twitter == null) return;
        try {
            twitter.tweet(tweet);
            logger.println("tweet complete.");
        } catch (TwitterException e) {
            logger.println("tweet failed.");
            e.printStackTrace(logger);
        }
    }

    public static String getTinyUrl(String fullUrl) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://tinyurl.com/api-create.php?url=" + fullUrl);

        try {
            HttpResponse response = httpclient.execute(get);
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class UCISearchOptions {
        public int whiteTime = 0;
        public int blackTime = 0;
        public int movesToGo = 0;
        public int depth = 100;
        public int nodes = 0;
        public boolean mate = false;
        public int moveTime = 0;
        public boolean infinite = false;
        public boolean ponder = false;

        public UCISearchOptions(String msg) {
            String[] searchOptionParts = msg.split(" ");
            for(int i = 1;i < searchOptionParts.length;i++) {
                if(searchOptionParts[i].equals(UCI_GO_DEPTH)) {
                    depth = Integer.parseInt(searchOptionParts[++i]);
                }
                if(searchOptionParts[i].equals(UCI_GO_NODES)) {
                    nodes = Integer.parseInt(searchOptionParts[++i]);
                }
                if(searchOptionParts[i].equals(UCI_GO_WHITE_TIME)) {
                    whiteTime = Integer.parseInt(searchOptionParts[++i]);
                }
                if(searchOptionParts[i].equals(UCI_GO_BLACK_TIME)) {
                    blackTime = Integer.parseInt(searchOptionParts[++i]);
                }
                if(searchOptionParts[i].equals(UCI_GO_MOVE_TIME)) {
                    moveTime = Integer.parseInt(searchOptionParts[++i]);
                }
                if(searchOptionParts[i].equals(UCI_GO_INFINITE)) {
                    infinite = true;
                }
                if(searchOptionParts[i].equals(UCI_GO_PONDER)) {
                    ponder = true;
                }
            }
        }

        @Override
        public String toString() {
            return "UCISearchOptions{" +
                    "whiteTime=" + whiteTime +
                    ", blackTime=" + blackTime +
                    ", movesToGo=" + movesToGo +
                    ", depth=" + depth +
                    ", nodes=" + nodes +
                    ", mate=" + mate +
                    ", moveTime=" + moveTime +
                    ", infinite=" + infinite +
                    '}';
        }
    }

    public static void main(String[] args) {
        InputReader inputReader = new InputReader(new DonkeyUCI());
        inputReader.start();
        try {
            inputReader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


    private static class InputReader extends Thread
    {
        private DonkeyUCI controller;

        public InputReader(DonkeyUCI controller) {
            this.controller = controller;
        }

        @Override
        public void run()
        {
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            String msg;

            while(!isInterrupted())
            {
                try
                {
                    if(stdin.ready())
                    {
                        msg = stdin.readLine();
                        controller.logger.println("Got: " + msg);
                        if(controller.handleUCIInput(msg)) {
                            interrupt();
                        }
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            controller.logger.println("UCI Aborted - exiting.");
        }
    }

    private static class SearchThread implements Runnable {
        private final PrintWriter logger;
        private UCISearchOptions searchOptions;
        private Searcher searcher;
        private Board searchBoard;

        public SearchThread(Searcher searcher, Board searchBoard, UCISearchOptions searchOptions, PrintWriter logger) {
            this.searcher = searcher;
            this.searchBoard = searchBoard;
            this.searchOptions = searchOptions;
            this.logger = logger;
        }

        public void run() {
            logger.println("search starting");
            searcher.search(searchBoard, searchOptions.depth);
        }
    }

    private class SearchTimerThread implements Runnable {
        private final long maxTime;

        public SearchTimerThread(long maxTime) {
            this.maxTime = maxTime;
        }

        public void run() {
            long start = System.currentTimeMillis();
            long end = System.currentTimeMillis();

            logger.println("searching for " + maxTime);
            while ((end - start <= maxTime)) {
                try {
                    Thread.sleep(maxTime - (end - start) + 1);
                } catch (InterruptedException e) {

                }
                end = System.currentTimeMillis();
            }

            logger.println("time's up!");

            stopSearch();
        }
    }

    private void stopSearch() {
        try {
            iterativeSearch.stop();

            Move[] pv = iterativeSearch.getPV();
            lastScore = pv[0].score;

            if(!pondering) {
                System.out.println("info " + search.stats.toInfoString() + " pv " + toAlgebraicLine(pv));
            }

            String bestMove = toAlgebraic(pv[0].fromSquare) + toAlgebraic(pv[0].toSquare) + promotePiece(pv[0].promoteTo);
            if(pv[1].moved != null) {

                String ponderMove = toAlgebraic(pv[1].fromSquare) + toAlgebraic(pv[1].toSquare) + promotePiece(pv[1].promoteTo);
                System.out.println("bestmove " + bestMove + " ponder " + ponderMove);
            }
            else {
                System.out.println("bestmove " + bestMove);
            }
        } catch (Exception e) {
            e.printStackTrace(logger);
        }
    }

    private String toAlgebraicLine(Move[] pv) {
        StringBuffer algebraicLine = new StringBuffer();
        for(int i = 0;i < pv.length;i++) {
            if(pv[i].moved == null) break;
            algebraicLine.append(toAlgebraic(pv[i].fromSquare)).append(toAlgebraic(pv[i].toSquare)).append(promotePiece(pv[i].promoteTo)).append(" ");
        }
        return algebraicLine.toString();
    }

    private String toAlgebraic(Square square) {
        return Constants.FILE_STRINGS[((square.index64 % 8))] + "" + ((square.index64 / 8)+1);
    }

    private String promotePiece(int promoteTo) {
        if(promoteTo == -1) {
            return "";
        }
        return Piece.PIECE_NAMES[promoteTo];
    }

    private boolean handleUCIInput(String msg) {
        if(msg.equals(UCI_UCI)) {
            inputUCI();
        }
        else if(msg.equals(UCI_IS_READY)) {
            twitter = twitterUser != null && twitterUser.length() > 0 ? new TwitterManager() : null;
            if(twitter != null) {
                twitter.connect();
            }

            System.out.println("readyok");
        }
        else if(msg.equals(UCI_SET_OPTION)) {
            inputUCIOption(msg.substring(UCI_SET_OPTION.length() + 1).trim());
        }
        else if(msg.equals(UCI_NEW_GAME)) {
            iterativeSearch.reset();
            eval.reset();
            talkedMate = false;
            pondering = false;
        }
        else if(msg.startsWith(UCI_POSITION)) {
            iterativeSearch.reset();
            eval.reset();
            inputUCIPosition(msg.substring(UCI_POSITION.length()+1).trim());
        }
        else if(msg.startsWith(UCI_GO)) {
            inputUCIGo(msg);
        }
        else if(msg.equals(UCI_PONDER_HIT)) {
            inputUCIPonderHit();
        }
        else if(msg.equals(UCI_STOP)) {
            inputUCIStop();
        }
        else if(msg.equals(UCI_EVAL)) {
            int score = eval.scorePosition(gameBoard, -INFINITY, INFINITY);
            System.out.println(gameBoard.turn == 1 ? score : -score);
            uciOk();
        }
        else if(msg.equals(UCI_QUIT)) {
            return true;
        }
        return false;
    }

    private void inputUCIOption(String option) {
        String[] optionParts = option.split(" ");
        String name = optionParts[1];
        String value = optionParts[3];

        switch(name) {
            case UCI_OPTION_TWEET_MAX: {
                tweetMax = Integer.parseInt(value);
                break;
            }
            case UCI_OPTION_TWEET_SOFT: {
                tweetSoft = Integer.parseInt(value);
                break;
            }
            case UCI_OPTION_TWITTER_USER: {
                twitterUser = value;
                break;
            }
            default:;
                logger.println("Ignoring unrecognized UCI option[" + name + ": " + value + "]");
        }
    }

    private void inputUCIPonderHit() {
        pondering = false;
        timerThread.start();
    }

    private void inputUCIStop() {
        stopSearch();
    }

    private void inputUCIGo(String msg) {
        UCISearchOptions searchOptions = new DonkeyUCI.UCISearchOptions(msg);
        logger.println(searchOptions);

        pondering = searchOptions.ponder;

        if(search.isDone()) {
            searchThread = new Thread(new SearchThread(iterativeSearch, gameBoard, searchOptions, logger));
            searchThread.setPriority(6);
            searchThread.start();
        }

        if(!searchOptions.infinite) {
            // search for some amount of time
            if(searchOptions.moveTime == 0) {
                searchOptions.moveTime = getTimeForMove(searchOptions);
            }
            timerThread = new Thread(new SearchTimerThread(searchOptions.moveTime));
            timerThread.setPriority(7);
            if(!searchOptions.ponder) {
                timerThread.start();
            }
        }
    }

    private void inputUCIPosition(String positionCommand) {
        gameBoard = new Board();
        if(positionCommand.startsWith(UCI_POSITION_START_POSITION)) {
            gameBoard.setEPDPosition(Board.initialFEN);
        }
        else if(positionCommand.startsWith(UCI_POSITION_FEN)) {
            fen = positionCommand.substring(UCI_POSITION_FEN.length()+1);
            gameBoard.setEPDPosition(fen);
        }
        else {
            gameBoard.setEPDPosition(Board.initialFEN);
        }

        if(positionCommand.contains(UCI_POSITION_MOVES)) {
            String movesString = positionCommand.substring(positionCommand.indexOf(UCI_POSITION_MOVES) + UCI_POSITION_MOVES.length()+1);
            String[] moves = movesString.split(" ");
            for(String moveString : moves) {
                int moveCount = moveGeneration.generateMoves(availableMoves, gameBoard);

                String from = moveString.substring(0,2);
                String to = moveString.substring(2,4);
                String promote = moveString.length() > 4 ? moveString.substring(4, 5) : "";


                int fromIndex64 = getIndex64(from);
                int toIndex64 = getIndex64(to);
                int promoteTo = offsetInArray(Piece.PIECE_NAMES, promote);

                for(int i = 0;i < moveCount;i++) {
                    if(availableMoves[i].fromSquare.index64 == fromIndex64 &&
                            availableMoves[i].toSquare.index64 == toIndex64 &&
                            availableMoves[i].promoteTo == promoteTo) {
                        logger.println("Move: " + availableMoves[i].toString());
                        gameBoard.make(availableMoves[i]);
                        break;
                    }
                }
            }
            fen = gameBoard.getFENPosition();
            logger.println(fen);
            logger.println(gameBoard);
        }
    }

    private int getTimeForMove(UCISearchOptions searchOptions) {
        int timeLeft = gameBoard.turn == 1?
                searchOptions.whiteTime :
                searchOptions.blackTime;

        int maxTime;
        if (gameBoard.moveIndex < 2) {
            maxTime = timeLeft / 36;
        } else if (gameBoard.moveIndex < 12) {
            maxTime = timeLeft / 32;
        } else if (gameBoard.moveIndex > 30 && timeLeft < 5000) {
            maxTime = timeLeft / 34;
        }
        else {
            maxTime = timeLeft / 29;
        }

        logger.println("Time Left: " + timeLeft);
        logger.println("MaxTime: " + maxTime);
        return maxTime;
    }

    private int getIndex64(String from) {
        return offsetInArray(Constants.FILE_STRINGS, from.substring(0, 1)) + ((Integer.parseInt(from.substring(1)) - 1) * 8);
    }

    private <T> int offsetInArray(T[] haystack, T needle) {
        for(int i = 0;i < haystack.length;i++) {
            if(haystack[i].equals(needle)) {
                return i;
            }
        }
        return -1;
    }

    private void inputUCI() {
        System.out.println("id name DonkeyFactory v" + DONKEY_VERSION);
        System.out.println("id author Josh Levine");
        System.out.println("option name debug type check");
        System.out.println("option name " + UCI_OPTION_TWEET_SOFT + " type spin default " + tweetSoft + " min 1 max 10");
        System.out.println("option name " + UCI_OPTION_TWEET_MAX + " type spin default " + tweetMax + " min 1 max 10");
        System.out.println("option name " + UCI_OPTION_TWITTER_USER + " type string");
        uciOk();
    }
    private static void uciOk() {
        System.out.println("uciok");
    }
}
