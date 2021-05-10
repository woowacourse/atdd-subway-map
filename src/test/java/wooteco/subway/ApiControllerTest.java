package wooteco.subway;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ApiControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(){
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
