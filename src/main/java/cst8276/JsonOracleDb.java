package cst8276;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/fetch-json")
public class JsonOracleDb extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ServletContext context;

    @Resource(lookup = "java:app/jdbc/myOracleDataSource")
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();
        context = getServletContext();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        context.log("doGet");

        try {
            // Fetch the JSON data from Oracle Database
            String jsonData = fetchJsonFromDatabase();

            // set response headers
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // use writer to send JSON to client browser
            PrintWriter writer = response.getWriter();
            writer.append(jsonData);
            writer.flush();
        } catch (Exception e) {
            context.log("Error fetching JSON from database or performing JNDI lookup", e);
            response.getWriter().write(e.getMessage());
            // Handle the exception appropriately
        }
    }

    private String fetchJsonFromDatabase() {
        String result = "";
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT data FROM json_data"; // Fetches JSON data with id 3
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                result = rs.getString("data");
                context.log("Fetched JSON data from database: " + result);
            }
        } catch (Exception e) {
            context.log("Error fetching JSON from database", e);
        }
        return result;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Simply call the doGet method from here to handle POST the same way as GET
        doGet(request, response);
    }

   
    
}
