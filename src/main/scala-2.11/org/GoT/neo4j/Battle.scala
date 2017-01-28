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
                  region: String) {

  val cqlAttakerKing: List[String] =
    if (this.attacker_king.isDefined) this.attacker_king.get.map { king =>
      s"""MERGE (c:Character {name: '${king}' })
            MERGE (b:Battle {name: '${name}'})
            ON CREATE SET
              b.region = '${this.region}',
              b.year = '${this.year}',
              b.type = '${this.battle_type}',
              b.major_death = ${this.major_death},
              b.major_capture = ${this.major_capture},
              b.summer = ${this.summer.getOrElse(false)}
        CREATE UNIQUE (c) -[:ATTACKER_KING_IN {won: ${attacker_outcome},
                                               size: ${attacker_size.getOrElse(-1)} }]-> (b)""".stripMargin
    }
    else
      List("")

  val cqlDefenderKing: List[String] =
    if (this.defender_king.isDefined) this.defender_king.get.map { king =>
      s"""MERGE (c:Character {name: '${king}' })
            MERGE (b:Battle {name: '${name}'})
            ON CREATE SET
              b.region = '${this.region}',
              b.year = '${this.year}',
              b.type = '${this.battle_type}',
              b.major_death = ${this.major_death},
              b.major_capture = ${this.major_capture},
              b.summer = ${this.summer.getOrElse(false)}
            CREATE UNIQUE (c) -[:DEFENDER_KING_IN {won: ${!attacker_outcome},
                                                   size: ${defender_size.getOrElse(-1)} }]-> (b)""".stripMargin
    }
    else
      List("")

  val cqlAttackerCommanders: List[String] =
    if (this.attacker_commander.isDefined) this.attacker_commander.get.map { cmd =>
      s"""MERGE (c:Character {name: '${cmd}' })
            MERGE (b:Battle {name: '${name}'})
            ON CREATE SET
              b.region = '${this.region}',
              b.year = '${this.year}',
              b.type = '${this.battle_type}',
              b.major_death = ${this.major_death},
              b.major_capture = ${this.major_capture},
              b.summer = ${this.summer.getOrElse(false)}
              CREATE UNIQUE (c) -[:COMMANDED_ATTACK_IN {won: ${attacker_outcome},
                                                        size: ${attacker_size.getOrElse(-1)} }]-> (b)""".stripMargin
    }
    else List("")

  val cqlDefenderCommanders: List[String] =
    if (this.defender_commander.isDefined) this.defender_commander.get.map { cmd =>
      s"""MERGE (c:Character {name: '${cmd}' })
          MERGE (b:Battle {name: '${name}'})
          ON CREATE SET
            b.region = '${this.region}',
            b.year = '${this.year}',
            b.type = '${this.battle_type}',
            b.major_death = ${this.major_death},
            b.major_capture = ${this.major_capture},
            b.summer = ${this.summer.getOrElse(false)}
          CREATE UNIQUE (c) -[:COMMANDED_DEFENCE_IN {won: ${!attacker_outcome},
                                                     size: ${defender_size.getOrElse(-1)} }]-> (b)""".stripMargin
    }
    else
      List("")

  val cqlAttackerHouses: List[String] = this.attackers.get.map { h =>
    s"""MERGE (h:House {name: '${h}' })
        MERGE (b:Battle {name: '${name}'})
        CREATE UNIQUE (h) -[:ATTACKER_IN {won: ${attacker_outcome} }]-> (b)""".stripMargin
  }

  val cqlDefenderHouses: List[String] = this.attackers.get.map { h =>
    s"""MERGE (h:House {name: '${h}' })
        MERGE (b:Battle {name: '${name}'})
        CREATE UNIQUE (h) -[:DEFENDER_IN {won: ${!attacker_outcome} }]-> (b)""".stripMargin
  }
}

object Battle {

  def apply(csvString: String): Battle = {

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

