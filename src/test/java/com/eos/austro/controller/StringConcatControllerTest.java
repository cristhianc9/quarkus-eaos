
package com.eos.austro.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@QuarkusTest
class StringConcatControllerTest {
    @Test
    void testConcatStringsSqlScriptInjection() {
        // Prueba con un script SQL típico
        RestAssured.given()
                .when()
                .post("/api/v1/test/uno/dos/tres;DROP TABLE users/cuatro/cinco")
                .then()
                .statusCode(400)
                .body(containsString("Parámetro inválido"));
        RestAssured.given()
                .when()
                .post("/api/v1/test/uno/dos/tres' OR '1'='1/cuatro/cinco")
                .then()
                .statusCode(400)
                .body(containsString("Parámetro inválido"));
    }

    @Test
    void testConcatStringsSuccess() {
        RestAssured.given()
                .when()
                .post("/api/v1/test/uno/dos/tres/cuatro/cinco")
                .then()
                .statusCode(200)
                .body(equalTo("uno dos tres cuatro cinco"));
    }

    @Test
    void testConcatStringsBlankParam() {
        // Cuando falta un parámetro, la ruta no coincide y debe retornar 404
        RestAssured.given()
                .when()
                .post("/api/v1/test/uno/dos//cuatro/cinco")
                .then()
                .statusCode(404);
    }

    @Test
    void testConcatStringsSqlInjectionChars() {
        // Prueba con ;
        RestAssured.given()
                .when()
                .post("/api/v1/test/uno/dos/tres;drop/cuatro/cinco")
                .then()
                .statusCode(400)
                .body(containsString("Parámetro inválido"));
        // Prueba con --
        RestAssured.given()
                .when()
                .post("/api/v1/test/uno/dos/tres--/cuatro/cinco")
                .then()
                .statusCode(400)
                .body(containsString("Parámetro inválido"));
        // Prueba con /* */ (la ruta no coincide, debe retornar 404)
        RestAssured.given()
                .when()
                .post("/api/v1/test/uno/dos/tre/*/cuatro/cinco")
                .then()
                .statusCode(404);
        RestAssured.given()
                .when()
                .post("/api/v1/test/uno/dos/tre*/cuatro/cinco")
                .then()
                .statusCode(400);
        // Prueba con comillas
        RestAssured.given()
                .when()
                .post("/api/v1/test/uno/dos/'/cuatro/cinco")
                .then()
                .statusCode(400)
                .body(containsString("Parámetro inválido"));
    }
}
