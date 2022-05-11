package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
        var section = findSection(LINE_ID);

        assertAll(
                () -> assertThat(section.getUpStationId()).isEqualTo(SECTION_REQUEST.getUpStationId()),
                () -> assertThat(section.getDownStationId()).isEqualTo(SECTION_REQUEST.getDownStationId()),
                () -> assertThat(section.getDistance()).isEqualTo(SECTION_REQUEST.getDistance())
        );
    }

    private Section findSection(Long lineId) {
        var sql = "SELECT * FROM section WHERE line_id = ?";

        RowMapper<Section> rowMapper = (rs, rowNum) -> {
            var id = rs.getLong("id");
            var upStationId = rs.getLong("up_station_id");
            var downStationId = rs.getLong("down_station_id");
            var distance = rs.getInt("distance");
            return new Section(id, upStationId, downStationId, distance);
        };

        return jdbcTemplate.queryForObject(sql, rowMapper, lineId);
    }

    @Test
    void delete() {
        var section = findSection(LINE_ID);
        assertDoesNotThrow(() -> sectionDao.delete(LINE_ID, section.getId()));
    }
}
