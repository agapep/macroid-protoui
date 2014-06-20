package macroid.protoui

import android.view.View

/**
 * Created by slovic on 14.06.14.
 */
trait AbstractReactiveVarible[T] {
  /**
   * pozwala zarejestrować wydarzenie które będzie wykonane po każdym
   * zaaktualizowaniu zmiennej.
   * @param action akcja wykonywana po każdej zmianie zmiennej
   * @param autostart jeśli true i zmienna jest już ustawiona to rejestrowana zmiana
   *                  wykona się od razu (nie czeka na kolejny update).
   */
  def onChange(action: (T) => Unit, autostart: Boolean = true): Unit

  /** pozwala na ciche zaaktualizowanie zmiennej. Po wykonaniu
    * zmienna nie wykonuje żadnych innych akcji. Przydatne w sytuacji
    * w której zmianę wywołało to co jest odświeżane za pomocą tej zmiennej:
    * Np: używamy ReactiveVarible do programowego zmiany zawartości EditText'a,
    * Nie byłoby rozsądne abyśmy w addTextChangedListener używali metody update
    * gdyż może to spowodować pojawienie się pętli nieskończonej.
    * @see update
    * @param obj nowa wartość zmiennej
    */
  def silentUpdate(obj:T): Unit

  /** pozwala na zaaktualizowanie zmiennej. Po wykonaniu updatu
    * zmienna wykonuje wszystkie akcje zarejestrowane za pomocą onChange.
    * @see silentUpdate jak update z tym, że nie wykonuje żadych akcji.
    * @param obj nowa wartość zmiennej
    */
  def update(obj:T): Unit

  /**
   * pozwala wyczyścić wszystkie zobowiązania zmiennej
   * (update nie będzie prowadził do propagowania tej
   * wartości np. do widoków).
   * po wykonaniu dalej można jej używać (można np
   * powiązać ją z innym widokiem).
   */
  def kill: Unit
}


class ReactiveVarible[T] extends AbstractReactiveVarible[T] {
  protected val name = "Var"
  protected var value: Option[T] = None
  var actions: List[(T) => Unit] = Nil

  override def onChange(action: (T) => Unit, autostart: Boolean): Unit = {
    actions = action :: actions
    for (actual <- value) if(autostart) action(actual)
  }

  override def silentUpdate(obj: T): Unit = value = Some(obj)

  def update(obj:T) {
    silentUpdate(obj)
    for (a <- actions) a(obj)
  }

  def get() = value

  override def kill: Unit = {
    actions = Nil
  }
}


class ViewableReactiveValue[T] extends ReactiveVarible[T] { //TODO remove
  override val name = "ViewableVar"
  def add[U <: View](v: U, f: (U, T) => Unit): Unit = {
    val action: T => Unit = f(v, _)
  actions = action :: actions
  for (actual <- value) f(v, actual)
  }
}

/**
 * Stworzone dla przypadków kiedy potrzebujemy ustawiać/nasłuchować zmian jednej
 * zmiennej z dwuch stron: np z formularza i z kodu który ten formularz poprawia[Walidator]
 * (zmienia wielkość liter itp.)
 *
 * Założenie jest takie, że formularz jest zmieniany na zewnątrz (przez użytkownika).
 * Zmienia jedno RV[val b] które wywołuje akcje drugiej strony (np: Walidator). Walidator może
 * natomiast chcieć zmienić aktualną wartość pól formularza zatem użyje do tego innego
 * RV [val a] który zaaktualizuje widok.
 *
 * Problem w powyższym scenariuszu stanowi to iż zarówno formularz jak i Walidator muszą
 * posiadać instancję obu zmiennych RV [a i b] mimo że kożystają tylko z części ich interfejsów.
 * Poniższa klasa pozwala na stworzenie takiej pary interfaców które:
 * - nasłuch na _1 i przechwytuje zmiany emitowane na _2 i odwrotnie
 * - nadawanie na _1 emituje zmiany na _2 i odwrotnie
 * - zmiana dowolnej wartości RV [_1 lub _2] powoduje zmianę wartości obu RV (są zawsze zgodne)
 *
 * WYDAJNOŚĆ: Wydajność tej implementacji jest dość niska.
 * TODO zaimplementować natywną implementację (bez parametrów)
 *
 * @param a pierwsza RV
 * @param b druga RV
 * @tparam T Typ RV
 */
class CrossReactiveVarible[T](a: AbstractReactiveVarible[T], b: AbstractReactiveVarible[T]) {
  def this() = this(new ReactiveVarible[T], new ReactiveVarible[T])

  val _1 = new AbstractReactiveVarible[T] {
    override def onChange(f: (T) => Unit, autostart: Boolean): Unit = b.onChange(f, autostart)
    override def silentUpdate(obj: T): Unit = a.silentUpdate(obj)
    override def update(obj: T): Unit = a.update(obj)
    override def kill: Unit = b.kill //TODO test this method
  }

  val _2 = new AbstractReactiveVarible[T] {
    override def onChange(f: (T) => Unit, autostart: Boolean): Unit = a.onChange(f, autostart)
    override def silentUpdate(obj: T): Unit = b.silentUpdate(obj)
    override def update(obj: T): Unit = b.update(obj)
    override def kill: Unit = a.kill //TODO test this method
  }
}