package org.rebeam

import scala.scalajs.js

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._

import org.rebeam.downshift.Downshift
import org.rebeam.downshift.Downshift._

/**
  * This replicates the downshift multiple selection demo from the material-ui examples,
  * it involves some raw javascript values and functions because it uses the startAdornment
  * property of an Input, via a TextField. It would probably be easier to use a similar
  * approach with a separate container for the chips - this would also flow better.
  */
object DownshiftMultiDemo {

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

  //TODO disiplay ellipsis when there are too many options?
  def getSuggestions(input: String, items: List[String]): List[String] = items.filter(_.toLowerCase.contains(input.toLowerCase)).take(10)

  case class Props(items: List[String])

  case class State(selectedItems: List[String], inputValue: String)

  class Backend(scope: BackendScope[Props, State]) {

    def renderItem(item: String, index: Int, itemProps: js.Object, highlightedIndex: Option[Int], selectedItem: Option[String]): VdomElement = {
      mui.MenuItem(
        key = item,
        selected = highlightedIndex.contains(index),
        component = "div": js.Any,
        // Downshift has provided us with an object containing props to make autocompletion work.
        // We can pass these to additionalProps, and they will be applied as props of the underlying
        // material-ui MenuItem
        additionalProps = itemProps
      )(item)
    }

    // On selection change, add item to selection, and clear input
    private val handleChange = (item: Option[String], c: RenderState[String]) => 
      scope.modState(
        s => s.copy(
          selectedItems = item.map(i => if (s.selectedItems.contains(i)) s.selectedItems else s.selectedItems :+ i).getOrElse(s.selectedItems), 
          inputValue = ""
        )
      ) >> Callback {
        c.openMenu()
        c.setHighlightedIndex(0)
      }

    // On input value change, update inputValue in state
    private val handleInputValueChange = (value: String, c: RenderState[String]) => scope.modState(_.copy(inputValue = value))

    // On clicking delete in a chip, remove the corresponding item from selection
    private def handleDelete(item: String): Callback = 
      scope.modState(s => s.copy(selectedItems = s.selectedItems.filter(_ != item)))

    //Provided directly to JS props, so needs to be a js function that runs immediately
    private val handleKeyDown: js.Function1[ReactKeyboardEvent, Unit] = (e: ReactKeyboardEvent) => {
      if (e.key.toLowerCase == "backspace") {
        scope.modState(s => s.copy(selectedItems = if (s.inputValue.isEmpty) s.selectedItems.dropRight(1) else s.selectedItems))
      } else {
        Callback.empty
      }
    }.runNow

    //TODO
    // 1. mui Styles
    def render(props: Props, state: State) = {

      <.div(
        ^.flexGrow := "1",
        ^.position := "relative",

        Downshift[String](
          itemToString = (i: String) => i.toString,
          onChange = handleChange,
          inputValue = state.inputValue,
          onInputValueChange = handleInputValueChange,
          selectedItem = None            
        )(

          (a: RenderState[String]) => {

            //Chips to display current selected items
            //Provided to a js object, so needs to be a raw node
            val chips = state.selectedItems.toVdomArray(
              item => (
                mui.Chip(
                  key = item,
                  tabIndex = -1: js.Any,
                  label = item: VdomNode,
                  // className={classes.chip}
                  onDelete = handleDelete(item),
                  style = mui.styles.Style(
                    "margin" -> "3px 6px 3px 0px"
                  )
                )
              )
            ).rawNode.asInstanceOf[js.Any]  //TODO why do we need to cast this? Otherwise leads to diverging implicit expansion for type scala.scalajs.js.|.Evidence[A1,Short] when trying to set in literal below

            val openAndHighlightFirst: js.Function0[Unit] = () => {
              a.openMenu()
              a.setHighlightedIndex(0)
            }


            // Get properties for input from Downshift, providing
            // our desired properties as a JS object
            val inputProps = 
              a.getInputProps(
                js.Dynamic.literal(
                  "placeholder" -> "Search for a country",
                  "startAdornment" -> chips,

                  // We need to pass this in here or downshift will override it
                  "onKeyDown" -> handleKeyDown,

                  "onFocus" -> openAndHighlightFirst,

                  // Input should wrap, in case we have a lot of chips
                  "style" -> js.Dynamic.literal(
                    "flexWrap" -> "wrap"
                  ),

                  // This is passed through to the actual <input> used by the
                  // textfield, we set the style so that the input is the same
                  // size as the chips (so that the component doesn't change size
                  // when adding the first chip), and has a better text alignment 
                  // to chips.
                  "inputProps" -> js.Dynamic.literal(
                    "style" -> js.Dynamic.literal(
                      "paddingTop" -> "10px",
                      "paddingBottom" -> "12px",
                    ),
                  ),
                )
              )

            <.div(

              mui.TextField(
                fullWidth = true, 
                InputProps = inputProps
              ),

              mui.Paper(
                square = true,
                style = mui.styles.Style(
                  "position" -> "absolute",
                  "zIndex" -> "1",
                  "marginTop" -> "4px",
                  "left" -> "0",
                  "right" -> "0"
                )
              )(
                getSuggestions(a.inputValue, props.items.filter(i => !state.selectedItems.contains(i))).zipWithIndex.map{
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
        )

      )
      

    }

  }

  //Just make the component constructor - props to be supplied later to make a component
  val ctor = ScalaComponent.builder[Props]("DownshiftDemo")
    .initialState(State(Nil, ""))
    .backend(new Backend(_))
    .render(s => s.backend.render(s.props, s.state))
    .build

}
