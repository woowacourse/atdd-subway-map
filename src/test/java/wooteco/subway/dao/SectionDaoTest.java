package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class SectionDaoTest {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Autowired
    public SectionDaoTest(NamedParameterJdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDao(jdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void save() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));
        Line line = lineDao.save(new Line("2호선", "green"));

        Section section = new Section(line.getId(), upStation.getId(), downStation.getId(), 10);

        assertThat(sectionDao.save(section)).isNotNull();
    }

    @DisplayName("노선 id 로 구간을 찾아낸다.")
    @Test
    void findAllByLineId() {
        Station upStation = stationDao.save(new Station("강남역"));
        Station downStation = stationDao.save(new Station("선릉역"));
        Line line = lineDao.save(new Line("2호선", "green"));

        Section section = new Section(line.getId(), upStation.getId(), downStation.getId(), 10);

        sectionDao.save(section);

        assertThat(sectionDao.findAllByLineId(line.getId()).size()).isEqualTo(1);
    }
}