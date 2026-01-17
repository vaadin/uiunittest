# UIUnitTest for Vaadin 8

This is an add-on library for Vaadin TestBench 5 for UI Unit Testing Vaadin 8 views and components. The API design is similar to UI Unit Testing for Vaadin 23 and 24.

## User manual

### Add dependency to the project

First you need to add dependency to the library in your project. Use test scope for the dependency. Check what is the latest actual version in the Directory.

```
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-testbench</artifactId>
    <version>5.4.1</version>
    <scope>test</scope>
</dependency>
```

### Create unit test

Then start writting unit tests for the application of yours. Below is a sample for a simple hello world application.

```
package org.myapp.tests;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ServiceException;
import com.vaadin.testbench.uiunittest.TestUI;
import com.vaadin.testbench.uiunittest.UIUnitTest;
import com.vaadin.testbench.uiunittest.views.TreeGridTestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeGrid;

/**
 * This class contains basic tests for the UI components of the application.
 */
public class BasicTest extends UIUnitTest {

    private MyUI ui;

    /**
     * Setup method that is executed before each test case.
     * It initializes the UI and mocks the Vaadin framework.
     *
     * @throws ServiceException if there is an error in the service.
     */
    @Before
    public void setup() throws ServiceException {
        ui = new MyUI();
        mockVaadin(ui);
    }

    /**
     * Cleanup method that is executed after each test case.
     * It tears down the test environment.
     */
    @After
    public void cleanup() {
        tearDown();
    }

    /**
     * Test case to verify the default behavior of the UI.
     * It sets a value in a TextField, clicks a Button, and checks the result.
     */
    @Test
    public void defaultTest() {
        test($(TextField.class).single()).setValue("John");
        test($(Button.class).caption("Greet").first()).click();
        assertEquals("Hello John!", test($(Notification.class).last()).getCaption());
    }
}
```
### Testing shortcuts

You can simulate keyboard shortcuts on the active view and verify the side effects without a browser. The `test(view).shortcut(...)` helper sends a shortcut event directly to the UI.

For example, the `ShortcutView` reacts to Page Up / Page Down and updates a label:

```java
@Test
public void shortcut_triggers_actions() {
    TestUI ui = new TestUI();
    mockVaadin(ui);
    ShortcutView view = navigate(ShortcutView.NAME, ShortcutView.class);

    // Simulate pressing Page Down
    test(view).shortcut(KeyCode.PAGE_DOWN);
    assertEquals("Next", $(Label.class).id("next").getValue());

    // Simulate pressing Page Up
    test(view).shortcut(KeyCode.PAGE_UP);
    assertEquals("Previous", $(Label.class).id("previous").getValue());
}
```

This pattern works for any global shortcut registered in your view.

### Waiting push updates

For push-enabled UIs you often need to wait until a background task has updated the UI. Use `waitWhile(component, condition, timeoutSeconds)` to block the test until the server-side update has completed.

In `PushTestView` a button starts an async task, shows a spinner style on a label and finally updates its value:

```java
@Test
public void wait_for_push_result() {
    TestUI ui = new TestUI();
    mockVaadin(ui);
    navigate(PushTestView.NAME, PushTestView.class);

    Label label = $(Label.class).id("push-label");
    Button button = $(Button.class).id("spin-button");

    test(button).click();
    assertFalse(button.isEnabled());

    // Wait until the push update has removed the spinner style
    waitWhile(label,
            l -> l.getStyleName().contains(ValoTheme.LABEL_SPINNER), 10);

    assertEquals("Hello", label.getValue());
    assertTrue(button.isEnabled());
}
```

This allows you to deterministically test push flows without Thread.sleep or a real browser.

### Testing Grid

The Grid tester provides high-level helpers for selection, clicking cells, editing component columns, sorting, column visibility and editor operations. You still use normal component queries to access nested components inside cells.

Below is a simplified example based on `GridTestView` and `GridEditorTestView`:

```java
@Test
public void grid_selection_and_editing() {
    TestUI ui = new TestUI();
    mockVaadin(ui);
    GridTestView view = navigate(GridTestView.NAME, GridTestView.class);

    Grid<GridTestView.Bean> grid = $(Grid.class).single();

    // Change selection mode and select two rows
    test($(RadioButtonGroup.class).caption("Mode").first())
            .clickItem(SelectionMode.MULTI);
    test(grid).clickToSelect(0);
    test(grid).clickToSelect(1);
    assertEquals(2, grid.getSelectedItems().size());

    // Work with a component column (TextField + Button) in a cell
    HorizontalLayout layout = (HorizontalLayout) test(grid).cell(1, 0);
    test($(layout, TextField.class).single()).setValue("New value");
    test($(layout, Button.class).single()).click();

    // Sort and toggle column visibility
    test(grid).toggleColumnSorting(0);        // uses Grid sort API
    test(grid).toggleColumnVisibility("VALUE");
}
```

For editor-enabled grids you can additionally use `test(grid).edit(row)`, `save()`, `cancel()` and `editorOpen()` as shown in `GridEditorTest`.

## Development instructions

### Important Files 
* com.vaadin.testbench.uiunittest.UIUniteTest.java: This is the base class you should extend in Unit tests.
* com.vaadin.testbench.uiunittest.AbstractUIUniteTest.java: The base class implementing component queries and registering component testers.
* com.vaadin.testbench.uiunittest.mocks: Mock / fake Vaadin environment enabling running the application or components under tests.
* com.vaadin.testbench.uiunittest.testers: A compact set of component helpers with methods to interact with components as a user with desired side effects.
* com.vaadin.testbench.uiunittest.testers.Tester.java: The base class for all the component testers.
* assembly/: this folder includes configuration for packaging the project into a JAR so that it works well with other Vaadin projects and the Vaadin Directory. There is usually no need to modify these files, unless you need to add JAR manifest entries.

### Running the test UI

Starting the test/demo server:
```
mvn jetty:run -Prun
```

This deploys demo at http://localhost:8080

### Branching information

* `master` the latest version of the starter, using latest stable platform version


## Publishing to Vaadin Directory

You can create the zip package needed for [Vaadin Directory](https://vaadin.com/directory/) using

```
mvn versions:set -DnewVersion=1.0.0 # You cannot publish snapshot versions 
mvn install -Pdirectory
```

The package is created as `target/uiunittest-1.0.0.zip`

For more information or to upload the package, visit https://vaadin.com/directory/my-components?uploadNewComponent