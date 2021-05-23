package wooteco.subway.line.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.badrequest.DuplicatedNameException;
import wooteco.subway.exception.notfound.LineNotFoundException;
import wooteco.subway.line.controller.dto.LineNameColorResponse;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.controller.dto.SectionRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.infra.StationDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class LineServiceTest {
    private LineService lineService;
    private StationDao stationDao;

    @Autowired
    public LineServiceTest(LineService lineService, StationDao stationDao) {
        this.lineService = lineService;
        this.stationDao = stationDao;
    }

    @DisplayName("지하철 노선 추가한다.")
    @Test
    void saveLine() {
        //given
        LineRequest 노선_저장요청 = new LineRequest("1호선", "빨간색", 1L, 2L, 20);

        //when
        LineNameColorResponse 저장_후_응답 = lineService.saveLine(노선_저장요청);

        //then
        assertThat(저장_후_응답.getName()).isEqualTo(노선_저장요청.getName());
        assertThat(저장_후_응답.getColor()).isEqualTo(노선_저장요청.getColor());
    }

    @DisplayName("중복된 노선 이름을 저장할 경우 예외 처리 한다.")
    @Test
    void saveLine2() {
        //given
        LineRequest 노선_저장요청 = new LineRequest("1호선", "빨간색", 1L, 2L, 20);
        lineService.saveLine(노선_저장요청);

        //when then
        assertThatThrownBy(() -> lineService.saveLine(노선_저장요청)).isInstanceOf(DuplicatedNameException.class);
    }

    @DisplayName("지하철 노선 전체 조회")
    @Test
    void findAll() {
        //given
        LineRequest 일호선_저장요청 = new LineRequest("1호선", "빨간색", 1L, 2L, 20);
        LineRequest 삼호선_저장요청 = new LineRequest("3호선", "빨간색", 1L, 2L, 20);
        LineNameColorResponse 일호선_저장응답 = lineService.saveLine(일호선_저장요청);
        LineNameColorResponse 삼호선_저장응답 = lineService.saveLine(삼호선_저장요청);

        //when
        List<LineNameColorResponse> 라인_전체_조회 = lineService.findAll();

        //then
        assertThat(라인_전체_조회).isNotNull();
        assertThat(라인_전체_조회).hasSize(2);
    }

    @DisplayName("지하철 노선 1개 조회")
    @Test
    void findById() {
        //given
        LineNameColorResponse 일호선_저장응답 = 일호선_저장응답();
        Long lineId = 일호선_저장응답.getId();

        //when
        LineResponse 조회한_노선 = lineService.findById(lineId);

        //then
        assertThat(조회한_노선.getId()).isEqualTo(일호선_저장응답.getId());
        assertThat(조회한_노선.getName()).isEqualTo(일호선_저장응답.getName());
        assertThat(조회한_노선.getColor()).isEqualTo(일호선_저장응답.getColor());
    }

    @DisplayName("존재하지 않는 지하철 노선 조회 시 예외 발생한다.")
    @Test
    void findByNotExistId() {
        //given
        LineNameColorResponse 일호선_저장응답 = 일호선_저장응답();

        //when
        assertThatThrownBy(() -> lineService.findById(999L))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void delete() {
        //given
        LineNameColorResponse 일호선_저장응답 = 일호선_저장응답();
        Long targetId = 일호선_저장응답.getId();

        //when
        lineService.delete(targetId);

        //then
        assertThatThrownBy(() -> lineService.findById(targetId)).isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void update() {
        //given
        LineNameColorResponse 일호선_저장응답 = 일호선_저장응답();
        Long targetId = 일호선_저장응답.getId();
        LineRequest 노선_수정요청 = new LineRequest("222호선", "노란색");

        //when
        lineService.update(targetId, 노선_수정요청);
        LineResponse 수정_후_노선 = lineService.findById(targetId);

        //then
        assertThat(수정_후_노선.getName()).isEqualTo(노선_수정요청.getName());
        assertThat(수정_후_노선.getColor()).isEqualTo(노선_수정요청.getColor());
    }

    @DisplayName("지하철 구간 추가한다.")
    @Test
    void addSection() {
        //given
        Station 강남역 = stationDao.save(new Station(1L, "강남역"));
        Station 잠실역 = stationDao.save(new Station(2L, "잠실역"));
        Station 잠실나루역 = stationDao.save(new Station(3L, "잠실나루역"));
        LineRequest 일호선_저장요청 = new LineRequest("1호선", "빨간색", 강남역.getId(), 잠실역.getId(), 20);
        LineNameColorResponse 일호선_저장응답 = lineService.saveLine(일호선_저장요청);
        Long targetId = 일호선_저장응답.getId();

        //when
        lineService.addSection(targetId, new SectionRequest(잠실나루역.getId(), 잠실역.getId(), 10));
        LineResponse 구간_추가된_일호선 = lineService.findById(targetId);

        //then
        assertThat(구간_추가된_일호선.getStations()).hasSize(3);
    }

    @DisplayName("지하철 구간 중간 추가 시 거리가 같으면 예외 처리 한다.")
    @Test
    void addSectionSameDistance() {
        //given
        Station 강남역 = stationDao.save(new Station(1L, "강남역"));
        Station 잠실역 = stationDao.save(new Station(2L, "잠실역"));
        Station 잠실나루역 = stationDao.save(new Station(3L, "잠실나루역"));
        LineRequest 일호선_저장요청 = new LineRequest("1호선", "빨간색", 강남역.getId(), 잠실역.getId(), 20);
        LineNameColorResponse 일호선_저장응답 = lineService.saveLine(일호선_저장요청);
        Long targetId = 일호선_저장응답.getId();

        //when then
        assertThatThrownBy(() -> lineService.addSection(targetId, new SectionRequest(잠실나루역.getId(), 잠실역.getId(), 20)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역과 역 사이 새로운 역을 추가할 때 기존 역 사이의 길이보다 크거나 같으면 등록할 수 없습니다.");
    }

    @DisplayName("존재하는 지하철 구간 삭제한다.")
    @Test
    void deleteSection() {
        //given
        Station 강남역 = stationDao.save(new Station(1L, "강남역"));
        Station 잠실역 = stationDao.save(new Station(2L, "잠실역"));
        Station 잠실나루역 = stationDao.save(new Station(3L, "잠실나루역"));
        LineRequest 일호선_저장요청 = new LineRequest("1호선", "빨간색", 강남역.getId(), 잠실역.getId(), 20);
        LineNameColorResponse 일호선_저장응답 = lineService.saveLine(일호선_저장요청);
        Long targetId = 일호선_저장응답.getId();

        lineService.addSection(targetId, new SectionRequest(잠실나루역.getId(), 잠실역.getId(), 10));

        //when
        lineService.deleteSection(targetId, 잠실역.getId());
        LineResponse 구간_추가된_노선 = lineService.findById(targetId);

        //then
        assertThat(구간_추가된_노선.getStations()).hasSize(2);
    }

    @DisplayName("존재하지 않는 지하철 구간 삭제하면 예외 처리한다.")
    @Test
    void notFoundLine() {
        //given
        Station 강남역 = stationDao.save(new Station(1L, "강남역"));
        Station 잠실역 = stationDao.save(new Station(2L, "잠실역"));
        Station 잠실나루역 = stationDao.save(new Station(3L, "잠실나루역"));
        LineRequest 일호선_저장요청 = new LineRequest("1호선", "빨간색", 강남역.getId(), 잠실역.getId(), 20);
        LineNameColorResponse 일호선_저장응답 = lineService.saveLine(일호선_저장요청);
        Long targetId = 일호선_저장응답.getId();

        lineService.addSection(targetId, new SectionRequest(잠실나루역.getId(), 잠실역.getId(), 10));
        assertThatThrownBy(() -> lineService.deleteSection(targetId, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("삭제하려는 역을 포함하는 구간이 존재하지 않습니다.");
    }

    private LineNameColorResponse 일호선_저장응답() {
        Station 강남역 = stationDao.save(new Station(1L, "강남역"));
        Station 잠실역 = stationDao.save(new Station(2L, "잠실역"));
        LineRequest 일호선_저장요청 = new LineRequest("1호선", "빨간색", 강남역.getId(), 잠실역.getId(), 20);
        return lineService.saveLine(일호선_저장요청);
    }
}