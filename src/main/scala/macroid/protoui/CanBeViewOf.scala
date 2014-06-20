package macroid.protoui

import macroid.util.Ui
import android.view.View
import macroid.{Tweak, ActivityContext}
import android.widget.{TextView, ListView}
import macroid.FullDsl._
import macroid.protoui.tweaks.Tweaks._
import scala.collection.JavaConversions.seqAsJavaList
import scala.language.implicitConversions

/**
 * Służy do definiowania widoków dla klasy T.
 * musisz zadbać aby dla widoku T istniała instancja CanBeViewOf[T]
 * najłatwiej jest to uczynić korzystając z AutoView[T]
 *
 * przykłady masz w obiekcie towarzyszącym CanBeView.
 * Created by slovic on 14.06.14.
 */

abstract class CanBeViewOf[T] {
  def createView(trig: AbstractReactiveVarible[T])(implicit c: ActivityContext): Ui[View]
}

abstract class CanBeViewOf2[T, S] {
  def createView(trigs: (AbstractReactiveVarible[T], AbstractReactiveVarible[S]))
  (implicit c: ActivityContext): Ui[View]
}

object CanBeViewOf {
  implicit def objectCanBeShown = new AutoView[AnyRef](
    implicit t => implicit c =>
      w[TextView] <~ rv((v:TextView,obj:AnyRef) => v.setText(obj.toString))
  )

  implicit def arrayCanBeShown[T <: AnyRef](implicit c: ActivityContext, cbs: CanBeViewOf[T]): CanBeViewOf[Array[T]] = new AutoView[Array[T]] (
    implicit t => implicit c =>
      w[ListView] <~ rv((v: ListView, data:Array[T]) => v.setAdapter(AutoAdapter[T](data)))
  )
}

class AutoView[T](f: AbstractReactiveVarible[T] => ActivityContext =>  Ui[View]) extends CanBeViewOf[T] {
  def this(f: (AbstractReactiveVarible[T], ActivityContext) => Ui[View]) = this( f.curried )

  def createView(trig: AbstractReactiveVarible[T])(implicit c: ActivityContext): Ui[View] = {
    f(trig)(c)
  }
}


class AutoView2[T, S](f: ((AbstractReactiveVarible[T], AbstractReactiveVarible[S])) => ActivityContext =>  Ui[View]) extends CanBeViewOf2[T,S] {
  def this(f: ((AbstractReactiveVarible[T],AbstractReactiveVarible[S]), ActivityContext) => Ui[View]) = {
    this( f.curried )
    val v = f.curried
  }
  def createView(trigs: (AbstractReactiveVarible[T],AbstractReactiveVarible[S]))(implicit c: ActivityContext): Ui[View] = {
    f(trigs._1, trigs._2)(c)
  }
}