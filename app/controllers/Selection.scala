package controllers

import play.api.mvc.{ Action, Controller, Result }
import play.api.data._
import play.api.data.Forms._

import models._

object Selection extends Controller {

  val login = Redirect(controllers.routes.Application.login)

  def UserAction(doWithUser: User => Result) = Action {
    implicit request =>
      request.session.get("connected").map { email =>
        val user = User(email)
        if (user != null) {
          doWithUser(user)
        } else {
          login
        }
      }.getOrElse(login)
  }

  def listGyms(select: Boolean) = UserAction {
    user =>
      val gyms = Gym.findByUser(user)
      if (gyms.isEmpty) Redirect(controllers.routes.Selection.newGym())
      else if (gyms.size == 1 && !select) Redirect(controllers.routes.Selection.getGym(gyms.head.id))
      else {
        Ok(views.html.listGyms(user, gyms))
      }
  }

  def newGym = UserAction {
    user =>
      val filledForm = Gym.form(user)
      Ok(views.html.addGym(user, filledForm))
  }

  def addGym = Action {
    implicit request =>
      request.session.get("connected").map { email =>
        val user = User(email)
        Gym.form(user).bindFromRequest.fold(
          failure => BadRequest,
          gym =>
            {
              Gym.save(gym)
              Redirect(controllers.routes.Selection.listMachines(gym.id, false))
            })
      }.getOrElse(login)
  }

  def getGym(gymId: Long) = Action {
    Redirect(controllers.routes.Selection.listMachines(gymId, false))
  }

  def listMachines(gymId: Long, select: Boolean) = UserAction {
    user =>
      val machines = Machine.findByGym(gymId)
      if (machines.isEmpty) Redirect(controllers.routes.Selection.newMachine(gymId))
      else if (machines.size == 1 && !select) Redirect(controllers.routes.Selection.getMachine(gymId, machines.head.id))
      else {
        val gym = Gym.findById(gymId)
        Ok(views.html.listMachines(user, gym, machines))
      }
  }

  def newMachine(gymId: Long) = UserAction {
    user =>
      val gym = Gym.findById(gymId.toLong)
      val filledForm = Machine.form(gymId)
      Ok(views.html.addMachine(user, filledForm, gym))
  }

  def addMachine(gymId: Long) = Action {
    implicit request =>
      Machine.form(gymId).bindFromRequest.fold(
        failure => BadRequest,
        machine => {
          Machine.save(machine)
          Redirect(controllers.routes.Selection.listExercises(gymId, machine.id, false))
        })
  }

  def getMachine(gymId: Long, machineId: Long) = Action {
    Redirect(controllers.routes.Selection.listExercises(gymId, machineId, false))
  }

  def listExercises(gymId: Long, machineId: Long, select: Boolean) = UserAction {
    user =>
      val exercises = Exercise.findByMachine(machineId)
      if (exercises.isEmpty) Redirect(controllers.routes.Selection.newExercise(gymId, machineId))
      else if (exercises.size == 1 && !select) Redirect(controllers.routes.Selection.getExercise(gymId, machineId, exercises.head.id))
      else {
        val gym = Gym.findById(gymId)
        val machine = Machine.findById(machineId)
        Ok(views.html.listExercises(user, gym, machine, exercises))
      }
  }

  def newExercise(gymId: Long, machineId: Long) = UserAction {
    user =>
      val gym = Gym.findById(gymId.toLong)
      val machine = Machine.findById(machineId)
      val filledForm = Exercise.form(machineId).fill(Exercise(0, Some("default"), Some("triceps"), 0))
      Ok(views.html.addExercise(user, filledForm, gym, machine))
  }

  def getExercise(gymId: Long, machineId: Long, exerciseId: Long) = Action {
    Redirect(controllers.routes.Selection.listPerformances(gymId, machineId, exerciseId))
  }

  def addExercise(gymId: Long, machineId: Long) = Action {
    implicit request =>
      Exercise.form(machineId).bindFromRequest.fold(
        failure => BadRequest,
        exercise => {
          Exercise.save(exercise)
          Redirect(controllers.routes.Selection.listPerformances(gymId, machineId, exercise.id))
        })
  }

  def listPerformances(gymId: Long, machineId: Long, exerciseId: Long) = UserAction {
    user =>
      val gym = Gym.findById(gymId.toLong)
      val machine = Machine.findById(machineId)
      val exercise = Exercise.findById(exerciseId)
      val performances = Performance.findByExercise(exerciseId)
      Ok(views.html.listPerformances(user, gym, machine, exercise, performances))
  }

  def newPerformance(gymId: Long, machineId: Long, exerciseId: Long) = UserAction {
    user =>
      val gym = Gym.findById(gymId.toLong)
      val machine = Machine.findById(machineId)
      val exercise = Exercise.findById(exerciseId)
      val previousPerformance = Performance.findByExercise(exerciseId).headOption
      val assumedWeight = if (previousPerformance.isDefined) previousPerformance.get.weight else 0
      val filledForm = Performance.form(user.id, exerciseId).fill(Performance(0, new java.util.Date(System.currentTimeMillis), assumedWeight, Some(""), 0, 0))
      Ok(views.html.addPerformance(user, filledForm, gym, machine, exercise))
  }

  def addPerformance(gymId: Long, machineId: Long, exerciseId: Long) = Action {
    implicit request =>
      request.session.get("connected").map { email =>
        val user = User(email);
        Performance.form(user.id, exerciseId).bindFromRequest.fold(
          failure => BadRequest,
          performance => {
            Performance.save(performance)
            Redirect(controllers.routes.Selection.listPerformances(gymId, machineId, exerciseId))
          })
      }.getOrElse(login)
  }
}