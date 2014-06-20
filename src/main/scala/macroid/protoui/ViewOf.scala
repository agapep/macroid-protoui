package macroid.protoui

import macroid.util.Ui
import android.view.View
import macroid.ActivityContext
import scala.language.implicitConversions
/**
 * Klasa reprezentuje widok generowany dla typu T który może
 * reprezentować dowolne obiekty tego typu. Objekt można w łatwy sposób
 * aktualizować przez wywołanie metody update lub za pomocą zmiennej value.
 *
 * aby użyć skorzystaj z metody apply obiektu towarzyszącego. np:
 * ViewOf[String] lub ViewOf[String]("40 i cztery")
 *
 * TODO: aktualnie nie można utworzyć RV samemu i przekazać ich do ViewOf.
 * są sytuacje kiedy byłoby to pożądane.
 * Created by slovic on 14.06.14.
 */
class ViewOf[T](val ui: Ui[View], val value: AbstractReactiveVarible[T]) {
  /**
   * skrót dla value.update
   */
  def update(obj: T) = value.update(obj)
  def view = ui.get
}

class ViewOf2[T, S](val ui: Ui[View], val value: AbstractReactiveVarible[T],
                 val value2: AbstractReactiveVarible[S]) {
  def view = ui.get
}

object ViewOf {
  def apply[T](implicit creator:CanBeViewOf[T], c: ActivityContext): ViewOf[T] = {
    val handler = new ReactiveVarible[T]
    new ViewOf(creator.createView(handler), handler)
  }

  def apply[T](obj: T)
              (implicit creator:CanBeViewOf[T], c: ActivityContext): ViewOf[T] = {
    val res = ViewOf[T]
    res.value.update(obj)
    res
  }

  def apply[T, S](implicit creator:CanBeViewOf2[T, S], c: ActivityContext): ViewOf2[T, S] = {
    val handler = new ReactiveVarible[T]
    val handler2 = new ReactiveVarible[S]
    new ViewOf2(creator.createView((handler, handler2)), handler, handler2)
  }


  implicit def toUi[T](s: ViewOf[T]):Ui[View] = s.ui
  implicit def toView[T](s: ViewOf[T]):View = macroid.FullDsl.getUi(s.ui)
  implicit def toReactiveVarible[T](s: ViewOf[T]) = s.value
}
