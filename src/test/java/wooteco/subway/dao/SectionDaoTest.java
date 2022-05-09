package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.section.SectionRequest;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;
    private SectionDao dao;

    @BeforeEach
    void setUp() {
        dao = new SectionDao(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("지하철 구간 저장")
    void save() {
        jdbcTemplate.execute("INSERT INTO LINE(name,color) VALUES('2호선','green')");
        jdbcTemplate.execute("INSERT INTO STATION(name) VALUES('선릉역')");
        jdbcTemplate.execute("INSERT INTO STATION(name) VALUES('구의역')");

        dao.save(Section.of(0L, new SectionRequest(1L, 2L, 5)));

        Boolean isSaved = jdbcTemplate.queryForObject("SELECT EXISTS (SELECT * FROM SECTION)", Boolean.class);

        assertThat(isSaved).isTrue();
    }
}
