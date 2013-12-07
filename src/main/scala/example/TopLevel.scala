package example

import akka.actor._
import spray.can.Http
import akka.io.IO
import akka.actor.Terminated
import akka.actor.SupervisorStrategy.{ Restart, Stop }

object TopLevel {
  def props: Props = Props(new TopLevel with TopLevelConfig {
    def interface = "localhost"
    def port = 8080
  })
  def name = "top-level"
}

trait TopLevelConfig {
  def interface: String
  def port: Int
}

class TopLevel extends Actor with ActorLogging {
  this: TopLevelConfig =>

  import context._

  val model = actorOf(Model.props, Model.name)
  context watch model

  val service = actorOf(Service.props(model), Service.name)
  context watch service

  IO(Http) ! Http.Bind(service, interface, port)

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _ if model == sender => Stop
    case _ if service == sender => Restart
  }

  def receive = {
    case Terminated(`model`) => context stop self
  }

}
