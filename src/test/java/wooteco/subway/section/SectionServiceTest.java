package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineService;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class SectionServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Autowired
    private SectionService sectionService;

    private Station savedUpStation;
    private Station savedDownStation;
    private Line savedLine;

    @BeforeEach
    private void initLine(){
        final Station upStation = new Station(null, "B역");
        final Station downStation = new Station(null, "D역");

        savedUpStation = stationService.save(upStation);
        savedDownStation = stationService.save(downStation);

        final Line line = new Line(null, "코기선", "black",
                savedUpStation.getId(), savedDownStation.getId());

        savedLine = lineService.create(line);
    }

    @DisplayName("노선 추가 시 상행, 하행 종점 추가")
    @Test
    public void saveTest(){
        final List<Station> stations = sectionService.findAllSectionInLine(savedLine.getId(), savedUpStation.getId());

        assertThat(stations.get(0).getName()).isEqualTo(savedUpStation.getName());
        assertThat(stations.get(1).getName()).isEqualTo(savedDownStation.getName());
    }

    @DisplayName("상행 종점역 -> 중간역 구간 등록")
    @Test
    public void addMiddleSectionFromFront(){
        final Station middleStation = new Station(null, "C역");
        final Station savedMiddleStation = stationService.save(middleStation);

        sectionService.addSection(savedLine.getId(), savedUpStation.getId(), savedMiddleStation.getId());

        final List<Station> stations = sectionService.findAllSectionInLine(savedLine.getId(), savedUpStation.getId());

        assertThat(stations.get(0).getName()).isEqualTo(savedUpStation.getName());
        assertThat(stations.get(1).getName()).isEqualTo(savedMiddleStation.getName());
        assertThat(stations.get(2).getName()).isEqualTo(savedDownStation.getName());
    }

    @DisplayName("중간역 -> 하행 종착역 구간 등록")
    @Test
    public void addMiddleSectionFromBack(){
        final Station middleStation = new Station(null, "C역");
        final Station savedMiddleStation = stationService.save(middleStation);

        sectionService.addSection(savedLine.getId(), savedMiddleStation.getId(), savedDownStation.getId());

        final List<Station> stations = sectionService.findAllSectionInLine(savedLine.getId(), savedUpStation.getId());

        assertThat(stations.get(0).getName()).isEqualTo(savedUpStation.getName());
        assertThat(stations.get(1).getName()).isEqualTo(savedMiddleStation.getName());
        assertThat(stations.get(2).getName()).isEqualTo(savedDownStation.getName());
    }
}