package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;

@JdbcTest
class StationDaoTest {

    private final StationDao stationDao;

    @Autowired
    public StationDaoTest(JdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("지하철역을 저장한다.")
    @Test
    void 지하철역_저장() {
        String stationName = "서울대입구역";
        Station station = new Station(stationName);

        Station savedStation = stationDao.save(station);

        assertThat(savedStation.getName()).isEqualTo(stationName);
    }

    @DisplayName("지하철역 이름으로 지하철역 이름 유무를 반환한다.")
    @ParameterizedTest
    @CsvSource({"강남역,true", "역삼역,false"})
    void 지하철역_이름_유무(String name, boolean expected) {
        stationDao.save(new Station("강남역"));

        boolean result = stationDao.existsByName(name);

        assertThat(result).isEqualTo(expected);
    }
    
    @DisplayName("중복된 지하철역을 저장할 경우 예외가 발생한다.")
    @Test
    void 중복된_지하철역_예외발생() {
        String stationName = "중동역";
        Station station = new Station(stationName);

        stationDao.save(station);

        assertThatThrownBy(() -> stationDao.save(station))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("존재하지 않는 지하철역을 조회할 경우 예외가 발생한다.")
    @Test
    void 지하철역_조회_예외발생() {
        Optional<Station> station = stationDao.findById(0L);

        assertThat(station).isEmpty();
    }

    @DisplayName("모든 지하철역을 조회한다.")
    @Test
    void 모든_지하철역_조회() {
        stationDao.save(new Station("중동역"));
        stationDao.save(new Station("상수역"));
        stationDao.save(new Station("서울대입구역"));

        assertThat(stationDao.findAll().size()).isEqualTo(3);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void 지하철역_삭제() {
        Station station = stationDao.save(new Station("서울대입구역"));

        stationDao.deleteById(station.getId());

        assertThat(stationDao.findAll().size()).isEqualTo(0);
    }
}
