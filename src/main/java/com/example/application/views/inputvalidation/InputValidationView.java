package com.example.application.views.inputvalidation;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Input Validation")
@Route(value = "hello", layout = MainLayout.class)
@Uses(Icon.class)
public class InputValidationView extends Composite<VerticalLayout> {

    public InputValidationView() {
        getContent().setHeightFull();
        getContent().setWidthFull();
    }
}
