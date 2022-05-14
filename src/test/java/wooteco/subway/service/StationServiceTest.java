package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@SpringBootTest
@Sql("/truncate.sql")
class StationServiceTest {

    private final StationService stationService;

    @Autowired
    public StationServiceTest(StationService stationService) {
        this.stationService = stationService;
    }

    @DisplayName("지하쳘역을 저장한다.")
    @Test
    void 지하철역_저장() {
        String name = "선릉역";
        StationRequest stationRequest = new StationRequest(name);

        StationResponse stationResponse = stationService.save(stationRequest);

        assertThat(stationResponse.getName()).isEqualTo(name);
    }

    @DisplayName("중복된 이름의 지하철역을 저장할 경우 예외를 발생시킨다.")
    @Test
    void 중복된_지하철역_저장_예외발생() {
        String name = "선릉역";
        StationRequest stationRequest = new StationRequest(name);

        stationService.save(stationRequest);

        assertThatThrownBy(() -> stationService.save(stationRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(name + "은 이미 존재하는 지하철역 이름입니다.");
    }

    @DisplayName("다수의 지하철역을 조회한다.")
    @Test
    void 다수_지하철역_조회() {
        stationService.save(new StationRequest("강남역"));
        stationService.save(new StationRequest("선릉역"));

        List<StationResponse> stations = stationService.findAll();

        assertThat(stations.size()).isEqualTo(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void 지하철역_삭제() {
        StationResponse stationResponse = stationService.save(new StationRequest("신림역"));

        stationService.deleteById(stationResponse.getId());

        assertThat(stationService.findAll().size()).isEqualTo(0);
    }
}
