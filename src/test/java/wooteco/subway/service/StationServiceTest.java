package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.fake.FakeSectionDao;
import wooteco.subway.service.fake.FakeStationDao;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new FakeStationDao(), new FakeSectionDao());
    }

    @Test
    @DisplayName("중복된 이름으로 지하철역을 생성할 수 없다.")
    void save() {
        // given
        final StationRequest request = new StationRequest("지하철역이름");
        stationService.save(request);

        // when & then
        assertThatThrownBy(() -> stationService.save(request))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("지하철역을 전체 조회할 수 있다.")
    void findAll() {
        // given
        final StationRequest station1 = new StationRequest("지하철역이름");
        final StationRequest station2 = new StationRequest("새로운지하철역이름");
        final StationRequest station3 = new StationRequest("또다른지하철역이름");

        stationService.save(station1);
        stationService.save(station2);
        stationService.save(station3);

        // when
        final List<StationResponse> responses = stationService.findAll();

        // then
        assertThat(responses).hasSize(3)
                .extracting("name")
                .contains("지하철역이름", "새로운지하철역이름", "또다른지하철역이름");
    }

    @Test
    @DisplayName("지하철역을 삭제할 수 있다.")
    void deleteById() {
        // given
        final StationRequest request = new StationRequest("지하철역이름");
        final StationResponse response = stationService.save(request);

        // when & then
        assertDoesNotThrow(() -> stationService.deleteById(response.getId()));
    }
}
