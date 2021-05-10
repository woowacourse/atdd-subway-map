package wooteco.subway;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        jdbcTemplate.execute(
                "DROP TABLE IF EXISTS STATION;" +
                        "DROP TABLE IF EXISTS LINE;" +
                        "DROP TABLE IF EXISTS SECTION;" +
                        "create table if not exists STATION\n" +
                        "(\n" +
                        "    id bigint auto_increment not null,\n" +
                        "    name varchar(255) not null unique,\n" +
                        "    primary key(id)\n" +
                        "    );\n" +
                        "\n" +
                        "create table if not exists LINE\n" +
                        "(\n" +
                        "    id bigint auto_increment not null,\n" +
                        "    name varchar(255) not null unique,\n" +
                        "    color varchar(20) not null,\n" +
                        "    primary key(id)\n" +
                        "    );\n" +
                        "\n" +
                        "create table if not exists SECTION\n" +
                        "(\n" +
                        "    id bigint auto_increment not null,\n" +
                        "    line_id bigint not null,\n" +
                        "    up_station_id bigint not null,\n" +
                        "    down_station_id bigint not null,\n" +
                        "    distance int,\n" +
                        "    primary key(id)\n" +
                        "    );");
    }
}
