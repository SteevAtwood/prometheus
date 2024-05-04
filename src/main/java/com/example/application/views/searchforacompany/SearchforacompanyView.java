package com.example.application.views.searchforacompany;

import com.example.application.data.JSONDataDisplay;
import com.example.application.data.SamplePerson;
import com.example.application.services.CompanyService;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

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
            String jsonData = companyService.getData();
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