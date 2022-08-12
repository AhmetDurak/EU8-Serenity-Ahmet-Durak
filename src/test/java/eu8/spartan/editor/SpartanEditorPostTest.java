package eu8.spartan.editor;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityTest;
import net.serenitybdd.rest.Ensure;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import utilities.ExcelUtil;
import utilities.SpartanNewBase;
import utilities.SpartanUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.rest.RestRequests.given;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.hamcrest.Matchers.*;

@SerenityTest
public class SpartanEditorPostTest extends SpartanNewBase {

    @Disabled
    @DisplayName("Editor should be able to POST")
    @Test
    public void postSpartanEditor() {
        // create one spartan using util

        Map<String, Object> spartan = SpartanUtil.getRandomSpartanMap();

        System.out.println("spartan = " + spartan);

        //send a post request as editor

            given()
                .auth().basic("editor", "editor")
                .and().accept(ContentType.JSON)
                .and().contentType(ContentType.JSON)
                .body(spartan)
            .when()
                .post("/spartans")
                .prettyPrint();
    }

    /**
     * status code is 201
     * content type is Json
     * success message is A Spartan is Born!
     * id is not null
     * name is correct
     * gender is correct
     * phone is correct
     *
     * check location header ends with newly generated id
     */

    @DisplayName("POST a new spartan")
    @Test
    public void test1(){
        Map<String, Object> spartan = SpartanUtil.getRandomSpartanMap();

            given()
                .auth().basic("admin", "admin")
                .and().accept(ContentType.JSON)
                .and().contentType(ContentType.JSON)
                .and().log().all()
                .body(spartan)
            .when()
                .post("/spartans")
            .then()
                .log().all();

        Ensure.that("status code is 201", report -> report.statusCode(201));
        Ensure.that("content type is Json", report -> report.contentType(ContentType.JSON));
        Ensure.that("success message is A Spartan is Born!", report -> report.body("success",is("A Spartan is Born!")));
        Ensure.that("id is not null", report -> report.body("data.id",notNullValue()));
        Ensure.that("name is correct", report -> report.body("data.name",is(spartan.get("name"))));
        Ensure.that("gender is correct", report -> report.body("data.gender",is(spartan.get("gender"))));
        Ensure.that("phone is correct", report -> report.body("data.phone",is(spartan.get("phone"))));
        String id = lastResponse().jsonPath().getString("data.id");
        Ensure.that("check location header ends with newly generated id",
                report -> report.header("location",endsWith(id)));

    }

    /**
     * we can give name to each execution using name = ""
     * and if you want to get index of iteration we can use {index}
     * and also if you want to include parameter in your test name
     * {0}, {1}, {2} --> based on the order you provide as parameter
     * @param name
     * @param gender
     * @param phone
     */

    @ParameterizedTest(name = "New Spartan {index} - name: {0}")
    @CsvFileSource(resources = "/spartanData.csv",numLinesToSkip = 1)
    public void postSpartanWithCsv(String name, String gender, long phone){
        Map<String,Object> spartan = new LinkedHashMap<>();
        spartan.put("name",name);
        spartan.put("gender",gender);
        spartan.put("phone",phone);

        given()
                .auth().basic("admin", "admin")
                .and().accept(ContentType.JSON)
                .and().contentType(ContentType.JSON)
                .and().log().all()
                .body(spartan)
                .when()
                .post("/spartans")
                .then()
                .log().all();

        Ensure.that("status code is 201", report -> report.statusCode(201));
        Ensure.that("content type is Json", report -> report.contentType(ContentType.JSON));
        Ensure.that("success message is A Spartan is Born!", report -> report.body("success",is("A Spartan is Born!")));
        Ensure.that("id is not null", report -> report.body("data.id",notNullValue()));
        Ensure.that("name is correct", report -> report.body("data.name",is(spartan.get("name"))));
        Ensure.that("gender is correct", report -> report.body("data.gender",is(spartan.get("gender"))));
        Ensure.that("phone is correct", report -> report.body("data.phone",is(spartan.get("phone"))));
        String id = lastResponse().jsonPath().getString("data.id");
        Ensure.that("check location header ends with newly generated id",
                report -> report.header("location",endsWith(id)));
    }


}
