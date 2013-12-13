package uk.gov.dfid.actors

import akka.actor.{Props, FSM, Actor}
import uk.gov.dfid.actors.States.{Downloading, Idle, State}
import uk.gov.dfid.actors.Commands.{Synchronise, Finish, Go}

class Coordinator extends Actor with FSM[State, Unit] {

  startWith(Idle, Unit)

  when(Idle) {
    case Event(Go, _) =>
      sender ! true
      goto(Downloading)
  }

  when(Downloading) {
    case Event(Finish, _) =>
      goto(Idle)
  }

  whenUnhandled {
    case Event(Go, _) => {
      println("Can't start a new download yet")
      sender ! false
      stay
    }
  }

  onTransition {
    case Idle -> Downloading => {
      println("Starting Registry Sync")
      context.actorOf(Props[Synchroniser]) ! Synchronise
    }
    case Downloading -> Idle => {
      println("Download complete.  Idling")
    }
  }

  initialize()
}
