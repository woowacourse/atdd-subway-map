package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.entity.StationEntity;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationDao stationDao;

    private Long stationAId;
    private Long stationBId;
    private Long stationCId;
    private Long stationDId;

    @BeforeEach
    void setUp() {
        stationAId = stationDao.save(new StationEntity.Builder("A").build()).getId();
        stationBId = stationDao.save(new StationEntity.Builder("B").build()).getId();
        stationCId = stationDao.save(new StationEntity.Builder("C").build()).getId();
        stationDId = stationDao.save(new StationEntity.Builder("D").build()).getId();
    }

    @DisplayName("라인을 등록한다.")
    @Test
    void createLine() {
        LineRequest lineRequest = new LineRequest("2호선", "그린", stationAId, stationBId, 10);

        assertDoesNotThrow(() -> lineService.createLine(lineRequest));
    }

    @DisplayName("라인을 등록할 때 이름이 중복되먄 예외 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        LineRequest lineRequest = new LineRequest("2호선", "그린", stationAId, stationBId, 10);
        lineService.createLine(lineRequest);

        assertThatThrownBy(() -> lineService.createLine(lineRequest))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("이미 같은 이름의 노선이 존재합니다.");
    }

    @DisplayName("라인을 수정할 때 라인이 존재하지 않으면 예외 발생한다.")
    @Test
    void updateLineWithNotExistingLine() {
        LineRequest lineRequest = new LineRequest("2호선", "그린", stationAId, stationBId, 10);

        assertThatThrownBy(() -> lineService.updateLine(0L, lineRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당 노선이 존재하지 않습니다.");
    }

    @DisplayName("라인을 삭제할 때 라인이 존재하지 않으면 예외 발생한다.")
    @Test
    void deleteLineWithNotExistingLine() {
        assertThatThrownBy(() -> lineService.deleteLine(0L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당 노선이 존재하지 않습니다.");
    }

    @DisplayName("구간을 생성할 때 라인이 존재하지 않으면 예외 발생한다.")
    @Test
    void createSectionWithNotExistingLineId() {
        SectionRequest sectionRequest = new SectionRequest(stationAId, stationBId, 10);

        assertThatThrownBy(() -> lineService.createSection(0L, sectionRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당 노선이 존재하지 않습니다.");
    }

    @DisplayName("구간을 삭제할 때 라인이 존재하지 않으면 예외 발생한다.")
    @Test
    void deleteSectionWithNotExistingLineId() {
        assertThatThrownBy(() -> lineService.deleteSection(0L, stationAId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당 노선이 존재하지 않습니다.");
    }

    @DisplayName("구간을 모두 불러온다.")
    @Test
    void findAllLine() {
        LineRequest lineRequest1 = new LineRequest("2호선", "그린", stationAId, stationBId, 10);
        lineService.createLine(lineRequest1);

        LineRequest lineRequest2 = new LineRequest("3호선", "주황", stationCId, stationDId, 10);
        lineService.createLine(lineRequest2);

        List<LineResponse> lineResponses = lineService.findAllLines();
        List<Long> stationIdResponses = lineResponses.stream()
                .map(LineResponse::getStations)
                .flatMap(List::stream)
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(lineResponses).hasSize(2),
                () -> assertThat(stationIdResponses).contains(stationAId, stationBId, stationCId, stationDId)
        );
    }
}
