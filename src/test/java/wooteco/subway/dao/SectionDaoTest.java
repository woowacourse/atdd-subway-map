package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.NoSuchElementException;
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

    private final Long lineId = 1L;
    private final SectionRequest sectionRequest = new SectionRequest(1L, 2L, 1);
    private SectionDao sectionDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(jdbcTemplate);
        insertSection(lineId, sectionRequest);
    }

    private void insertSection(Long lineId, SectionRequest sectionRequest) {
        sectionDao.save(lineId, sectionRequest);
    }

    @Test
    void create() {
        var section = findSection(lineId);

        assertAll(
                () -> assertThat(section.getUpStationId()).isEqualTo(sectionRequest.getUpStationId()),
                () -> assertThat(section.getDownStationId()).isEqualTo(sectionRequest.getDownStationId()),
                () -> assertThat(section.getDistance()).isEqualTo(sectionRequest.getDistance())
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
        var section = findSection(lineId);
        assertDoesNotThrow(() -> sectionDao.delete(lineId, section.getId()));
    }

    @Test
    void deleteByInvalidId() {
        assertThatThrownBy(() -> sectionDao.delete(lineId, -1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void findByLineId() {
        var section = sectionDao.findByLineId(lineId);

        assertAll(
                () -> assertThat(sectionRequest.getUpStationId()).isEqualTo(section.getUpStationId()),
                () -> assertThat(sectionRequest.getDownStationId()).isEqualTo(section.getDownStationId())
        );
    }

    @Test
    void findByInvalidLineId() {
        assertThatThrownBy(() -> sectionDao.findByLineId(-1L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
