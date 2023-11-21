package com.jetbrains.hellowebapp.resources;

import com.jetbrains.hellowebapp.entity.Company;
import com.jetbrains.hellowebapp.entity.Department;
import com.jetbrains.hellowebapp.entity.Employee;
import com.jetbrains.hellowebapp.entity.EmployeeWithDetails;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Path("/employeeResource")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {

    private static final String URL = "jdbc:postgresql://localhost:5432/jerseydb2";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    private final Jdbi jdbi;

    public HelloResource() {
        this.jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/jerseydb2", "postgres", "123");
    }



    @GET
    @Path("/getAllCompanies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Company> getAllCompanies() throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");

        List<Company> companies = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM company";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");

                    Company company = new Company();
                    company.setId(id);
                    company.setName(name);

                    companies.add(company);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return companies;
    }

    @GET
    @Path("/getAllDepartments")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Department> getAllDepartments() throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");

        List<Department> departments = new ArrayList<>();

        // Use Jdbi to execute the query and map the result set to Department objects
        try (Handle handle = jdbi.open()) {
            departments = handle.createQuery("SELECT * FROM department")
                    .mapToBean(Department.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return departments;
    }

    @GET
    @Path("/getCompany/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Company getCompanyById(@PathParam("id") Integer id) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        Company company = null;

        // Use Jdbi to execute the query and map the result to a Company object
        try (Handle handle = jdbi.open()) {
            company = handle.createQuery("SELECT * FROM company WHERE id = :id")
                    .bind("id", id)
                    .mapToBean(Company.class)
                    .stream().findAny()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return company;
    }

    @GET
    @Path("/getDepartment/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Department getDepartmentById(@PathParam("id") Integer id) throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");

        Department department = null;

        // Use Jdbi to execute the query and map the result to a Department object
        try (Handle handle = jdbi.open()) {
            department = handle.createQuery("SELECT * FROM department WHERE id = :id")
                    .bind("id", id)
                    .mapToBean(Department.class)
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return department;
    }

    @GET
    @Path("/getDepartmentByCompany/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Department> getDepartmentByCompanyId(@PathParam("id") Integer id) throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");


        List<Department> departments = new ArrayList<>();

        // Use Jdbi to execute the query and map the results to a list of Department objects
        try (Handle handle = jdbi.open()) {
            departments = handle.createQuery("SELECT * FROM department WHERE company_id = :id")
                    .bind("id", id)
                    .mapToBean(Department.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return departments;
    }

    @GET
    @Path("/getEmployeesById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Employee> getEmployeesById(@PathParam("id") int id) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");


        List<Employee> employees = new ArrayList<>();

        // Use Jdbi to execute the query and map the results to a list of Employee objects
        try (Handle handle = jdbi.open()) {
            employees = handle.createQuery("SELECT * FROM employee WHERE id = :id")
                    .bind("id", id)
                    .mapToBean(Employee.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return employees;
    }


    @GET
    @Path("/getEmployeesByDepartmentId/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Employee> getEmployeesByDepartmentId(@PathParam("id") int id) throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");


        List<Employee> employees = new ArrayList<>();

        // Use Jdbi to execute the query and map the results to a list of Employee objects
        try (Handle handle = jdbi.open()) {
            employees = handle.createQuery("SELECT * FROM employee WHERE department_id = :id")
                    .bind("id", id)
                    .mapToBean(Employee.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return employees;
    }



    @GET
    @Path("/getAllEmployees")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EmployeeWithDetails> getAllEmployees(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("filter") @DefaultValue("") String filter,
            @QueryParam("sort") @DefaultValue("") String sort) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");


        // Building the SQL query dynamically based on parameters
        List<EmployeeWithDetails> sortedEmployeeWithDetails = getSortedEmployeeWithDetails(page, size, filter, sort);

        // Sorting the result based on different fields

        if (sort.equals("employee_id") || sort.equals("employee_name") || sort.isEmpty()) {

            // Building the SQL query dynamically based on parameters
            return getSortedEmployeeWithDetails(page, size, filter, sort);
        } else if (sort.equals("department_name")) {
            List<EmployeeWithDetails> sortedEmployeeWithDetails1 = getSortedEmployeeWithDetails(page, size, filter, sort);
            return sortedEmployeeWithDetails1.stream().sorted(Comparator.comparing(e -> e.getDepartmentName().toLowerCase())).toList();
        } else if (sort.equals("company_name")) {
            List<EmployeeWithDetails> sortedEmployeeWithDetails2 = getSortedEmployeeWithDetails(page, size, filter, sort);
            return sortedEmployeeWithDetails2.stream().sorted(Comparator.comparing(e -> e.getCompanyName().toLowerCase())).toList();
        } else {
            return getSortedEmployeeWithDetails(page, size, filter, sort);
        }

    }


    private List<EmployeeWithDetails> getSortedEmployeeWithDetails(int page, int size, String filter, String sort) throws ClassNotFoundException {
        List<EmployeeWithDetails> employeeWithDetails = new ArrayList<>();

        Class.forName("org.postgresql.Driver");

        // Use Jdbi to execute the query and map the results to a list of EmployeeWithDetails objects
        try (Handle handle = jdbi.open()) {
            // Building the SQL query with filtering, sorting, and pagination
            String sql = "SELECT e.id AS employee_id, " +
                    "e.name AS employee_name, " +
                    "d.id AS department_id, " +
                    "d.name AS department_name, " +
                    "c.id AS company_id, " +
                    "c.name AS company_name " +
                    "FROM employee e " +
                    "JOIN department d ON e.department_id = d.id " +
                    "JOIN company c ON d.company_id = c.id " +
                    "WHERE 1=1";

            // Adding filtering condition
            if (filter != null && !filter.isEmpty()) {
                sql += String.format(" AND e.name ILIKE '%%%s%%'", filter);
                // Add more conditions for other fields as needed
            }

            // Adding sorting condition
            if (sort != null && !sort.isEmpty()) {
                sql += String.format(" ORDER BY %s", sort);
            }

            // Adding pagination
            int offset = (page - 1) * size;
            sql += String.format(" LIMIT %d OFFSET %d", size, offset);

            // Map the query result to a list of EmployeeWithDetails
            employeeWithDetails = handle.createQuery(sql)
                    .mapToBean(EmployeeWithDetails.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return employeeWithDetails;
    }


    @GET
    @Path("/getAllEmployeesByDepartment/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EmployeeWithDetails> getAllEmployees1(
            @PathParam("id") int id,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("filter") @DefaultValue("") String filter,
            @QueryParam("sort") @DefaultValue("") String sort) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");


        // Building the SQL query dynamically based on parameters
        List<EmployeeWithDetails> sortedEmployeeWithDetails = getSortedEmployeeWithDetailsForDepartmentId(page, size, filter, sort,id);

        // Sorting the result based on different fields

        if (sort.equals("employee_id") || sort.equals("employee_name") || sort.isEmpty()) {

            // Building the SQL query dynamically based on parameters
            return getSortedEmployeeWithDetails(page, size, filter, sort);
        } else if (sort.equals("department_name")) {
            List<EmployeeWithDetails> sortedEmployeeWithDetails1 = getSortedEmployeeWithDetails(page, size, filter, sort);
            return sortedEmployeeWithDetails1.stream().sorted(Comparator.comparing(e -> e.getDepartmentName().toLowerCase())).toList();
        } else if (sort.equals("company_name")) {
            List<EmployeeWithDetails> sortedEmployeeWithDetails2 = getSortedEmployeeWithDetails(page, size, filter, sort);
            return sortedEmployeeWithDetails2.stream().sorted(Comparator.comparing(e -> e.getCompanyName().toLowerCase())).toList();
        } else {
            return getSortedEmployeeWithDetails(page, size, filter, sort);
        }

    }


    private List<EmployeeWithDetails> getSortedEmployeeWithDetailsForDepartmentId(int page, int size, String filter, String sort,int id) throws ClassNotFoundException {
        List<EmployeeWithDetails> employeeWithDetails = new ArrayList<>();

        Class.forName("org.postgresql.Driver");

        // Use Jdbi to execute the query and map the results to a list of EmployeeWithDetails objects
        try (Handle handle = jdbi.open()) {
            // Building the SQL query with filtering, sorting, and pagination
            String sql =
                    "SELECT e.id AS employee_id, " +
                    "e.name AS employee_name, " +
                    "d.id AS department_id, " +
                    "d.name AS department_name, " +
                    "c.id AS company_id, " +
                    "c.name AS company_name " +
                    "FROM employee e " +
                    "JOIN department d ON e.department_id = d.id " +
                    "JOIN company c ON d.company_id = c.id " +
                    "WHERE 1=1";

            // Adding filtering condition
            if (filter != null && !filter.isEmpty()) {
                sql += String.format(" AND e.name ILIKE '%%%s%%'", filter);
                // Add more conditions for other fields as needed
            }

            // Adding sorting condition
            if (sort != null && !sort.isEmpty()) {
                sql += String.format(" ORDER BY %s", sort);
            }

            // Adding pagination
            int offset = (page - 1) * size;
            sql += String.format(" LIMIT %d OFFSET %d", size, offset);

            // Map the query result to a list of EmployeeWithDetails
            employeeWithDetails = handle.createQuery(sql)
                    .mapToBean(EmployeeWithDetails.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return employeeWithDetails;
    }



    @POST
    @Path("/saveDepartment")
    public Response saveDepartment(Department department) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        String sql = "INSERT INTO department (name, company_id) VALUES (?, ?)";


        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, department.getName());
            preparedStatement.setInt(2, department.getCompanyId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                return Response.status(Response.Status.CREATED).entity(department).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Failed to insert department.").build();
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
        }
    }








    @POST
    @Path("/saveEmployee")
    public Employee saveEmployee(Employee employee) throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");


        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql = "INSERT INTO employee (name, department_id) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, employee.getName());
            preparedStatement.setInt(2, employee.getDepartmentId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Failed to insert employee, no rows affected.");
            }

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                employee.setId(resultSet.getInt(1));
            } else {
                throw new RuntimeException("Failed to retrieve generated key for employee.");
            }

            return employee;
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
            return null; // Return null or handle the error accordingly
        } finally {
            // Close resources in reverse order of creation to avoid potential issues
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace(); // Handle or log the exception according to your needs
            }
        }
    }

    @POST
    @Path("/saveCompany")
    public Response saveCompany(Company company) throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");

        String sql = "INSERT INTO company (name) VALUES (?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, company.getName());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                return Response.status(Response.Status.CREATED).entity(company).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Failed to insert company.").build();
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/deleteEmployee/{id}")
    public void deleteEmployee(@PathParam("id") Integer id) throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");


        String sql = "DELETE FROM employee WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }
    }

    @PUT
    @Path("/updateEmployee")
    public Employee updateEmployee(Employee employee) throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");


        String updateSql = "UPDATE employee SET name=?, department_id=? WHERE id=?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            updateStatement.setString(1, employee.getName());
            updateStatement.setInt(2, employee.getDepartmentId());
            updateStatement.setInt(3, employee.getId());

            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                // If the update was successful, retrieve the updated employee
                String selectSql = "SELECT * FROM employee WHERE id = ?";
                try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
                    selectStatement.setInt(1, employee.getId());

                    try (ResultSet resultSet = selectStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Map the result set to an Employee object
                            Employee updatedEmployee = new Employee();
                            updatedEmployee.setId(resultSet.getInt("id"));
                            updatedEmployee.setName(resultSet.getString("name"));
                            updatedEmployee.setDepartmentId(resultSet.getInt("department_id"));

                            return updatedEmployee;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception according to your needs
        }

        return null; // Return null if the update was not successful
    }




}