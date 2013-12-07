package example

import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike }
import akka.actor.ActorDSL._
import akka.actor.{ Terminated, Props, ActorRef, ActorSystem }
import akka.testkit.{ EventFilter, TestActorRef, ImplicitSender, TestKit }

class TopLevelSpec extends TestKit(ActorSystem()) with FlatSpecLike with BeforeAndAfterAll with ImplicitSender {

  override def afterAll() {
    super.afterAll()
    system.shutdown()
  }

  trait Case {
    val top = TestActorRef(new TopLevel with TopLevelConfig {

      def createModel = context.actorOf(Props(new Act {
        become { case _ => throw new Exception("crash") }
      }))

      def createService(model: ActorRef) = context.actorOf(Props(new Act {
        become { case _ => throw new Exception("boom") }
      }))

      def interface: String = "localhost"
      def port: Int = (10000 + math.random * 50000).toInt
    })
    watch(top)
  }

  "TopLevel" should "restart service if it dies once" in new Case {
    EventFilter[Exception](occurrences = 1) intercept {
      top.underlyingActor.service ! 'bang
    }
    expectNoMsg()
  }

  it should "restart service if it dies 10 times" in new Case {
    EventFilter[Exception](occurrences = 10) intercept {
      (1 to 10) foreach { x =>
        top.underlyingActor.service ! 'bang
      }
    }
    expectNoMsg()
  }

  it should "stop itself if model dies" in new Case {
    EventFilter[Exception](occurrences = 2) intercept {
      top.underlyingActor.model ! 'bang
    }

    assert(expectMsgType[Terminated].actor === top)
  }

}
