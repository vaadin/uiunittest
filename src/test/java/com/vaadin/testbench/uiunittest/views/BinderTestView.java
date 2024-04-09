package com.vaadin.testbench.uiunittest.views;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class BinderTestView extends TestView {
    public static final String NAME = "binder";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getComponent() {
        VerticalLayout layout = new VerticalLayout();
        FormLayout form = new FormLayout();
        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");
        form.addComponents(firstName, lastName);

        Binder<Person> binder = new Binder<>(Person.class);
        binder.forField(firstName).asRequired()
                .withValidator(
                        new StringLengthValidator("Length: 2 - 20!", 2, 20))
                .bind("firstName");
        binder.forField(lastName).asRequired()
                .withValidator(
                        new StringLengthValidator("Length: 2 - 20!", 2, 20))
                .bind("lastName");
        binder.readBean(new Person("John", "Doe"));

        binder.addValueChangeListener(e -> {
            if (e.isUserOriginated()) {
                Notification.show("Value: " + e.getValue().toString());
            }
        });

        Button disable = new Button("Disable");
        disable.addClickListener(e -> {
            form.setEnabled(false);
        });

        Button readOnly = new Button("Read only");
        readOnly.addClickListener(e -> {
            binder.setReadOnly(true);
        });

        layout.addComponents(form, disable, readOnly);
        return layout;
    }

    public static class Person {
        private String firstName;
        private String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}