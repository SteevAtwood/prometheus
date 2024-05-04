package com.example.application.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;

import java.util.Iterator;
import java.util.Map;

public class JSONDataDisplay {

    public static void buildAccordionFromJson(Accordion accordion, String sectionTitle, JsonNode node) {
        VerticalLayout layout = new VerticalLayout();

        buildLayoutFromJson(layout, node);

        AccordionPanel panel = new AccordionPanel(sectionTitle, layout);
        accordion.add(panel);
    }

    private static void buildLayoutFromJson(VerticalLayout layout, JsonNode node) {
        if (node.isTextual()) {
            layout.add(new Span(node.asText()));
        } else if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();
                JsonNode valueNode = field.getValue();

                if (valueNode.isObject() || valueNode.isArray()) {

                    VerticalLayout nestedLayout = new VerticalLayout();
                    buildLayoutFromJson(nestedLayout, valueNode);
                    layout.add(new AccordionPanel(key, nestedLayout));
                } else {
                    layout.add(new Span(key + ": " + valueNode.asText()));
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.isObject() || item.isArray()) {
                    VerticalLayout nestedLayout = new VerticalLayout();
                    buildLayoutFromJson(nestedLayout, item);
                    layout.add(nestedLayout);
                } else {
                    layout.add(new Span(item.asText()));
                }
            }
        } else {
            layout.add(new Span("Засекреченные данные"));
        }
    }
}
