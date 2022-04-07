package com.polykhel.quarkushop.web;

import com.polykhel.quarkushop.domain.enums.CartStatus;
import com.polykhel.quarkushop.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.Map;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class CartResourceTest {

    private static final String INSERT_WRONG_CART_IN_DB = "insert into carts values (999, current_timestamp, current_timestamp, 'NEW', 3)";
    private static final String DELETE_WRONG_CART_IN_DB = "delete from carts where id = 999";

    DataSource datasource;

    public CartResourceTest(DataSource datasource) {
        this.datasource = datasource;
    }

    @Test
    void testFindAll() {
        get("/carts")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThan(0));
    }

    @Test
    void testFindAllActiveCarts() {
        get("/carts/active")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testGetActiveCartForCustomer() {
        get("/carts/customer/3")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(OK.getStatusCode())
                .body(containsString("Peter"));
    }

    @Test
    void testFindById() {
        get("/carts/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("status"))
                .body(containsString("NEW"));

        get("/carts/100")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDelete() {
        get("/carts/active")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("NEW"));

        delete("/carts/1")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        get("/carts/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("CANCELED"));
    }

    @Test
    void testGetActiveCartForCustomerWhenThereAreTwoCartsInDB() {
        executeSql(INSERT_WRONG_CART_IN_DB);

        get("/carts/customer/3")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body(containsString("Many active carts detected !!!"));

        executeSql(DELETE_WRONG_CART_IN_DB);
    }

    @Test
    void testCreateCart() {
        var requestParams = Map.of("firstName", "Saul", "lastName", "Berenson", "email", "call.saul@mail.com");

        var newCustomerId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var response = post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertThat(response.get("id")).isNotNull();
        assertThat(response).containsEntry("status", CartStatus.NEW.name());

        delete("/carts/" + response.get("id"))
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        delete("/customers/" + newCustomerId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testFailCreateCartWhileHavingAlreadyActiveCart() {
        var requestParams = Map.of("firstName", "Saul", "lastName", "Berenson", "email", "call.saul@mail.com");

        var newCustomerId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        var newCartId = post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body(containsString("There is already an active cart"));

        assertThat(newCartId).isNotZero();

        delete("/carts/" + newCartId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        delete("/customers/" + newCustomerId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    private void executeSql(String query) {
        try (var connection = datasource.getConnection()) {
            var statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new IllegalStateException("Error has occured while trying to execute SQL Query: " + e.getMessage());
        }
    }

}
