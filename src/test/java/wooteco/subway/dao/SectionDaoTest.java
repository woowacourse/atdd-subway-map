package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixtures.LINE;
import static wooteco.subway.Fixtures.STATION;
import static wooteco.subway.Fixtures.STATION_2;
import static wooteco.subway.Fixtures.STATION_3;
import static wooteco.subway.Fixtures.getSection;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
class SectionDaoTest {
    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("지하철 구간을 저장한다.")
    @Test
    void save() {
        Long stationId = stationDao.save(STATION);
        Long stationId2 = stationDao.save(STATION_2);
        Long lineId = lineDao.save(LINE);
        Long sectionId = sectionDao.save(new Section(lineId, stationId, stationId2, 10));
        assertThat(sectionDao.findById(sectionId))
                .isEqualTo(getSection(sectionId, new Section(lineId, stationId, stationId2, 10)));
    }

    @DisplayName("해당 지하철 노선 id의 지하철 구간들을 조회한다.")
    @Test
    void findAllByLineId() {
        //given
        Long stationId = stationDao.save(STATION);
        Long stationId2 = stationDao.save(STATION_2);
        Long stationId3 = stationDao.save(STATION_3);
        Long lineId = lineDao.save(LINE);
        Section section1 = new Section(lineId, stationId, stationId2, 10);
        Long id = sectionDao.save(section1);
        Section section2 = new Section(lineId, stationId2, stationId3, 10);
        Long id2 = sectionDao.save(section2);

        //when
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        //then
        assertThat(sections)
                .containsOnly(
                        getSection(id, section1),
                        getSection(id2, section2)
                );
    }

    @DisplayName("해당 지하철 노선 upStationId의 지하철 구간들을 조회한다.")
    @Test
    void findByUpStationId() {
        Long stationId = stationDao.save(STATION);
        Long stationId2 = stationDao.save(STATION_2);
        Long lineId = lineDao.save(LINE);
        Section section1 = new Section(lineId, stationId, stationId2, 10);
        Long id = sectionDao.save(section1);
        Section section = sectionDao.findByUpStationId(section1.getLineId(), section1.getUpStationId());
        assertThat(section)
                .isEqualTo(getSection(id, section1));
    }

    @DisplayName("해당 지하철 노선 downStationId의 지하철 구간들을 조회한다.")
    @Test
    void findByDownStationId() {
        Long stationId = stationDao.save(STATION);
        Long stationId2 = stationDao.save(STATION_2);
        Long lineId = lineDao.save(LINE);
        Section section1 = new Section(lineId, stationId, stationId2, 10);
        Long id = sectionDao.save(section1);
        Section section = sectionDao.findByDownStationId(section1.getLineId(), section1.getDownStationId());
        assertThat(section)
                .isEqualTo(getSection(id, section1));
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void delete() {
        //given
        Long stationId = stationDao.save(STATION);
        Long stationId2 = stationDao.save(STATION_2);
        Long lineId = lineDao.save(LINE);
        Long id = sectionDao.save(new Section(lineId, stationId, stationId2, 10));
        assertThat(sectionDao.findById(id))
                .isNotNull();

        //when
        sectionDao.delete(id);

        //then
        assertThatThrownBy(() -> sectionDao.findById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("해당 지하철 노선의 모든 지하철 구간을 삭제한다.")
    @Test
    void deleteAllByLineId() {
        //given
        Long stationId = stationDao.save(STATION);
        Long stationId2 = stationDao.save(STATION_2);
        Long lineId = lineDao.save(LINE);
        sectionDao.save(new Section(lineId, stationId, stationId2, 10));

        //when
        sectionDao.deleteAllByLineId(lineId);

        //then
        assertThat(sectionDao.findAllByLineId(lineId))
                .hasSize(0);
    }
}
