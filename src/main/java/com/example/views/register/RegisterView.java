package com.example.views.register;

import com.example.data.Role;
import com.example.models.User;
import com.example.services.UserService;
import com.example.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

@AnonymousAllowed
@PageTitle("Register")
@Route(value = "register")
public class RegisterView extends HorizontalLayout {

    private final User user;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private TextField usernameField = new TextField("Username");
    private PasswordField passwordField = new PasswordField("Password");
    private PasswordField confirmPasswordField = new PasswordField("Confirm Password");
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

        binder.forField(confirmPasswordField).asRequired()
                .withValidator(Validator.from(v-> v != null
                        && Objects.equals(v, passwordField.getValue()),"Passwords must match")).bind(User::getPassword, User::setPassword);

        binder.forField(usernameField)
                .asRequired("Username is required")
                .withValidator(v -> v != null && !v.trim().isEmpty(), "Provide a non-empty username")
                .bind(User::getUsername, User::setUsername);

        binder.forField(emailField)
                .asRequired("Email is required")
                .withValidator(v -> v != null && !v.trim().isEmpty(), "Provide a non-empty email address")
                .withValidator(v -> isValidEmail(v), "Invalid email address")
                .bind(User::getEmail, User::setEmail);

        formLayout.add(usernameField,emailField,passwordField,confirmPasswordField);
        formLayout.setColspan(usernameField, 2);
        formLayout.setColspan(emailField, 2);
        formLayout.setColspan(passwordField, 1);
        formLayout.setColspan(confirmPasswordField, 1);

        binder.bindInstanceFields(this);

        Button saveButton = new Button("Register", e->saveUser() );
        formLayout.add(saveButton);
        formLayout.setColspan(saveButton,2);
        binder.setBean(user);
        return formLayout;
    }

    private void saveUser(){
        if (binder.isValid()) {
            try {
                User newUser = new User();  // Create a new instance
                binder.writeBean(newUser);
                newUser.setRoles(Collections.singleton(Role.USER));
                newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
                userService.create(newUser);
                Notification.show("Registration successful for: " + newUser.getUsername());
                UI.getCurrent().navigate(LoginView.class);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        } else {
            Notification.show("Please fix the validation errors.");
        }
    }

    private boolean isValidEmail(String email) {
        // Use a regular expression for basic email validation
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
