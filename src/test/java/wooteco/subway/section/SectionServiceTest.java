package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class SectionServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private StationService stationService;

    @Autowired
    private SectionTestUtils sectionTestUtils;

    private StationResponse aStation;
    private StationResponse bStation;
    private StationResponse cStation;
    private StationResponse dStation;
    private StationResponse eStation;

    private Line testLine;

    private int initialDistance = 10;
    private int insertDistance = 2;

    @BeforeEach
    private void initLine() {
        aStation = stationService.save(new StationRequest("A역"));
        bStation = stationService.save(new StationRequest("B역"));
        cStation = stationService.save(new StationRequest("C역"));
        dStation = stationService.save(new StationRequest("D역"));
        eStation = stationService.save(new StationRequest("E역"));

        final LineRequest sample = new LineRequest("코기선", "black", bStation.getId(), dStation.getId(), initialDistance);
        final LineResponse lineResponse = lineService.create(sample);
        testLine = sample.toLine(lineResponse.getId());
    }

    @DisplayName("상행 종점을 등록한다.")
    @Test
    public void addFinalUpStation() {
        SectionRequest sectionRequest = new SectionRequest(aStation.getId(), bStation.getId(), Integer.MAX_VALUE);
        sectionService.addSection(testLine.getId(), sectionRequest);

        sectionTestUtils.assertStationOrder(testLine, aStation, bStation, dStation);
        sectionTestUtils.assertSectionDistance(testLine, Integer.MAX_VALUE, initialDistance);
    }

    @DisplayName("하행 종점을 등록한다.")
    @Test
    public void addFinalDownStation() {
        SectionRequest sectionRequest = new SectionRequest(dStation.getId(), eStation.getId(), Integer.MAX_VALUE);
        sectionService.addSection(testLine.getId(), sectionRequest);

        sectionTestUtils.assertStationOrder(testLine, bStation, dStation, eStation);
        sectionTestUtils.assertSectionDistance(testLine, initialDistance, Integer.MAX_VALUE);
    }

    @DisplayName("중간 구간을 등록한다. - 상행 기준")
    @Test
    public void addMiddleStationFromFront() {
        SectionRequest sectionRequest = new SectionRequest(bStation.getId(), cStation.getId(), insertDistance);
        sectionService.addSection(testLine.getId(), sectionRequest);

        sectionTestUtils.assertStationOrder(testLine, bStation, cStation, dStation);
        sectionTestUtils.assertSectionDistance(testLine, insertDistance, initialDistance - insertDistance);
    }

    @DisplayName("중간 구간을 등록한다. - 하행 기준")
    @Test
    public void addMiddleStationFromBack() {
        SectionRequest sectionRequest = new SectionRequest(cStation.getId(), dStation.getId(), insertDistance);
        sectionService.addSection(testLine.getId(), sectionRequest);

        sectionTestUtils.assertStationOrder(testLine, bStation, cStation, dStation);
        sectionTestUtils.assertSectionDistance(testLine, initialDistance - insertDistance, insertDistance);
    }

    @DisplayName("존재하지 않는 노선에 구간 추가 시 예외가 발생한다.")
    @Test
    public void addSectionInNonExistLine() {
        assertThatThrownBy(() -> {
            SectionRequest sectionRequest = new SectionRequest(aStation.getId(), bStation.getId(), Integer.MAX_VALUE);
            sectionService.addSection(Long.MAX_VALUE, sectionRequest);
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("존재하지 않는 구간을 등록 시 예외가 발생한다.")
    @Test
    public void addNonExistSection() {
        assertThatThrownBy(() -> {
            SectionRequest sectionRequest = new SectionRequest(aStation.getId(), cStation.getId(), insertDistance);
            sectionService.addSection(testLine.getId(), sectionRequest);
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("중복 구간을 등록 시 예외가 발생한다.")
    @Test
    public void addDuplicatedSection() {
        assertThatThrownBy(() -> {
            SectionRequest sectionRequest = new SectionRequest(bStation.getId(), dStation.getId(), insertDistance);
            sectionService.addSection(testLine.getId(), sectionRequest);
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("중간 구간 추가시 기존 거리보다 더 큰 구간 거리로 등록할 수 없다.")
    @Test
    public void addBiggerDistance() {
        assertThatThrownBy(() -> {
            SectionRequest sectionRequest = new SectionRequest(bStation.getId(), cStation.getId(), Integer.MAX_VALUE);
            sectionService.addSection(testLine.getId(), sectionRequest);
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("노선의 중간역을 제거한다.")
    @Test
    public void deleteMiddleStation() {
        SectionRequest sectionRequest = new SectionRequest(bStation.getId(), cStation.getId(), insertDistance);
        sectionService.addSection(testLine.getId(), sectionRequest);

        sectionService.deleteSection(testLine.getId(), cStation.getId());

        sectionTestUtils.assertStationOrder(testLine, bStation, dStation);
        sectionTestUtils.assertSectionDistance(testLine, initialDistance);
    }

    @DisplayName("노선의 상행 종점역을 제거한다.")
    @Test
    public void deleteFinalUpStation() {
        SectionRequest sectionRequest = new SectionRequest(bStation.getId(), cStation.getId(), insertDistance);
        sectionService.addSection(testLine.getId(), sectionRequest);

        sectionService.deleteSection(testLine.getId(), bStation.getId());

        sectionTestUtils.assertStationOrder(testLine, cStation, dStation);
        sectionTestUtils.assertSectionDistance(testLine, initialDistance - insertDistance);
    }

    @DisplayName("노선의 하행 종점역을 제거한다.")
    @Test
    public void deleteFinalDownStation() {
        SectionRequest sectionRequest = new SectionRequest(bStation.getId(), cStation.getId(), insertDistance);
        sectionService.addSection(testLine.getId(), sectionRequest);

        sectionService.deleteSection(testLine.getId(), dStation.getId());

        sectionTestUtils.assertStationOrder(testLine, bStation, cStation);
        sectionTestUtils.assertSectionDistance(testLine, insertDistance);
    }

    @DisplayName("존재하지 않는 노선에 구간 삭제 시 예외가 발생한다.")
    @Test
    public void deleteSectionInNonExistLine() {
        assertThatThrownBy(() -> {
            sectionService.deleteSection(Long.MAX_VALUE, bStation.getId());
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("노선에 존재하지 않는 역을 제거할 수 없다.")
    @Test
    public void deleteNonExistStation() {
        assertThatThrownBy(() -> sectionService.deleteSection(testLine.getId(), aStation.getId()))
                .isInstanceOf(LineException.class);
    }

    @DisplayName("종점뿐인 노선에서 구간을 제거할 수 없다.")
    @Test
    public void deleteSectionWhenExistOnlyFinalStations() {
        assertThatThrownBy(() -> sectionService.deleteSection(testLine.getId(), bStation.getId()))
                .isInstanceOf(LineException.class);
    }
}
