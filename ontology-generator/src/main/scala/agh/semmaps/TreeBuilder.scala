package agh.semmaps

// Topological-like sort, as suggested by Ziyao Wei
// in http://stackoverflow.com/questions/28240029/building-a-tree-from-a-list-and-isdescendant-and-isascendant-functions/

object TreeBuilder {

  def apply(objects: Set[JmlObject]): List[JmlTree] = {
    val ancestorless = findAncestorless(objects)
    println(ancestorless)

    ???
  }

  def findAncestorless(in: Set[JmlObject]): Set[JmlObject] =
    in filter (obj ⇒ !(in exists (_ isAncestor obj)))

}
