package com.clashofserres.cinematch.frontend.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Movie Search")
@Route("movies")
@Menu(order = 1, icon = LineAwesomeIconUrl.SEARCH_SOLID, title = "Movie Search")
public class MovieSearchView extends VerticalLayout {

	public MovieSearchView() {
		setPadding(true);
		setSpacing(true);

		// Search bar
		TextField searchField = new TextField();
		searchField.setPlaceholder("Search movies...");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setWidthFull();
		// Trigger search on each keystroke
		searchField.setValueChangeMode(ValueChangeMode.EAGER);

		HorizontalLayout searchBar = new HorizontalLayout(searchField);
		searchBar.setAlignItems(Alignment.END);
		searchBar.setWidthFull();

		// Placeholder for results
		VerticalLayout resultsPlaceholder = new VerticalLayout();
		resultsPlaceholder.add("Movie results will appear here…");

		searchField.addValueChangeListener(event -> {
			String query = event.getValue();
			if (query != null && query.length() > 1) {
				// TODO: implement actual movie search
				resultsPlaceholder.removeAll();
				resultsPlaceholder.add("Searching for: " + query + " …");
			} else {
				resultsPlaceholder.removeAll();
				resultsPlaceholder.add("Movie results will appear here…");
			}
		});

		add(searchBar, resultsPlaceholder);
	}
}
