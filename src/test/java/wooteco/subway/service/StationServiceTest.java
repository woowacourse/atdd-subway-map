package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@SpringBootTest
class StationServiceTest extends ServiceTest {

    private final StationService stationService;

    @Autowired
    public StationServiceTest(StationService stationService) {
        this.stationService = stationService;
    }

    @DisplayName("지하철 역을 저장한다.")
    @Test
    void save() {
        StationRequest stationRequest = new StationRequest("선릉역");

        StationResponse stationResponse = stationService.save(stationRequest);

        assertThat(stationResponse.getName()).isEqualTo(stationRequest.getName());
    }

    @DisplayName("같은 이름의 지하철 역을 저장하는 경우 예외가 발생한다.")
    @Test
    void saveExistingName() {
        StationRequest stationRequest = new StationRequest("선릉역");

        stationService.save(stationRequest);

        assertThatThrownBy(() -> stationService.save(stationRequest))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("모든 지하철 역을 조회한다.")
    @Test
    void findAll() {
        StationRequest station1 = new StationRequest("선릉역");
        StationRequest station2 = new StationRequest("잠실역");
        StationRequest station3 = new StationRequest("사우역");

        stationService.save(station1);
        stationService.save(station2);
        stationService.save(station3);

        assertThat(stationService.findAll().size()).isEqualTo(3);
    }

    @DisplayName("지하철 역을 삭제한다.")
    @Test
    void deleteById() {
        StationRequest stationRequest = new StationRequest("선릉역");
        StationResponse stationResponse = stationService.save(stationRequest);

        stationService.deleteById(stationResponse.getId());

        assertThat(stationService.findAll().size()).isZero();
    }
}
