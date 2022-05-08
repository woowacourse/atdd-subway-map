package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.Fixtures.LINE;
import static wooteco.subway.Fixtures.SECTION;
import static wooteco.subway.Fixtures.SECTION_2;
import static wooteco.subway.Fixtures.STATION;
import static wooteco.subway.Fixtures.STATION_2;
import static wooteco.subway.Fixtures.STATION_3;
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

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void saveSection() {
        Section resSection = sectionService.saveSection(SECTION);
        assertThat(resSection)
                .isEqualTo(getSection(resSection.getId(), SECTION));
    }

    @DisplayName("해당 지하철 노선 안의 역들을 조회할 수 있다.")
    @Test
    void findStationsOfLine() {
        //given
        Station saveStation = stationService.save(STATION);
        Station saveStation2 = stationService.save(STATION_2);
        Station saveStation3 = stationService.save(STATION_3);
        Line saveLine = lineService.save(LINE);
        sectionService.saveSection(SECTION);
        sectionService.saveSection(SECTION_2);

        //when then
        assertThat(lineService.findStationsOfLine(saveLine))
                .containsOnly(saveStation, saveStation2, saveStation3);
    }
}
