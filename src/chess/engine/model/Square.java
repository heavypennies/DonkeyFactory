/* $Id$ */

package chess.engine.model;

import chess.engine.utils.MoveGeneration;

/**
 * @author Joshua Levine <levinester@gmail.com>
 * @version $Revision$ $Name$ $Date$
 */
public enum Square
{
  A1(0),   B1(1),   C1(2),   D1(3),   E1(4),   F1(5),   G1(6),   H1(7),
  A2(16),  B2(17),  C2(18),  D2(19),  E2(20),  F2(21),  G2(22),  H2(23),
  A3(32),  B3(33),  C3(34),  D3(35),  E3(36),  F3(37),  G3(38),  H3(39),
  A4(48),  B4(49),  C4(50),  D4(51),  E4(52),  F4(53),  G4(54),  H4(55),
  A5(64),  B5(65),  C5(66),  D5(67),  E5(68),  F5(69),  G5(70),  H5(71),
  A6(80),  B6(81),  C6(82),  D6(83),  E6(84),  F6(85),  G6(86),  H6(87),
  A7(96),  B7(97),  C7(98),  D7(99),  E7(100), F7(101), G7(102), H7(103),
  A8(112), B8(113), C8(114), D8(115), E8(116), F8(117), G8(118), H8(119);

  public int index128;
  public int index64;
  public int rank;
  public int file;
  public int color;
  public long mask_on;
  public long mask_on_rl90;
  public long mask_on_rl45;
  public long mask_on_rr45;
  public long mask_off;
  public long mask_off_rl90;
  public long mask_off_rl45;
  public long mask_off_rr45;

  public long[] kingArea = new long[2];

  Square(int index)
  {
    this.index128 = index;

    rank = index / 16;
    file = index % 16;
    if (index % 2 == 1)
    {
      color = 1;
    }
    else
    {
      color = 0;
    }

    index64 = (rank * 8) + file;
    mask_on = 1L << index64;
    mask_on_rl90 = (1L << 63) >>> MoveGeneration.init_l90[index64];
    mask_on_rl45 = (1L << 63) >>> MoveGeneration.init_l45[index64];
    mask_on_rr45 = (1L << 63) >>> MoveGeneration.init_r45[index64];

    mask_off = ~mask_on;
    mask_off_rl90 = ~mask_on_rl90;
    mask_off_rl45 = ~mask_on_rl45;
    mask_off_rr45 = ~mask_on_rr45;
    
    assert Long.numberOfTrailingZeros(mask_on) == index64;
    assert 64 - Long.numberOfTrailingZeros(mask_on) == index64;

    assert (~mask_off) == mask_on;
  }
}
