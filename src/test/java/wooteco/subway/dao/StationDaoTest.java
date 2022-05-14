package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class StationDaoTest {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    private Station savedStation;

    @Autowired
    StationDaoTest(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.stationDao = new StationDao(namedParameterJdbcTemplate);
        this.lineDao = new LineDao(namedParameterJdbcTemplate);
        this.sectionDao = new SectionDao(namedParameterJdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        savedStation = stationDao.save(new Station("강남역"));
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void saveStation() {
        Station station = new Station("아차산역");
        Station savedStation = stationDao.save(station);

        assertAll(
                () -> assertThat(savedStation.getId()).isNotZero(),
                () -> assertThat(savedStation.getName()).isEqualTo("아차산역")
        );
    }

    @DisplayName("특정 지하철역을 이름으로 조회한다.")
    @Test
    void findByName() {
        Optional<Station> wrappedStation = stationDao.findByName("강남역");
        assert (wrappedStation).isPresent();

        assertAll(
                () -> assertThat(wrappedStation.get().getId()).isNotZero(),
                () -> assertThat(wrappedStation.get().getName()).isEqualTo("강남역")
        );
    }

    @DisplayName("특정 지하철역을 삭제한다.")
    @Test
    void deleteById() {
        stationDao.deleteById(savedStation.getId());

        Optional<Station> wrappedStation = stationDao.findById(savedStation.getId());
        assertThat(wrappedStation).isEmpty();
    }

    @DisplayName("특정 지하철역을 아이디로 조회한다.")
    @Test
    void findById() {
        Optional<Station> wrappedStation = stationDao.findById(savedStation.getId());
        assert (wrappedStation).isPresent();

        assertAll(
                () -> assertThat(wrappedStation.get().getId()).isEqualTo(savedStation.getId()),
                () -> assertThat(wrappedStation.get().getName()).isEqualTo(savedStation.getName())
        );
    }

    @DisplayName("특정 노선에 포함되는 지하철역들을 조회한다.")
    @Test
    void findAllByLineId() {
        Station newStation = stationDao.save(new Station("아차산역"));
        Line savedLine = lineDao.save(new Line("5호선", "bg-purple-600"));
        sectionDao.save(new Section(savedStation, newStation, 10, savedLine.getId()));

        List<Station> savedStations = stationDao.findAllByLineId(savedLine.getId());

        assertThat(savedStations).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(savedStation, newStation));
    }
}
