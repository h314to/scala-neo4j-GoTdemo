package org.GoT.neo4j

import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}

/**
  * Created by agapito on 25/01/2017.
  */
object Main extends App {

  // Connect to database
  val driver = GraphDatabase.driver("bolt://localhost/7687", AuthTokens.basic("neo4j", "admin"))
  val session = driver.session

  // Book titles
  val bookTitle = Map(
    1 -> "A Game of Thrones",
    2 -> "A Clash of Kings",
    3 -> "A Storm of Swords",
    4 -> "A Feast for Crows",
    5 -> "A Dance with Dragons")

  val booksCypher = bookTitle.map{ case (n,title) => s"CREATE (:Book {title:'$title'})" }.foreach(session.run)

  // Constraints for names
  val constraintsCypher = List(
    "CREATE CONSTRAINT ON (c:Character) ASSERT c.name IS UNIQUE",
    "CREATE CONSTRAINT ON (h:House) ASSERT h.name IS UNIQUE",
    "CREATE CONSTRAINT ON (b:Book) ASSERT b.title IS UNIQUE"
  )
  // Extract data
  val file = io.Source.fromFile("character-deaths.csv")
  val characters = file.getLines.drop(1).map{ line =>

    val col = line.split(",").map(_.trim)

    val name         = col(0)
    val house        = if (col(1).isEmpty) None else Some(col(1))
    val deathYear    = if (col(2).isEmpty) None else Some(col(2).toInt)
    val bookDeath    = if (col(3).isEmpty) None else Some(col(3).toInt)
    val deathChapter = if (col(4).isEmpty) None else Some(col(4).toInt)
    val introChapter = if (col(5).isEmpty) None else Some(col(5).toInt)
    val gender       = if (col(6)  == "1") "Male" else "Female"
    val isNoble      = if (col(7)  == "1") true else false

    val inBook = col.slice(8,13)
                    .map(b => if (b == "1") true else false)
                    .zipWithIndex
                    .map{ case (b,i) => (i+1,b) }.toMap

    val bookIntro = inBook.filter(_._2).keys.min

    Character(name, house, deathYear, bookDeath, deathChapter, bookIntro, introChapter,
              gender, isNoble, inBook)
  }.toList

  // create characters
  val charsCypher = characters.map{c =>
    s"CREATE (:Character { name: '${c.name}', gender: '${c.gender}', is_noble: ${c.isNoble} })"
  }.foreach(session.run)

  // create houses
  val houses = characters.filter(_.house.isDefined).map(_.house.get).distinct
  val housesCypher= houses.map(name => s"CREATE (:House { name: '${name}' })").foreach(session.run)

  // create indexes to speed up things
  val indexesCypher = List(
    "CREATE INDEX ON :Character (name)",
    "CREATE INDEX ON :House(name)"
  ).foreach(session.run)

  // set allegiences
  val allegienceCypher = characters.filter(_.house.isDefined).map{ c =>
    s"""MATCH (char:Character {name: '${c.name}' }),(house:House {name: '${c.house.get}' })
        CREATE (char) -[:BELONGS_TO]-> (house)""".stripMargin
  }.foreach(session.run)

  val inBooksCypher = ( for{
      c <- characters
      i <- 1 to 5
      if c.inBook(i)
    } yield s"""MATCH (char:Character {name: '${c.name}' }), (book:Book {title: '${bookTitle(i)}' })
    CREATE (char) -[:IS_IN]-> (book)""".stripMargin ).foreach(session.run)



  // bye bye
  session.close

}

case class Character(
  name         : String,
  house        : Option[String],
  deathYear    : Option[Int],
  bookDeath    : Option[Int],
  deathChapter : Option[Int],
  bookIntro    : Int,
  introChapter : Option[Int],
  gender       : String,
  isNoble      : Boolean,
  inBook       : Map[Int,Boolean]
)
