package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class SubwaySectionDaoTest {

    private final SectionDao<Section> sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    private Station 강남역;
    private Station 역삼역;
    private Line 분당선;

    @Autowired
    public SubwaySectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new SubwaySectionDao(jdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        강남역 = stationDao.save(new Station("강남역"));
        역삼역 = stationDao.save(new Station("역삼역"));
        분당선 = lineDao.save(new Line("분당선", "노랑이"));
    }

    @Test
    void delete() {
        //given
        Station 잠실역 = stationDao.save(new Station("잠실역"));
        sectionDao.save(new Section(분당선, 강남역, 역삼역, 5));
        sectionDao.save(new Section(분당선, 역삼역, 잠실역, 5));

        //when
        int affectedQueryCount = sectionDao.deleteSection(분당선.getId(), 강남역.getId());

        //then
        assertThat(affectedQueryCount).isEqualTo(1);
    }

    @Test
    void delete2() {
        //given
        Station 잠실역 = stationDao.save(new Station("잠실역"));
        sectionDao.save(new Section(분당선, 강남역, 역삼역, 5));
        sectionDao.save(new Section(분당선, 역삼역, 잠실역, 5));

        //when
        int affectedQueryCount = sectionDao.deleteSection(분당선.getId(), 역삼역.getId());

        //then
        assertThat(affectedQueryCount).isEqualTo(2);
    }
}