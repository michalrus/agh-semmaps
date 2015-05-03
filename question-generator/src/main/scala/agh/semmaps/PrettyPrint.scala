package agh.semmaps

/**
 * Created by m on 5/3/15.
 */
object PrettyPrint {

  def apply(trees: List[JmlTree]): Unit = {
    def prtr(indent: Int)(tree: JmlTree): Unit = {
      val ind = " " * (indent * 2)
      println(s"$ind${tree.node}")
      tree.children foreach prtr(indent + 1)
    }
    trees foreach { tree ⇒
      println("------------------------------------------------------")
      prtr(0)(tree)
    }
    println("------------------------------------------------------")
  }

}
