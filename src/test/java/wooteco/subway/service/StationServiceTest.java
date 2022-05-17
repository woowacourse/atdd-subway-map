package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DeleteUsingDateException;
import wooteco.subway.exception.ExistKeyException;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@SuppressWarnings("NonAsciiCharacters")
class StationServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    private StationService stationService;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new StationDao(jdbcTemplate, dataSource));
        lineService = new LineService(new LineDao(jdbcTemplate, dataSource),
                new StationDao(jdbcTemplate, dataSource), new SectionDao(jdbcTemplate, dataSource));
    }

    @DisplayName("존재하는 역 이름으로 새로운 노선을 생성하려 시도하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenCreateStationWithExistName() {
        StationRequest request = new StationRequest("선릉역");
        stationService.create(request);

        assertThatThrownBy(() -> stationService.create(request))
                .isInstanceOf(ExistKeyException.class)
                .hasMessageMatching("요청하신 역의 이름은 이미 존재합니다.");
    }

    @DisplayName("삭제하려는 역이 노선에 포함되어있으면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenDeleteUsingStationInLine() {
        StationResponse 선릉역 = stationService.create(new StationRequest("선릉역"));
        StationResponse 선정릉역 = stationService.create(new StationRequest("선정릉역"));

        lineService.create(new LineRequest("분당선", "yellow", 선릉역.getId(),
                선정릉역.getId(), 10));

        assertThatThrownBy(() -> stationService.delete(선릉역.getId()))
                .isInstanceOf(DeleteUsingDateException.class);
    }
}
