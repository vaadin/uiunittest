/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest;

import java.io.Serializable;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.vaadin.testbench.uiunittest.testers.AbstractDateFieldTester;
import com.vaadin.testbench.uiunittest.testers.AbstractFieldTester;
import com.vaadin.testbench.uiunittest.testers.AbstractMultiSelectTester;
import com.vaadin.testbench.uiunittest.testers.AbstractSingleSelectTester;
import com.vaadin.testbench.uiunittest.testers.ButtonTester;
import com.vaadin.testbench.uiunittest.testers.ComboBoxTester;
import com.vaadin.testbench.uiunittest.testers.ComponentTester;
import com.vaadin.testbench.uiunittest.testers.GridTester;
import com.vaadin.testbench.uiunittest.testers.MenuBarTester;
import com.vaadin.testbench.uiunittest.testers.TabSheetTester;
import com.vaadin.testbench.uiunittest.testers.TreeGridTester;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.pro.licensechecker.LicenseChecker;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractMultiSelect;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/**
 * Base class for unit testing complex Vaadin Components and UIs. Mock and tear
 * down methods are un-implemented at this level. Extend this class to create
 * base class for different environments such as CDI and Spring by implementing
 * the methods.
 */
@SuppressWarnings({ "java:S4274", "java:S100" })
public abstract class AbstractUIUnitTest {

    /**
     * Create mocked Vaadin environment with blank UI without Atmosphere
     * support. This is enough for common use cases testing standalone server
     * side components. Session is locked. UI and VaadinSession thread locals
     * are set.
     *
     * @see #tearDown()
     * @see #mockVaadin(UI)
     *
     * @throws ServiceException
     * @return Plank mock UI instance
     */
    public abstract UI mockVaadin() throws ServiceException;

    /**
     * Create mocked Vaadin environment with given UI without Atmosphere
     * support. This is makes possible to test more complex UI logic. Session is
     * locked. UI and VaadinSession thread locals are set.
     *
     * @see #mockVaadin()
     * @see #tearDown()
     *
     * @param ui
     *            UI instance
     * @throws ServiceException
     */
    public abstract void mockVaadin(UI ui) throws ServiceException;

    /**
     * Do clean-up of the mocked Vaadin created with mockVaadin methods. This
     * may be necessary especially if custom UI have side effects.
     *
     * @see #mockVaadin()
     * @see #mockVaadin(UI)
     */
    public abstract void tearDown();

    /**
     * Navigate to a view
     *
     * @param <T>
     *            Target type
     * @param name
     *            The navigation path as a String, can include url parameters
     * @param clazz
     *            The class of the target view
     * @return A view instance
     */
    @SuppressWarnings("unchecked")
    public <T> T navigate(String name, Class<T> clazz) {
        assert (name != null);
        assert (clazz != null);
        Navigator nav = UI.getCurrent().getNavigator();
        assert (nav != null) : "Navigator does not exists";
        nav.navigateTo(name);
        View view = nav.getCurrentView();
        assert (view.getClass().equals(clazz));
        return (T) view;
    }

    /**
     * Find all components of given type recursively within the UI.
     * 
     * @see QueryResult
     *
     * @param <T>
     *            Component type
     * @param clazz
     *            Component class
     * @return QueryResult of components
     */
    @SuppressWarnings("unchecked")
    public <T extends ClientConnector> QueryResult<T> $(Class<T> clazz) {
        assert (clazz != null);
        assert (UI.getCurrent() != null) : "UI has not been setup";
        if (clazz.isAssignableFrom(Window.class)) {
            return new QueryResult<>(
                    (Collection<T>) UI.getCurrent().getWindows());
        }
        if (clazz.equals(Notification.class)) {
            return new QueryResult<>((Collection<T>) UI.getCurrent()
                    .getExtensions().stream()
                    .filter(ext -> ext.getClass().equals(Notification.class))
                    .collect(Collectors.toList()));
        }
        return $(UI.getCurrent(), clazz);
    }

    /**
     * Find all components of given type recursively within the given component
     * container.
     * 
     * @see QueryResult
     *
     * @param <T>
     *            Component type
     * @param container
     *            Component container to search
     * @param clazz
     *            Component class
     * @return QueryResult of components
     */
    @SuppressWarnings("unchecked")
    public <T extends ClientConnector> QueryResult<T> $(HasComponents container,
            Class<T> clazz) {
        assert (container != null && clazz != null);
        QueryResult<T> result = new QueryResult<>();
        Iterator<Component> iter = container.iterator();
        if (container instanceof Grid) {
            @SuppressWarnings("rawtypes")
            Grid grid = (Grid) container;
            if (grid.getEditor().getBinder() != null) {
                List<T> fields = (List<T>) grid.getEditor().getBinder()
                        .getFields()
                        .filter(field -> field.getClass()
                                .isAssignableFrom(clazz))
                        .collect(Collectors.toList());
                result.addAll(fields);
            }
        }
        while (iter.hasNext()) {
            Component component = iter.next();
            if (component.getClass().equals(clazz)) {
                result.add((T) component);
            }
            if (component instanceof HasComponents) {
                result.addAll($((HasComponents) component, clazz));
            }
        }
        return result;
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public ButtonTester test(Button component) {
        return new ButtonTester(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public <T> ComboBoxTester<T> test(ComboBox<T> component) {
        return new ComboBoxTester<>(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public <T extends Temporal & TemporalAdjuster & Serializable & Comparable<? super T>, R extends Enum<R>> AbstractDateFieldTester<T, R> test(
            AbstractDateField<T, R> component) {
        return new AbstractDateFieldTester<>(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public TabSheetTester test(TabSheet component) {
        return new TabSheetTester(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public <T> GridTester<T> test(Grid<T> component) {
        return new GridTester<>(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public <T> TreeGridTester<T> test(TreeGrid<T> component) {
        return new TreeGridTester<>(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public <T> AbstractFieldTester<T> test(AbstractField<T> component) {
        return new AbstractFieldTester<>(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public <T> AbstractSingleSelectTester<T> test(
            AbstractSingleSelect<T> component) {
        return new AbstractSingleSelectTester<>(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public <T> AbstractMultiSelectTester<T> test(
            AbstractMultiSelect<T> component) {
        return new AbstractMultiSelectTester<>(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public MenuBarTester test(MenuBar component) {
        return new MenuBarTester(component);
    }

    /**
     * Perform operations with the component as a user. E.g. if the operation
     * fires an event as an side effect, it has isUserOriginated = true.
     *
     * @param component
     *            The component
     * @return Tester for operations
     */
    public ComponentTester test(AbstractComponent component) {
        return new ComponentTester(component);
    }

    /**
     * Print the component tree for debugging. This includes all components
     * within the UI and all windows. Also notifications are printed.
     * Indentation is used to indicate component hierarchy.
     */
    public void printComponentTree() {
        Utils.printComponentTree();
    }

    /**
     * Utility method that waits while condition is true. Unlocks the mocked
     * session and returns lock after wait ends. This is useful when waiting
     * background thread activity to complete and letting ui.access to happen.
     *
     * @see UI#access(Runnable)
     *
     * @param <T>
     *            Parameter type
     * @param param
     *            Parameter for the predicate
     * @param condition
     *            Boolean predicate, can be lambda expression
     * @param timeout
     *            Wait maximum seconds
     */
    public <T> void waitWhile(T param, Predicate<T> condition, int timeout) {
        assert (param != null);
        assert (condition != null);
        assert (VaadinSession.getCurrent().hasLock());
        timeout = timeout * 10;
        VaadinSession.getCurrent().unlock();
        try {
            int i = 0;
            do {
                try {
                    Thread.sleep(100);
                    i++;
                } catch (InterruptedException e) {
                    // Ignore
                }
            } while (testWaitCondition(param, condition) && i < timeout);
        } finally {
            VaadinSession.getCurrent().lock();
        }
    }

    private <T> boolean testWaitCondition(T param, Predicate<T> condition) {
        boolean result;
        VaadinSession.getCurrent().lock();
        try {
            result = condition.test(param);
        } finally {
            VaadinSession.getCurrent().unlock();
        }
        return result;
    }

    static {
        LicenseChecker.checkLicenseFromStaticBlock("vaadin-testbench", "5.2",
                null);
    }

    /**
     * Result type for component searches.
     *
     * @see #$(Class)
     * @see #$(HasComponents, Class)
     *
     * @param <T>
     *            Component type
     */
    @SuppressWarnings("serial")
    public static class QueryResult<T extends ClientConnector>
            extends ArrayList<T> {
        private static final String NO_CLASS_MATCHES = "No class matches";

        public QueryResult(Collection<T> list) {
            super(list);
        }

        public QueryResult() {
            super();
        }

        /**
         * Find the component by id using exact match. Returns the first
         * matching component by id within the search results assuming ids are
         * unique.
         *
         * @param id
         *            The id as String
         * @return Component instance, can be null
         * @throws AssertionError
         *             if more than one component is found with the given id
         */
        public T id(String id) {
            if (isEmpty()) {
                Utils.printComponentTree();
                throw new AssertionError(NO_CLASS_MATCHES);
            }
            List<T> matching = stream()
                    .filter(c -> ((Component) c).getId() != null
                            && ((Component) c).getId().equals(id))
                    .collect(Collectors.toList());
            if (matching.size() > 1) {
                Utils.printComponentTree();
                throw new AssertionError(
                        "There are more than one component with id " + id);
            }
            return matching.isEmpty() ? null : matching.get(0);
        }

        /**
         * Find components by matching style name, using partial matching.
         * Result can contain many components.
         *
         * @param styleName
         *            Style name as String
         * @return Result set of components
         */
        public QueryResult<T> styleName(String styleName) {
            if (isEmpty()) {
                Utils.printComponentTree();
                throw new AssertionError(NO_CLASS_MATCHES);
            }
            return new QueryResult<>(stream()
                    .filter(c -> ((Component) c).getStyleName() == null ? false
                            : ((Component) c).getStyleName()
                                    .contains(styleName))
                    .collect(Collectors.toList()));
        }

        /**
         * Find components by matching caption, using partial matching. Result
         * can contain many components.
         *
         * @param caption
         *            Caption as String
         * @return Result set of components
         */
        public QueryResult<T> caption(String caption) {
            if (isEmpty()) {
                Utils.printComponentTree();
                throw new AssertionError(NO_CLASS_MATCHES);
            }
            return new QueryResult<>(stream()
                    .filter(c -> ((Component) c).getCaption() == null ? false
                            : ((Component) c).getCaption().contains(caption))
                    .collect(Collectors.toList()));
        }

        /**
         * Return the first component in the list.
         * <p>
         * Note, if you assume there is only one component, use single() method.
         *
         * @return Component, null if the list was empty.
         */
        public T first() {
            if (isEmpty()) {
                return null;
            }
            return get(0);
        }

        /**
         * Return the last component in the list.
         *
         * @return Component, null if the list was empty.
         */
        public T last() {
            if (isEmpty()) {
                return null;
            }
            return get(size() - 1);
        }

        /**
         * Return the only component in the list if it exists, otherwise fail
         * with assertion.
         *
         * @return Component.
         */
        public T single() {
            if (isEmpty()) {
                Utils.printComponentTree();
                throw new AssertionError("There are were no matches");
            } else if (size() > 1) {
                Utils.printComponentTree();
                throw new AssertionError("There are more than one components");
            }
            return get(0);
        }
    }
}
