package com.example.application.views.searchforacompany;

import com.example.application.data.JSONDataDisplay;
import com.example.application.services.CompanyService;
import com.example.application.views.MainLayout;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.io.IOException;

@PageTitle("Search for a company")
@Route(value = "grid-with-filters", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class SearchforacompanyView extends VerticalLayout {

    private HorizontalLayout searchLine = new HorizontalLayout();
    private VerticalLayout resultLayout = new VerticalLayout();
    private final CompanyService companyService;

    public SearchforacompanyView(CompanyService companyService) {
        this.companyService = companyService;
        setSizeFull();
        addClassNames("searchforacompany-view");

        createSearchLine();
        resultLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.START);
        resultLayout.setWidthFull();

        add(searchLine);
        add(resultLayout);
    }

    private void createSearchLine() {
        searchLine.setPadding(false);
        searchLine.setSpacing(false);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Введите ИНН");
        searchField.setClearButtonVisible(true);

        Button searchButton = new Button("Получить данные");
        searchButton.addClickListener(e -> {

            String InnParam = searchField.getValue();
            String jsonData = companyService.getData(InnParam);
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode rootNode = objectMapper.readTree(jsonData);
                Accordion accordion = new Accordion();

                JSONDataDisplay.buildAccordionFromJson(accordion, "Результат", rootNode);

                resultLayout.removeAll();
                resultLayout.add(accordion);
            } catch (IOException ex) {
                resultLayout.add(new Span("Ошибка при обработке данных: " + ex.getMessage()));
            }
        });

        searchButton.addClickShortcut(Key.ENTER);
        searchLine.add(searchField, searchButton);
    }
}