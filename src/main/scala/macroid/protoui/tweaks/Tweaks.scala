package macroid.protoui.tweaks

import macroid.protoui.{AbstractReactiveVarible, ReactiveVarible}
import macroid.Tweak
import android.view.View
import android.view.View

/**
 * Created by slovic on 16.06.14.
 */
object Tweaks {

  val blackBackground = Tweak[View](v => v.setBackgroundColor(v.getResources.getColor(android.R.color.black)))

  def rv[P,V <: View]( f:(V, P) => Unit )(implicit t: AbstractReactiveVarible[P]): Tweak[V] = {
    Tweak(v => t.onChange(f(v, _)))
  }

}
