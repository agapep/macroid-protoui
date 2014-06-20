package macroid.protoui

import android.view.View
import macroid.Tweak

/**
 * Created by slovic on 20.06.14.
 */
package object tweaks {
  def react[P,V <: View]( f:(V, P) => Unit )(implicit t: AbstractReactiveVarible[P]): Tweak[V] = {
    Tweak(v => t.onChange(f(v, _)))
  }
}
