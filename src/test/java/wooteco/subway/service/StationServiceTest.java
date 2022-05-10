package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class StationServiceTest {

    private final StationDao stationDao;
    private final StationService stationService;

    StationServiceTest(StationDao stationDao, StationService stationService) {
        this.stationDao = stationDao;
        this.stationService = stationService;
    }

    @Test
    @DisplayName("역을 생성한다.")
    void create() {
        StationRequest stationRequest = new StationRequest("강남역");

        StationResponse actual = stationService.create(stationRequest);

        assertThat(actual.getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("전체 역을 반환한다")
    void show() {
        stationDao.save("강남역");
        stationDao.save("선릉역");

        List<StationResponse> actual = stationService.show();

        assertThat(actual).hasSize(2);
    }

}
