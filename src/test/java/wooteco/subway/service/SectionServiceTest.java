package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixtures.getSection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

class SectionServiceTest extends ServiceTest {
    @Autowired
    private SectionService sectionService;
    @Autowired
    private StationService stationService;
    @Autowired
    private LineService lineService;

    private final Line line = new Line("신분당선", "red", 1L, 2L, 10);
    private final Section section = new Section(1L, 1L, 2L, 10);
    private final Section section2 = new Section(1L, 2L, 3L, 5);

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void saveSection() {
        Section resSection = sectionService.saveSection(section);
        assertThat(resSection)
                .isEqualTo(getSection(resSection.getId(), section));
    }

    @DisplayName("해당 지하철 노선 안의 역들을 조회할 수 있다.")
    @Test
    void findStationsOfLine() {
        //given
        Station station = new Station("강남역");
        Station station2 = new Station("선릉역");
        Station station3 = new Station("잠실역");
        Station saveStation = stationService.save(station);
        Station saveStation2 = stationService.save(station2);
        Station saveStation3 = stationService.save(station3);
        Line saveLine = lineService.save(line);
        sectionService.saveSection(section);
        sectionService.saveSection(section2);

        //when then
        assertThat(lineService.findStationsOfLine(saveLine))
                .containsOnly(saveStation, saveStation2, saveStation3);
    }
}
