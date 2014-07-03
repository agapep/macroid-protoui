package macroid.protoui

import macroid.util.Ui
import android.view.View
import macroid.{CanTweak, Tweak, ActivityContext}
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
  import macroid.FullDsl._
  def withTweak[W <: View, R] (t: Tweak[W])
                            (implicit ct: CanTweak[Ui[View], Tweak[W], View]):
                            ViewOf[T] = new ViewOf(ui <~ t, value)

  def withTweak[W <: View, R] (f: AbstractReactiveVarible[T] => Tweak[W])
                              (implicit ct: CanTweak[Ui[View], Tweak[W], View]):
                              ViewOf[T] = new ViewOf(ui <~ f(value), value)

  def snail:ViewOf[T] = ???
  def view = ui.get
}

class ViewOf2[T, S](ui: Ui[View], value: AbstractReactiveVarible[T],
                 val value2: AbstractReactiveVarible[S]) extends ViewOf[T](ui, value)

object ViewOf {
  //tested
  def apply[T](implicit creator:CanBeViewOf[T], c: ActivityContext): ViewOf[T] = {
    val handler = new ReactiveVarible[T]
    new ViewOf(creator.createView(handler), handler)
  }

  def apply[T](tweaks: Tweak[View]*)
              (implicit creator:CanBeViewOf[T], c: ActivityContext): ViewOf[T] = {
    import macroid.FullDsl._
    val handler = new ReactiveVarible[T]
    val view = tweaks.foldLeft[Ui[View]](creator.createView(handler))((v:Ui[View],t: Tweak[View]) => v <~ t)
    new ViewOf(view, handler)
  }

  //tested
  def apply[T](obj: T)
              (implicit creator:CanBeViewOf[T], c: ActivityContext): ViewOf[T] = {
    val res = ViewOf[T]
    res.value.update(obj)
    res
  }

  //tested
  def apply[T, S](implicit creator:CanBeViewOf2[T, S], c: ActivityContext): ViewOf2[T, S] = {
    val handler = new ReactiveVarible[T]
    val handler2 = new ReactiveVarible[S]
    new ViewOf2(creator.createView((handler, handler2)), handler, handler2)
  }

  //tested
  def apply[T, S](obj: T, obj2: S)(implicit creator:CanBeViewOf2[T, S], c: ActivityContext): ViewOf2[T, S] = {
    val res = ViewOf[T, S]
    res.value.update(obj)
    res.value2.update(obj2)
    res
  }


  implicit def toUi[T](s: ViewOf[T]):Ui[View] = s.ui
  implicit def toView[T](s: ViewOf[T]):View = macroid.FullDsl.getUi(s.ui)
  implicit def toReactiveVarible[T](s: ViewOf[T]) = s.value
}
