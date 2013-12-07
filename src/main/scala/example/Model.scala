package example

import akka.actor.{ Props, Actor }

case class Item(id: Int, title: String, desc: String)
case class ItemSummary(id: Int, title: String)

object Model {
  def props: Props = Props(new Model)
  def name = "model"
}

class Model extends Actor {

  val items = Item(1, "foo", "More information about Foo") ::
    Item(2, "bar", "More information about Bar") ::
    Item(3, "qux", "More information about Qux") ::
    Item(4, "quux", "More information about Quux") ::
    Item(5, "quuux", "More information about Quuux") ::
    Nil

  def receive = {
    case id: Int =>
      sender ! items.find(_.id == id).getOrElse(None)

    case 'list =>
      sender ! items.map(i => ItemSummary(i.id, i.title))

  }

}
