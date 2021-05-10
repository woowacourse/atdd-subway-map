package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.LineService;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class SectionServiceTest {

    // TODO :: 테스트 의존 풀기

    @Autowired
    private LineService lineService;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private StationService stationService;

    @Autowired
    private SectionService sectionService;

    private Station savedUpStation;
    private Station savedDownStation;
    private Line savedLine;

    private int initialDistance = 10;
    private int insertDistance = 2;

    @BeforeEach
    private void initLine() {
        final Station upStation = new Station("B역");
        final Station downStation = new Station("D역");

        savedUpStation = stationService.save(upStation);
        savedDownStation = stationService.save(downStation);

        final Line line = new Line("코기선", "black", savedUpStation.getId(), savedDownStation.getId(), initialDistance);
        savedLine = lineService.create(line);
    }

    @DisplayName("노선 추가 시 상행, 하행 종점 추가")
    @Test
    public void saveTest() {
        validateStationOrder(savedUpStation, savedDownStation);
        validateFinalStation(savedUpStation, savedDownStation);

        validateStationDistance(savedUpStation, savedDownStation, initialDistance);
    }

    @DisplayName("상행 종점역 -> 중간역 구간 등록")
    @Test
    public void addMiddleSectionFromFront() {
        final Station middleStation = new Station("C역");
        final Station savedMiddleStation = stationService.save(middleStation);

        sectionService.addSection(savedLine.getId(), savedUpStation.getId(), savedMiddleStation.getId(), insertDistance);

        validateStationOrder(savedUpStation, savedMiddleStation, savedDownStation);
        validateFinalStation(savedUpStation, savedDownStation);

        validateStationDistance(savedUpStation, savedMiddleStation, insertDistance);
        validateStationDistance(savedMiddleStation, savedDownStation, initialDistance - insertDistance);
    }

    @DisplayName("중간역 -> 하행 종착역 구간 등록")
    @Test
    public void addMiddleSectionFromBack() {
        final Station middleStation = new Station("C역");
        final Station savedMiddleStation = stationService.save(middleStation);

        sectionService.addSection(savedLine.getId(), savedMiddleStation.getId(), savedDownStation.getId(), insertDistance);

        validateStationOrder(savedUpStation, savedMiddleStation, savedDownStation);
        validateFinalStation(savedUpStation, savedDownStation);

        validateStationDistance(savedUpStation, savedMiddleStation, initialDistance - insertDistance);
        validateStationDistance(savedMiddleStation, savedDownStation, insertDistance);
    }

    @DisplayName("새로운 상행 종점역 구간 등록")
    @Test
    public void addFinalSectionFromFront() {
        final Station newFrontStation = new Station("A역");
        final Station savedNewFrontStation = stationService.save(newFrontStation);

        sectionService.addSection(savedLine.getId(), savedNewFrontStation.getId(), savedUpStation.getId(), insertDistance);

        validateStationOrder(savedNewFrontStation, savedUpStation, savedDownStation);
        validateFinalStation(savedNewFrontStation, savedDownStation);

        validateStationDistance(savedNewFrontStation, savedUpStation, insertDistance);
        validateStationDistance(savedUpStation, savedDownStation, initialDistance);
    }

    @DisplayName("새로운 하행 종점역 구간 등록")
    @Test
    public void addFinalSectionFromDown() {
        final Station newBackStation = new Station("E역");
        final Station savedNewBackStation = stationService.save(newBackStation);

        sectionService.addSection(savedLine.getId(), savedDownStation.getId(), savedNewBackStation.getId(), insertDistance);

        validateStationOrder(savedUpStation, savedDownStation, savedNewBackStation);
        validateFinalStation(savedUpStation, savedNewBackStation);

        validateStationDistance(savedUpStation, savedDownStation, initialDistance);
        validateStationDistance(savedDownStation, savedNewBackStation, insertDistance);
    }

    @DisplayName("노선의 중간 역을 삭제한다.")
    @Test
    public void deleteStationInLine(){
        final Station middleStation = new Station("C역");
        final Station savedMiddleStation = stationService.save(middleStation);

        sectionService.addSection(savedLine.getId(), savedMiddleStation.getId(), savedDownStation.getId(), insertDistance);
        sectionService.deleteSection(savedLine.getId(), savedMiddleStation.getId());

        validateStationOrder(savedUpStation, savedDownStation);
        validateFinalStation(savedUpStation, savedDownStation);
        validateStationDistance(savedUpStation, savedDownStation, initialDistance);
    }

    @DisplayName("노선의 상행 종점 역을 삭제한다.")
    @Test
    public void deleteFrontStationInLine(){
        final Station middleStation = new Station("C역");
        final Station savedMiddleStation = stationService.save(middleStation);

        sectionService.addSection(savedLine.getId(), savedMiddleStation.getId(), savedDownStation.getId(), insertDistance);
        sectionService.deleteSection(savedLine.getId(), savedUpStation.getId());

        validateStationOrder(savedMiddleStation, savedDownStation);
        validateFinalStation(savedMiddleStation, savedDownStation);
        validateStationDistance(savedMiddleStation, savedDownStation, insertDistance);
    }

    @DisplayName("노선의 하행 종점 역을 삭제한다.")
    @Test
    public void deleteBackStationInLine(){
        final Station middleStation = new Station("C역");
        final Station savedMiddleStation = stationService.save(middleStation);

        sectionService.addSection(savedLine.getId(), savedMiddleStation.getId(), savedDownStation.getId(), insertDistance);
        sectionService.deleteSection(savedLine.getId(), savedDownStation.getId());

        validateStationOrder(savedUpStation, savedMiddleStation);
        validateFinalStation(savedUpStation, savedMiddleStation);
        validateStationDistance(savedUpStation, savedMiddleStation, initialDistance-insertDistance);
    }

    @DisplayName("노선이 종점 뿐 일 경우 역을 삭제할 수 없다.")
    @Test()
    public void deleteFinalStation() {
        assertThatThrownBy(()->{
            sectionService.deleteSection(savedLine.getId(), savedDownStation.getId());
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행 -> 중간역 삽입 시 상행 - 하행의 거리보다 큰 거리로 추가할 수 없다.")
    @Test()
    public void addFrontSectionWithInvalidDistance() {
        assertThatThrownBy(()->{
            final Station middleStation = new Station("C역");
            final Station savedMiddleStation = stationService.save(middleStation);

            sectionService.addSection(savedLine.getId(), savedUpStation.getId(), savedMiddleStation.getId(), initialDistance+1);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("중간역 -> 하행 삽입 시 상행 - 하행의 거리보다 큰 거리로 추가할 수 없다.")
    @Test()
    public void addBackSectionWithInvalidDistance() {
        assertThatThrownBy(()->{
            final Station middleStation = new Station("C역");
            final Station savedMiddleStation = stationService.save(middleStation);

            sectionService.addSection(savedLine.getId(), savedMiddleStation.getId(), savedDownStation.getId(), initialDistance+1);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private void validateStationOrder(final Station... expectOrders) {
        final List<Station> stations = sectionService.findAllSectionInLine(savedLine.getId());

        int index = 0;
        for (final Station station : expectOrders) {
            assertThat(stations.get(index++).getName()).isEqualTo(station.getName());
        }
    }

    private void validateStationDistance(final Station upStation, final Station downStation, final int expectedDistance){
        final int actualDistance = sectionDao.findDistance(savedLine.getId(), upStation.getId(), downStation.getId());
        assertThat(expectedDistance).isEqualTo(actualDistance);
    }

    private void validateFinalStation(final Station expectedUpStation, final Station expectedDownStation){
        assertThat(lineDao.findUpStationId(savedLine.getId())).isEqualTo(expectedUpStation.getId());
        assertThat(lineDao.findDownStationId(savedLine.getId())).isEqualTo(expectedDownStation.getId());
    }
}