package example

import akka.actor.ActorSystem

object Boot extends App {

  implicit val system = ActorSystem("my-example")
  system.actorOf(TopLevel.props, TopLevel.name)

}
