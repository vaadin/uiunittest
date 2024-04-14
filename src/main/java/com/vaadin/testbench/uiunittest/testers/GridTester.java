/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.testers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.event.EventRouter;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.ColumnState;
import com.vaadin.testbench.uiunittest.Utils;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.EditorCancelEvent;
import com.vaadin.ui.components.grid.EditorImpl;
import com.vaadin.ui.components.grid.EditorOpenEvent;
import com.vaadin.ui.components.grid.EditorSaveEvent;
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
     * Component produced by the renderer otherwise it is the value. Asserts
     * that column is visible.
     *
     * @param column
     *            Column index including hidden columns
     * @param row
     *            The row index
     * @return Cell content
     */
    public Object cell(int column, int row) {
        assert (column > -1 && column < getComponent().getColumns()
                .size()) : "Column out of bounds";
        assert (row > -1 && row < size()) : "Row out of bounds";
        assert (!getComponent().getColumns().get(column)
                .isHidden()) : "The column is hidden";
        T cat = (T) item(row);
        ValueProvider<T, ?> vp = getComponent().getColumns().get(column)
                .getValueProvider();
        Object content = vp.apply(cat);
        return content;
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
     * selection mode is Single, selection is updated accordingly. Asserts that
     * column is visible.
     *
     * @param column
     *            Column index including hidden columns
     * @param row
     *            Row index
     */
    public void click(int column, int row) {
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        assert (column > -1 && column < getComponent().getColumns()
                .size()) : "Column out of bounds";
        assert (row > -1 && row < size()) : "Row out of bounds";
        assert (!getComponent().getColumns().get(column)
                .isHidden()) : "The column is hidden";
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

    /**
     * Simulate user opening the editor if Grid and Editor are interactable.
     * This will fire EditorOpenEvent and populate the editor with the item.
     * <p>
     * <em>Note</em>: There is a limitation. Programmatic opening of the editor
     * from application logic will require client round trip, which is not
     * possible in Unit testing. E.g. if you have ItemClickListener that opens
     * the editor, clicking the item will do nothing.
     *
     * @see #editorOpen()
     * @see #cancel()
     * @see #save()
     *
     * @param row
     *            Row index to start the editor
     */
    public void edit(int row) {
        Grid<T> grid = getComponent();
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        assert (getComponent().getEditor().isEnabled()) : "Editor is disabled";
        T editing = item(row);
        if (grid.getEditor().isBuffered()) {
            grid.getEditor().editRow(row);
            grid.getEditor().getBinder().readBean(editing);
        } else {
            grid.getEditor().getBinder().setBean(editing);
        }
        fireEditorEvent(new EditorOpenEvent<T>(grid.getEditor(), editing));
        setEdited(editing);
    }

    private void setEdited(T edited) {
        Grid<T> grid = getComponent();
        EditorImpl<T> editor = (EditorImpl<T>) grid.getEditor();
        @SuppressWarnings("rawtypes")
        Class<? extends EditorImpl> clazz = editor.getClass();
        try {
            Field editedField = clazz.getDeclaredField("edited");
            editedField.setAccessible(true);
            editedField.set(editor, edited);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private T getEdited() {
        Grid<T> grid = getComponent();
        EditorImpl<T> editor = (EditorImpl<T>) grid.getEditor();
        Class<? extends EditorImpl> clazz = editor.getClass();
        try {
            Field editedField = clazz.getDeclaredField("edited");
            editedField.setAccessible(true);
            return (T) editedField.get(editor);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the open state of the Editor.
     *
     * @see #edit(int)
     * @see #save()
     * @see #cancel()
     *
     * @return boolean value
     */
    public boolean editorOpen() {
        return getComponent().getEditor().isOpen();
    }

    /**
     * Simulates clicking save button of the editor. If Editor is in buffered
     * mode, bean is written if is valid. Validation errors will prevent editor
     * saving and closing. If save is success, Editor is closed and
     * EditorSaveEvent will be fired.
     * <p>
     * <em>Note</em>: There is a limitation. Programmatic saving of the editor
     * from application logic will require client round trip, which is not
     * possible in Unit testing. E.g. if you have logic that force saves the
     * editor, it will do nothing.
     *
     * @see #edit(int)
     * @see #cancel()
     */
    public void save() {
        Grid<T> grid = getComponent();
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        assert (getComponent().getEditor().isEnabled()) : "Editor is disabled";
        assert (editorOpen()) : "Editor is closed";
        T editing = getEdited();
        if (grid.getEditor().isBuffered()) {
            try {
                grid.getEditor().getBinder().writeBean(editing);
            } catch (ValidationException e) {
            }
        }
        fireEditorEvent(new EditorSaveEvent<T>(grid.getEditor(), editing));
        grid.getDataProvider().refreshItem(editing);
        setEdited(null);
    }

    /**
     * Simulates clicking cancel button of the editor. If Editor is in buffered
     * mode, bean is not written and Grid is not updated. Editor is closed.
     *
     * @see #editorOpen()
     * @see #edit(int)
     * @see #save()
     */
    public void cancel() {
        Grid<T> grid = getComponent();
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        assert (getComponent().getEditor().isEnabled()) : "Editor is disabled";
        assert (editorOpen()) : "Editor is closed";
        T editing = getEdited();
        fireEditorEvent(new EditorCancelEvent<T>(grid.getEditor(), editing));
        setEdited(null);
    }

    /**
     * Toggles the visibility of the given column by index. The
     * ColumnVisibilityChangeEvent fired will have userOriginated = true.
     *
     * @param columnIndex
     *            int
     */
    public void toggleColumnVisibility(int columnIndex) {
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        Grid<T> grid = getComponent();
        assert (columnIndex < grid.getColumns().size()
                && columnIndex > -1) : "Column index out of bounds";
        Column<T, ?> column = grid.getColumns().get(columnIndex);
        assert column
                .isHidable() : "Column hiding is not enabled for this column";
        boolean hidden = column.isHidden();
        Class<?> clazz = column.getClass();
        try {
            Method getStateMethod = clazz.getDeclaredMethod("getState");
            getStateMethod.setAccessible(true);
            ColumnState state = (ColumnState) getStateMethod.invoke(column);
            state.hidden = !hidden;
            grid.fireColumnVisibilityChangeEvent(column, !hidden, true);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggles the visibility of the given column by caption value. The
     * ColumnVisibilityChangeEvent fired will have userOriginated = true.
     * 
     * @param caption
     *            The caption as string
     */
    public void toggleColumnVisibility(String caption) {
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        Grid<T> grid = getComponent();
        for (int i = 0; i < grid.getColumns().size(); i++) {
            Column<T, ?> column = grid.getColumns().get(i);
            String hidingCaption = column.getHidingToggleCaption() == null
                    ? column.getCaption()
                    : column.getHidingToggleCaption();
            if (hidingCaption.equals(caption)) {
                toggleColumnVisibility(i);
                return;
            }
        }
        assert (false) : "No match for the given caption";
    }

    /**
     * Simulate toggling of the sorting direction of the column. Asserts that
     * column is visible. SortEvent fired will have userOriginated = true.
     *
     * @param columnIndex
     *            Index of the column including hidden columns.
     */
    public void toggleColumnSorting(int columnIndex) {
        assert (isInteractable()) : "Can't interact with disabled or invisible Grid";
        Grid<T> grid = getComponent();
        assert (columnIndex < grid.getColumns().size()
                && columnIndex > -1) : "Column index out of bounds";
        assert (!grid.getColumns().get(columnIndex)
                .isHidden()) : "The column is hidden";
        Column<T, ?> column = grid.getColumns().get(columnIndex);
        assert column
                .isSortable() : "Column sorting is not enabled for this column";
        List<GridSortOrder<T>> newOrders = new ArrayList<>();
        boolean changed = false;
        for (int i = 0; i < grid.getSortOrder().size(); i++) {
            GridSortOrder<T> order = grid.getSortOrder().get(i);
            GridSortOrder<T> newOrder;
            if (order.getSorted().equals(column)) {
                SortDirection newDirection = order.getDirection().getOpposite();
                newOrder = new GridSortOrder<>(order.getSorted(), newDirection);
                changed = true;
            } else {
                newOrder = new GridSortOrder<>(order.getSorted(),
                        order.getDirection());
            }
            newOrders.add(newOrder);
        }
        if (!changed) {
            newOrders.add(new GridSortOrder<>(column, SortDirection.ASCENDING));
        }

        Class<?> clazz = getComponent().getClass();
        while (!clazz.equals(Grid.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Method setSortOrderMethod = clazz.getDeclaredMethod("setSortOrder",
                    List.class, Boolean.TYPE);
            setSortOrderMethod.setAccessible(true);
            setSortOrderMethod.invoke(getComponent(), newOrders, true);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // Internal utility method for firing simulated Editor events
    private void fireEditorEvent(EventObject event) {
        Grid<T> grid = getComponent();
        @SuppressWarnings("unchecked")
        Class<EditorImpl<T>> clazz = (Class<EditorImpl<T>>) grid.getEditor()
                .getClass();
        try {
            Field field = clazz.getDeclaredField("eventRouter");
            field.setAccessible(true);
            EventRouter eventRouter = (EventRouter) field.get(grid.getEditor());
            eventRouter.fireEvent(event);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
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
