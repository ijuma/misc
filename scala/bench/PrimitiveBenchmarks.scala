package scala {
  trait IntFunction[+R] extends Function[Int, R] {
    def apply(i: Int): R
  }
}

package bench {

import gnu.trove._

class PrimitivesBenchmark {
  implicit def toIntProcedure(f: Function[Int, Unit]): TIntProcedure = new TIntProcedure() {
    def execute(i: Int) = {
      /* Only works with patched scalac */
      f.asInstanceOf[IntFunction[_]](i)
      true
    }
  }
  
  implicit def toIntProcedureWithBoxing(f: Function[Int, Unit]): TIntProcedure = new TIntProcedure() {
    def execute(i: Int) = {
      f(i)
      true
    }
  }

  private def sumWithFunctionAndBoxing(list: TIntArrayList): Int = {
    var sum = 0
    /* Do not rely on implicit because of ambiguity */
    list.forEach(toIntProcedureWithBoxing(sum += _))
    sum
  }
  
  private def sumWithFunction(list: TIntArrayList): Int = {
    var sum = 0
    /* Do not rely on implicit because of ambiguity */
    list.forEach(toIntProcedure(sum += _))
    sum
  }
  
  private def sumWithInnerClass(list: TIntArrayList): Int = {
    var sum = 0
    val p = new TIntProcedure() {
      def execute(i: Int) = {
        sum += i
        true
      }
    }
    list.forEach(p)
    sum
  }
  
  def benchWithFunction() {
    bench("Sum with function (no boxing)", sumWithFunction)
  }
  
  def benchWithFunctionAndBoxing() {
    bench("Sum with function (boxing)", sumWithFunctionAndBoxing)
  }
  
  def benchSumWithInnerClass() {
    bench("Sum with inner class", sumWithInnerClass)
  }
  
  def bench(name: String, f: TIntArrayList => Int) {
    val listSize = 1000 * 1000 * 100
    val list = new TIntArrayList(listSize)
    for (i <- 0 to listSize)
      list.add(i)
    
    val times = new TLongArrayList()
    val results = new TIntArrayList()
    for (i <- 0 to 10) {
      val (time, result) = innerBench(list, f)
      results.add(result)
      times.add(time)
    }
    println("Benchmarking: " + name)
    println("Results: " + results)
    println("Times: " + times)
  }
  
  private def innerBench(list: TIntArrayList, f: TIntArrayList => Int): (Long, Int) = {
    System.gc()
    val start = System.currentTimeMillis
    val result = f(list)
    val end = System.currentTimeMillis
    System.gc()
    (end - start) -> result
  }
}

object PrimitivesBenchmarks {
  def main(a: Array[String]) {
    val pt = new PrimitivesBenchmark()
    /* 
     * Uncomment the benchmark that should be run. Ideally, only one should run at a time to
     * avoid interference.
     */
    //pt.benchSumWithInnerClass()
    //pt.benchWithFunction
    pt.benchWithFunctionAndBoxing
  }
}
}