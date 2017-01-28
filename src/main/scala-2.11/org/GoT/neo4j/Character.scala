package org.GoT.neo4j

import scala.collection.immutable.IndexedSeq

/**
  * Created by agapito on 26/01/2017.
  */

case class Character(name: String,
                     house: Option[String],
                     deathYear: Option[Int],
                     bookDeath: Option[Int],
                     deathChapter: Option[Int],
                     bookIntro: Int,
                     introChapter: Option[Int],
                     gender: String,
                     isNoble: Boolean,
                     inBook: Map[Int, Boolean]) {

  val cqlCharacter = s"""MERGE(:Character { id:   '${this.name}' + '_' + '${this.house.getOrElse("None")}',
                                            name: '${this.name}',
                                            gender: '${this.gender}',
                                            book: ${this.bookIntro},
                                            chapter: ${this.introChapter.getOrElse(-1)},
                                            is_noble: ${this.isNoble} })""".stripMargin

  val cqlAllegience: String =
    if (this.house.isDefined)
      s"""MERGE (char:Character {name: '${this.name}' })
          MERGE (house:House {name: '${this.house.get}' })
          CREATE UNIQUE (char) -[:BELONGS_TO]-> (house)""".stripMargin
    else
      ""

  val cqlState: String =
    if (this.deathChapter.isDefined || this.bookDeath.isDefined || this.deathChapter.isDefined)
      s"""MERGE (char:Character {name: '${this.name}' })
          MERGE (st:State {name: 'Dead'})
          CREATE UNIQUE (char) -[:IS {year: ${this.deathYear.getOrElse(-1)},
                                      book: ${this.bookDeath.getOrElse(-1)},
                                      chapter: ${this.deathChapter.getOrElse(-1)} } ]-> (st)""".stripMargin
    else
      s"""MERGE (char:Character {name: '${this.name}' })
          MERGE (st:State {name: 'Alive'})
          CREATE UNIQUE (char) -[:IS { chapter: ${this.introChapter.getOrElse(-1)} } ]-> (st)""".stripMargin


  val cqlInBook: IndexedSeq[String] = for {i <- 1 to 5 if this.inBook(i)} yield
    s"""MERGE (char:Character {name: '${this.name}' })
        MERGE (book:Book {title: '${Book.title(i)}' })
        CREATE UNIQUE (char) -[:IS_IN]-> (book)""".stripMargin

}

object Character {

  // using the apply method in the companion object to overload the constructor
  def apply(csvString: String): Character = {

    val col = csvString.split(",").map(_.trim)

    val name = col(0)
    val house = if (col(1).isEmpty) None else Some(col(1))
    val deathYear = if (col(2).isEmpty) None else Some(col(2).toInt)
    val bookDeath = if (col(3).isEmpty) None else Some(col(3).toInt)
    val deathChapter = if (col(4).isEmpty) None else Some(col(4).toInt)
    val introChapter = if (col(5).isEmpty) None else Some(col(5).toInt)
    val gender = if (col(6) == "1") "Male" else "Female"
    val isNoble = col(7) == "1"

    val inBook = col.slice(8, 13)
      .map(b => b == "1")
      .zipWithIndex
      .map { case (b, i) => (i + 1, b) }.toMap

    val bookIntro = inBook.filter(_._2).keys.min

    new Character(name, house, deathYear, bookDeath, deathChapter, bookIntro, introChapter, gender, isNoble, inBook)
  }
}
