# Spring Support for UIUnitTest PRD

## Purpose

Add first-class, optional Spring support to UIUnitTest for Vaadin 8 so Spring Boot based Vaadin projects can write UI unit tests with minimal boilerplate. The primary reference use case is refactoring the Bakery starter test base to depend on reusable UIUnitTest base classes instead of maintaining its own Spring/Vaadin bootstrap code.

## Goals

- Provide a generic `SpringUIUnitTest` base for Vaadin 8 applications using Vaadin Spring and Spring Boot.
- Provide an optional `SpringSecurityUIUnitTest` base with convenient authentication helpers for projects that use Spring Security.
- Keep all Spring and Spring Security dependencies `provided` and `optional` so non-Spring projects do not get them transitively on the classpath, in WAR files, or in other packaging outputs.
- Make the common test authoring experience easy: configure the application once in a small project-specific base class, then use `mockVaadin()`, `navigate(MyView.class)`, component queries, and testers in individual tests.
- Add a small Spring test UI fixture to verify Spring context creation, Vaadin Spring view navigation, component autowiring, presenter autowiring, and singleton application service wiring.

## Non-Goals

- Do not make Spring support part of the existing vanilla `UIUnitTest` execution path.
- Do not require Spring or Spring Security dependencies for users testing non-Spring Vaadin applications.
- Do not embed Bakery-specific application classes, role names, users, or security defaults in UIUnitTest.
- Do not attempt to replace Spring Boot's full integration test stack; this feature is for fast server-side Vaadin UI unit tests.

## Target User Experience

Spring Vaadin projects should define one small project-specific test base:

```java
public abstract class AbstractUITest extends SpringSecurityUIUnitTest {

    @Override
    protected Class<?>[] getConfigurationClasses() {
        return new Class<?>[] { Application.class };
    }

    @Override
    protected Class<? extends UI> getUiClass() {
        return AppUI.class;
    }

    @Override
    protected void configureSecurityContext() {
        authenticateAsAdmin();
    }

    protected void authenticateAsAdmin() {
        authenticate("admin@vaadin.com", Role.ADMIN);
    }
}
```

Individual tests should remain small:

```java
public class OrdersViewTest extends AbstractUITest {

    @Before
    public void setup() throws ServiceException {
        mockVaadin();
    }

    @After
    public void cleanup() {
        tearDown();
    }

    @Test
    public void adminCanOpenOrders() {
        OrdersView view = navigate(OrdersView.class);
        assertNotNull(view);
    }
}
```

## Public API

### `SpringUIUnitTest`

Package: `com.vaadin.testbench.uiunittest`

Extends: `AbstractUIUnitTest`

Responsibilities:

- Create and refresh an `AnnotationConfigWebApplicationContext`.
- Store the context in the mock servlet context using `WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE`.
- Create a Spring-aware Vaadin servlet service using `SpringVaadinServletService` and `SpringVaadinServlet`.
- Create mock HTTP session, Vaadin session, request, response, and current Vaadin instances.
- Use `SpringUIProvider` to create Spring-managed UI instances when possible.
- Support explicit UI initialization through `mockVaadin(UI ui)`.
- Initialize push configuration for `@Push` UIs.
- Run Spring Boot `ApplicationRunner` and `CommandLineRunner` beans by default.
- Clean up UI, Vaadin session, Vaadin service, request/response thread locals, and Spring application context in `tearDown()`.

Required project hook:

```java
protected abstract Class<?>[] getConfigurationClasses();
```

Convenience hooks:

```java
protected Class<? extends UI> getUiClass();

protected void configureContext(AnnotationConfigWebApplicationContext context);

protected void beforeMockVaadin();

protected void afterContextRefresh(AnnotationConfigWebApplicationContext context);

protected boolean runSpringBootStartupRunners();
```

Navigation helper:

```java
protected <T extends View> T navigate(Class<T> viewClass);
```

This helper requires `@SpringView`, derives the view name with Vaadin Spring conventions, and delegates to the existing `navigate(String, Class<T>)` implementation.

### `SpringSecurityUIUnitTest`

Package: `com.vaadin.testbench.uiunittest`

Extends: `SpringUIUnitTest`

Responsibilities:

- Provide a generic authentication helper for Spring Security based projects.
- Clear Spring Security context during teardown.
- Keep application-specific role helpers in downstream projects.

Public/protected helper shape:

```java
protected void configureSecurityContext();

protected void authenticate(String username, String... authorities);
```

`SpringSecurityUIUnitTest` should call `configureSecurityContext()` from its `beforeMockVaadin()` implementation. The default implementation is no-op.

## Dependency Requirements

Add Spring dependencies to `pom.xml` as `provided` and `optional`:

- `com.vaadin:vaadin-spring`
- `org.springframework:spring-web`
- `org.springframework.boot:spring-boot`
- `org.springframework.security:spring-security-core`, only for `SpringSecurityUIUnitTest`

The project currently targets Java 8 and Vaadin 8.14.3. If Spring Boot dependency management is added, use a Java 8 compatible Boot line such as Spring Boot 2.7.x.

Test-scope dependencies may be added as needed to compile and run the Spring fixture tests, but the published library must not make Spring dependencies mandatory for non-Spring users.

## Test Fixture

Add a small Spring-based test UI under test sources, separate from the existing non-Spring `TestUI` and `TestView` classes.

Suggested package:

```text
src/test/java/com/vaadin/testbench/uiunittest/spring
```

Suggested fixture classes:

- `SpringTestConfiguration`
- `SpringTestUI`
- `TestView`
- `TestPresenter`
- `ApplicationScopedMockService`
- `AutowiredTestButton`

Fixture behavior:

- `SpringTestUI` is a Spring-managed Vaadin UI and wires Vaadin Spring view navigation.
- `TestView` is a `@SpringView`.
- `TestView` receives a `TestPresenter` through Spring autowiring.
- `TestView` also receives one autowired Vaadin component, such as `AutowiredTestButton`.
- `TestPresenter` receives `ApplicationScopedMockService` through Spring autowiring.
- User interaction with the autowired component should update visible UI state through the presenter/service so tests can verify the full chain.

## Integration Tests

Add JUnit 4 tests that extend the new base classes and verify:

- `mockVaadin()` creates a Spring-managed UI.
- `mockVaadin(UI ui)` still supports manually provided UI instances.
- `navigate(TestView.class)` works with `@SpringView`.
- The view receives its presenter through Spring.
- The presenter receives the application-scoped mock service through Spring.
- The view receives an autowired Vaadin component through Spring.
- Component query and tester helpers from `AbstractUIUnitTest` work with the Spring-created UI.
- `ApplicationRunner` and `CommandLineRunner` beans are run when enabled.
- `tearDown()` clears Vaadin current instances and closes the Spring application context.
- `SpringSecurityUIUnitTest.authenticate(...)` sets a Spring Security authentication and teardown clears it.

## Documentation

Update the README with:

- A short section for Spring Vaadin projects.
- Maven dependency guidance explaining that Spring dependencies are optional/provided in UIUnitTest and must be present in the consuming project's test classpath.
- A minimal `SpringUIUnitTest` project base example.
- A minimal `SpringSecurityUIUnitTest` project base example using `authenticate(...)`.
- A Bakery-style migration note showing that application-specific role helpers belong in the downstream project base.

## Bakery Migration Target

After this feature is released, the Bakery starter should be able to replace its custom `AbstractUITest` Spring bootstrap logic with a small subclass of `SpringSecurityUIUnitTest`.

The remaining Bakery-specific base should only define:

- Application configuration classes.
- Default UI class.
- Default security context behavior.
- Bakery role helper methods such as `authenticateAsAdmin()`, `authenticateAsBaker()`, and `authenticateAsBarista()`.

All generic servlet context, Spring context, Vaadin service, UI provider, startup runner, and teardown behavior should live in UIUnitTest.

## Acceptance Criteria

- Existing non-Spring tests continue to pass unchanged.
- Non-Spring consumers do not receive Spring dependencies transitively.
- Spring fixture tests prove view, presenter, service, and Vaadin component autowiring.
- Spring fixture tests prove `navigate(Class<T>)` works for `@SpringView` classes.
- Security fixture tests prove `authenticate(username, authorities...)` works without adding Bakery-specific concepts to UIUnitTest.
- README examples are sufficient to create a project-specific Spring UI test base without copying low-level bootstrap code.
- The Bakery project can be refactored to a thin application-specific test base on top of the new UIUnitTest classes.

## Risks and Mitigations

- Spring optional dependencies can still make compilation fragile if versions drift. Mitigate by using Java 8 compatible Spring Boot dependency management and test coverage in this repository.
- Vaadin Spring UI/view creation has several supported application patterns. Mitigate by keeping `getUiClass()` as a fallback and allowing context customization hooks.
- Spring Security should not become mandatory. Mitigate by keeping security support in `SpringSecurityUIUnitTest` and marking Spring Security dependencies optional/provided.
- Startup runners may have side effects in some applications. Mitigate with `runSpringBootStartupRunners()` so projects can disable them.

## Open Questions

- Should `SpringMockServletContext` become a reusable public mock class, or remain an implementation detail inside `SpringUIUnitTest` initially?
- Should `configureContext(...)` support active profiles directly, or should projects set profiles on the context in the hook?
- Should the security helper accept already-created `Authentication` instances in addition to username/authority strings?