package chess;

/**
 * Created by IntelliJ IDEA.
 * User: jlevine
 * Date: 1/10/13
 * Time: 8:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Quadratics {
  private static final int Q_SIZE = 3;
  private final double[] ms;
  private final boolean[] bs;

  int doubles;
  int booleans;

  public Quadratics(int size) {
    ms = new double[size];
    bs = new boolean[size];
  }

  public void setQuadric(double m00, double m01, double m02, boolean b) {
    ms[++doubles] = m00;
    ms[++doubles] = m01;
    ms[++doubles] = m02;
    bs[++booleans] = b;
  }

  public int getSize() {
    return booleans;
  }

  public double getM00(int index) {
    return ms[index * Q_SIZE];
  }

  public double getM01(int index) {
    return ms[(index * Q_SIZE) + 1];
  }

  public double getM02(int index) {
    return ms[(index * Q_SIZE) + 2];
  }

  public boolean getBoolean(int index) {
    return bs[index];
  }
}

/*
Example usage:

   Quadratics q = new Quadratics(2);

   q.setQuadric(1,2,3, false);
   q.setQuadric(1,2,3, true);

   Generates 3 objects and has smaller stack frames and simpler usage

   double m00 = q.getM00(1);
   double m01 = q.getM01(1);
   double m02 = q.getM02(1);
   boolean b = q.getBoolean(1);

*/
