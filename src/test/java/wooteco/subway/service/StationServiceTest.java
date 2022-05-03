package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.StationDao;

class StationServiceTest {

    @Test
    @DisplayName("지하철 역을 저장할 수 있다.")
    void saveStation(){
        StationService.createStation("강남역");

        Assertions.assertThat(StationDao.findByName("강남역")).isEqualTo(true);
    }

    @Test
    @DisplayName("지하철 역을 저장할 수 있다.")
    void NonSaveDuplicateStation(){
        StationService.createStation("역삼역");
        
        Assertions.assertThatThrownBy(() -> StationService.createStation("역삼역"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}