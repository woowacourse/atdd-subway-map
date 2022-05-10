package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Station;
import wooteco.subway.dto.info.LineInfo;
import wooteco.subway.dto.info.RequestLineInfo;
import wooteco.subway.dto.info.ResponseLineInfo;

public class LineServiceTest {

    private LineService lineService;
    private FakeLineDao fakeLineDao;
    private FakeSectionDao fakeSectionDao;
    private FakeStationDao fakeStationDao;

    @BeforeEach
    void setUp() {
        fakeLineDao = new FakeLineDao();
        fakeSectionDao = new FakeSectionDao();
        fakeStationDao = new FakeStationDao();
        lineService = new LineService(fakeLineDao, fakeSectionDao, fakeStationDao);

        fakeStationDao.save(new Station("강남역"));
        fakeStationDao.save(new Station("선릉역"));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        RequestLineInfo lineInfoToRequest = new RequestLineInfo("2호선", "red", 1L, 2L, 10);
        ResponseLineInfo lineInfoToResponse = lineService.save(lineInfoToRequest);

        assertThat(lineInfoToResponse.getName()).isEqualTo(lineInfoToRequest.getName());
    }

    @DisplayName("중복된 이름으로 지하철 노선 생성 요청 시 예외를 던진다.")
    @Test
    void createLineWithDuplicateName() {
        RequestLineInfo lineInfoToRequest = new RequestLineInfo("2호선", "red", 1L, 2L, 10);
        lineService.save(lineInfoToRequest);

        assertThatThrownBy(() -> lineService.save(lineInfoToRequest)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("중복된 지하철 노선 이름입니다.");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        RequestLineInfo lineInfoToRequest = new RequestLineInfo("2호선", "red", 1L, 2L, 10);
        RequestLineInfo lineInfoToRequest2 = new RequestLineInfo("3호선", "red", 1L, 2L, 10);
        lineService.save(lineInfoToRequest);
        lineService.save(lineInfoToRequest2);

        assertThat(lineService.findAll()).hasSize(2);
    }

    @DisplayName("지하철 노선 조회한다.")
    @Test
    void getLine() {
        RequestLineInfo lineInfoToRequest = new RequestLineInfo("2호선", "red", 1L, 2L, 10);
        ResponseLineInfo lineInfoToResponse = lineService.save(lineInfoToRequest);

        assertThat(lineService.find(lineInfoToResponse.getId()).getName()).isEqualTo(lineInfoToRequest.getName());
    }

    @DisplayName("존재하지 않는 지하철 노선 조회 요청 시 예외를 던진다.")
    @Test
    void getLineNotExists() {
        assertThatThrownBy(() -> lineService.find(1L)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 지하철 노선 id입니다.");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        RequestLineInfo lineInfoToRequest = new RequestLineInfo("2호선", "red", 1L, 2L, 10);
        ResponseLineInfo lineInfoToResponse = lineService.save(lineInfoToRequest);

        LineInfo lineInfoToRequest2 = new LineInfo(lineInfoToResponse.getId(), "3호선", "red");
        lineService.update(lineInfoToRequest2);
        assertThat(lineService.find(lineInfoToResponse.getId()).getName()).isEqualTo(lineInfoToRequest2.getName());
    }

    @DisplayName("존재하지 않는 지하철 노선 수정 요청 시 예외를 던진다.")
    @Test
    void updateLineNotExists() {
        LineInfo lineInfo = new LineInfo(1L, "2호선", "green");
        assertThatThrownBy(() -> lineService.update(lineInfo)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 지하철 노선 id입니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        RequestLineInfo lineInfoToRequest = new RequestLineInfo("2호선", "red", 1L, 2L, 10);
        ResponseLineInfo lineInfoToResponse = lineService.save(lineInfoToRequest);

        lineService.delete(lineInfoToResponse.getId());
        assertThat(lineService.findAll()).hasSize(0);
    }

    @DisplayName("존재하지 않는 지하철 노선 삭제 요청 시 예외를 던진다.")
    @Test
    void deleteLineNotExists() {
        assertThatThrownBy(() -> lineService.delete(1L)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 지하철 노선 id입니다.");
    }
}
