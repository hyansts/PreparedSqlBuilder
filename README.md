# Prepared SQL Builder

Prepared SQL Builder is a Java library for building SQL queries. It is a type-safe alternative to concatenating SQL
strings with user input and provides a fluent API allowing for IDE auto-completion and type checking.

![psb_example](https://github.com/user-attachments/assets/f9afdd09-71ea-43ea-a30f-45b6a5b0693f)

## Usage

- Here's a basic example of how to use the library:

```java
void example() {
    SqlQuery query = SqlQueryFactory.createQuery();

    EmployeesDbTable employees = new EmployeesDbTable();

    query.select(employees.name)
         .from(employees)
         .where(employees.name.eq("John").and(employees.age.gt(30)));

    query.getSql(); // "SELECT name FROM employees WHERE name = ? AND age > ?"
    query.getValues(); // ["John", 30]
}
```

- You can start building your query using the `SqlQueryFactory` class. It can create three different types of queries:

```java
// A common SQL query
SqlQuery query = SqlQueryFactory.createQuery();
// A SQL subquery to be used as a derived table or as part of a query
SqlSubquery subquery = SqlQueryFactory.createSubquery();
// A scalar subquery that returns a single value of a user defined type
SqlScalarSubquery<T> scalarSubquery = SqlQueryFactory.createScalarSubquery();
```

- To build a query, you'll first need to have a model for your table(s) and columns. The API provides a way to create
  those classes, here's an example:

```java 
private static class EmployeesDbTable extends BaseDbTable<EmployeesDbTable> {

    public final DbTableField<Integer> id = new DbTableField<>("id", this);
    public final DbTableField<String> name = new DbTableField<>("name", this);
    public final DbTableField<Integer> age = new DbTableField<>("age", this);
    public final DbTableField<Boolean> is_active = new DbTableField<>("is_active", this);
    public final DbTableField<Integer> department_id = new DbTableField<>("department_id", this);

    public EmployeesDbTable() { super("employees"); }
}
```

- To model your tables, the API includes the `BaseDbTable` abstract class that you can extend. The table name is
  defined in the constructor.
- To model your columns, you can use the `DbTableField` class. The fields need a name and a table of origin to be
  instantiated. The fields also have a generic type, this is the Java representation of the column type, this is not the
  same as your SQL database type, but rather the type the data will be converted to when the column is fetched.

There are a number of built-in features like conditional operators, aggregate functions, aliases and more.

## Installation

- You can add the library as a dependency using JitPack:

### Maven

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

```xml
<dependency>
	<groupId>com.github.hyansts</groupId>
	<artifactId>PreparedSqlBuilder</artifactId>
	<version>v1.0.0</version>
</dependency>
```

### Gradle

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation 'com.github.hyansts:PreparedSqlBuilder:v1.0.0'
}
```

Check out the [JitPack PreparedSqlBuilder](https://jitpack.io/#hyansts/PreparedSqlBuilder) page for more information.

## License

This project is licensed under the [Apache 2.0 License](LICENSE).