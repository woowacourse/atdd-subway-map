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

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

class SectionsServiceTest extends ServiceTest {
    private static final Station station = new Station("강남역");

    @Autowired
    private SectionsService sectionsService;
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

        Section resSection = sectionsService.save(SECTION);
        assertThat(resSection)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(SECTION);
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
        sectionsService.save(SECTION_2);
        sectionsService.save(SECTION);

        //then
        assertThat(sectionsService.findStationsOfLine(SECTION.getLineId()))
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
        sectionsService.save(SECTION);
        sectionsService.save(SECTION_2);

        //when then
        assertThat(sectionsService.findStationsOfLine(saveLine.getId()))
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
        sectionsService.save(SECTION);
        sectionsService.save(SECTION_2);
        sectionsService.save(SECTION_3);

        //when then
        assertThat(sectionsService.findStationsOfLine(saveLine.getId()))
                .hasSize(4);
        assertThat(sectionsService.findStationsOfLine(saveLine.getId()))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(saveStation, saveStation2, saveStation4, saveStation3));
    }

    @DisplayName("중복된 구간을 등록할 수 없다.")
    @Test
    void saveSectionError_alreadyExistSection() {
        //given
        stationService.save(STATION);
        stationService.save(STATION_2);
        lineService.save(LINE);
        sectionsService.save(SECTION);

        //when then
        assertThatThrownBy(() -> sectionsService.save(SECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 구간입니다.");
    }

    @DisplayName("중복된 구간을 등록할 수 없다.")
    @Test
    void saveSectionError_noStation() {
        //given
        lineService.save(LINE);

        //when then
        assertThatThrownBy(() -> sectionsService.save(SECTION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 아이디의 역을 찾을 수 없습니다.");
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
        sectionsService.save(SECTION);

        //when then
        assertThatThrownBy(() -> sectionsService.save(SECTION_4))
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
        sectionsService.save(SECTION_2);

        //when then
        assertThat(sectionsService.save(SECTION))
                .isNotNull();
        assertThatThrownBy(() -> sectionsService.save(SECTION_5))
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
        sectionsService.save(SECTION);
        sectionsService.save(SECTION_2);
        sectionsService.save(SECTION_4);

        //when
        sectionsService.delete(saveLine.getId(), saveStation.getId());

        //then
        assertThat(sectionsService.findStationsOfLine(saveLine.getId()))
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
        sectionsService.save(SECTION);
        sectionsService.save(SECTION_2);
        sectionsService.save(SECTION_4);

        //when
        sectionsService.delete(saveLine.getId(), saveStation4.getId());

        //then
        assertThat(sectionsService.findStationsOfLine(saveLine.getId()))
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
        sectionsService.save(SECTION);
        sectionsService.save(SECTION_2);
        sectionsService.save(SECTION_4);

        //when
        sectionsService.delete(saveLine.getId(), saveStation2.getId());

        //then
        assertThat(sectionsService.findStationsOfLine(saveLine.getId()))
                .containsExactly(saveStation, saveStation3, saveStation4);
        assertThat(sectionsService.findById(4L).getDistance())
                .isEqualTo(15);
    }

    @DisplayName("노선이 1개 이하의 구간을 가지고 있으면 역을 삭제할 수 없다.")
    @Test
    void deleteSection_OneSectionError() {
        //given
        Station saveStation = stationService.save(STATION);
        stationService.save(STATION_2);
        Line saveLine = lineService.save(LINE);
        sectionsService.save(SECTION);

        //when then
        assertThatThrownBy(() -> sectionsService.delete(saveLine.getId(), saveStation.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 지하철 노선은 1개 이하의 구간을 가지고 있어 역을 삭제할 수 없습니다.");
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        //given
        Station resStation = stationService.save(station);
        Long id = resStation.getId();

        //when
        sectionsService.deleteStationById(id);

        //then
        assertThat(stationService.findAll())
                .isNotIn(resStation);
    }

    @DisplayName("없는 지하철역을 삭제할 수 없다.")
    @Test
    void delete_error() {
        assertThatThrownBy(() -> sectionsService.deleteStationById(100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 아이디의 역을 찾을 수 없습니다.");
    }

    @DisplayName("이미 노선에 등록된 지하철 역은 삭제할 수 없다.")
    @Test
    void delete_linkedError() {
        //given
        Station savedStation = stationService.save(STATION);
        stationService.save(STATION_2);
        lineService.save(LINE);
        sectionsService.save(SECTION);

        //when then
        assertThatThrownBy(() -> sectionsService.deleteStationById(savedStation.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철 노선에 해당 역이 등록되어있어 역을 삭제할 수 없습니다.");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        Line resLine = lineService.save(LINE);
        Long id = resLine.getId();

        //when
        sectionsService.deleteLineById(id);

        //then
        assertThat(lineService.findAll())
                .isEmpty();
    }

    @DisplayName("없는 지하철 노선을 삭제할 수 없다.")
    @Test
    void deleteLine_error() {
        assertThatThrownBy(() -> sectionsService.deleteLineById(100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 아이디의 노선을 찾을 수 없습니다.");
    }
}
