package com.example.application.views.history;

import com.example.application.data.History;
import com.example.application.services.CompanyService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.persistence.criteria.Predicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@AnonymousAllowed
@Route(value = "history", layout = MainLayout.class)
@RouteAlias(value = "history", layout = MainLayout.class)
@Uses(Icon.class)
public class HistoryView extends Div {

    private Grid<History> grid;
    private Filters filters;
    private final CompanyService companyService;

    public HistoryView(CompanyService companyService) {
        this.companyService = companyService;
        setSizeFull();
        addClassNames("history-view");

        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public static class Filters extends Div implements Specification<History> {

        private final TextField inn = new TextField("INN");
        private final DatePicker startDate = new DatePicker("Start Date");
        private final DatePicker endDate = new DatePicker("End Date");
        private final TextField userId = new TextField("User ID");
        private final TextField fileName = new TextField("File Name");

        public Filters(Runnable onSearch) {

            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);
            inn.setPlaceholder("Введите ИНН");

            HorizontalLayout filterLayout = new HorizontalLayout();
            filterLayout.setSpacing(true);
            filterLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            filterLayout.add(inn, createDateRangeFilter(), userId, fileName);

            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                inn.clear();
                startDate.clear();
                endDate.clear();
                userId.clear();
                fileName.clear();
                onSearch.run();
            });

            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            filterLayout.add(resetBtn, searchBtn);

            add(filterLayout, actions);

        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("From");

            endDate.setPlaceholder("To");

            startDate.setAriaLabel("From date");
            endDate.setAriaLabel("To date");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassNames(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }

        @Override
        public Predicate toPredicate(Root<History> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!inn.isEmpty()) {
                String lowerCaseFilter = inn.getValue().toLowerCase();
                Predicate firstINNMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("INN")),
                        lowerCaseFilter + "%");

                predicates.add(criteriaBuilder.or(firstINNMatch));
            }
            if (startDate != null && startDate.getValue() != null) {
                String databaseColumn = "date";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get(databaseColumn),
                        criteriaBuilder.literal(startDate.getValue())));
            }
            if (endDate != null && endDate.getValue() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("date"),
                        Timestamp.valueOf(endDate.getValue().atStartOfDay().plusDays(1))));
            }

            if (userId != null && !userId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("userId"),
                        Long.parseLong(userId.getValue())));
            }

            if (fileName != null && !fileName.isEmpty()) {
                String ignore = "- ()";
                String lowerCaseFileName = ignoreCharacters(ignore, fileName.getValue().toLowerCase());
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("fileName")),
                        "%" + lowerCaseFileName + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    public static String ignoreCharacters(String characters, String in) {
        String result = in;
        for (int i = 0; i < characters.length(); i++) {
            result = result.replace("" + characters.charAt(i), "");
        }
        return result;
    }

    private Component createGrid() {
        grid = new Grid<>(History.class, false);
        grid.addColumn("INN").setAutoWidth(true);
        grid.addColumn("date").setAutoWidth(true);
        grid.addColumn("userId").setAutoWidth(true);
        grid.addColumn("fileName").setAutoWidth(true);

        grid.setItems(query -> companyService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }
}
