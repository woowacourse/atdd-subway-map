package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.StationResponse;

class StationServiceTest {

    private final StationService stationService = new StationService();

    @Test
    @DisplayName("지하철역 추가, 조회, 삭제 테스트")
    void StationCRDTest() {
        stationService.save("station1");
        stationService.save("station2");
        stationService.save("station3");

        List<StationResponse> stations = stationService.findAll();

        assertThat(stations).hasSize(3)
                .extracting("name")
                .containsExactly("station1", "station2", "station3");

        stationService.delete(stations.get(0).getId());
        stationService.delete(stations.get(1).getId());
        stationService.delete(stations.get(2).getId());

        assertThat(StationDao.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("중복된 이름 입력 시 예외 발생 테스트")
    void validateDuplicationNameTest() {
        stationService.save("station1");

        assertThatThrownBy(() -> stationService.save("station1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 역 이름입니다.");
    }

    @Test
    @DisplayName("없는 역을 제거하면 예외가 발생한다.")
    void deleteNotExistStation() {
        assertThatThrownBy(() -> stationService.delete(0l))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 역입니다.");
    }
}
