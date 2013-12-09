package example

import org.scalatest.FlatSpec

class ModelSpec extends FlatSpec {

  "A Model" should "return a list of 5 ItemSummaries" in new Model {
    assert(list.size === 5)
  }

  it should "return 3 items containing 'Qu'" in new Model {
    assert(query("Qu").size === 3)
  }

  it should "return item 1 when asked" in new Model {
    val item = get(1).get
    assert(item.id === 1)
    assert(item.title === "foo")
    assert(item.stock === 2)
  }

  it should "return None when requested item is not found" in new Model {
    assert(get(100) === None)
  }

}
