package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.exception.service.ValidationFailureException;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.SectionDao;
import wooteco.subway.line.section.SectionRequest;
import wooteco.subway.line.section.SectionResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;
import wooteco.subway.station.Stations;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationService stationService;

    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Line lineA;
    private Section sectionAB;
    private Section sectionBC;

    @BeforeEach
    void setUp() {
        stationA = new Station(1L, "역A");
        stationB = new Station(2L, "역B");
        stationC = new Station(3L, "역C");
        lineA = new Line(1L, "노선A", "black");
        sectionAB = Section.Builder().id(1L).lineId(1L).upStationId(1L).downStationId(2L).distance(3).build();
        sectionBC = Section.Builder().id(2L).lineId(1L).upStationId(2L).downStationId(3L).distance(7).build();
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void findLine() {
        // given
        given_findLine();

        // when
        final LineResponse lineResponse = lineService.findLine(1L);

        // then
        then(lineDao).should(times(1)).findById(anyLong());
        then(sectionDao).should(times(1)).findByLineId(anyLong());
        then(stationService).should(times(1)).findByIds(anyList());

        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getStations()).extracting("id").containsExactlyInAnyOrder(1L, 2L);
    }

    private void given_findLine() {
        given_composeLine();
        given_composeStation();
    }

    private void given_composeLine() {
        given(lineDao.findById(1L)).willReturn(Optional.of(lineA));

        final List<Section> sectionGroup = Arrays.asList(sectionAB);
        given(sectionDao.findByLineId(lineA.getId())).willReturn(sectionGroup);
    }

    private void given_composeStation() {
        final Stations stations = new Stations(Arrays.asList(stationA, stationB));
        given(stationService.findByIds(Arrays.asList(1L, 2L))).willReturn(stations);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        final LineRequest lineRequest = new LineRequest("노선A", "black", 1L, 2L, 3, 100);
        given(lineDao.save(lineRequest.toEntity())).willReturn(lineA);
        given(sectionDao.save(lineRequest.toSectionEntity(lineA.getId()))).willReturn(sectionAB);
        given_findLine();

        // when
        final LineResponse lineResponse = lineService.createLine(lineRequest);

        // then
        then(lineDao).should(times(1)).save(any(Line.class));
        then(sectionDao).should(times(1)).save(any(Section.class));

        assertThat(lineResponse.getId()).isEqualTo(1L);
        assertThat(lineResponse.getStations()).extracting("id").containsExactlyInAnyOrder(1L, 2L);
    }

    @DisplayName("노선 생성시 상행과 하행이 같으면 예외발생")
    @Test
    void createLine_fail_sameStations() {
        // given
        final LineRequest lineRequest = new LineRequest("노선A", "black", 1L, 1L, 3, 100);
        given(lineDao.save(lineRequest.toEntity())).willReturn(lineA);

        // when, then
        assertThatThrownBy(() -> lineService.createLine(lineRequest))
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("상행역과 하행역이 같은 구간은 추가할 수 없습니다.");

        then(lineDao).should(times(1)).save(any(Line.class));
    }

    @DisplayName("추가하려는 구간의 상행과 하행이 둘 다 존재하면 예외발생.")
    @Test
    void addSection_fail_bothExistentStation() {
        // given
        final SectionRequest sectionRequest = new SectionRequest(1L, 2L, 7);
        given_composeLine();

        // when, then
        assertThatThrownBy(() -> lineService.addSection(lineA.getId(), sectionRequest))
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
    }

    @DisplayName("추가하려는 구간의 상행과 하행이 둘 다 존재하지 않으면 예외발생.")
    @Test
    void addSection_fail_notExistentStation() {
        // given
        final SectionRequest sectionRequest = new SectionRequest(3L, 3L, 7);
        given_composeLine();

        // when, then
        assertThatThrownBy(() -> lineService.addSection(lineA.getId(), sectionRequest))
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("상행역과 하행역 둘 다 포함되어있지 않습니다.");
    }

    @DisplayName("추가하려는 역이 종점인 구간을 추가한다.")
    @Test
    void addSection_terminalStation() {
        // given
        final SectionRequest sectionRequest = new SectionRequest(2L, 3L, 7);
        given_composeLine();

        given(sectionDao.save(sectionRequest.toEntity(lineA.getId()))).willReturn(sectionBC);

        // when
        final SectionResponse sectionResponse = lineService.addSection(lineA.getId(), sectionRequest);

        // then
        then(sectionDao).should(times(1)).save(any(Section.class));

        assertThat(sectionResponse.getId()).isEqualTo(sectionBC.getId());
    }

    @DisplayName("추가하려는 역이 중간인 구간을 추가한다.")
    @Test
    void addSection_middleStation() {
        // given
        final SectionRequest sectionRequest = new SectionRequest(1L, 3L, 1);
        given_composeLine();

        final Section updatedSection = Section.Builder()
            .id(sectionAB.getId())
            .lineId(lineA.getId())
            .upStationId(3L)
            .downStationId(2L)
            .distance(2).build();

        willDoNothing().given(sectionDao).update(updatedSection);

        given(sectionDao.save(sectionRequest.toEntity(lineA.getId()))).willReturn(sectionBC);

        // when
        final SectionResponse sectionResponse = lineService.addSection(lineA.getId(), sectionRequest);

        // then
        then(sectionDao).should(times(1)).update(any(Section.class));
        then(sectionDao).should(times(1)).save(any(Section.class));

        assertThat(sectionResponse.getId()).isEqualTo(sectionBC.getId());
    }

    @DisplayName("구간이 하나일 때 제거하면 예외가 발생한다.")
    @Test
    void deleteSection_fail_minimumSize() {
        // given
        given_composeLine();

        // when, then
        assertThatThrownBy(() -> lineService.deleteSection(lineA.getId(), sectionAB.getDownStationId()))
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("구간이 2개 미만이면 지울 수 없습니다.");
    }

    @DisplayName("종점인 구간을 제거한다.")
    @Test
    void deleteSection_terminalStation() {
        // given
        given_composeLineWithTwoSections();

        willDoNothing().given(sectionDao).deleteById(sectionAB.getId());

        // when
        lineService.deleteSection(lineA.getId(), sectionAB.getUpStationId());

        // then
        then(sectionDao).should(times(1)).deleteById(anyLong());
    }

    @DisplayName("중간인 구간을 제거한다.")
    @Test
    void deleteSection_middleStation() {
        // given
        given_composeLineWithTwoSections();

        willDoNothing().given(sectionDao).deleteById(sectionAB.getId());
        willDoNothing().given(sectionDao).deleteById(sectionBC.getId());

        final Section newSection = Section.Builder()
            .lineId(lineA.getId())
            .upStationId(sectionAB.getUpStationId())
            .downStationId(sectionBC.getDownStationId())
            .distance(sectionAB.getDistance() + sectionBC.getDistance())
            .build();

        given(sectionDao.save(newSection)).willReturn(any(Section.class));

        // when
        lineService.deleteSection(lineA.getId(), sectionAB.getDownStationId());

        // then
        then(sectionDao).should(times(2)).deleteById(anyLong());
        then(sectionDao).should(times(1)).save(any(Section.class));
    }

    private void given_composeLineWithTwoSections() {
        given(lineDao.findById(1L)).willReturn(Optional.of(lineA));

        final List<Section> sectionGroup = Arrays.asList(sectionAB, sectionBC);
        given(sectionDao.findByLineId(lineA.getId())).willReturn(sectionGroup);
    }
}
