package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.dao.StationDao;

class StationServiceTest {

    private final StationDao stationDao = new StationDao();
    private final StationService service = new StationService(stationDao);

    @BeforeEach
    void setUp() {
        stationDao.removeAll();
    }

    @DisplayName("역 이름을 입력받아서 해당 이름을 가진 역을 등록한다.")
    @Test
    void register() {
        final Station created = service.register("선릉역");

        assertThat(created.getName()).isEqualTo("선릉역");
    }

    @DisplayName("이미 존재하는 역이름으로 등록하려할 시 예외가 발생한다.")
    @Test
    void registerDuplicateName() {
        service.register("선릉역");

        assertThatThrownBy(() -> service.register("선릉역"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 이미 존재하는 역이름입니다.");
    }
}