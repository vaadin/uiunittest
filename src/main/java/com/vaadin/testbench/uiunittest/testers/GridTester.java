/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.data.ValueProvider;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.testbench.uiunittest.Utils;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.components.grid.SingleSelectionModelImpl;

public class GridTester<T> extends Tester<Grid<T>> {

    public GridTester(Grid<T> grid) {
        super(grid);
    }

    /**
     * Return the content of the cell. If ComponentRenderer was used it is the
     * Component produced by the renderer otherwise it is the value.
     *
     * @param column
     *            Column index
     * @param row
     *            The row index
     * @return Cell content
     */
    public Object cell(int column, int row) {
        assert (column > -1 && column < getComponent().getColumns()
                .size()) : "Column out of bounds";
        assert (row > -1 && row < size()) : "Row out of bounds";
        T cat = (T) item(row);
        ValueProvider<T, ?> vp = getComponent().getColumns().get(column)
                .getValueProvider();
        Object content = vp.apply(cat);
        if (content instanceof AbstractComponent) {
            AbstractComponent component = ((AbstractComponent) content);
            component.setParent(getComponent());
            component.attach();
        }
        return vp.apply(cat);
    }

    /**
     * Return data item of the row.
     *
     * @param row
     *            Row index
     * @return The item
     */
    public T item(int row) {
        assert (row > -1 && row < size()) : "Row out of bounds";
        return getComponent().getDataCommunicator().fetchItemsWithRange(row, 1)
                .get(0);
    }

    /**
     * Return the total amount of rows as reported by DataProvider.
     *
     * @return int value
     */
    public int size() {
        return getComponent().getDataCommunicator().getDataProviderSize();
    }

    /**
     * Simulate click in given cell. Will trigger ItemClick event as a user. If
     * selection mode is Single, selection is updated accordingly.
     *
     * @param column
     *            Column index
     * @param row
     *            Row index
     */
    public void click(int column, int row) {
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        assert (column > -1 && column < getComponent().getColumns()
                .size()) : "Column out of bounds";
        assert (row > -1 && row < size()) : "Row out of bounds";
        T item = item(row);
        MouseEventDetails details = new MouseEventDetails();
        details.setButton(MouseButton.LEFT);
        Grid.ItemClick<T> event = new Grid.ItemClick<T>(getComponent(),
                getComponent().getColumns().get(column), item, details, row);
        fireSimulatedEvent(event);
        if (getComponent()
                .getSelectionModel() instanceof SingleSelectionModel) {
            if (getComponent().getSelectedItems().contains(item)) {
                deselect(item);
            } else {
                select(item);
            }
        }
    }

    /**
     * Simulate clicking of the multiselect checkbox column of the Grid as a
     * user on a given item.
     * 
     * @param row
     *            The row index
     */
    public void clickToSelect(int row) {
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        assert ((getComponent()
                .getSelectionModel() instanceof MultiSelectionModel)) : "Grid is not in multiselect mode";
        T item = item(row);
        clickToSelect(item);
    }

    /**
     * Simulate clicking of the multiselect checkbox column of the Grid as a
     * user on a given item.
     *
     * @param item
     *            Item
     */
    public void clickToSelect(T item) {
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        assert ((getComponent()
                .getSelectionModel() instanceof MultiSelectionModel)) : "Grid is not in multiselect mode";
        if (getComponent().getSelectedItems().contains(item)) {
            deselect(Utils.setOfItems(item));
        } else {
            select(Utils.setOfItems(item));
        }
    }

    protected void select(Set<T> items) {
        assert (getComponent()
                .getSelectionModel() instanceof MultiSelectionModel) : "Grid is not multiselect";
        assert (items != null) : "Items can't be null";
        Set<T> copy = getComponent().getSelectedItems().stream()
                .map(Objects::requireNonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        copy.addAll(items);
        Set<T> removed = new LinkedHashSet<>(getComponent().getSelectedItems());
        updateSelection(copy, removed);
    }

    protected void deselect(Set<T> items) {
        assert (getComponent()
                .getSelectionModel() instanceof MultiSelectionModel) : "Grid is not multiselect";
        assert (items != null) : "Items can't be null";
        Set<T> copy = getComponent().getSelectedItems().stream()
                .map(Objects::requireNonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        copy.removeAll(items);
        Set<T> removed = new LinkedHashSet<>(getComponent().getSelectedItems());
        updateSelection(copy, removed);
    }

    private void updateSelection(Set<T> copy, Set<T> removed) {
        MultiSelectionModelImpl<T> model = (MultiSelectionModelImpl<T>) getComponent()
                .getSelectionModel();
        Class<?> clazz = model.getClass();
        try {
            Method updateSelectionMethod = clazz.getDeclaredMethod(
                    "updateSelection", Set.class, Set.class, Boolean.TYPE);
            updateSelectionMethod.setAccessible(true);
            updateSelectionMethod.invoke(model, copy, removed, true);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected void select(T item) {
        assert (getComponent()
                .isEnabled()) : "Can't interact with disabled Grid";
        assert (getComponent()
                .getSelectionModel() instanceof SingleSelectionModel) : "Grid is not singleselect";
        String key = getComponent().getDataCommunicator().getKeyMapper()
                .key(item);
        SingleSelectionModelImpl<T> model = (SingleSelectionModelImpl<T>) getComponent()
                .getSelectionModel();
        Class<?> clazz = model.getClass();
        try {
            Method setSelectedFromClientMethod = clazz
                    .getDeclaredMethod("setSelectedFromClient", String.class);
            setSelectedFromClientMethod.setAccessible(true);
            setSelectedFromClientMethod.invoke(model, key);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    protected void deselect(T item) {
        item = null;
        select(item);
    }

    @Override
    protected Grid<T> getComponent() {
        return super.getComponent();
    }
}
