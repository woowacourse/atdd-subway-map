package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.domain.Station;

@JdbcTest
class StationServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        this.stationService = new StationService(new JdbcStationDao(jdbcTemplate));
    }

    @Test
    @DisplayName("이미 존재하는 역 이름이 있을 때 예외가 발생한다.")
    void saveExceptionByDuplicatedName() {
        stationService.save(new Station("오리"));
        assertThatThrownBy(() -> stationService.save(new Station("오리")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 역 이름입니다.");
    }
}
