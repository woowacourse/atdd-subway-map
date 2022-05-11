package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.section.SectionRequest;

@JdbcTest
public class SectionDaoTest {

    private final Long LINE_ID = 1L;
    private final SectionRequest SECTION_REQUEST = new SectionRequest(1L, 2L, 1);
    private SectionDao sectionDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(jdbcTemplate);
        insertSection(LINE_ID, SECTION_REQUEST);
    }

    private void insertSection(Long lineId, SectionRequest sectionRequest) {
        sectionDao.save(lineId, sectionRequest);
    }

    @Test
    void create() {
        //when
        RowMapper<Section> rowMapper = (rs, rowNum) -> {
            var upStationId = rs.getLong("up_station_id");
            var downStationId = rs.getLong("down_station_id");
            var distance = rs.getInt("distance");
            return new Section(upStationId, downStationId, distance);
        };

        var sql = "SELECT * FROM section WHERE line_id = ?";
        var section = jdbcTemplate.queryForObject(sql, rowMapper, LINE_ID);

        //then
        assertAll(
                () -> assertThat(section.getUpStationId()).isEqualTo(SECTION_REQUEST.getUpStationId()),
                () -> assertThat(section.getDownStationId()).isEqualTo(SECTION_REQUEST.getDownStationId()),
                () -> assertThat(section.getDistance()).isEqualTo(SECTION_REQUEST.getDistance())
        );
    }

    @Test
    void delete() {
    }
}
