package uk.gov.dfid.actors

object States {

  sealed trait State
  case object Idle extends State
  case object Downloading extends State

}
