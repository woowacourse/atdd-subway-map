package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.StationCreateRequest;
import wooteco.subway.dto.StationCreateResponse;

@SpringBootTest
@Transactional
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Test
    @DisplayName("중복된 이름으로 지하철역을 생성할 수 없다.")
    void save() {
        // given
        final StationCreateRequest request = new StationCreateRequest("지하철역이름");
        stationService.save(request);

        // when & then
        assertThatThrownBy(() -> stationService.save(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지하철역을 전체 조회할 수 있다.")
    void findAll() {
        // given
        final StationCreateRequest station1 = new StationCreateRequest("지하철역이름");
        final StationCreateRequest station2 = new StationCreateRequest("새로운지하철역이름");
        final StationCreateRequest station3 = new StationCreateRequest("또다른지하철역이름");

        stationService.save(station1);
        stationService.save(station2);
        stationService.save(station3);

        // when
        final List<StationCreateResponse> stations = stationService.findAll();

        // then
        assertThat(stations).hasSize(3)
                .extracting("name")
                .contains("지하철역이름", "새로운지하철역이름", "또다른지하철역이름");
    }

    @Test
    @DisplayName("지하철역을 삭제할 수 있다.")
    void deleteById() {
        // given
        final StationCreateRequest request = new StationCreateRequest("지하철역이름");
        final StationCreateResponse response = stationService.save(request);

        // when & then
        assertDoesNotThrow(() -> stationService.deleteById(response.getId()));
    }
}
