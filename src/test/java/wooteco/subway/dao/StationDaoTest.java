package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.dao.DaoFixtures.강남역;
import static wooteco.subway.dao.DaoFixtures.역삼역;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
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

    @Autowired
    public StationDaoTest(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new SubwaySectionDao(jdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("역을 저장하면 auto increment 된 역을 반환한다.")
    void save() {
        Station station = stationDao.save(강남역);
        assertAll(
                () -> assertThat(station.getId()).isInstanceOf(Long.class),
                () -> assertThat(station.getName()).isEqualTo("강남역")
        );
    }

    @Test
    @DisplayName("저장된 모든 역을 반환한다.")
    void findAll() {
        stationDao.save(강남역);
        stationDao.save(역삼역);
        List<Station> stations = stationDao.findAll();
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("이름에 따라 저장된 역이 있는지 확인한다.")
    void existsByName() {
        stationDao.save(강남역);
        assertAll(
                () -> assertThat(stationDao.existsByName("강남역")).isTrue(),
                () -> assertThat(stationDao.existsByName("역삼역")).isFalse()
        );
    }

    @Test
    @DisplayName("Id에 따라 역을 삭제한다.")
    void deleteById() {
        Station station = stationDao.save(강남역);
        int affectedQuery = stationDao.deleteById(station.getId());
        assertThat(affectedQuery).isEqualTo(1);
    }

    @Test
    @DisplayName("Id에 따라 역을 찾는다.")
    void findById() {
        Station savedStation = stationDao.save(강남역);
        Station station = stationDao.findById(savedStation.getId());
        assertThat(station.getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("Id에 따라 저장된 역이 있는지 확인한다.")
    void nonExistsById() {
        Station station = stationDao.save(강남역);
        assertAll(
                () -> assertThat(stationDao.nonExistsById(station.getId())).isFalse(),
                () -> assertThat(stationDao.nonExistsById(station.getId() + 100)).isTrue()
        );
    }

    @Test
    @DisplayName("노선 Id에 따라 저장된 역을 반환한다.")
    void findByLineId() {
        Station 강남역 = stationDao.save(new Station("강남역"));
        Station 역삼역 = stationDao.save(new Station("역삼역"));
        Station 잠실역 = stationDao.save(new Station("잠실역"));
        Station 선릉역 = stationDao.save(new Station("선릉역"));
        Line 분당선 = lineDao.save(new Line("분당선", "노랑이"));
        Line 호선2 = lineDao.save(new Line("2호선", "초록"));
        sectionDao.save(new Section(분당선, 강남역, 역삼역, 5));
        sectionDao.save(new Section(분당선, 역삼역, 선릉역, 5));
        sectionDao.save(new Section(호선2, 잠실역, 선릉역, 5));
        List<Station> stations = stationDao.findAllByLineId(분당선.getId());
        assertThat(stations.size()).isEqualTo(3);
    }
}
