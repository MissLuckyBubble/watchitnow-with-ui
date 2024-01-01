package com.example.views.register;

import com.example.models.User;
import com.example.services.UserService;
import com.example.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.component.confirmdialog.*;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@AnonymousAllowed
@PageTitle("Register")
@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends Main implements HasComponents, HasStyle {

    private User user;

    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    private TextField usernameField = new TextField("Username");
    private PasswordField passwordField = new PasswordField("Password");
    private PasswordField confirmPasswordFiled = new PasswordField("Confirm Password");
    private EmailField emailField = new EmailField("Email");
    private BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);


    @Autowired
    public RegisterView(User user, UserService userService, PasswordEncoder passwordEncoder) {
        this.user = user;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        initContent();
    }

    private void initContent() {
        Div headerLayout = new Div();
        H1 header = new H1("Register");
        headerLayout.add(header);

        FormLayout formLayout = getFormLayout();



        Div loginLinkDiv = new Div();
        Anchor loginLink = new Anchor("login", "Already have an account? Login.");
        loginLinkDiv.add(loginLink);

        VerticalLayout centerLayout = new VerticalLayout();
        centerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        centerLayout.add(header, formLayout, loginLinkDiv);

        centerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        centerLayout.setAlignSelf(FlexComponent.Alignment.CENTER, formLayout);
        add(centerLayout);
    }

    private FormLayout getFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("50%");

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("100%", 2));

        binder.forField(confirmPasswordFiled);
        formLayout.add(usernameField,emailField,passwordField,confirmPasswordFiled);
        formLayout.setColspan(usernameField, 2);
        formLayout.setColspan(emailField, 2);


        submitButton(usernameField.getValue(), passwordField.getValue(), confirmPasswordFiled.getValue(), emailField.getValue(), formLayout);

        return formLayout;
    }

    private void submitButton(String username, String password, String confirmPassword, String email, FormLayout formLayout) {
        Button registerButton = new Button("Register", event -> {

            Notification.show("Registration successful for: " + username);
        });
        formLayout.add(registerButton);
        formLayout.setColspan(registerButton, 2);
    }

}
