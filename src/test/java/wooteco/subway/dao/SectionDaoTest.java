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
import java.util.Map;

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
        Long lineId = LineSimpleJdbcInsert.executeAndReturnKey(Map.of("name", "2호선", "color", "green")).longValue();
        Long upStationId = StationSimpleJdbcInsert.executeAndReturnKey(Map.of("name", "선릉역")).longValue();
        Long downStationId = StationSimpleJdbcInsert.executeAndReturnKey(Map.of("name", "구의역")).longValue();

        dao.save(new Section(0L, lineId, upStationId, downStationId, 5));

        Boolean isSaved = jdbcTemplate.queryForObject("SELECT EXISTS (SELECT * FROM SECTION)", Boolean.class);

        assertThat(isSaved).isTrue();
    }

    @Test
    @DisplayName("지하철 구간 조회")
    void findAllByLineId() {
        Long lineId = LineSimpleJdbcInsert.executeAndReturnKey(Map.of("name", "2호선", "color", "green")).longValue();
        Long upStationId = StationSimpleJdbcInsert.executeAndReturnKey(Map.of("name", "선릉역")).longValue();
        Long downStationId = StationSimpleJdbcInsert.executeAndReturnKey(Map.of("name", "구의역")).longValue();

        dao.save(new Section(0L, lineId, upStationId, downStationId, 5));

        List<Section> sections = dao.findAllByLineId(lineId);

        assertThat(sections).isNotNull();
    }
}
