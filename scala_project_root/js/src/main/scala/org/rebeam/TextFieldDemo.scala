package org.rebeam

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object TextFieldDemo {

  type Props = Unit

  case class State(inputValue: String)

  class Backend(scope: BackendScope[Props, State]) {

    def render(props: Props, state: State) = 
      mui.TextField(
        fullWidth = true, 
        // value = state.inputValue,
        onChange = (e: ReactEventFromInput) => {
          val s = e.target.value
          scope.modState(_.copy(inputValue = s)) >> Callback{println(s"Input '${e.target.value}'")}
        },
        value = state.inputValue
      )  
  }

  //Just make the component constructor - props to be supplied later to make a component
  def ctor = ScalaComponent.builder[Props]("TextFieldDemo")
    .initialState(State(""))
    .backend(new Backend(_))
    .render(s => s.backend.render(s.props, s.state))
    .build

}
