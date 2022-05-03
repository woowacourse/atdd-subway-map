package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.StationDao;

import java.util.NoSuchElementException;

class StationServiceTest {

    @Test
    @DisplayName("지하철 역을 저장할 수 있다.")
    void saveStation() {
        StationService.createStation("강남역");

        Assertions.assertThat(StationDao.existStationByName("강남역")).isEqualTo(true);
    }

    @Test
    @DisplayName("중복된 지하철 역을 저장할 수 없다.")
    void NonSaveDuplicateStation() {
        StationService.createStation("역삼역");

        Assertions.assertThatThrownBy(() -> StationService.createStation("역삼역"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("역 삭제 성공")
    void deleteStation() {
        StationService.createStation("용문역");
        StationService.deleteStation("용문역");

        Assertions.assertThat(StationDao.existStationByName("용문역")).isFalse();
    }

    @Test
    @DisplayName("역 삭제 실패")
    void failDeleteStation() {
        Assertions.assertThatThrownBy(() -> StationService.deleteStation("선릉역"))
                .isInstanceOf(NoSuchElementException.class);
    }
}
