package org.GoT.neo4j

import org.scalatest.FunSuite

/**
  * Created by agapito on 26/01/2017.
  */
class CharacterTest extends FunSuite {

  val aryaCSV= "Arya Stark,Stark,,,,2,0,1,1,1,1,1,1"
  val arya = new Character(
    "Arya Stark",  // name
    Some("Stark"), // house
    None, // deathYear
    None, // bookDeath
    None, // deathChapter
    1, // bookIntro
    Some(2), // introChapter
    "Female", // gender
    true, // isNoble
    Map(1->true, 2 -> true, 3 -> true, 4-> true, 5->true) //inBook
  )

  test("Character CSV string is parsed correctly") {

    val a = Character(aryaCSV)

    assert(
      a.name == "Arya Stark" &&
      a.house.contains("Stark") &&
      a.deathYear.isEmpty &&
      a.bookDeath.isEmpty &&
      a.deathChapter.isEmpty &&
      a.bookIntro == 1 &&
      a.introChapter.contains(2) &&
      a.gender == "Female" &&
      a.isNoble &&
      a.inBook == Map(1->true, 2 -> true, 3 -> true, 4-> true, 5->true)

    )
  }

}
