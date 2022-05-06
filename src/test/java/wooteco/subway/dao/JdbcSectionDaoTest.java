package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class JdbcSectionDaoTest {

    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);
        stationDao = new JdbcStationDao(jdbcTemplate);
        lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Section 을 저장할 수 있다.")
    void save() {
        // given
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
        Station upStation = stationDao.save(new Station("오리"));
        Station downStation = stationDao.save(new Station("배카라"));
        Section section = new Section(null, line.getId(), upStation, downStation, 1);

        // when
        Section savedSection = sectionDao.save(section);

        // then
        assertThat(savedSection.getId()).isNotNull();
    }

    @Nested
    @DisplayName("이미 존재하는 upStation, downStation 인지 확인할 수 있다.")
    class ExistByUpStationAndDownStation {

        @Test
        void isTrue() {
            // given
            Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
            Station upStation = stationDao.save(new Station("오리"));
            Station downStation = stationDao.save(new Station("배카라"));
            Section section = new Section(null, line.getId(), upStation, downStation, 1);
            sectionDao.save(section);

            // when & then
            assertThat(sectionDao.existByUpStationAndDownStation(upStation, downStation)).isTrue();
        }

        @Test
        void isFalse() {
            Station upStation = stationDao.save(new Station("오리"));
            Station downStation = stationDao.save(new Station("배카라"));

            assertThat(sectionDao.existByUpStationAndDownStation(upStation, downStation)).isFalse();
        }
    }

    @Test
    @DisplayName("Line Id에 해당하는 Section을 조회할 수 있다.")
    void findAllByLineId() {
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
        Station station1 = stationDao.save(new Station("오리"));
        Station station2 = stationDao.save(new Station("배카라"));
        Station station3 = stationDao.save(new Station("오카라"));

        List<Section> expected = List.of(sectionDao.save(new Section(null, line.getId(), station1, station2, 1)),
                sectionDao.save(new Section(null, line.getId(), station2, station3, 1)));

        assertThat(sectionDao.findAllByLineId(line.getId())).isEqualTo(expected);
    }
}
