package org.rebeam

import scala.scalajs.js

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

import org.rebeam.downshift.Downshift
import org.rebeam.downshift.Downshift._

object DownshiftDemo {

  val countries: List[String] = List (
    "Afghanistan",
    "Aland Islands",
    "Albania",
    "Algeria",
    "American Samoa",
    "Andorra",
    "Angola",
    "Anguilla",
    "Antarctica",
    "Antigua and Barbuda",
    "Argentina",
    "Armenia",
    "Aruba",
    "Australia",
    "Austria",
    "Azerbaijan",
    "Bahamas",
    "Bahrain",
    "Bangladesh",
    "Barbados",
    "Belarus",
    "Belgium",
    "Belize",
    "Benin",
    "Bermuda",
    "Bhutan",
    "Bolivia, Plurinational State of",
    "Bonaire, Sint Eustatius and Saba",
    "Bosnia and Herzegovina",
    "Botswana",
    "Bouvet Island",
    "Brazil",
    "British Indian Ocean Territory",
    "Brunei Darussalam"
  )

  def getSuggestions(input: String, items: List[String]): List[String] = items.filter(_.toLowerCase.contains(input.toLowerCase)).take(5)

  case class Props(items: List[String])

  case class State(selectedItem: Option[String])

  class Backend(scope: BackendScope[Props, State]) {

    def renderItem(item: String, index: Int, itemProps: js.Object, highlightedIndex: Option[Int], selectedItem: Option[String]): VdomElement = {
      mui.MenuItem(
        key = item,
        selected = highlightedIndex.contains(index),
        component = "div": js.Any,

        // Downshift has provided us with an object containing props to make autocompletion work.
        // We can pass these to additionalProps, and they will be applied as props of the underlying
        // material-ui MenuItem
        additionalProps = itemProps,

        // Additional style for selection
        style = mui.styles.Style(
          "fontWeight" -> (if (selectedItem.contains(item)) "500" else "400")
        )
      )(item)
    }

    //TODO
    // 1. mui Styles
    // 2. Multi-selection demo with chips

    def render(props: Props, state: State) = {

      <.div(
        Downshift[String](
          itemToString = (i: String) => i.toString,

          selectedItem = state.selectedItem,
          onChange = (item: Option[String], c: RenderState[String]) => 
            Callback{println(s"onChange, item $item")} >> scope.modState(_.copy(selectedItem = item)),
        )(

          (a: RenderState[String]) => {

            // Get properties for input from Downshift, providing
            // our desired properties as a JS object
            val inputProps = 
              a.getInputProps(
                js.Dynamic.literal(
                  placeholder = "Search for a country"
                )
              )

            js.Dynamic.global.console.log(inputProps)

            <.div(

              mui.TextField(
                fullWidth = true, 
                InputProps = inputProps
              ),

              mui.Paper(square = true)(
                getSuggestions(a.inputValue, props.items).zipWithIndex.map{
                  case (item, index) => renderItem(
                    item, 
                    index,
                    a.getItemProps(ItemData(item, index, disabled = false)),
                    a.highlightedIndex,
                    a.selectedItem
                  )
                }: _*
              ).when(a.isOpen)

            )

          }
        ),

        <.span(state.selectedItem.getOrElse("No selection"): String)
      )

    }

  }

  //Just make the component constructor - props to be supplied later to make a component
  def ctor = ScalaComponent.builder[Props]("DownshiftDemo")
    .initialState(State(None))
    .backend(new Backend(_))
    .render(s => s.backend.render(s.props, s.state))
    .build

}
