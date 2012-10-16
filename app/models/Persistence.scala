package models

class Persistence {

}

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Schema
import org.squeryl.annotations.Column
import org.squeryl.KeyedEntity
import org.squeryl.dsl.{OneToMany, ManyToOne}

object Database extends Schema {
  
  val usersTable: Table[User] = table[User]("users")
  val gymsTable: Table[Gym] = table[Gym]("gyms")
  val machinesTable: Table[Machine] = table[Machine]("machines")
  val exercisesTable: Table[Exercise] = table[Exercise]("exercises")
  val performancesTable: Table[Performance] = table[Performance]("performances")
  
  on(usersTable) {
    u => declare { u.id is(autoIncremented("users_id_seq")) }
  }

  on(gymsTable) {
    g => declare { g.id is(autoIncremented("gyms_id_seq")) }
  }

  on(machinesTable) {
    m => declare { m.id is(autoIncremented("machines_id_seq")) }
  }

  on(exercisesTable) {
    e => declare { e.id is(autoIncremented("exercises_id_seq")) }
  }

  on(performancesTable) {
    p => declare { p.id is(autoIncremented("performances_id_seq")) }
  }
  
  val gymsToAdmin = oneToManyRelation(usersTable, gymsTable).via((u,g) => u.id === g.adminid)
  val machinesToGym = oneToManyRelation(gymsTable, machinesTable).via((g,m) => g.id === m.gymid)
  val exercisesToMachine = oneToManyRelation(machinesTable, exercisesTable).via((m,e) => m.id === e.machineid)
  val performancesToUser = oneToManyRelation(usersTable, performancesTable).via((u,p) => u.id === p.userid)
  val performancesToExercise = oneToManyRelation(exercisesTable, performancesTable).via((e,p) => e.id === p.exerciseid)
}