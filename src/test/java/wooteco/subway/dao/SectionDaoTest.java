package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixtures.LINE;
import static wooteco.subway.Fixtures.STATION;
import static wooteco.subway.Fixtures.STATION_2;
import static wooteco.subway.Fixtures.STATION_3;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

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
        Station station = stationDao.findById(stationDao.save(STATION));
        Station station2 = stationDao.findById(stationDao.save(STATION_2));
        Long lineId = lineDao.save(LINE);
        Section section = new Section(lineId, station, station2, 10);
        Long sectionId = sectionDao.save(section);
        assertThat(sectionDao.findById(sectionId))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(section);
    }

    @DisplayName("해당 지하철 노선 id의 지하철 구간(역 정보 포함)들을 조회한다.")
    @Test
    void findAllSectionsByLineId() {
        //given
        Station station = stationDao.findById(stationDao.save(STATION));
        Station station2 = stationDao.findById(stationDao.save(STATION_2));
        Station station3 = stationDao.findById(stationDao.save(STATION_3));
        Long lineId = lineDao.save(LINE);
        Section section1 = new Section(lineId, station, station2, 10);
        sectionDao.save(section1);
        Section section2 = new Section(lineId, station2, station3, 10);
        sectionDao.save(section2);

        //when
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        //then
        assertThat(sections)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(section1, section2));
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void delete() {
        //given
        Station station = stationDao.findById(stationDao.save(STATION));
        Station station2 = stationDao.findById(stationDao.save(STATION_2));
        Long lineId = lineDao.save(LINE);
        Section section = new Section(lineId, station, station2, 10);
        Long id = sectionDao.save(section);
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
        Station station = stationDao.findById(stationDao.save(STATION));
        Station station2 = stationDao.findById(stationDao.save(STATION_2));
        Long lineId = lineDao.save(LINE);
        Section section = new Section(lineId, station, station2, 10);
        Long id = sectionDao.save(section);

        //when
        sectionDao.deleteAllByLineId(lineId);

        //then
        assertThat(sectionDao.findAllByLineId(lineId))
                .hasSize(0);
    }

    @DisplayName("해당 아이디의 구간이 있는지 확인한다.")
    @Test
    void hasSection() {
        Station station = stationDao.findById(stationDao.save(STATION));
        Station station2 = stationDao.findById(stationDao.save(STATION_2));
        Long lineId = lineDao.save(LINE);
        Section section = new Section(lineId, station, station2, 10);
        Long id = sectionDao.save(section);

        //when then
        assertThat(sectionDao.hasSection(id))
                .isTrue();
        assertThat(sectionDao.hasSection(100L))
                .isFalse();
    }
}
