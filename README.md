# UIUnitTest for Vaadin 8

This is an add-on library for Vaadin TestBench 5 for UI Unit Testing Vaadin 8 views and components. The API design is similar to UI Unit Testing for Vaadin 23 and 24.

## Development instructions

### Important Files 
* com.vaadin.testbench.uiunittest.UIUniteTest.java: This is the base class you should extend in Unit tests.
* com.vaadin.testbench.uiunittest.testers: A compact set of component helpers.
* assembly/: this folder includes configuration for packaging the project into a JAR so that it works well with other Vaadin projects and the Vaadin Directory. There is usually no need to modify these files, unless you need to add JAR manifest entries.

### Deployment

Starting the test/demo server:
```
mvn jetty:run
```

This deploys demo at http://localhost:8080

### Branching information

* `master` the latest version of the starter, using latest stable platform version


## Publishing to Vaadin Directory

You should change the `organisation.name` property in `pom.xml` to your own name/organization.

```
    <organization>
        <name>###author###</name>
    </organization>
```

You can create the zip package needed for [Vaadin Directory](https://vaadin.com/directory/) using

```
mvn versions:set -DnewVersion=1.0.0 # You cannot publish snapshot versions 
mvn install -Pdirectory
```

The package is created as `target/{project-name}-1.0.0.zip`

For more information or to upload the package, visit https://vaadin.com/directory/my-components?uploadNewComponent