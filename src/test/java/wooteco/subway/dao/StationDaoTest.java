package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    private final SectionDao<Section> sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    private Station 강남역;
    private Station 역삼역;
    private Station 잠실역;
    private Station 선릉역;
    private Line 분당선;

    @Autowired
    public StationDaoTest(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new SubwaySectionDao(jdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        강남역 = stationDao.save(new Station("강남역"));
        역삼역 = stationDao.save(new Station("역삼역"));
        잠실역 = stationDao.save(new Station("잠실역"));
        선릉역 = stationDao.save(new Station("선릉역"));
        분당선 = lineDao.save(new Line("분당선", "노랑이"));
        Line 호선2 = lineDao.save(new Line("2호선", "초록"));
        sectionDao.save(new Section(분당선, 강남역, 역삼역, 5));
        sectionDao.save(new Section(분당선, 역삼역, 선릉역, 5));
        sectionDao.save(new Section(호선2, 잠실역, 선릉역, 5));
    }

    @Test
    void findByLineId() {
        List<Station> stations = stationDao.findByLineId(분당선.getId());
        assertThat(stations.size()).isEqualTo(3);
    }
}