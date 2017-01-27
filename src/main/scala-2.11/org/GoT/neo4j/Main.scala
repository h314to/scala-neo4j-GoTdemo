package org.GoT.neo4j

import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}

/**
  * Created by agapito on 25/01/2017.
  */
object Main extends App {

  // Connect to database
  val driver = GraphDatabase.driver("bolt://localhost/7687", AuthTokens.basic("neo4j", "admin"))
  val session = driver.session

  // Constraints for names (also creates indexes)
  val constraintsCypher = Schema.constraint.foreach(session.run)

  // create books
  val booksResult = Book.cqlBooks.foreach(session.run)

  // Extract all character data
  val fileDeaths = io.Source.fromFile("character-deaths.csv")
  val characters: List[Character] = fileDeaths.getLines.drop(1).map(Character(_)).toList

  // Extract all battle data
  val fileBattles = io.Source.fromFile("character-deaths.csv")
  val battles = fileDeaths.getLines.drop(1).map(Battle(_)).toList

  // create characters
  val charsResult = characters.map(_.cqlCharacter).foreach(session.run)

  // create houses
  //val houses = characters.filter(_.house.isDefined).map(_.house.get).distinct
  //val housesCypher = houses.map(name => s"MERGE (:House { name: '${name}' })").foreach(session.run)

  // set allegiences
  val allegienceResult = characters.filter(_.house.isDefined).map(_.cqlAllegience).foreach(session.run)

  // set books
  val inBooksResult = characters.flatMap(_.cqlInBook).foreach(session.run)

  // bye bye
  session.close
}

