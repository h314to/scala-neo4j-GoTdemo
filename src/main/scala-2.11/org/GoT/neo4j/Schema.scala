package org.GoT.neo4j

/**
  * Created by agapito on 27/01/2017.
  */
object Schema {
  val constraint = List(
    "CREATE CONSTRAINT ON (c:Character) ASSERT c.name IS UNIQUE",
    "CREATE CONSTRAINT ON (h:House) ASSERT h.name IS UNIQUE",
    "CREATE CONSTRAINT ON (b:Book) ASSERT b.title IS UNIQUE")
}
