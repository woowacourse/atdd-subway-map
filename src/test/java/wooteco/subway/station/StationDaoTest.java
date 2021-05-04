package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DuplicateException;

class StationDaoTest {

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao();
    }

    @Test
    @DisplayName("역 한개가 저장된다.")
    void save() {
        Station station = stationDao.save(new Station("잠실역"));
        assertThat(station.getId()).isEqualTo(1L);
        assertThat(station.getName()).isEqualTo("잠실역");
    }

    @Test
    @DisplayName("중복된 이름을 갖는 역은 저장이 안된다.")
    void duplicateSaveValidate() {
        Station station = new Station("잠실역");
        stationDao.save(station);

        assertThatThrownBy(() -> {
            stationDao.save(station);
        }).isInstanceOf(DuplicateException.class);
    }

    @Test
    void findAll() {

    }

}