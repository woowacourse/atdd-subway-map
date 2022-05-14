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
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class LineServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    private LineService lineService;

    private StationResponse savedStation1;
    private StationResponse savedStation2;

    @BeforeEach
    void setUp() {
        StationService stationService = new StationService(new StationDao(jdbcTemplate, dataSource));
        lineService = new LineService(new LineDao(jdbcTemplate, dataSource),
                new StationDao(jdbcTemplate, dataSource), new SectionDao(jdbcTemplate, dataSource));
        savedStation1 = stationService.create(new StationRequest("선릉역"));
        savedStation2 = stationService.create(new StationRequest("선정릉역"));
    }

    @DisplayName("새로운 노선 셍성 정보를 이용해 노선을 생성한다.")
    @Test
    void create() {
        LineRequest request =
                new LineRequest("신분당선", "red", savedStation1.getId(), savedStation2.getId(), 10);

        LineResponse response = lineService.create(request);

        assertAll(
                () -> assertThat(response.getName()).isEqualTo(request.getName()),
                () -> assertThat(response.getColor()).isEqualTo(request.getColor()),
                () -> assertThat(response.getStations()).hasSize(2)
        );
    }

    @DisplayName("존재하지 않는 역으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenCreateLineWithNotExistsStation() {
        LineRequest request =
                new LineRequest("신분당선", "red", savedStation1.getId(), 100L, 10);

        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행,하행 중복된 역으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenCreateLineWithDuplicateStation() {
        LineRequest request =
                new LineRequest("신분당선", "red", savedStation1.getId(), savedStation1.getId(), 10);

        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("거리가 0이하인 구간으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void throwsExceptionWhenCreateLineWithZeroDistance() {
        LineRequest request =
                new LineRequest("신분당선", "red", savedStation1.getId(), savedStation1.getId(), 10);

        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
