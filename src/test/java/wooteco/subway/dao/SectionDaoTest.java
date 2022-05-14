package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.jdbc.LineJdbcDao;
import wooteco.subway.dao.jdbc.SectionJdbcDao;
import wooteco.subway.dao.jdbc.StationJdbcDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;
    private LineDao lineDao;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionJdbcDao(jdbcTemplate);
        lineDao = new LineJdbcDao(jdbcTemplate);
        stationDao = new StationJdbcDao(jdbcTemplate);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Line line = lineDao.save(new Line("line", "color"));
        Station station1 = stationDao.save(new Station("station1"));
        Station station2 = stationDao.save(new Station("station2"));
        Section section = new Section(station1, station2, 10);

        // when & then
        assertThat(sectionDao.save(line.getId(), section)).isNotNull();
    }

    @DisplayName("line id로 노선에 포함된 구간들을 찾는다")
    @Test
    void findSectionByLineId() {
        // given
        Line line = new Line("line", "color");
        Line savedLine = lineDao.save(line);

        Station upStation = new Station("upStation");
        Station downStation = new Station("downStation");
        Station savedUpStation = stationDao.save(upStation);
        Station savedDownStation = stationDao.save(downStation);

        Section section = new Section(savedUpStation, savedDownStation, 10);
        Section savedSection = sectionDao.save(savedLine.getId(), section);

        // when
        List<Section> sections = sectionDao.findByLineId(savedLine.getId());

        // then
        assertThat(sections).containsExactly(savedSection);
    }

    @DisplayName("구간 정보를 업데이트한다.")
    @Test
    void update() {
        // given
        Line line = new Line("line", "color");
        Line savedLine = lineDao.save(line);

        Station station1 = stationDao.save(new Station("station1"));
        Station station2 = stationDao.save(new Station("station2"));
        Station station3 = stationDao.save(new Station("station3"));
        Station station4 = stationDao.save(new Station("station4"));

        Section section = new Section(station1, station2, 10);
        Section savedSection = sectionDao.save(savedLine.getId(), section);

        // when
        Section updateSection = new Section(savedSection.getId(), station3, station4, 5);
        sectionDao.batchUpdate(savedLine.getId(), List.of(updateSection));

        // then
        List<Section> sections = sectionDao.findByLineId(savedLine.getId());
        assertThat(sections).containsExactly(updateSection);
    }
}
