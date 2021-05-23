package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("Stations 테스트")
class StationsTest {

    private static final List<Station> STATIONS = new ArrayList<>();

    @BeforeAll
    static void beforeAllSetUp() {
        STATIONS.add(new Station(1L, "강남역"));
        STATIONS.add(new Station(2L, "잠실역"));
        STATIONS.add(new Station(3L, "역삼역"));
    }

    @Test
    @DisplayName("구간의 순서에 맞게 지하철 역을 정렬한다")
    void sort() {
        //given
        Stations stationManager = new Stations(STATIONS);
        List<Section> sections = Arrays.asList(
            new Section(2L, 3L, 10),
            new Section(3L, 1L, 10)
        );

        List<StationResponse> answer = Arrays.asList(
            new StationResponse(STATIONS.get(1)),
            new StationResponse(STATIONS.get(2)),
            new StationResponse(STATIONS.get(0))
        );

        Sections sectionManager = new Sections(sections);

        //when
        stationManager.sort(sectionManager);

        //then
        assertThat(stationManager.stream().map(StationResponse::new)
            .collect(Collectors.toList()))
            .usingRecursiveComparison()
            .isEqualTo(answer);
    }
}