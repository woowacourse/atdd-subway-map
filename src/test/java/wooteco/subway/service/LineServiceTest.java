package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.station.StationResponse;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.exception.line.NoSuchLineException;
import wooteco.subway.exception.section.NoSuchSectionException;

class LineServiceTest extends ServiceTest {

    private static final String LINE_NAME = "7호선";
    private static final String LINE_COLOR = "bg-red-600";

    @InjectMocks
    private LineService lineService;

    private Station upStation;
    private Station downStation;
    private LineRequest request;

    @BeforeEach
    void setUpData() {
        upStation = new Station(1L, "선릉역");
        downStation = new Station(2L, "삼성역");
        request = new LineRequest(LINE_NAME, LINE_COLOR, upStation.getId(), downStation.getId(), 10);
    }

    @Test
    @DisplayName("노선과 구간을 생성한다.")
    void Create_WithSection_Success() {
        // given
        final Line expected = new Line(1L, LINE_NAME, LINE_COLOR);
        given(lineDao.insert(any(Line.class)))
                .willReturn(Optional.of(expected));

        given(stationDao.findById(any(Long.class)))
                .willReturn(Optional.of(upStation))
                .willReturn(Optional.of(downStation));

        given(sectionDao.insert(any(Section.class)))
                .willReturn(any(Long.class));

        // when
        final LineResponse actual = lineService.create(request);

        // then
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getColor()).isEqualTo(expected.getColor());

        final List<String> expectedStationNames = Stream.of(upStation, downStation)
                .map(Station::getName)
                .collect(Collectors.toList());
        final List<String> actualStationNames = actual.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(actualStationNames).isEqualTo(expectedStationNames);
    }

    @Test
    @DisplayName("저장하려는 노선의 이름이 중복되면 예외를 던진다.")
    void Create_DuplicateName_ExceptionThrown() {
        // given
        given(lineDao.insert(any(Line.class)))
                .willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(DuplicateLineException.class);
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void FindAll() {
        // given
        final List<Line> expected = List.of(
                new Line(1L, LINE_NAME, LINE_COLOR),
                new Line(2L, "5호선", "bg-blue-600")
        );
        given(lineDao.findAll())
                .willReturn(expected);

        final List<Station> expectedStations1 = List.of(
                upStation,
                downStation
        );
        final List<Station> expectedStations2 = List.of(
                new Station(3L, "왕십리역"),
                new Station(4L, "답십리역")
        );
        given(stationDao.findAllByLineId(any(Long.class)))
                .willReturn(expectedStations1)
                .willReturn(expectedStations2);

        // when
        final List<LineResponse> actual = lineService.findAll();

        // then
        assertThat(actual).hasSameSizeAs(expected);

        final LineResponse actualLine1 = actual.get(0);
        assertThat(actualLine1.getName()).isEqualTo(LINE_NAME);

        final List<String> actualStationNames1 = actualLine1.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(actualStationNames1).containsExactly(upStation.getName(), downStation.getName());

        final LineResponse actualLine = actual.get(1);
        assertThat(actualLine.getName()).isEqualTo("5호선");
        final List<String> actualStationNames2 = actualLine.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(actualStationNames2).containsExactly("왕십리역", "답십리역");
    }

    @Test
    @DisplayName("id에 해당하는 노선을 조회한다.")
    void FindById() {
        // given
        final long id = 1L;
        final Line expected = new Line(id, LINE_NAME, LINE_COLOR);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(expected));

        final Sections sections = new Sections(List.of(
                new Section(1L, expected, upStation, downStation, new Distance(10))
        ));
        given(sectionDao.findAllByLineId(any(Long.class)))
                .willReturn(sections);

        // when
        final LineResponse actual = lineService.findById(id);

        // then
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getColor()).isEqualTo(expected.getColor());

        final List<String> actualStationNames = actual.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        final List<String> expectedStationNames = List.of(upStation.getName(), downStation.getName());
        assertThat(actualStationNames).isEqualTo(expectedStationNames);
    }

    @Test
    @DisplayName("id에 해당하는 노선이 존재하지 않으면 예외를 던진다.")
    void FindById_NotExistId_ExceptionThrown() {
        // given
        final long id = 1L;
        given(lineDao.findById(id))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> lineService.findById(id))
                .isInstanceOf(NoSuchLineException.class);
    }

    @Test
    @DisplayName("노선에 해당하는 역이 존재하지 않으면 예외를 던진다.")
    void FindById_EmptyStations_ExceptionThrown() {
        // given
        final Line line = new Line(1L, LINE_NAME, LINE_COLOR);
        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));

        given(sectionDao.findAllByLineId(any(Long.class)))
                .willReturn(new Sections(Collections.emptyList()));

        // then
        final Long lineId = line.getId();
        assertThatThrownBy(() -> lineService.findById(lineId))
                .isInstanceOf(NoSuchSectionException.class);
    }

    @Test
    @DisplayName("id에 해당하는 노선 정보를 수정한다.")
    void UpdateById() {
        // given
        final long id = 1L;

        final Line existLine = new Line("xxx", "xx-x-xx");
        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(existLine));

        final Line updateLine = new Line(LINE_NAME, LINE_COLOR);
        given(lineDao.updateById(id, updateLine))
                .willReturn(Optional.of(updateLine));

        // then
        assertThatCode(() -> lineService.updateById(id, request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("업데이트하려는 노선이 존재하지 않으면 예외를 던진다.")
    void UpdateById_NotExistId_ExceptionThrown() {
        // given
        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lineService.updateById(1L, request))
                .isInstanceOf(NoSuchLineException.class);
    }

    @Test
    @DisplayName("업데이트하려는 이름이 중복되면 예외를 던진다.")
    void UpdateById_DuplicateName_ExceptionThrown() {
        // given
        final long id = 1L;

        final Line existLine = new Line("xxx", "xx-x-xx");
        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(existLine));

        final Line updateLine = new Line(LINE_NAME, LINE_COLOR);
        given(lineDao.updateById(id, updateLine))
                .willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lineService.updateById(id, request))
                .isInstanceOf(DuplicateLineException.class);
    }

    @Test
    @DisplayName("id에 해당하는 노선을 삭제한다.")
    void DeleteById() {
        // given
        final long id = 1L;
        given(lineDao.deleteById(id))
                .willReturn(1);

        // then
        assertThatCode(() -> lineService.deleteById(id))
                .doesNotThrowAnyException();
    }
}