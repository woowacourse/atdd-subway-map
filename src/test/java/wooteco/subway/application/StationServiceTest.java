package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.BlankArgumentException;
import wooteco.subway.exception.NotExistStationException;

class StationServiceTest {

    private StationService stationService = new StationService();

    @BeforeEach
    void setUp() {
        StationDao.deleteAll();
    }

    @DisplayName("지하철역 저장")
    @Test
    void saveByName() {
        String stationName = "something";
        Station station = stationService.saveByName(stationName);
        assertThat(StationDao.findById(station.getId())).isNotEmpty();
    }

    @DisplayName("중복된 지하철역 저장")
    @Test
    void saveByDuplicateName() {
        String stationName = "something";
        stationService.saveByName(stationName);

        assertThatThrownBy(() -> stationService.saveByName(stationName))
            .isInstanceOf(DuplicateException.class);
    }

    @DisplayName("지하철 역 이름에 빈 문자열을 저장할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveByEmptyName(String stationName) {
        assertThatThrownBy(() -> stationService.saveByName(stationName))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 역 삭제")
    @Test
    void deleteById() {
        Station station = stationService.saveByName("강남역");

        stationService.deleteById(station.getId());

        assertThat(StationDao.existByName("강남역")).isFalse();
    }

    @DisplayName("존재하지 않는 지하철 역 삭제")
    @Test
    void deleteNotExistStation() {
        assertThatThrownBy(() -> stationService.deleteById(50L))
            .isInstanceOf(NotExistStationException.class);
    }
}
