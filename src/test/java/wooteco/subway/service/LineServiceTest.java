package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static wooteco.subway.Fixtures.BLUE;
import static wooteco.subway.Fixtures.GANGNAM;
import static wooteco.subway.Fixtures.HYEHWA;
import static wooteco.subway.Fixtures.LINE_2;
import static wooteco.subway.Fixtures.LINE_4;
import static wooteco.subway.Fixtures.RED;
import static wooteco.subway.Fixtures.SECTION_1_2;
import static wooteco.subway.Fixtures.SECTION_1_2_SHORT;
import static wooteco.subway.Fixtures.SECTION_1_3;
import static wooteco.subway.Fixtures.SECTION_2_3;
import static wooteco.subway.Fixtures.SECTION_2_3_SHORT;
import static wooteco.subway.Fixtures.SINSA;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.dto.request.UpdateLineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.exception.duplicate.DuplicateLineException;
import wooteco.subway.exception.notfound.NotFoundLineException;
import wooteco.subway.exception.notfound.NotFoundStationException;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private StationService stationService;

    @Test
    @DisplayName("지하철 노선을 생성한다. 이때 관련 구간을 같이 생성한다.")
    void create() {
        // given
        final Line savedLine = new Line(1L, LINE_2, RED, new Sections(SECTION_1_2));
        final CreateLineRequest request = new CreateLineRequest(LINE_2, RED, 1L, 2L, 10);
        final Sections sections = new Sections(List.of(SECTION_1_2));

        // mocking
        given(lineRepository.save(any(Line.class))).willReturn(1L);
        given(stationService.show(1L)).willReturn(new Station(1L, HYEHWA));
        given(stationService.show(2L)).willReturn(new Station(2L, SINSA));

        given(lineRepository.find(any(Long.class))).willReturn(savedLine);

        // when
        final LineResponse response = lineService.create(request);
        final List<StationResponse> stationResponses = response.getStations();

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo(request.getName());
            assertThat(response.getColor()).isEqualTo(request.getColor());
            assertThat(stationResponses.get(0).getId()).isEqualTo(1L);
            assertThat(stationResponses.get(0).getName()).isEqualTo(HYEHWA);
            assertThat(stationResponses.get(1).getId()).isEqualTo(2L);
            assertThat(stationResponses.get(1).getName()).isEqualTo(SINSA);
        });
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 생성하면, 예외를 발생한다.")
    void createWithDuplicateName() {
        // given
        final CreateLineRequest request = new CreateLineRequest(LINE_2, RED, 1L, 2L, 10);

        // mocking
        given(lineRepository.save(any(Line.class))).willThrow(DuplicateKeyException.class);

        // when & then
        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(DuplicateLineException.class);
    }

    @Test
    @DisplayName("지하철 노선 목록을 조회한다. 관련 역들도 함께 조회한다.")
    void showAll() {
        // given
        final List<Line> lines = List.of(new Line(1L, LINE_2, RED, new Sections(SECTION_1_2)),
                new Line(2L, LINE_4, BLUE, new Sections(SECTION_1_2)));

        // mocking
        given(lineRepository.findAll()).willReturn(lines);

        // when
        final List<LineResponse> responses = lineService.showAll();

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("노선을 조회한다. 관련 역들도 함께 조회한다.")
    void show() {
        // mocking
        given(lineRepository.find(1L)).willReturn(new Line(1L, LINE_2, RED, new Sections(SECTION_1_2)));
        // when
        final LineResponse response = lineService.show(1L);
        final StationResponse stationResponse1 = response.getStations().get(0);
        final StationResponse stationResponse2 = response.getStations().get(1);

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo(LINE_2);
            assertThat(response.getColor()).isEqualTo(RED);
            assertThat(response.getStations()).hasSize(2);
            assertThat(stationResponse1.getId()).isEqualTo(1L);
            assertThat(stationResponse1.getName()).isEqualTo(HYEHWA);
            assertThat(stationResponse2.getId()).isEqualTo(2L);
            assertThat(stationResponse2.getName()).isEqualTo(SINSA);
        });
    }

    @Test
    @DisplayName("없는 지하철 노선을 조회하면, 예외를 발생시킨다.")
    void showWhenNotWithExistLineId() {
        // mocking
        given(lineRepository.find(1L)).willThrow(NotFoundLineException.class);

        // when & then
        assertThatThrownBy(() -> lineService.show(1L))
                .isInstanceOf(NotFoundLineException.class);
    }

    @Test
    @DisplayName("노선을 업데이트 한다.")
    void updateLine() {
        // given
        final long id = 1L;
        final String name = "2호선";
        final String color = "bg-red-600";

        // mocking
        given(lineRepository.existsById(1L)).willReturn(true);

        // when
        lineService.updateLine(id, new UpdateLineRequest(name, color));

        // then
        verify(lineRepository).update(any(Line.class));
    }

    @Test
    @DisplayName("없는 노선을 없데이트 한다면, 예외를 발생시킨다.")
    void updateWithNotExistLineId() {
        // mocking
        given(lineRepository.existsById(1L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> lineService.updateLine(1L, new UpdateLineRequest(LINE_4, BLUE)))
                .isInstanceOf(NotFoundLineException.class);
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void deleteLine() {
        // given
        long id = 1L;
        final String name = "2호선";
        final String color = "bg-red-600";

        // mocking
        given(lineRepository.existsById(1L)).willReturn(true);

        // when
        lineService.deleteLine(id);

        // then
        verify(lineRepository).deleteById(id);
    }

    @Test
    @DisplayName("없는 노선을 삭제하면, 예외를 발생시킨다.")
    void deleteWithNotExistLineId() {
        // mocking
        given(lineRepository.existsById(1L)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> lineService.deleteLine(1L))
                .isInstanceOf(NotFoundLineException.class);
    }

    @Test
    @DisplayName("노선의 끝에 구간을 등록한다.")
    void createSectionLast() {
        // given
        final long lineId = 1L;

        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        given(sectionRepository.findAllByLineId(lineId)).willReturn(
                new Sections(List.of(SECTION_1_2)));
        given(stationService.show(2L)).willReturn(new Station(2L, SINSA));
        given(stationService.show(3L)).willReturn(new Station(3L, GANGNAM));

        // when
        lineService.createSection(lineId, new CreateSectionRequest(2L, 3L, 10));

        // then
        verify(sectionRepository).batchSave(1L, List.of(SECTION_2_3));
    }

    @Test
    @DisplayName("노선의 처음에 구간을 등록한다.")
    void createSectionFirst() {
        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        given(sectionRepository.findAllByLineId(any(Long.class))).willReturn(
                new Sections(List.of(SECTION_2_3)));
        given(stationService.show(1L)).willReturn(new Station(1L, HYEHWA));
        given(stationService.show(2L)).willReturn(new Station(2L, SINSA));

        // when
        lineService.createSection(1L, new CreateSectionRequest(1L, 2L, 10));

        // then
        verify(sectionRepository).batchSave(1L, List.of(SECTION_1_2));
    }

    @Test
    @DisplayName("노선의 중간에 상행역이 겹치는 구간을 등록한다.")
    void createSectionMiddleSameUp() {
        // given
        final long lineId = 1L;

        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        given(sectionRepository.findAllByLineId(lineId)).willReturn(
                new Sections(List.of(SECTION_1_3)));
        given(stationService.show(1L)).willReturn(new Station(1L, HYEHWA));
        given(stationService.show(2L)).willReturn(new Station(2L, SINSA));

        // when
        lineService.createSection(lineId, new CreateSectionRequest(1L, 2L, 5));

        // then
        verify(sectionRepository).batchDelete(1L, List.of(SECTION_1_3));
        verify(sectionRepository).batchSave(1L, List.of(SECTION_1_2_SHORT, SECTION_2_3_SHORT));
    }

    @Test
    @DisplayName("노선의 중간에 하행역이 겹치는 구간을 등록한다.")
    void createSectionMiddleSameDown() {
        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        given(sectionRepository.findAllByLineId(any(Long.class))).willReturn(
                new Sections(List.of(SECTION_1_3)));
        given(stationService.show(2L)).willReturn(new Station(2L, SINSA));
        given(stationService.show(3L)).willReturn(new Station(3L, GANGNAM));

        // when
        lineService.createSection(1L, new CreateSectionRequest(2L, 3L, 5));

        // then
        verify(sectionRepository).batchDelete(1L, List.of(SECTION_1_3));
        verify(sectionRepository).batchSave(1L, List.of(SECTION_2_3_SHORT, SECTION_1_2_SHORT));
    }

    @Test
    @DisplayName("기존보다 긴 구간을 등록하면, 예외를 발생시킨다.")
    void createWithLongDistance() {
        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        given(sectionRepository.findAllByLineId(any(Long.class))).willThrow(IllegalArgumentException.class);

        // when
        assertThatThrownBy(() -> lineService.createSection(1L, new CreateSectionRequest(1L, 2L, 5)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("없는 라인의 구간을 등록하면, 예외를 발생시킨다.")
    void createWithNotExistLine() {
        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(false);

        // when
        assertThatThrownBy(() -> lineService.createSection(1L, new CreateSectionRequest(1L, 2L, 5)))
                .isInstanceOf(NotFoundLineException.class);

    }

    @Test
    @DisplayName("없는 역을 가진 구간을 등록하면, 예외를 발생시킨다.")
    void createWithNotExistStation() {
        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        doThrow(NotFoundStationException.class).when(stationService).validateNotExistStation(any(Long.class));

        // when
        assertThatThrownBy(() -> lineService.createSection(1L, new CreateSectionRequest(1L, 2L, 5)))
                .isInstanceOf(NotFoundStationException.class);
    }

    @Test
    @DisplayName("지하철 구간을 삭제한다.")
    void deleteSection() {
        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        given(sectionRepository.findAllByLineId(any(Long.class))).willReturn(
                new Sections(List.of(SECTION_1_2, SECTION_2_3)));

        // when
        lineService.deleteSection(1L, 1L);

        // then
        verify(sectionRepository).batchSave(1L, List.of());
        verify(sectionRepository).batchDelete(1L, List.of(SECTION_1_2));
    }

    @Test
    @DisplayName("구간을 삭제 후 노선에 구간이 하나만 남는다면, 예외를 발생시킨다.")
    void deleteWhenRemainOneSection() {
        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        given(sectionRepository.findAllByLineId(any(Long.class))).willReturn(
                new Sections(List.of(SECTION_1_2)));

        // when
        assertThatThrownBy(() -> lineService.deleteSection(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("없는 노선의 구간을 삭제하면, 예외를 발생시킨다.")
    void deleteWithNotExistLine() {
        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(false);

        // when
        assertThatThrownBy(() -> lineService.deleteSection(1L, 1L))
                .isInstanceOf(NotFoundLineException.class);
    }

    @Test
    @DisplayName("없는 역의 구간을 삭제하면, 예외를 발생시킨다.")
    void deleteWithNotExistStation() {
        // mocking
        given(lineRepository.existsById(any(Long.class))).willReturn(true);
        doThrow(NotFoundStationException.class).when(stationService).validateNotExistStation(any(Long.class));

        // when
        assertThatThrownBy(() -> lineService.deleteSection(1L, 1L))
                .isInstanceOf(NotFoundStationException.class);
    }
}
