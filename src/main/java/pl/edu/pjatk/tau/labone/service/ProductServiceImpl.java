package pl.edu.pjatk.tau.labone.service;

import org.springframework.stereotype.Component;
import pl.edu.pjatk.tau.labone.domain.Product;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Component
public class ProductServiceImpl  implements ProductService {
    private Connection connection;
    private PreparedStatement addProductStmt;
    private PreparedStatement getAllProductsStmt;
    private PreparedStatement deleteProductStmt;
    private PreparedStatement getProductStmt;
    private PreparedStatement updateProductStmt;
    private PreparedStatement deleteAllProductsStmt;

    public ProductServiceImpl(Connection connection) throws SQLException {
        this.connection = connection;
        if (!isDatabaseReady()) {
            createTables();
        }
        setConnection(connection);
    }

    public ProductServiceImpl() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/workdb");
        if (!isDatabaseReady()) {
            createTables();
        }
        setConnection(this.connection);
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection;
        addProductStmt = connection.prepareStatement("INSERT INTO Product (name, price) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        deleteProductStmt = connection.prepareStatement("DELETE FROM Product where id = ?");
        deleteAllProductsStmt = connection.prepareStatement("DELETE FROM Product");
        getAllProductsStmt = connection.prepareStatement("SELECT id, name, price FROM Product ORDER BY id");
        getProductStmt = connection.prepareStatement("SELECT id, name, price FROM Product WHERE id = ?");
        updateProductStmt = connection.prepareStatement("UPDATE Product SET name=?,price=? WHERE id = ?");
    }

    public void createTables() throws SQLException {
        connection.createStatement().executeUpdate("CREATE TABLE Product(id int GENERATED BY DEFAULT AS IDENTITY, name varchar(255) NOT NULL, price double)");
    }

    public boolean isDatabaseReady() {
        try {
            ResultSet rs = connection.getMetaData().getTables(null, null, null, null);
            boolean tableExists = false;
            while (rs.next()) {
                if ("Product".equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                    tableExists = true;
                    break;
                }
            }
            return tableExists;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int addProduct(Product p) {
        int count = 0;
        try {
            addProductStmt.setString(1, p.getName());
            addProductStmt.setBigDecimal(2, p.getPrice());
            count = addProductStmt.executeUpdate();
            ResultSet generatedKeys = addProductStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                p.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
        return count;
    }

    @Override
    public int deleteProduct(Product p) {
        try {
            deleteProductStmt.setInt(1, p.getId());
            return deleteProductStmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
    }

    @Override
    public int updateProduct(Product p) throws SQLException {
        int count = 0;
        try {
            updateProductStmt.setString(1, p.getName());
            updateProductStmt.setBigDecimal(2, p.getPrice());
            updateProductStmt.setInt(3, p.getId());
            count = updateProductStmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
        if (count <= 0)
            throw new SQLException("Product not found for update");
        return count;
    }

    @Override
    public Product getProduct(int id) throws SQLException {
        try {
            getProductStmt.setInt(1, id);
            ResultSet rs = getProductStmt.executeQuery();
            if(rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getBigDecimal("price"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException((e.getMessage() + "\n" + e.getStackTrace().toString()));
        }
        throw new SQLException("Product with id " + id + " does not exist");
    }

    @Override
    public int deleteAll() {
        try {
            return deleteAllProductsStmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = new LinkedList<>();
        try {
            ResultSet rs = getAllProductsStmt.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getBigDecimal("price"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
        return products;
    }
}