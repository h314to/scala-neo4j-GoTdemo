package org.GoT.neo4j

/**
  * Created by agapito on 27/01/2017.
  */
case class Battle(name: String,
                  year: Int,
                  battle_number: Int,
                  attacker_king: Option[List[String]],
                  defender_king: Option[List[String]],
                  attackers: Option[List[String]],
                  defenders: Option[List[String]],
                  attacker_outcome: Boolean,
                  battle_type: String,
                  major_death: Boolean,
                  major_capture: Boolean,
                  attacker_size: Option[Int],
                  defender_size: Option[Int],
                  attacker_commander: Option[List[String]],
                  defender_commander: Option[List[String]],
                  summer: Option[Boolean],
                  location: Option[String],
                  region: String)

object Battle {

  def apply(csvString: String) : Battle = {

    val col = csvString.split(",").map(_.trim)

    def getEntries(str: String): Option[List[String]] = {
      if (str.isEmpty) None
      else Some(str.split("/").toList)
    }

    val name = col(0)
    val year = col(1).toInt
    val battle_number = col(2).toInt
    val attacker_king = getEntries(col(3))
    val defender_king = getEntries(col(4))
    val attackers = getEntries(col.slice(5, 9).mkString("/"))
    val defenders = getEntries(col.slice(9, 13).mkString("/"))
    val attacker_outcome = col(13) == "win"
    val battle_type = col(14)
    val major_death = col(15) == "1"
    val major_capture = col(16) == "1"
    val attacker_size = if (col(17).isEmpty) None else Some(col(17).toInt)
    val defender_size = if (col(18).isEmpty) None else Some(col(18).toInt)
    val attacker_commander = getEntries(col(19))
    val defender_commander = getEntries(col(20))
    val summer = if (col(21).isEmpty) None else Some(col(21) == "1")
    val location = if (col(22).isEmpty) None else Some(col(22))
    val region = col(23)

    new Battle(name, year, battle_number, attacker_king, defender_king, attackers, defenders, attacker_outcome,
      battle_type, major_death, major_capture, attacker_size, defender_size, attacker_commander, defender_commander,
      summer, location, region)
  }
}

