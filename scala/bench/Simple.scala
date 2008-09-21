package scala.bench

/* 
// A very simple demo class, to produce an easy-to-profile CPU-bound
// program.  I'd like to see this program ported to as many different
// JVM-based languages as possible, such as Scala, Clojure, JRuby, etc.
// See "A Plea For Programs", 
// http://blogs.azulsystems.com/cliff/2008/09/a-plea-for-prog.html
public class simple {

  public static void main( String []args ) {
    int sum=0;
    for( int i=0; i<100; i++ )
      sum += test(sum);
    System.out.println(sum);
  }
  public static long test(int sum) {
    for( int i=1; i<10000000; i++ )
      sum += (sum^i)/i;
    return sum;
  }

}
*/

/* To run:
 * - scalac Simple.scala
 * - scala scala.bench.FastSimple
 * - scala scala.bench.SlowSimple
 */

/*
 * A "fast" translation to Scala. It uses while loops and the bytecode should be similar
 * to the Java version.
 */
object FastSimple {

  def main(args: Array[String]) {
    var sum = 0
    var i = 0
    while (i < 100) {
      sum += test(sum)
      i += 1
    }
    println(sum)
  }
  
  def test(sum: Int) = {
    var result = sum
    var i = 1
    while (i < 10000000) {
      result += (result ^ i) / i
      i += 1
    }
    result
  }
}

/*
 * This translation uses a combination of Range and fold to simplify the code.
 * Unfortunately this version causes a lot of boxing and the performance is much worse as a
 * result.
 */
object SlowSimple {
  def main(args: Array[String]) {
    val sum = (0 until 100).foldLeft(0)((sum, i) => sum + test(sum))
    println(sum)
  }
  
  def test(sum: Int) = (1 until 10000000).foldLeft(sum)((acc, i) => acc + (acc ^ i) / i)
}