package example

import org.scalatest.{ FlatSpecLike, BeforeAndAfterAll }
import akka.testkit.{ TestActorRef, ImplicitSender, TestKit }
import akka.actor.ActorSystem

class ModelSpec extends TestKit(ActorSystem()) with FlatSpecLike with ImplicitSender with BeforeAndAfterAll {

  override def afterAll() {
    system.shutdown()
  }

  val model = TestActorRef(new Model)

  "A Model" should "return a list of 5 ItemSummaries" in {
    model ! 'list
    val lst = expectMsgType[Seq[ItemSummary]]
    assert(lst.size === 5)
  }

  it should "return item 1 when asked" in {
    model ! 1
    val item = expectMsgType[Item]
    assert(item.id === 1)
    assert(item.title === "foo")
  }

  it should "return None when requested item is not found" in {
    model ! 10
    expectMsg(None)
  }

}
