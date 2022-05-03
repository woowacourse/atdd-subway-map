package wooteco.subway.ui;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SubwayControllerAdviceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("없는 경로 api 요청")
    void notFoundUrl() {
        RestAssured.given().log().all()
                .when()
                .post("/test/not_found")
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
