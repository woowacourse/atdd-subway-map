package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Test
    @DisplayName("중복된 이름으로 지하철역을 생성할 수 없다.")
    void save() {
        // given
        final StationRequest request = new StationRequest("강남역");
        stationService.save(request);

        // when & then
        assertThatThrownBy(() -> stationService.save(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지하철역을 전체 조회할 수 있다.")
    void findAll() {
        // given
        final StationRequest request1 = new StationRequest("강남역");
        final StationRequest request2 = new StationRequest("역삼역");
        final StationRequest request3 = new StationRequest("선릉역");

        stationService.save(request1);
        stationService.save(request2);
        stationService.save(request3);
        // when
        final List<StationResponse> stations = stationService.findAll();

        // then
        assertThat(stations).hasSize(3)
                .extracting("name")
                .contains("강남역", "역삼역", "선릉역");
    }

    @Test
    @DisplayName("지하철역을 삭제할 수 있다.")
    void deleteById() {
        // given
        final StationRequest request = new StationRequest("강남역");
        final StationResponse response = stationService.save(request);

        // when & then
        assertDoesNotThrow(() -> stationService.deleteById(response.getId()));
    }
}
