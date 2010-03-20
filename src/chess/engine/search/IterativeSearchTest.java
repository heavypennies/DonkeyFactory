/* $Id$ */

package chess.engine.search;

import chess.engine.model.Board;
import chess.engine.model.Move;
import chess.engine.utils.MoveGeneration;

import java.util.Date;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class IterativeSearchTest //extends TestCase
{
  public void testSimpleABSearch()
  {

// MATS

/*
    Board searchBoard = new Board("2r1k2r/pp2bp1p/1q3pp1/3P1b2/4N3/2Qp1N2/PP3PPP/R3R1K1"); // w k - bm d6; id MATS001;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 0;
    searchBoard.stats.blackQueensideRookMoves++;
*/

/*
    Board searchBoard = new Board("r1b2rk1/2p1nppp/pp1q1n2/3p4/3P4/P1NBP3/1PQ1NPPP/R3K2R"); // w KQ - bm e4;id MATS002;
    searchBoard.stats.whiteCastleFlag = 0;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.stats.blackKingMoves = 1;
    searchBoard.stats.blackKingsideRookMoves = 1;
*/


/*

    Board searchBoard = new Board("2r1r1k1/1p1q1ppp/3p1b2/p2P4/3Q4/5N2/PP2RPPP/4R1K1"); // w - - bm Qg4; MATS003;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
*/

/*
    Board searchBoard = new Board("2rr2k1/1b3ppp/pb2p3/1p2P3/1P2BPnq/P1N3P1/1B2Q2P/R4R1K"); // b	- - bm Rxc3; id MATS004;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/

/*
    Board searchBoard = new Board("8/pR4pk/1b6/2p5/N1p5/8/PP1r2PP/6K1"); // b - - bm Rxb2; id MATS005;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/

/*
Board searchBoard = new Board("2r3k1/p2q1rpp/1p3pn1/3P4/4P2P/PQ4B1/6P1/2R2RK1"); // w - - bm d6; id MATS006;
Board searchBoard = new Board("3k4/8/3n2pp/pp1Pp3/P3P2P/3K2P1/8/3B4"); // b - - bm b4; id MATS007;
    searchBoard.turn = 0;
*./
Board searchBoard = new Board("r1b2rk1/5ppp/p3p3/1pnpP3/5P2/2N3P1/PPP3BP/R4RK1"); // w - - bm Ne2; id MATS008;
 */
/*
Board searchBoard = new Board("2r2k2/1p3p1p/p1N3p1/3p4/8/1P2PP2/P5PP/2R2K2"); // w - - bm Rc5; id MATS009;
    Board searchBoard = new Board("4rnk1/pp1q1ppp/2p4r/3p4/3P4/4P1NP/PPQ2PP1/R3R1K1"); // w - - bm b4; id MATS010;
Board searchBoard = new Board("r2q1rk1/pp2bppp/4pn2/3nN1B1/3P4/1B5Q/PP3PPP/3R1RK1"); // w - - bm f4; id MATS011;
Board searchBoard = new Board("2b1k2r/1p2q1pp/p1p5/P7/3r1P2/1Q1N4/1P1bP1BP/R4RK1"); // b k - bm Be6; id MATS012;
    searchBoard.turn = 0;
Board searchBoard = new Board("r1q2r1k/p2n2pp/1p1p1b2/2pP1p2/2P1pP2/1QB1N1P1/PP2P2P/R4R1K"); // w - - bm Nxf5; id MATS013;
Board searchBoard = new Board("4r3/p1p2bk1/3p2p1/2p4p/4PR2/1P1B4/P1P3PP/6K1"); // b - - bm c4; id MATS014;
    searchBoard.turn = 0;
Board searchBoard = new Board("r2q1rk1/1p4pp/4p1n1/pNbpPp2/P1P5/3Q4/1P3PPP/R1B1R1K1"); // b - - bm Qh4; id MATS015;
    searchBoard.turn = 0;
*/
/*
Board searchBoard = new Board("b7/3knp2/p2p2pb/1p1Pp2p/1B2P1PP/1P1B1P2/P7/5NK1"); // b - - bm hxg4; id MATS016;
    searchBoard.turn = 0;
*/
/*
Board searchBoard = new Board("2n5/1b2k3/p2pB1p1/BpbPp1P1/4P3/1P4K1/P1N5/8"); // b - - bm Ba7; id MATS017;
    searchBoard.turn = 0;
Board searchBoard = new Board("4r1k1/pppb1pp1/1n6/3P4/2PN4/3B4/P1P4P/K5R1"); // b - - bm Na4; id MATS018;
    searchBoard.turn = 0;

 */
/*
    Board searchBoard = new Board("4r1k1/1p3p1p/4b1p1/3p4/3P1P2/6P1/4B2P/2R3K1"); // b - - bm Bd7; id MATS019;
    searchBoard.turn = 0;
*/
/*
Board searchBoard = new Board("2r1k2r/5ppp/p1pRp3/8/8/4P3/PPP3PP/2KR4"); // b k - bm h5; id MATS020;
Board searchBoard = new Board("r3kb1r/2p3pp/p1n1p3/1pn1P3/8/1BPq4/P2N1PPP/R1BQ1RK1"); // w kq - bm Bc2; id MATS021;
Board searchBoard = new Board("2kr3r/pp1n4/2pb2q1/3pp2p/2P5/1B1P1R2/PP1NQ1PP/5RK1"); // b - - bm e4; id MATS022;
    searchBoard.turn = 0;
Board searchBoard = new Board("r1bq1rk1/ppp2pbp/6p1/8/4p3/BP2P3/P1P1BPPP/R2QK2R"); // w KQ - am Bxf8; id MATS023;
Board searchBoard = new Board("r3k2r/1ppnqppp/p1pb4/4p3/4P3/P2P1P1P/1PPBNP2/R2QK2R"); // b KQkq - bm Nc5; id MATS024;
    searchBoard.turn = 0;
    Board searchBoard = new Board("2kr3r/pp1n4/2pb2q1/3pp2p/2P5/1B1P1R2/PP1NQ1PP/5RK1"); // b - - bm e4; id MATS022;
        searchBoard.turn = 0;
*/

// ORIGINALS

//    Board searchBoard = new Board();

   Board searchBoard = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
   searchBoard.turn = 1;

/*
   Board searchBoard = new Board("rnbqkbnr/ppp1pppp/8/8/2pPP3/8/PP3PPP/RNBQKBNR");
   searchBoard.turn = 0;
*/
/*
   Board searchBoard = new Board("rnbqkb1r/ppp1pp1p/3p1np1/8/3P4/4PN2/PPP2PPP/RNBQKB1R");
   searchBoard.turn = 1;
*/
/*
   Board searchBoard = new Board("rnbqkbnr/ppp1pppp/8/3p4/2PP4/8/PP2PPPP/RNBQKBNR");
   searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("8/8/3K4/8/3Bk3/4P3/p7/8"); // b - - bm e4; id MATS022;
        searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rn6/p4kpp/2P5/8/4r3/1p3P2/1B3K1P/1N3B1R"); // b - - bm e4; id MATS022;
        searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rn2kbnr/5ppp/bqp5/pp1pP3/3P1P2/5N2/PP2B1PP/RNBQ1RK1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("2kr2r1/pp3pBp/4b2Q/2Pq4/8/5P2/PPP3PP/RN2K1NR");
    searchBoard.stats.blackCastleFlag = 2;
*/
/*
    Board searchBoard = new Board("1rb4R/pp6/3pkp1P/5p2/2p5/5B2/P1P2PP1/6K1");
    searchBoard.turn = 0;
    searchBoard.stats.whiteKingMoves ++;
    searchBoard.stats.blackKingMoves ++;
*/
/*
    Board searchBoard = new Board("1r4k1/5rpp/p7/3pPb2/8/6PP/PPP5/2KR3R");
    searchBoard.stats.whiteCastleFlag = 2;
    searchBoard.stats.blackCastleFlag = 1;
*/
/*
    Board searchBoard = new Board("5k2/pp3p2/2n4P/4b3/6B1/1P4P1/P1Q2PK1/4q3");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("r1b1qrk1/ppp2p2/5R2/2n1P2Q/8/2P5/P1P3PP/R5K1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("1r1qr3/ppp1n2k/2n1p1pp/3p4/3P1Q2/2PB1N2/P1P2PPP/R1B3K1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("3r1rk1/4npp1/qpnNp2p/3pP3/3P4/P4N2/1Q3PPP/1RR3K1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("1n2qrk1/1b3p2/r4R1p/ppppP1pQ/3P4/2P5/PPBN3K/5R2");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("r1b1qrk1/ppp2p2/4n2R/4P2Q/8/2P5/P1P3PP/R5K1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("8/6k1/6p1/7p/3P1P2/3RK1P1/8/3r4");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("3rk3/4r1p1/6Pp/R2NPp2/3P4/4K3/P2N1P2/8");
    searchBoard.turn = 0;
*/

//    Board searchBoard = new Board("r3nrk1/p1qp1b1p/6pp/2b3P1/1QP2P2/2BP2P1/P1N2N2/1K3R2");
//    Board searchBoard = new Board("r2qkbnr/3p1ppp/b1n1p3/2p5/Q1P1P3/5N2/PP1P1PPP/RNB1K2R");
//    Board searchBoard = new Board("3r2k1/4b3/4p1p1/3rPpp1/ppR5/5N2/PPP2P1P/R5K1");
//    Board searchBoard = new Board("rnbqkb1r/pp2pppp/2p2n2/8/3P4/2N2p2/PPP1B1PP/R1BQK1NR");
//    Board searchBoard = new Board("r1bqk2r/ppp1bppp/2pn4/8/3p4/5N2/PPP2PPP/RNBQR1K1");
//    Board searchBoard = new Board("r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/5N2/PPPPBPPP/RNBQK2R");
//    Board searchBoard = new Board("r1bqkb1r/p4pp1/2p2n1p/n7/4p3/5N2/PPPPBPPP/RNBQK2R");
//    Board searchBoard = new Board("2k4r/ppp3pp/4pn2/3r3b/1B6/2PN1P2/P1P3PP/2R1R1K1");
//    Board searchBoard = new Board("rnb1kbnr/ppp1pppp/8/3q4/8/2N5/PPPP1PPP/R1BQKBNR");
/*
    Board searchBoard = new Board("r2q2k1/pppnb1p1/3pb2p/8/3QP3/2N5/PPP2PPP/3R1RK1");
    searchBoard.turn = 1;
    searchBoard.stats.blackCastleFlag = Board.Stats.O_O;
    searchBoard.stats.whiteCastleFlag = Board.Stats.O_O;
*/
//    Board searchBoard = new Board("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR");
//    Board searchBoard = new Board("r3r2k/ppBnNpbp/5np1/8/3NP3/8/PPP2PPP/3RR1K1");
//    Board searchBoard = new Board("r1bq1rk1/pppnppbp/3p1np1/8/2BPPB2/2N2N2/PPP2PPP/R2QK2R");
//    Board searchBoard = new Board("r3kb1r/pp1bqppp/5n2/4Q3/2B1P3/2N5/PPP2PPP/R3K2R");
//    Board searchBoard = new Board("r1b2rk1/ppp1qppp/1bnp1n2/4p1N1/1PB1P3/1QPP4/P4PPP/RNB1K2R");
//    Board searchBoard = new Board("3r2k1/5pp1/5n1p/p1q5/P4B2/2r5/4QPPP/1R2KR2");
/*
    Board searchBoard = new Board("1r1r2k1/p1p1qpp1/3bbn1p/4P3/3p1P1B/p2B4/2PQN1PP/2KR3R");
    searchBoard.stats.whiteCastleFlag = 2;
    searchBoard.stats.blackCastleFlag = 1;
*/
/*
    Board searchBoard = new Board("r1bq1rk1/ppp2p1p/3p3p/4p3/2BbP3/2P2Q2/PP3PPP/RN2R1K1");
    searchBoard.turn = 0;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;

*/
/*
    Board searchBoard = new Board("4r1k1/p1p2ppp/8/1P6/8/2N1bq1P/PP5K/R3R3");
    searchBoard.turn = 0;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
*/
    // can't see mate?
/*
    Board searchBoard = new Board("4rrk1/3b1p1p/2nqp3/p2pN1Pp/1ppP1P1Q/2P2P2/PP1NBK2/7R");
    searchBoard.turn = 0;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 2;
*/
/*
    Board searchBoard = new Board("8/p5kp/8/2p2K2/P7/8/1P3P2/8");
    searchBoard.turn = 0;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
*/
/*
    Board searchBoard = new Board("r7/4k3/5p2/5Pp1/2Kp2P1/P2R4/8/8");
    searchBoard.turn = 1;
    searchBoard.stats.whiteCastleFlag = 2;
    searchBoard.stats.blackCastleFlag = 1;
*/

/*
    Board searchBoard = new Board("8/8/2B3N1/5p2/6p1/6pk/4K2b/7r");
*/

/*
    Board searchBoard = new Board("r1b2rk1/p4p1p/1p2p1p1/4P3/1n6/P1N2N2/1P3PPP/3R1RK1");
    searchBoard.turn = 0;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 2;

*/
    // Kh1 ?? 
/*
    Board searchBoard = new Board("r1bq1r1k/ppp1bppp/2n1pn2/3p4/3P4/2NBPN2/PPPB1PPP/R2Q1RK1");
    searchBoard.turn = 1;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.whiteKingMoves = 1;
    searchBoard.stats.whiteKingsideRookMoves = 1;
    searchBoard.stats.blackKingMoves = 1;
    searchBoard.stats.blackKingsideRookMoves = 1;
    searchBoard.stats.blackCastleFlag = 1;
*/
    // No Trade/
/*
    Board searchBoard = new Board("8/8/8/kqQ1B3/8/6P1/5NK1/8");
    searchBoard.turn = 1;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
*/

    // stonewall - no progress
/*
    Board searchBoard = new Board("2b1k3/2p1n3/p1Pp1p1p/P2BpP1P/1P2K1P1/8/1R6/8");
    searchBoard.turn = 1;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
*/

    // Mate in 7
/*
    Board searchBoard = new Board("5Q2/p2k2r1/3P4/p1p1p3/2q5/8/5P2/4K3");
    searchBoard.turn = 0;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 2;
*/

    // sac rook?
/*
    Board searchBoard = new Board("5r2/3k2p1/ppp5/3p1rbp/1P1Pp3/PN2P2P/2R1RPP1/6K1");
    searchBoard.turn = 0;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 2;
*/

/*
    Board searchBoard = new Board("1k1r1b1r/1b2p1pp/p4n2/2qpNP2/2pR4/2N4P/1PQ1BPP1/3R2K1");
    searchBoard.turn = 0;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 2;
*/
/*
    Board searchBoard = new Board("8/1r2p1k1/1p1p1p2/pBbPn1p1/4P3/5PKP/2PB2P1/7R");
    searchBoard.turn = 1;
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
*/

/*
    Board searchBoard = new Board("3r1rk1/1pp2ppp/2n2n2/p1b1qN2/3pPN2/P2P3P/1PP3P1/R1BQ1R1K");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rnb1kbnr/pppqpppp/8/8/3P4/2N5/PPP2PPP/R1BQKBNR");
    searchBoard.turn = 0;
*/

/*
    Board searchBoard = new Board("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R");
    searchBoard.turn = 0;
*/

/*
    Board searchBoard = new Board("8/pkpR3p/2p3p1/1br5/P1p5/2P5/5PPP/1R4K1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 0;
    searchBoard.stats.blackKingMoves++;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("r1bq1rk1/pp3ppp/2n1pn2/8/1b1P4/2NB1N2/PP3PPP/R1BQ1RK1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 0;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("8/6K1/8/8/1k4p1/pp5p/6r1/4R3");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/

/*
    Board searchBoard = new Board("r1bqk2r/ppp2ppp/2n5/3p4/1b1Pn3/2PB1N2/PP3PPP/RNBQK2R");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rnbqk1nr/ppppbppp/4p3/8/2PPP3/8/PP3PPP/RNBQKBNR");
    searchBoard.turn = 0;
*/

//    Board searchBoard = new Board("r1bqk2r/4nnpp/1ppp1p2/8/2BBP3/P4N2/2P1QPPP/R3K2R");
//    Board searchBoard = new Board("8/5p1p/2k2np1/8/1P6/8/5P1P/5BK1");
//    Board searchBoard = new Board("rnbqkbnr/8/pppppppp/3P4/3NP3/2NBB3/PPP2PPP/R2QK2R");
    

/*
    Board searchBoard = new Board("1r3rk1/p1p2ppp/b1p1pn2/2q5/8/1N2PN2/P4PPP/R1BQR1K1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/

/*
    Board searchBoard = new Board("r4rk1/p1p1nppp/2n5/1pb1p3/q3P3/P2P1QNP/BP3PP1/R1B2RK1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("8/2p5/8/p3K3/1pQ5/1Pk3P1/8/8");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/

/*
    Board searchBoard = new Board("rn2k2r/pp2bppp/8/3P4/2P5/8/PP3PPP/RN2K2R");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("r3k2r/pppbnppp/1n6/1P1P4/2PNp3/1P6/3PBPPP/RNB1K2R");
    searchBoard.turn = 0;
*/
    // Nh6??
/*
    Board searchBoard = new Board("rnbqkbnr/ppp1pppp/8/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rn3bnr/p1p1k1pp/bp2pp2/8/3P4/1NN1B3/P1P2PPP/1R1Q2K1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackKingMoves++;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rn2kbnr/p1p3pp/1p2pp2/8/2bP4/2N1BN2/P1P2PPP/1R1Q2K1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.turn = 0;
*/
//    Board searchBoard = new Board("4r3/1pp2k2/6p1/pP1bPp2/P7/5N1P/7K/2R5");
//    Board searchBoard = new Board("3r4/r4pp1/pp2pk1p/2p5/2PPR3/7R/P4PPP/6K1");
/*
    Board searchBoard = new Board("6k1/p1N2ppp/8/3Kp3/4P2P/1P2r3/2P4R/8");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.turn = 0;
*/

/*
    Board searchBoard = new Board("r2qk2r/pppbbppp/2n1pn2/3p4/3P1P2/2PBPN2/PP1N2PP/R1BQK2R");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("8/3k2pp/5p2/R7/p7/3N3P/2p2PP1/6K1");
    searchBoard.turn = 0;
*/

/*
    Board searchBoard = new Board("2r5/1R2bkpp/2n5/2P1p3/pBP1Pp2/P4P2/5PKP/8");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("1q2k1r1/r7/2P2ppp/1P2p2n/p1Qp4/R5P1/4PP1P/R5K1");
    searchBoard.stats.whiteCastleFlag = 0;
    searchBoard.stats.blackKingMoves++;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("N2k1b1r/p5pp/b1n5/2p5/2p1n3/6P1/PBK2P1P/3R1B1R");
    searchBoard.stats.whiteKingMoves++;
    searchBoard.stats.blackKingMoves++;
    searchBoard.turn = 0;

*/
//    Board searchBoard = new Board("8/2rk2p1/4p2p/p1P1P3/2bBP3/r3K3/P1R3PP/2R5");
//    Board searchBoard = new Board("r2k3r/1p1q2pp/p4p2/3p1nB1/2nQ4/1P3N2/P1P2PPP/R3R1K1");

//    Board searchBoard = new Board("6r1/p1p2p1k/2p4p/5N2/8/6R1/n3rPPP/5RK1");

/*
    Board searchBoard = new Board("r1bqk2r/pppp1ppp/2n5/3Pp3/1b6/6P1/PP1PPPBP/R1BQK1NR");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 0;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rnbqkb1r/ppp1pppp/5n2/3p4/2PP4/5N2/PP2PPPP/RNBQKB1R");
    searchBoard.stats.whiteCastleFlag = 0;
    searchBoard.stats.blackCastleFlag = 0;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("2k5/R2b1pp1/Rn1r3p/1Pp5/2p2N1P/2N3P1/1P3nBK/4r3");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 2;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rB5r/P2Q1nk1/1p3pp1/3p4/3P3p/8/5PP1/5RK1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 0;
    searchBoard.stats.blackKingMoves++;
    searchBoard.turn = 0;
*/
/*

    Board searchBoard = new Board("rnb1kbnr/ppq2ppp/2pp4/8/4P3/2N2N2/PPP1BPPP/R1BQK2R");
    searchBoard.turn = 1;
*/
    // Mate in 7

/*
    Board searchBoard = new Board("1k2q2r/pp4pp/2br1p2/2p4P/P1P5/2B5/1Q3Pp1/1R4K1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/

    // Mate in 8
//    Board searchBoard = new Board("r1bqk2r/p1pp1ppp/1p6/3Np3/2B1P1n1/5Q2/PPP3PP/R1B1b2K");

    // Dead black king
/*
    Board searchBoard = new Board("r1bq1rk1/pp3p2/2n1pn1Q/2b5/3p4/3B1N2/PPP1N1PP/R4RK1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/

    // black king 2
/*
    Board searchBoard = new Board("2r1r3/1pbb1pkp/p1p5/2P5/1PnNp3/7P/PBB5/3R1RK1");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 1;
*/

/*
    Board searchBoard = new Board("3r1rk1/1pp2p1p/p3q1p1/8/4N3/P1PB4/1PK2QP1/7R");
    searchBoard.stats.whiteCastleFlag = 2;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("3r2k1/3b3p/5b1q/Q2Pp3/P1P1P1P1/5p1r/1P2BR1B/2R4K");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 1;
*/
/*
    Board searchBoard = new Board("r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/2N2N2/PPPP1PPP/R1BQK2R");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("8/1p3p2/p4k2/P2b3p/KPp1p1p1/2P1P1P1/4B2P/8");
    searchBoard.stats.whiteCastleFlag = 0;
    searchBoard.stats.blackCastleFlag = 0;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rnb1kbnr/ppp1pppp/8/4q3/8/2N5/PPPPBPPP/R1BQK1NR");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("1kr3nr/pp1b2pp/1b2p3/1P1pP3/8/P2B1N2/3Q1PPP/4K2R");
    searchBoard.turn = 0;
    searchBoard.stats.whiteCastleFlag = 0;
    searchBoard.stats.blackCastleFlag = 2;
*/
/*
    Board searchBoard = new Board("rnb2rk1/ppp1b1pp/8/4N3/8/3Q4/PPP3PP/RNB1K2n");
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/

/*
    new Piece(0,searchBoard, 1, Piece.PAWN, Square.A2);
    new Piece(1,searchBoard, 1, Piece.PAWN, Square.B2);
    new Piece(2,searchBoard, 1, Piece.PAWN, Square.C2);
    new Piece(3,searchBoard, 1, Piece.PAWN, Square.D2);
    new Piece(4,searchBoard, 1, Piece.PAWN, Square.E4);
    new Piece(5,searchBoard, 1, Piece.PAWN, Square.F2);
    new Piece(6,searchBoard, 1, Piece.PAWN, Square.G2);
    new Piece(7,searchBoard, 1, Piece.PAWN, Square.H2);
    new Piece(8,searchBoard, 1, Piece.KNIGHT, Square.B1);
    new Piece(9,searchBoard, 1, Piece.KNIGHT, Square.F3);
    new Piece(10,searchBoard, 1, Piece.BISHOP, Square.C1);
    new Piece(11,searchBoard, 1, Piece.BISHOP, Square.B5);
    new Piece(12,searchBoard, 1, Piece.ROOK, Square.A1);
    new Piece(13,searchBoard, 1, Piece.ROOK, Square.H1);
    new Piece(14,searchBoard, 1, Piece.QUEEN, Square.D1);
    new Piece(15,searchBoard, 1, Piece.KING, Square.E1);

    new Piece(16,searchBoard, 0, Piece.PAWN, Square.A7);
    new Piece(17,searchBoard, 0, Piece.PAWN, Square.B7);
    new Piece(18,searchBoard, 0, Piece.PAWN, Square.C7);
    new Piece(19,searchBoard, 0, Piece.PAWN, Square.D7);
    new Piece(20,searchBoard, 0, Piece.PAWN, Square.E5);
    new Piece(21,searchBoard, 0, Piece.PAWN, Square.F7);
    new Piece(22,searchBoard, 0, Piece.PAWN, Square.H7);
    new Piece(23,searchBoard, 0, Piece.PAWN, Square.G7);
    new Piece(24,searchBoard, 0, Piece.KNIGHT, Square.C6);
    new Piece(25,searchBoard, 0, Piece.KNIGHT, Square.G8);
    new Piece(26,searchBoard, 0, Piece.BISHOP, Square.C8);
    new Piece(27,searchBoard, 0, Piece.BISHOP, Square.E7);
    new Piece(28,searchBoard, 0, Piece.ROOK, Square.A8);
    new Piece(29,searchBoard, 0, Piece.ROOK, Square.H8);
    new Piece(30,searchBoard, 0, Piece.QUEEN, Square.D8);
    new Piece(31,searchBoard, 0, Piece.KING, Square.E8);
*/
/*
    Board searchBoard = new Board("r6k/ppp4p/4B3/6p1/4K3/8/PP5P/4R3");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/

//    Board searchBoard = new Board("r1bqk2r/pp2npbp/2npp1p1/2p5/2B1P3/2NPBN2/PPP2PPP/R2Q1RK1");

    // Mate in 9
/*
    Board searchBoard = new Board("4r1k1/pp1qrp2/2p2b1p/2Pp4/1P1N1P2/6Pb/P4Q1P/1BR1R1K1 ");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("rn1qk1nr/pppb1ppp/8/3p4/1b1P4/2N2N2/PPP1BPPP/R1BQK2R ");
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("r3kbr1/3p1p1p/p7/1b1Pq3/1p6/1B3Q2/PPP2PPP/R4K1R");
    searchBoard.stats.whiteKingMoves = 1;
    searchBoard.turn = 1;
*/

/*
    Board searchBoard = new Board("1k6/pp5p/2n3b1/8/P3p3/2K1B3/1PP1BP1P/8");
    searchBoard.stats.whiteCastleFlag = 2;
    searchBoard.stats.blackCastleFlag = 2;
    searchBoard.turn = 0;
*/
/*
    Board searchBoard = new Board("3r2k1/2p1bppp/pp2p3/3qPb2/2N5/1P2Q2P/PB1p2PK/3R4");
    searchBoard.stats.whiteCastleFlag = 1;
    searchBoard.stats.blackCastleFlag = 1;
    searchBoard.turn = 0;
*/
//    Board searchBoard = new Board("r1bqkb1r/pp1n1ppp/2n1p3/1BppP3/3P4/2N2N2/PPP2PPP/R1BQK2R");
//    Board searchBoard = new Board("8/8/8/8/8/7K/5Q2/7k");
/*
    new Piece(0,searchBoard, 1, Piece.PAWN, Square.A3);
    new Piece(1,searchBoard, 1, Piece.PAWN, Square.B4);
    new Piece(2,searchBoard, 1, Piece.PAWN, Square.C4);
    new Piece(3,searchBoard, 1, Piece.KING, Square.H1);
    new Piece(4,searchBoard, 0, Piece.PAWN, Square.A7);
    new Piece(5,searchBoard, 0, Piece.PAWN, Square.B6);
    new Piece(6,searchBoard, 0, Piece.PAWN, Square.C6);
    new Piece(7,searchBoard, 0, Piece.KING, Square.H8);

    new Piece(0,searchBoard, 1, Piece.PAWN, Square.D5);
    new Piece(1,searchBoard, 1, Piece.PAWN, Square.E4);
    new Piece(2,searchBoard, 1, Piece.PAWN, Square.G3);
    new Piece(3,searchBoard, 1, Piece.KNIGHT, Square.C3);
    new Piece(4,searchBoard, 1, Piece.KNIGHT, Square.E6);
    new Piece(5,searchBoard, 1, Piece.KING, Square.E8);

    new Piece(6,searchBoard, 0, Piece.KING, Square.D6);
*/

    System.out.println(new Date());

    MoveGeneration moveGen = new MoveGeneration();
    BoardEvaluator eval = new SimpleEvaluator(moveGen);

    Move[] moves = Move.createMoves(100);
    moveGen.generateFullMoves(moves, searchBoard);

    System.err.println("Moves: " + Move.toString(moves));
    System.err.println(searchBoard);

    ABSearch search = new ABSearch(moveGen, eval);
    IterativeSearch iterativeSearch = new IterativeSearch(search, moveGen, eval);

    int score = iterativeSearch.search(searchBoard,50);

    System.err.println(searchBoard);
    System.err.println("Score: " + score);
    System.err.println("SearchStats: " + search.stats);
    System.err.println("Best Line: " + Move.toString(search.getPV()));
  }

  public static void main(String[] args)
  {
    new IterativeSearchTest().testSimpleABSearch();
  }
}