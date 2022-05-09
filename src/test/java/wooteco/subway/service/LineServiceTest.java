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
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class LineServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    private LineService lineService;
    private SectionDao sectionDao;

    private StationResponse savedStation1;
    private StationResponse savedStation2;
    private StationResponse savedStation3;

    @BeforeEach
    void setUp() {
        StationService stationService = new StationService(new StationDao(jdbcTemplate, dataSource));
        lineService = new LineService(new LineDao(jdbcTemplate, dataSource),
                new SectionDao(jdbcTemplate, dataSource), new StationDao(jdbcTemplate, dataSource));
        savedStation1 = stationService.create(new StationRequest("선릉역"));
        savedStation2 = stationService.create(new StationRequest("선정릉역"));
        savedStation3 = stationService.create(new StationRequest("한티역"));
    }

    @DisplayName("새로운 노선 셍성 정보를 이용해 노선을 생성한다.")
    @Test
    void create() {
        String lineName = "신분당선";
        String lineColor = "red";
        int distance = 10;
        LineRequest request = new LineRequest(lineName, lineColor, savedStation1.getId(), savedStation2.getId(), distance);

        LineResponse response = lineService.create(request);

        assertAll(
                () -> assertThat(response.getName()).isEqualTo(lineName),
                () -> assertThat(response.getColor()).isEqualTo(lineColor),
                () -> assertThat(response.getStations()).hasSize(2)
        );
    }
}
