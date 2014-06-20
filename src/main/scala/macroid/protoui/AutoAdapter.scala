package macroid.protoui

import android.widget.ArrayAdapter
import macroid.ActivityContext
import android.view.{ViewGroup, View}
import scala.collection.mutable

/**
 *
 * Created by slovic on 15.06.14.
 */
class AutoAdapter[T <: AnyRef : CanBeViewOf](c: ActivityContext, list: Array[T]) extends
  ArrayAdapter(c.get.getBaseContext, 0, list) {
  implicit val _ = c
  val shownIndex = mutable.Map[View, ViewOf[T]]()
  //pobiera seq[T]
  //trzyma mutable.Map[View ,Shown[T]] które stworzył
  //jest to niezbędne gdyż konstrukcja adaptera przekazuje
  //tylko convertView: View a nie convertView:Show[View]
  //jeśli mapa będzie wystarczająco wydajna nie powinno się
  //to odbić na działaniu.

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val shown = if (convertView == null) {
      val shown = ViewOf[T]
      shownIndex(shown.view) = shown //add shown to index
      shown
    } else {
      shownIndex.getOrElseUpdate(convertView, ViewOf[T])
    }

    val result = shown.ui.get //need to create view first
    shown.value.update(getItem(position)) //next update it
    result //and returns all.
  }
}

object AutoAdapter {
  def apply[T <: AnyRef](list: Array[T])(implicit c: ActivityContext, cbs: CanBeViewOf[T]) = new AutoAdapter[T](c, list)
}
