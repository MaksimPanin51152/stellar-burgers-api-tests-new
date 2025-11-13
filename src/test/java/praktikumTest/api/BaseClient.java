package praktikumTest.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseClient {

    protected static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    protected static RequestSpecification baseSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType(ContentType.JSON)
            .build();

    static {
        RestAssured.requestSpecification = baseSpec;
    }
}

