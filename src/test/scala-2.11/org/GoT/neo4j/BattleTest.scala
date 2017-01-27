package org.GoT.neo4j

import org.scalatest.FunSuite

/**
  * Created by agapito on 27/01/2017.
  */
class BattleTest extends FunSuite {

  val riverrrunCSV = "Battle of Riverrun,298,3,Joffrey/Tommen Baratheon,Robb Stark,Lannister,,,,Tully,,,,win,pitched battle,0,1,15000,10000,Jaime Lannister/Andros Brax,Edmure Tully/Tytos Blackwood,1,Riverrun,The Riverlands"

  test("Battle CSV is parsed correctly.") {

    val r = Battle(riverrrunCSV)
    assert(
      r.name == "Battle of Riverrun" &&
      r.year == 298 &&
      r.battle_number == 3 &&
      r.attacker_king.contains(List("Joffrey", "Tommen Baratheon")) &&
      r.defender_king.contains(List("Robb Stark")) &&
      r.attackers.contains(List("Lannister")) &&
      r.defenders.contains(List("Tully")) &&
      r.attacker_outcome &&
      r.battle_type == "pitched battle" &&
      ! r.major_death &&
      r.major_capture &&
      r.attacker_size.contains(15000) &&
      r.defender_size.contains(10000) &&
      r.attacker_commander.contains(List("Jaime Lannister", "Andros Brax")) &&
      r.defender_commander.contains(List("Edmure Tully", "Tytos Blackwood")) &&
      r.summer.contains(true) &&
      r.location.contains("Riverrun") &&
      r.region == "The Riverlands"
    )
  }
}
