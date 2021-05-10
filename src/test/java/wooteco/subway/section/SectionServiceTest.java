package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineException;
import wooteco.subway.line.LineService;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class SectionServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Autowired
    private SectionService sectionService;

    private Station aStation;
    private Station bStation;
    private Station cStation;
    private Station dStation;
    private Station eStation;

    private Line savedLine;

    private int initialDistance = 10;
    private int insertDistance = 2;

    @BeforeEach
    private void initLine() {
        aStation = stationService.save(new Station("A역"));
        bStation = stationService.save(new Station("B역"));
        cStation = stationService.save(new Station("C역"));
        dStation = stationService.save(new Station("D역"));
        eStation = stationService.save(new Station("E역"));

        final Line line = new Line("코기선", "black", bStation.getId(), dStation.getId(), initialDistance);
        savedLine = lineService.create(line);
    }

    @DisplayName("노선 추가 시 상행, 하행 종점 추가")
    @Test
    public void saveTest() {
        validateStationOrder(bStation, dStation);
        validateFinalStation(bStation, dStation);

        validateStationDistance(bStation, dStation, initialDistance);
    }

    @DisplayName("상행 종점역 -> 중간역 구간 등록")
    @Test
    public void addMiddleSectionFromFront() {
        sectionService.addSection(savedLine.getId(), bStation.getId(), cStation.getId(), insertDistance);

        validateStationOrder(bStation, cStation, dStation);
        validateFinalStation(bStation, dStation);

        validateStationDistance(bStation, cStation, insertDistance);
        validateStationDistance(cStation, dStation, initialDistance - insertDistance);
    }

    @DisplayName("중간역 -> 하행 종착역 구간 등록")
    @Test
    public void addMiddleSectionFromBack() {
        sectionService.addSection(savedLine.getId(), cStation.getId(), dStation.getId(), insertDistance);

        validateStationOrder(bStation, cStation, dStation);
        validateFinalStation(bStation, dStation);

        validateStationDistance(bStation, cStation, initialDistance - insertDistance);
        validateStationDistance(cStation, dStation, insertDistance);
    }

    @DisplayName("새로운 상행 종점역 구간 등록")
    @Test
    public void addFinalSectionFromFront() {
        sectionService.addSection(savedLine.getId(), aStation.getId(), bStation.getId(), insertDistance);

        validateStationOrder(aStation, bStation, dStation);
        validateFinalStation(aStation, dStation);

        validateStationDistance(aStation, bStation, insertDistance);
        validateStationDistance(bStation, dStation, initialDistance);
    }

    @DisplayName("새로운 하행 종점역 구간 등록")
    @Test
    public void addFinalSectionFromDown() {
        sectionService.addSection(savedLine.getId(), dStation.getId(), eStation.getId(), insertDistance);

        validateStationOrder(bStation, dStation, eStation);
        validateFinalStation(bStation, eStation);

        validateStationDistance(bStation, dStation, initialDistance);
        validateStationDistance(dStation, eStation, insertDistance);
    }

    @DisplayName("노선의 중간 역을 삭제한다.")
    @Test
    public void deleteStationInLine(){
        sectionService.addSection(savedLine.getId(), cStation.getId(), dStation.getId(), insertDistance);
        sectionService.deleteSection(savedLine.getId(), cStation.getId());

        validateStationOrder(bStation, dStation);
        validateFinalStation(bStation, dStation);

        validateStationDistance(bStation, dStation, initialDistance);
    }

    @DisplayName("노선의 상행 종점 역을 삭제한다.")
    @Test
    public void deleteFrontStationInLine(){
        sectionService.addSection(savedLine.getId(), cStation.getId(), dStation.getId(), insertDistance);
        sectionService.deleteSection(savedLine.getId(), bStation.getId());

        validateStationOrder(cStation, dStation);
        validateFinalStation(cStation, dStation);

        validateStationDistance(cStation, dStation, insertDistance);
    }

    @DisplayName("노선의 하행 종점 역을 삭제한다.")
    @Test
    public void deleteBackStationInLine(){
        sectionService.addSection(savedLine.getId(), cStation.getId(), dStation.getId(), insertDistance);
        sectionService.deleteSection(savedLine.getId(), dStation.getId());

        validateStationOrder(bStation, cStation);
        validateFinalStation(bStation, cStation);

        validateStationDistance(bStation, cStation, initialDistance-insertDistance);
    }

    @DisplayName("노선이 종점 뿐 일 경우 역을 삭제할 수 없다.")
    @Test
    public void deleteFinalStation() {
        assertThatThrownBy(()->{
            sectionService.deleteSection(savedLine.getId(), dStation.getId());
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("노선에 존재하지 않는 역을 삭제할 수 없다.")
    @Test
    public void deleteNonExistentStation() {
        assertThatThrownBy(()->{
            sectionService.deleteSection(savedLine.getId(), aStation.getId());
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("노선에 둘 다 존재하지 않는 역으로 구간을 추가할 수 없다.")
    @Test
    public void addSectionWithNonExistentStations() {
        assertThatThrownBy(()->{
            sectionService.addSection(savedLine.getId(), aStation.getId(), eStation.getId(), initialDistance);
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("이미 존재하는 구간으로 구간을 추가할 수 없다.")
    @Test
    public void addSectionWithExistentSection() {
        assertThatThrownBy(()->{
            sectionService.addSection(savedLine.getId(), aStation.getId(), bStation.getId(), initialDistance);
            validateStationOrder(aStation, bStation, dStation);
            sectionService.addSection(savedLine.getId(), aStation.getId(), bStation.getId(), initialDistance);
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("상행 -> 중간역 삽입 시 상행 - 하행의 거리보다 같거나 큰 거리로 추가할 수 없다.")
    @Test
    public void addFrontSectionWithInvalidDistance() {
        assertThatThrownBy(()->{
            sectionService.addSection(savedLine.getId(), bStation.getId(), cStation.getId(), initialDistance+1);
        }).isInstanceOf(LineException.class);
    }

    @DisplayName("중간역 -> 하행 삽입 시 상행 - 하행의 거리보다 같거나 큰 거리로 추가할 수 없다.")
    @Test
    public void addBackSectionWithInvalidDistance() {
        assertThatThrownBy(()->{
            sectionService.addSection(savedLine.getId(), cStation.getId(), dStation.getId(), initialDistance+1);
        }).isInstanceOf(LineException.class);
    }

    private void validateStationOrder(final Station... expectOrders) {
        final List<Long> stations = sectionService.allStationIdInLine(savedLine.getId());

        int index = 0;
        for (final Station station : expectOrders) {
            assertThat(stations.get(index++)).isEqualTo(station.getId());
        }
    }

    private void validateStationDistance(final Station upStation, final Station downStation, final int expectedDistance){
        final int actualDistance = sectionService.distance(savedLine.getId(), upStation.getId(), downStation.getId());
        assertThat(expectedDistance).isEqualTo(actualDistance);
    }

    private void validateFinalStation(final Station expectedUpStation, final Station expectedDownStation){
        assertThat(lineService.upStationId(savedLine.getId())).isEqualTo(expectedUpStation.getId());
        assertThat(lineService.downStationId(savedLine.getId())).isEqualTo(expectedDownStation.getId());
    }
}