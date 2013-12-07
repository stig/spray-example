package example

import akka.actor.{ Props, Actor }

case class ModelResponse(input: String, output: String)

object Model {
  def props: Props = Props(new Model)
  def name = "model"
}

class Model extends Actor {

  def receive = {
    case s: String =>
      sender ! ModelResponse(s, s.reverse)
  }

}
