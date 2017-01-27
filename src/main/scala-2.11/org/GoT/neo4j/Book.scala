package org.GoT.neo4j

import scala.collection.immutable.Iterable

/**
  * Created by agapito on 27/01/2017.
  */
object Book {

  val title = Map(
    1 -> "A Game of Thrones",
    2 -> "A Clash of Kings",
    3 -> "A Storm of Swords",
    4 -> "A Feast for Crows",
    5 -> "A Dance with Dragons")

  val cqlBooks: Iterable[String] = title.map{ case (n, name) => s"MERGE (:Book {title:'$name'})" }
}
