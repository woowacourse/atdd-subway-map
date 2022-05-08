package wooteco.subway.dao.section;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.line.JdbcLineDao;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.station.JdbcStationDao;
import wooteco.subway.dao.station.StationDao;
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
        long id = lineDao.save(new Line("신분당선", "bg-red-600"));
        Station upStation = stationDao.findById(stationDao.save(new Station("오리")));
        Station downStation = stationDao.findById(stationDao.save(new Station("배카라")));
        Section section = new Section(null, id, upStation, downStation, 1);

        // when
        long savedSectionId = sectionDao.save(section);

        // then
        assertThat(savedSectionId).isNotNull();
    }

    @Test
    @DisplayName("Line Id에 해당하는 Section을 조회할 수 있다.")
    void findAllByLineId() {
        long id = lineDao.save(new Line("신분당선", "bg-red-600"));
        Station station1 = stationDao.findById(stationDao.save(new Station("오리")));
        Station station2 = stationDao.findById(stationDao.save(new Station("배카라")));
        Station station3 = stationDao.findById(stationDao.save(new Station("오카라")));

        sectionDao.save(new Section(id, station1, station2, 2));
        sectionDao.save(new Section(id, station2, station3, 2));

        assertThat(sectionDao.findAllByLineId(id)).hasSize(2);
    }

    @Test
    @DisplayName("Sections를 업데이트할 수 있다.")
    void updateSections() {
        long id = lineDao.save(new Line("신분당선", "bg-red-600"));
        Station station1 = stationDao.findById(stationDao.save(new Station("오리")));
        Station station2 = stationDao.findById(stationDao.save(new Station("배카라")));
        Station station3 = stationDao.findById(stationDao.save(new Station("오카라")));

        long sectionId1 = sectionDao.save(new Section(id, station1, station2, 1));
        long sectionId2 = sectionDao.save(new Section(id, station2, station3, 1));

        List<Section> sections = List.of(new Section(sectionId1, id, station1, station3, 3),
                new Section(sectionId2, id, station2, station3, 1));

        assertThat(sectionDao.updateSections(sections)).isEqualTo(2);
    }

    @Test
    @DisplayName("Section을 삭제할 수 있다.")
    void delete() {
        long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
        Station station1 = stationDao.findById(stationDao.save(new Station("오리")));
        Station station2 = stationDao.findById(stationDao.save(new Station("배카라")));
        long sectionId = sectionDao.save(new Section(lineId, station1, station2, 10));

        assertThat(sectionDao.delete(sectionId)).isEqualTo(1);
    }
}
