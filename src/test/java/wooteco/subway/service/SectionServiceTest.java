package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import wooteco.subway.dto.*;
import wooteco.subway.exception.ClientException;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private StationService stationService;

    @Autowired
    private LineService lineService;

    @DisplayName("지하철 구간 저장")
    @Test
    void save() {
        StationResponse firstStation = stationService.save(new StationRequest("역삼역"));
        StationResponse secondStation = stationService.save(new StationRequest("삼성역"));
        StationResponse thirdStation = stationService.save(new StationRequest("잠실역"));

        LineRequest line = new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10);;
        LineResponse saveLine = lineService.save(line);

        assertThatNoException().isThrownBy(() -> sectionService.save(saveLine.getId(), new SectionRequest(secondStation.getId(),
                thirdStation.getId(), 10), lineService.findSections(saveLine.getId()), lineService.findById(saveLine.getId())));
    }

    @DisplayName("지하철 구간 저장 예외 - 상행역과 하행역이 같은 경우")
    @Test
    void checkSameUpDownStation() {
        StationResponse firstStation = stationService.save(new StationRequest("역삼역"));
        StationResponse secondStation = stationService.save(new StationRequest("삼성역"));

        LineRequest line = new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10);;
        LineResponse saveLine = lineService.save(line);

        assertThatThrownBy(() ->sectionService.save(saveLine.getId(), new SectionRequest(firstStation.getId(),
                firstStation.getId(), 10), lineService.findSections(saveLine.getId()), lineService.findById(saveLine.getId())))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("상행역과 하행역이 같을 수 없습니다.");
    }

    @DisplayName("지하철 구간 저장 예외 - 존재하지 않는 노선")
    @Test
    void checkNotExistLine() {
        assertThatThrownBy(() -> sectionService.save(0L, new SectionRequest(1L,
                1L, 10), lineService.findSections(1L), lineService.findById(1L)))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void delete() {
        StationResponse firstStation = stationService.save(new StationRequest("역삼역"));
        StationResponse secondStation = stationService.save(new StationRequest("삼성역"));
        StationResponse thirdStation = stationService.save(new StationRequest("잠실역"));

        LineRequest line = new LineRequest("9호선", "red", firstStation.getId(), secondStation.getId(), 10);
        LineResponse saveLine = lineService.save(line);

        sectionService.save(saveLine.getId(), new SectionRequest(secondStation.getId(), thirdStation.getId(), 10),
                lineService.findSections(saveLine.getId()), lineService.findById(saveLine.getId()));

        assertThatNoException().isThrownBy(() -> sectionService.delete(saveLine.getId(), firstStation.getId()));
    }

    @DisplayName("지하철 구간 삭제 예외 - 존재하지 않는 노선")
    @Test
    void checkNotExistDeleteLine() {
        assertThatThrownBy(() -> sectionService.delete(0L, 1L))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("존재하지 않는 노선입니다.");
    }
}
