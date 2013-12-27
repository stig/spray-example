package example

import org.scalatest.{ FlatSpecLike, BeforeAndAfterAll }
import akka.testkit.{ TestActorRef, ImplicitSender, TestKit }
import akka.actor.{ Props, ActorSystem }

class ModelActorSpec extends TestKit(ActorSystem()) with FlatSpecLike with ImplicitSender with BeforeAndAfterAll {

  override def afterAll() {
    system.shutdown()
  }

  val model = TestActorRef(Props[ModelActor])

  "A Model" should "return a list of 5 ItemSummaries" in {
    model ! 'list
    val lst = expectMsgType[ItemSummaries]
    assert(lst.items.size === 5)
  }

  it should "return 3 items containing 'Qu'" in {
    model ! ('query, "Qu")
    val lst = expectMsgType[ItemSummaries]
    assert(lst.items.size === 3)
  }

  it should "return item 1 when asked" in {
    model ! 1
    val item = expectMsgType[Item]
    assert(item.id === 1)
    assert(item.title === "foo")
  }

  it should "return ItemNotFound when requested item is not found" in {
    model ! 10
    expectMsg(ItemNotFound)
  }

}
