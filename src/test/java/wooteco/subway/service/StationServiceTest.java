package wooteco.subway.service;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.ClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Test
    @DisplayName("역정보 저장")
    void save() {
        StationRequest station = new StationRequest("역삼역");
        StationResponse newStation = stationService.save(station);

        assertThat(station.getName()).isEqualTo(newStation.getName());
    }

    @DisplayName("중복된 역이름 저장시 예외")
    @Test
    void duplicateStation() {
        StationRequest station = new StationRequest("역삼역");
        StationRequest duplicateStation = new StationRequest("역삼역");
        stationService.save(station);

        assertThatThrownBy(() -> stationService.save(duplicateStation))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("이미 등록된 지하철역입니다.");
    }

    @Test
    @DisplayName("역 정보들 조회")
    void findAll() {
        StationRequest firstStation = new StationRequest("역삼역");
        StationRequest secondStation = new StationRequest("삼성역");
        stationService.save(firstStation);
        stationService.save(secondStation);

        List<StationResponse> stations = stationService.findAll();
        StationResponse stationResponse = stations.stream()
                .filter(station -> station.getName().equals("역삼역"))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지하철 정보가 없습니다."));

        assertThat(stationResponse.getName()).isEqualTo(firstStation.getName());
    }

    @Test
    @DisplayName("역 정보를 삭제")
    void deleteStation() {
        StationRequest station = new StationRequest("역삼역");
        StationResponse newStation = stationService.save(station);

        assertThat(stationService.delete(newStation.getId())).isEqualTo(1);
    }
}
