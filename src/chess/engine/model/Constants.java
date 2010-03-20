/* $Id$ */

package chess.engine.model;

/**
 * @author Joshua Levine <jlevine@theladders.com>
 * @version $Revision$ $Name$ $Date$
 */
public class Constants
{
  public static int[] TO_64 =
          {
                   0,  1,  2,  3,  4,  5,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1,
                   8,  9, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1,
                  16, 17, 18, 19, 20, 21, 22, 23, -1, -1, -1, -1, -1, -1, -1, -1,
                  24, 25, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1,
                  32, 33, 34, 35, 36, 37, 38, 39, -1, -1, -1, -1, -1, -1, -1, -1,
                  40, 41, 42, 43, 44, 45, 46, 47, -1, -1, -1, -1, -1, -1, -1, -1,
                  48, 49, 50, 51, 52, 53, 54, 55, -1, -1, -1, -1, -1, -1, -1, -1,
                  56, 57, 58, 59, 60, 61, 62, 63, -1, -1, -1, -1, -1, -1, -1, -1,
          };

  public static String[] FILE_STRINGS = { "a", "b", "c", "d", "e", "f", "g", "h" };

  public static long ONE = 1L;
  public static long ALL;

  static
  {
    long all = 0;

    for(int t = 0;t < 64;t++)
    {
      all |= ONE << t;
    }

    ALL = all;
  }

  public static int RANK_1 = 0;
  public static int RANK_2 = 1;
  public static int RANK_3 = 2;
  public static int RANK_4 = 3;
  public static int RANK_5 = 4;
  public static int RANK_6 = 5;
  public static int RANK_7 = 6;
  public static int RANK_8 = 7;

  public static int FILE_A = 0;
  public static int FILE_B = 1;
  public static int FILE_C = 2;
  public static int FILE_D = 3;
  public static int FILE_E = 4;
  public static int FILE_F = 5;
  public static int FILE_G = 6;
  public static int FILE_H = 7;

  public static long FILE_A_MASK =  Square.A1.mask_on |
                                          Square.A2.mask_on |
                                          Square.A3.mask_on |
                                          Square.A4.mask_on |
                                          Square.A5.mask_on |
                                          Square.A6.mask_on |
                                          Square.A7.mask_on |
                                          Square.A8.mask_on;

  public static long FILE_B_MASK =  Square.B1.mask_on |
                                          Square.B2.mask_on |
                                          Square.B3.mask_on |
                                          Square.B4.mask_on |
                                          Square.B5.mask_on |
                                          Square.B6.mask_on |
                                          Square.B7.mask_on |
                                          Square.B8.mask_on;

  public static long FILE_C_MASK =  Square.C1.mask_on |
                                          Square.C2.mask_on |
                                          Square.C3.mask_on |
                                          Square.C4.mask_on |
                                          Square.C5.mask_on |
                                          Square.C6.mask_on |
                                          Square.C7.mask_on |
                                          Square.C8.mask_on;

  public static long FILE_D_MASK =  Square.D1.mask_on |
                                          Square.D2.mask_on |
                                          Square.D3.mask_on |
                                          Square.D4.mask_on |
                                          Square.D5.mask_on |
                                          Square.D6.mask_on |
                                          Square.D7.mask_on |
                                          Square.D8.mask_on;

  public static long FILE_E_MASK =  Square.E1.mask_on |
                                          Square.E2.mask_on |
                                          Square.E3.mask_on |
                                          Square.E4.mask_on |
                                          Square.E5.mask_on |
                                          Square.E6.mask_on |
                                          Square.E7.mask_on |
                                          Square.E8.mask_on;

  public static long FILE_F_MASK =  Square.F1.mask_on |
                                          Square.F2.mask_on |
                                          Square.F3.mask_on |
                                          Square.F4.mask_on |
                                          Square.F5.mask_on |
                                          Square.F6.mask_on |
                                          Square.F7.mask_on |
                                          Square.F8.mask_on;

  public static long FILE_G_MASK =  Square.G1.mask_on |
                                          Square.G2.mask_on |
                                          Square.G3.mask_on |
                                          Square.G4.mask_on |
                                          Square.G5.mask_on |
                                          Square.G6.mask_on |
                                          Square.G7.mask_on |
                                          Square.G8.mask_on;

  public static long FILE_H_MASK =  Square.H1.mask_on |
                                          Square.H2.mask_on |
                                          Square.H3.mask_on |
                                          Square.H4.mask_on |
                                          Square.H5.mask_on |
                                          Square.H6.mask_on |
                                          Square.H7.mask_on |
                                          Square.H8.mask_on;


  public static long RANK_8_MASK =  Square.A8.mask_on |
                                          Square.B8.mask_on |
                                          Square.C8.mask_on |
                                          Square.D8.mask_on |
                                          Square.E8.mask_on |
                                          Square.F8.mask_on |
                                          Square.G8.mask_on |
                                          Square.H8.mask_on;

  public static long RANK_7_MASK =  Square.A7.mask_on |
                                          Square.B7.mask_on |
                                          Square.C7.mask_on |
                                          Square.D7.mask_on |
                                          Square.E7.mask_on |
                                          Square.F7.mask_on |
                                          Square.G7.mask_on |
                                          Square.H7.mask_on;

  public static long RANK_6_MASK =  Square.A6.mask_on |
                                          Square.B6.mask_on |
                                          Square.C6.mask_on |
                                          Square.D6.mask_on |
                                          Square.E6.mask_on |
                                          Square.F6.mask_on |
                                          Square.G6.mask_on |
                                          Square.H6.mask_on;

  public static long RANK_5_MASK =  Square.A5.mask_on |
                                          Square.B5.mask_on |
                                          Square.C5.mask_on |
                                          Square.D5.mask_on |
                                          Square.E5.mask_on |
                                          Square.F5.mask_on |
                                          Square.G5.mask_on |
                                          Square.H5.mask_on;

  public static long RANK_4_MASK =  Square.A4.mask_on |
                                          Square.B4.mask_on |
                                          Square.C4.mask_on |
                                          Square.D4.mask_on |
                                          Square.E4.mask_on |
                                          Square.F4.mask_on |
                                          Square.G4.mask_on |
                                          Square.H4.mask_on;

  public static long RANK_3_MASK =  Square.A3.mask_on |
                                          Square.B3.mask_on |
                                          Square.C3.mask_on |
                                          Square.D3.mask_on |
                                          Square.E3.mask_on |
                                          Square.F3.mask_on |
                                          Square.G3.mask_on |
                                          Square.H3.mask_on;

  public static long RANK_2_MASK =  Square.A2.mask_on |
                                          Square.B2.mask_on |
                                          Square.C2.mask_on |
                                          Square.D2.mask_on |
                                          Square.E2.mask_on |
                                          Square.F2.mask_on |
                                          Square.G2.mask_on |
                                          Square.H2.mask_on;

  public static long RANK_1_MASK =  Square.A1.mask_on |
                                          Square.B1.mask_on |
                                          Square.C1.mask_on |
                                          Square.D1.mask_on |
                                          Square.E1.mask_on |
                                          Square.F1.mask_on |
                                          Square.G1.mask_on |
                                          Square.H1.mask_on;

  public static long[] FILE_MASKS = { FILE_A_MASK , FILE_B_MASK, FILE_C_MASK, FILE_D_MASK, FILE_E_MASK, FILE_F_MASK, FILE_G_MASK, FILE_E_MASK };

}
