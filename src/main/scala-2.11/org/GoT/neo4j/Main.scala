package org.GoT.neo4j

import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}

/**
  * Created by agapito on 25/01/2017.
  */
object Main extends App {

  // Connect to database
  val driver = GraphDatabase.driver("bolt://localhost/7687", AuthTokens.basic("neo4j", "admin"))
  val session = driver.session

  // Drop everything on start if running in debug mode
  val debug = Option(System.getProperty("debug")) match {
    case Some(x: String) => x.toBoolean
    case None => false
  }
  if (debug) {
    println("WARN: Running in debug mode.")
    println("WARN: Dropping all constraints and deleting all nodes.")
    val dropResult = Schema.dropAndDelete.foreach(session.run)
  }

  // Constraints for names (also creates indexes)
  val constraintsResult = Schema.constraint.foreach(session.run)

  // create books
  val booksResult = Book.cqlBooks.foreach(session.run)

  // Extract all character data
  val fileDeaths = io.Source.fromFile("character-deaths.csv")
  val characters = fileDeaths.getLines.drop(1).map(Character(_)).toList

  // create characters, allegiences, books, and set state (alive/dead)
  val charsResult = characters.map(_.cqlCharacter).foreach(session.run)
  val allegienceResult = characters.filter(_.house.isDefined).map(_.cqlAllegience).foreach(session.run)
  val inBooksResult = characters.flatMap(_.cqlInBook).foreach(session.run)
  val stateResult = characters.map(_.cqlState).foreach(session.run)

  // Extract all battle data
  val fileBattles = io.Source.fromFile("battles.csv")
  val battles = fileBattles.getLines.drop(1).map(Battle(_)).toList

  // Create battles, kings, defenders/attackers
  val attackerResult = battles.filter(_.attacker_king.isDefined).flatMap(_.cqlAttakerKing).foreach(session.run)
  val defenderResult = battles.filter(_.defender_king.isDefined).flatMap(_.cqlDefenderKing).foreach(session.run)
  val attackerCmdResult = battles.filter(_.attacker_commander.isDefined).flatMap(_.cqlAttackerCommanders).foreach(session.run)
  val defenderCmdResult = battles.filter(_.defender_commander.isDefined).flatMap(_.cqlDefenderCommanders).foreach(session.run)
  val attackerHouseResult = battles.filter(_.attackers.isDefined).flatMap(_.cqlAttackerHouses).foreach(session.run)
  val defenderHouseResult = battles.filter(_.defenders.isDefined).flatMap(_.cqlDefenderHouses).foreach(session.run)

  // bye bye
  session.close()
}

