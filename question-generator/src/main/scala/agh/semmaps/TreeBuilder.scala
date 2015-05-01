package agh.semmaps

// Topological-like sort, as suggested by Ziyao Wei
// in http://stackoverflow.com/questions/28240029/building-a-tree-from-a-list-and-isdescendant-and-isascendant-functions/

object TreeBuilder {

  def apply(objects: Set[JmlObject]): List[JmlTree] = {
    final case class MutableJmlTree(node: JmlObject, children: collection.mutable.HashSet[MutableJmlTree]) {
      def toImmutable: JmlTree = JmlTree(node, children.toSet.map((child: MutableJmlTree) ⇒ child.toImmutable))
    }

    var trees = List.empty[MutableJmlTree]
    var lastLayer = List.empty[MutableJmlTree]
    var left = objects

    while (left.nonEmpty) {
      val ancestorless = findAncestorless(left)

      if (lastLayer.isEmpty) {
        trees = ancestorless.toList map (x ⇒ MutableJmlTree(x, collection.mutable.HashSet.empty))
        lastLayer = trees
      }
      else {
        var nextLayer = List.empty[MutableJmlTree]
        ancestorless foreach { obj ⇒
          lastLayer foreach { subtree ⇒
            if (subtree.node isAncestor obj) {
              val newOne = MutableJmlTree(obj, collection.mutable.HashSet.empty)
              nextLayer ::= newOne
              subtree.children += newOne
            }
          }
        }
        lastLayer = nextLayer
      }

      left --= ancestorless
    }

    trees map (_.toImmutable)
  }

  def findAncestorless(in: Set[JmlObject]): Set[JmlObject] =
    in filter (obj ⇒ !(in exists (_ isAncestor obj)))

  def prettyPrint(trees: List[JmlTree]): Unit = {
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
