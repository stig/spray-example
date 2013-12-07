package example

import akka.actor.{ Props, ActorLogging, Actor }
import spray.can.Http
import akka.io.IO

object TopLevel {
  def props: Props = Props(new TopLevel)
  def name = "top-level"
}

class TopLevel extends Actor with ActorLogging {

  import context._

  val model = actorOf(Model.props, Model.name)

  val service = actorOf(Service.props(model), Service.name)

  IO(Http) ! Http.Bind(service, "localhost", 8080)

  def receive = {
    case Http.Bound =>
      log.info("Bound!")

    case Http.CommandFailed =>
      log.error("Port busy?")
  }

}
