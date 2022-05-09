package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.Fixtures.LINE;
import static wooteco.subway.Fixtures.SECTION;
import static wooteco.subway.Fixtures.SECTION_2;
import static wooteco.subway.Fixtures.SECTION_3;
import static wooteco.subway.Fixtures.SECTION_4;
import static wooteco.subway.Fixtures.SECTION_5;
import static wooteco.subway.Fixtures.STATION;
import static wooteco.subway.Fixtures.STATION_2;
import static wooteco.subway.Fixtures.STATION_3;
import static wooteco.subway.Fixtures.STATION_4;
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

    @DisplayName("맨 처음 지하철 구간을 생성한다.")
    @Test
    void saveSection() {
        //given
        stationService.save(STATION);
        stationService.save(STATION_2);
        lineService.save(LINE);
        Section resSection = sectionService.save(SECTION);
        assertThat(resSection)
                .isEqualTo(getSection(resSection.getId(), SECTION));
    }

    @DisplayName("상행종점을 등록하고, 해당 지하철 노선 안의 역들을 조회할 수 있다.")
    @Test
    void saveSection_upStation() {
        //given
        Station saveStation = stationService.save(STATION);
        Station saveStation2 = stationService.save(STATION_2);
        Station saveStation3 = stationService.save(STATION_3);
        lineService.save(LINE);

        //when
        sectionService.save(SECTION_2);
        sectionService.save(SECTION);

        //then
        assertThat(sectionService.findStationsOfLine(SECTION.getLineId()))
                .containsExactly(saveStation, saveStation2, saveStation3);
    }

    @DisplayName("하행종점을 등록하고, 해당 지하철 노선 안의 역들을 조회할 수 있다.")
    @Test
    void saveSection_downStation() {
        //given
        Station saveStation = stationService.save(STATION);
        Station saveStation2 = stationService.save(STATION_2);
        Station saveStation3 = stationService.save(STATION_3);
        Line saveLine = lineService.save(LINE);
        sectionService.save(SECTION);
        sectionService.save(SECTION_2);

        //when then
        assertThat(sectionService.findStationsOfLine(saveLine.getId()))
                .containsExactly(saveStation, saveStation2, saveStation3);
    }

    @DisplayName("노선의 역 사이에 새로운 역을 등록하고, 해당 노선 안의 역들을 조회할 수 있다.")
    @Test
    void saveSection_middleStation() {
        //given
        Station saveStation = stationService.save(STATION);
        Station saveStation2 = stationService.save(STATION_2);
        Station saveStation3 = stationService.save(STATION_3);
        Station saveStation4 = stationService.save(STATION_4);
        Line saveLine = lineService.save(LINE);
        sectionService.save(SECTION);
        sectionService.save(SECTION_2);
        sectionService.save(SECTION_3);

        //when then
        assertThat(sectionService.findStationsOfLine(saveLine.getId()))
                .containsExactly(saveStation, saveStation2, saveStation4, saveStation3);
    }

    @DisplayName("중복된 구간을 등록할 수 없다.")
    @Test
    void saveSectionError_alreadyExistSection() {
        //given
        stationService.save(STATION);
        stationService.save(STATION_2);
        lineService.save(LINE);
        sectionService.save(SECTION);

        //when then
        assertThatThrownBy(() -> sectionService.save(SECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 구간입니다.");
    }

    @DisplayName("중복된 구간을 등록할 수 없다.")
    @Test
    void saveSectionError_noStation() {
        //given
        lineService.save(LINE);

        //when then
        assertThatThrownBy(() -> sectionService.save(SECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 안에 존재하지 않는 아이디의 역이 있습니다.");
    }

    @DisplayName("기존 역과 연결될 수 없는 구간을 등록할 수 없다.")
    @Test
    void saveSectionError_noLink() {
        //given
        stationService.save(STATION);
        stationService.save(STATION_2);
        stationService.save(STATION_3);
        stationService.save(STATION_4);
        lineService.save(LINE);
        sectionService.save(SECTION);

        //when then
        assertThatThrownBy(() -> sectionService.save(SECTION_4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 구간은 역과 연결될 수 없습니다.");
    }

    @DisplayName("중간에 추가되는 새 구간의 길이가 기존 구간의 길이보다 작아야 한다.")
    @Test
    void saveSectionError_distance() {
        //given
        stationService.save(STATION);
        stationService.save(STATION_2);
        stationService.save(STATION_3);
        stationService.save(STATION_4);
        lineService.save(LINE);
        sectionService.save(SECTION_2);

        //when then
        assertThat(sectionService.save(SECTION))
                .isNotNull();
        assertThatThrownBy(() -> sectionService.save(SECTION_5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새 구간의 길이가 기존 역 사이 길이보다 작아야 합니다.");
    }

    @DisplayName("노선의 상행 역을 지울 수 있다.")
    @Test
    void deleteSection_upStation() {
        //given
        Station saveStation = stationService.save(STATION);
        Station saveStation2 = stationService.save(STATION_2);
        Station saveStation3 = stationService.save(STATION_3);
        Station saveStation4 = stationService.save(STATION_4);
        Line saveLine = lineService.save(LINE);
        sectionService.save(SECTION);
        sectionService.save(SECTION_2);
        sectionService.save(SECTION_4);

        //when
        sectionService.deleteSection(saveLine.getId(), saveStation.getId());

        //then
        assertThat(sectionService.findStationsOfLine(saveLine.getId()))
                .containsExactly(saveStation2, saveStation3, saveStation4);
    }

    @DisplayName("노선의 하행 역을 지울 수 있다.")
    @Test
    void deleteSection_downStation() {
        //given
        Station saveStation = stationService.save(STATION);
        Station saveStation2 = stationService.save(STATION_2);
        Station saveStation3 = stationService.save(STATION_3);
        Station saveStation4 = stationService.save(STATION_4);
        Line saveLine = lineService.save(LINE);
        sectionService.save(SECTION);
        sectionService.save(SECTION_2);
        sectionService.save(SECTION_4);

        //when
        sectionService.deleteSection(saveLine.getId(), saveStation4.getId());

        //then
        assertThat(sectionService.findStationsOfLine(saveLine.getId()))
                .containsExactly(saveStation, saveStation2, saveStation3);
    }

    @DisplayName("노선의 중간 역을 지울 수 있다.")
    @Test
    void deleteSection_middleStation() {
        //given
        Station saveStation = stationService.save(STATION);
        Station saveStation2 = stationService.save(STATION_2);
        Station saveStation3 = stationService.save(STATION_3);
        Station saveStation4 = stationService.save(STATION_4);
        Line saveLine = lineService.save(LINE);
        sectionService.save(SECTION);
        sectionService.save(SECTION_2);
        sectionService.save(SECTION_4);

        //when
        sectionService.deleteSection(saveLine.getId(), saveStation2.getId());

        //then
        assertThat(sectionService.findStationsOfLine(saveLine.getId()))
                .containsExactly(saveStation, saveStation3, saveStation4);
        assertThat(sectionService.findById(4L).getDistance())
                .isEqualTo(15);
    }

    @DisplayName("노선이 1개 이하의 구간을 가지고 있으면 역을 삭제할 수 없다.")
    @Test
    void deleteSection_OneSectionError() {
        //given
        Station saveStation = stationService.save(STATION);
        stationService.save(STATION_2);
        Line saveLine = lineService.save(LINE);
        sectionService.save(SECTION);

        //when then
        assertThatThrownBy(() -> sectionService.deleteSection(saveLine.getId(), saveStation.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 지하철 노선은 1개 이하의 구간을 가지고 있어 역을 삭제할 수 없습니다.");
    }
}
