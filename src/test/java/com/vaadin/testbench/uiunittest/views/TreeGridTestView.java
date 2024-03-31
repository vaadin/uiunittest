/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.uiunittest.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.vaadin.testbench.uiunittest.TestView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.VerticalLayout;

public class TreeGridTestView extends TestView {
    public static final String NAME = "treegrid";

    @Override
    public String getName() {
        return NAME;
    }

    DepartmentData departmentData;

    public DepartmentData getData() {
        return departmentData;
    }

    @Override
    public Component getComponent() {
        VerticalLayout content = new VerticalLayout();
        departmentData = new DepartmentData();
        TreeGrid<Department> grid = new TreeGrid<>();

        grid.setItems(departmentData.getRootDepartments(),
                departmentData::getChildDepartments);
        Column<Department, String> col = grid.addColumn(Department::getName)
                .setCaption("Department Name");
        grid.setHierarchyColumn(col);
        grid.addColumn(Department::getManager).setCaption("Manager");

        grid.addExpandListener(e -> {
            if (e.isUserOriginated()) {
                Notification.show("Expanded " + e.getExpandedItem().getName());
            }
        });

        grid.addCollapseListener(e -> {
            if (e.isUserOriginated()) {
                Notification
                        .show("Collapsed " + e.getCollapsedItem().getName());
            }
        });

        Button disable = new Button("Disable");
        disable.addClickListener(e -> {
            grid.setEnabled(false);
        });
 
        content.addComponents(grid, disable);
        return content;
    }

    public static class DepartmentData {
        private static final List<Department> DEPARTMENT_LIST = createDepartmentList();

        private static List<Department> createDepartmentList() {
            List<Department> departmentList = new ArrayList<>();

            departmentList.add(
                    new Department(1, "Product Development", null, "Päivi"));
            departmentList.add(
                    new Department(11, "Flow", departmentList.get(0), "Pekka"));
            departmentList.add(new Department(111, "Flow Core",
                    departmentList.get(1), "Pekka"));
            departmentList.add(new Department(111, "Flow Components",
                    departmentList.get(1), "Gilberto"));
            departmentList.add(new Department(12, "Design",
                    departmentList.get(0), "Pekka"));
            departmentList.add(
                    new Department(13, "DJO", departmentList.get(0), "Thomas"));
            departmentList.add(new Department(14, "Component",
                    departmentList.get(0), "Tomi"));
            departmentList.add(new Department(2, "HR", null, "Anne"));
            departmentList.add(
                    new Department(21, "Office", departmentList.get(7), "Anu"));
            departmentList.add(new Department(22, "Employee",
                    departmentList.get(7), "Minna"));
            departmentList.add(new Department(3, "Marketing", null, "Niko"));
            departmentList.add(new Department(31, "Growth",
                    departmentList.get(10), "Ömer"));
            departmentList.add(new Department(32, "Demand Generation",
                    departmentList.get(10), "Marcus"));
            departmentList.add(new Department(33, "Product Marketing",
                    departmentList.get(10), "Pekka"));
            departmentList.add(new Department(34, "Brand Experience",
                    departmentList.get(10), "Eero"));

            return departmentList;
        }

        public List<Department> getDepartments() {
            return DEPARTMENT_LIST;
        }

        public List<Department> getRootDepartments() {
            return DEPARTMENT_LIST.stream()
                    .filter(department -> department.getParent() == null)
                    .collect(Collectors.toList());
        }

        public List<Department> getChildDepartments(Department parent) {
            return DEPARTMENT_LIST
                    .stream().filter(department -> Objects
                            .equals(department.getParent(), parent))
                    .collect(Collectors.toList());
        }
    }

    public static class Department {
        private int id;
        private String name;
        private String manager;
        private Department parent;

        public Department(int id, String name, Department parent,
                String manager) {
            this.id = id;
            this.name = name;
            this.manager = manager;
            this.parent = parent;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getManager() {
            return manager;
        }

        public void setManager(String manager) {
            this.manager = manager;
        }

        public Department getParent() {
            return parent;
        }

        public void setParent(Department parent) {
            this.parent = parent;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
