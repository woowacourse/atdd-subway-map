package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.dao.StationDao;

class StationServiceTest {

    private final StationService service = new StationService(new StationDao());

    @DisplayName("역 이름을 입력받아서 해당 이름을 가진 역을 등록한다.")
    @Test
    void register() {
        final Station created = service.register("선릉역");

        assertThat(created.getName()).isEqualTo("선릉역");
    }
}