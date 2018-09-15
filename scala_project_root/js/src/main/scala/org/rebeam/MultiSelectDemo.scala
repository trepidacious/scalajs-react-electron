package org.rebeam

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

/**
  * Demonstrate MultiSelect component
  */
object MultiSelectDemo {

  val MultiSelectCountry = MultiSelect.component[Country]

  case class Country(name: String, code: String)

  val countries: List[Country] = List(    
    Country("Afghanistan",	"AF"),
    Country("Aland Islands",	"AX"),
    Country("Albania",	"AL"),
    Country("Algeria",	"DZ"),
    Country("American Samoa",	"AS"),
    Country("Andorra",	"AD"),
    Country("Angola",	"AO"),
    Country("Anguilla",	"AI"),
    Country("Antarctica",	"AQ"),
    Country("Antigua and Barbuda",	"AG"),
    Country("Argentina",	"AR"),
    Country("Armenia",	"AM"),
    Country("Aruba",	"AW"),
    Country("Australia",	"AU"),
    Country("Austria",	"AT"),
    Country("Azerbaijan",	"AZ"),
    Country("Bahrain",	"BH"),
    Country("Bahamas",	"BS"),
    Country("Bangladesh",	"BD"),
    Country("Barbados",	"BB"),
    Country("Belarus",	"BY"),
    Country("Belgium",	"BE"),
    Country("Belize",	"BZ"),
    Country("Benin",	"BJ"),
    Country("Bermuda",	"BM"),
    Country("Bhutan",	"BT"),
    Country("Bolivia, Plurinational State of",	"BO"),
    Country("Bonaire, Sint Eustatius and Saba",	"BQ"),
    Country("Bosnia and Herzegovina",	"BA"),
    Country("Botswana",	"BW"),
    Country("Bouvet Island", "BV"),
    Country("Brazil",	"BR"),
    Country("British Indian Ocean Territory",	"IO"),
    Country("Brunei Darussalam",	"BN"),
    Country("Bulgaria",	"BG"),
    Country("Burkina Faso",	"BF"),
    Country("Burundi",	"BI"),
    Country("Cambodia",	"KH"),
    Country("Cameroon",	"CM"),
    Country("Canada",	"CA"),
    Country("Cape Verde",	"CV"),
    Country("Cayman Islands",	"KY"),
    Country("Central African Republic",	"CF"),
    Country("Chad",	"TD"),
    Country("Chile",	"CL"),
    Country("China",	"CN")
  )

  case class Props(items: List[Country])

  case class State(selectedItems: List[Country])

  class Backend(scope: BackendScope[Props, State]) {

    // On selection change, update state
    private val handleSelectionChange = (newSelection: List[Country]) => scope.modState(_.copy(selectedItems = newSelection))

    def render(props: Props, state: State) = MultiSelectCountry(
      MultiSelect.Props(
        countries, 
        state.selectedItems, 
        handleSelectionChange,
        (c: Country) => s"${c.name} (${c.code})"
      )
    )

  }

  //Just make the component constructor - props to be supplied later to make a component
  def component = ScalaComponent.builder[Props]("DownshiftDemo")
    .initialState(State(Nil))
    .backend(new Backend(_))
    .render(s => s.backend.render(s.props, s.state))
    .build

}
