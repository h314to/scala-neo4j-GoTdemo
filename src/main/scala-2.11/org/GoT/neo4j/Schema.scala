package org.GoT.neo4j

/**
  * Created by agapito on 27/01/2017.
  */
object Schema {
  val constraint = List(
    "CREATE INDEX ON :Character(name)",
    "CREATE CONSTRAINT ON (c:Character) ASSERT c.id IS UNIQUE",
    "CREATE CONSTRAINT ON (h:House) ASSERT h.name IS UNIQUE",
    "CREATE CONSTRAINT ON (b:Book) ASSERT b.title IS UNIQUE")

  val dropAndDelete = List(
    "MATCH (n) DETACH DELETE n",
    "DROP INDEX ON :Character(name)",
    "DROP CONSTRAINT ON (c:Character) ASSERT c.id IS UNIQUE",
    "DROP CONSTRAINT ON (h:House) ASSERT h.name IS UNIQUE",
    "DROP CONSTRAINT ON (b:Book) ASSERT b.title IS UNIQUE")
}
