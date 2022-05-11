package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import wooteco.subway.domain.Section;

import javax.sql.DataSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert LineSimpleJdbcInsert;
    private SimpleJdbcInsert StationSimpleJdbcInsert;
    private SectionDao dao;

    @BeforeEach
    void setUp() {
        dao = new SectionDao(dataSource);
        LineSimpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
        StationSimpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("지하철 구간 저장")
    void save() {
        dao.save(new Section(0L, 1L, 1L, 2L, 5));

        Boolean isSaved = jdbcTemplate.queryForObject("SELECT EXISTS (SELECT * FROM SECTION)", Boolean.class);

        assertThat(isSaved).isTrue();
    }

    @Test
    @DisplayName("지하철 구간 조회")
    void findAllByLineId() {
        dao.save(new Section(0L, 1L, 1L, 2L, 5));

        List<Section> result = dao.findAllByLineId(1L);

        assertThat(result.size() > 0).isTrue();
    }

    @Test
    @DisplayName("지하철 구간 삭제")
    void delete() {
        dao.save(new Section(0L, 1L, 1L, 2L, 5));
        Long id = jdbcTemplate.queryForObject("SELECT id FROM SECTION WHERE line_id = 1", Long.class);

        dao.delete(id);

        assertThat(dao.findAllByLineId(1L).size()).isEqualTo(0);
    }
}
