package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineCreateResponse;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class LineCreateServiceTest {

    private final LineCreateService lineCreateService;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineCreateServiceTest(LineCreateService sectionService, SectionDao sectionDao, StationDao stationDao) {
        this.lineCreateService = sectionService;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @AfterEach
    void reset() {
        stationDao.deleteAll();
        sectionDao.deleteAll();
    }

    @DisplayName("station 이 하나 없는 경우, Line 생성에 실패한다")
    @Test
    void stationNotExistLineCreateFail() {
        LineCreateRequest request = new LineCreateRequest("2호선", "bg-green", 1L, 2L, 3);

        assertThatThrownBy(() -> lineCreateService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 역입니다.");
    }

    @DisplayName("section 길이가 0보다 작거나 같은 경우 생성에 실패한다")
    @Test
    void sectionLengthLineCreateFail() {
        stationDao.save("선릉역");
        stationDao.save("잠실역");

        LineCreateRequest request = new LineCreateRequest("2호선", "bg-green", 1L, 2L, 0);

        assertThatThrownBy(() -> lineCreateService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 종점간의 거리는 0보다 커야합니다.");
    }

    @DisplayName("Line 이 성공적으로 생성된다")
    @Test
    void lineCreateSuccessfully() {
        stationDao.save("선릉역");
        stationDao.save("잠실역");

        String lineName = "2호선";
        String lineColor = "bg-green";

        LineCreateRequest request = new LineCreateRequest(lineName, lineColor, 1L, 2L, 1);
        LineCreateResponse response = lineCreateService.create(request);

        assertThat(response.getId()).isNotEqualTo(0L);
        assertThat(response.getName()).isEqualTo(lineName);
        assertThat(response.getColor()).isEqualTo(lineColor);
        assertThat(response.getStations().size()).isEqualTo(2);
    }
}
