package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

class StationServiceTest {

    private final StationDao stationDao = StationDao.getInstance();
    private final StationService stationService = new StationService(stationDao);

    @AfterEach
    void afterEach() {
        stationDao.clear();
    }

    @Test
    @DisplayName("이미 존재하는 역 이름이 있을 때 예외가 발생한다.")
    void saveExceptionByDuplicatedName() {
        stationDao.save(new Station("오리"));
        assertThatThrownBy(() -> stationService.save(new Station("오리")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 역 이름입니다.");
    }
}
