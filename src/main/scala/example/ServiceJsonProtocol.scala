package example

import spray.json._

sealed trait StockLevel
object StockLevel {
  def apply(level: Int) =
    if (level > 3) InStock
    else if (level > 0) LowStock
    else SoldOut
}
case object InStock extends StockLevel
case object LowStock extends StockLevel
case object SoldOut extends StockLevel

case class PublicItem(id: Int, stockLevel: StockLevel, title: String, desc: String)
object PublicItem {
  def apply(i: Item): PublicItem = PublicItem(i.id, StockLevel(i.stock), i.title, i.desc)
}

case class PublicItemSummary(id: Int, stockLevel: StockLevel, title: String)
object PublicItemSummary {
  def apply(i: ItemSummary): PublicItemSummary = PublicItemSummary(i.id, StockLevel(i.stock), i.title)
}

trait ServiceJsonProtocol extends DefaultJsonProtocol {

  implicit object StockLevelFmt extends JsonFormat[StockLevel] {
    def write(obj: StockLevel) = JsString(obj.toString)
    def read(json: JsValue): StockLevel = json match {
      case JsString("InStock") => InStock
      case JsString("LowStock") => LowStock
      case JsString("SoldOut") => SoldOut
      case _ => throw new Exception("Unsupported StockLevel")
    }
  }

  implicit val publicItemFmt = jsonFormat4(PublicItem.apply)
  implicit val publicItemSummaryFmt = jsonFormat3(PublicItemSummary.apply)

}
