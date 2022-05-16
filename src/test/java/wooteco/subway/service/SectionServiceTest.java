package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Name;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.section.SectionCreationRequest;
import wooteco.subway.dto.section.SectionDeletionRequest;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.line.NoSuchLineException;
import wooteco.subway.exception.section.NoSuchSectionException;

class SectionServiceTest extends ServiceTest {

    @InjectMocks
    private SectionService sectionService;

    private Station upStation;
    private Station downStation;
    private Sections sections;
    private Line line;

    @BeforeEach
    void setUpData() {
        upStation = new Station(1L, "서울숲역");
        downStation = new Station(2L, "왕십리역");
        final Section section = new Section(1L, line, upStation, downStation, new Distance(10));
        sections = new Sections(List.of(section));
        line = new Line(1L, new Name("수인분당선"), "bg-yellow-600", sections);
    }

    @Test
    @DisplayName("구간을 등록하려는 노선이 존재하지 않으면 예외를 던진다.")
    void Create_NotExistLine_ExceptionThrown() {
        // given
        final SectionCreationRequest request = new SectionCreationRequest(1L, 888L, 999L, 10);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> sectionService.save(request))
                .isInstanceOf(NoSuchLineException.class);
    }

    @Test
    @DisplayName("등록하려는 상행역과 하행역이 모두 노선에 포함되어 있지 않으면 등록할 수 없다.")
    void Create_BothStationNotEnrolledInLine_ExceptionThrown() {
        // given
        final SectionCreationRequest request = new SectionCreationRequest(1L, 888L, 999L, 10);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));

        final List<Station> stations = List.of(
                new Station(1L, "왕십리역"),
                new Station(2L, "서울숲역")
        );
        given(stationDao.findAllByLineId(any(Long.class)))
                .willReturn(stations);

        // then
        assertThatThrownBy(() -> sectionService.save(request))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("상행역과 하행역 중 하나의 역만 노선에 포함되어 있어야 합니다.");
    }

    @Test
    @DisplayName("등록하려는 상행역과 하행역이 모두 노선에 이미 포함되어 있으면 등록할 수 없다.")
    void Create_BothStationEnrolledInLine_ExceptionThrown() {
        // given
        final long stationOneId = 1L;
        final long stationTwoId = 2L;
        final SectionCreationRequest request = new SectionCreationRequest(1L, stationOneId, stationTwoId, 10);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));

        final List<Station> stations = List.of(
                new Station(stationOneId, "왕십리역"),
                new Station(stationTwoId, "서울숲역")
        );
        given(stationDao.findAllByLineId(any(Long.class)))
                .willReturn(stations);

        // then
        assertThatThrownBy(() -> sectionService.save(request))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("상행역과 하행역 중 하나의 역만 노선에 포함되어 있어야 합니다.");
    }

    @ParameterizedTest
    @DisplayName("역 사이에 새로운 구간을 등록할 경우 기존 구간의 길이보다 작지 않으면 등록할 수 없다.")
    @CsvSource(value = {"1:999:10", "1:999:11", "999:2:10", "999:2:11"}, delimiter = ':')
    void Create_InMiddleInValidDistance_ExceptionThrown(final Long upStationId, final Long downStationId,
                                                        final int distance) {
        // given
        final long lineId = 1L;
        final SectionCreationRequest request = new SectionCreationRequest(lineId, upStationId, downStationId, distance);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));

        final Station upStation = new Station(1L, "왕십리역");
        final Station downStation = new Station(2L, "서울숲역");
        final List<Station> stations = List.of(
                upStation,
                downStation
        );
        given(stationDao.findAllByLineId(any(Long.class)))
                .willReturn(stations);

        final Section section = new Section(1L, line, upStation, downStation, new Distance(10));
        given(sectionDao.findBy(any(Long.class), any(Long.class), any(Long.class)))
                .willReturn(Optional.of(section));

        given(stationDao.findById(any(Long.class)))
                .willReturn(Optional.of(upStation))
                .willReturn(Optional.of(downStation));

        // then
        assertThatThrownBy(() -> sectionService.save(request))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("기존 구간의 길이 보다 작지 않습니다.");
    }

    @Test
    @DisplayName("상행 종점 구간을 연장한다.")
    void Create_NewUpStation_Success() {
        // given
        final long lineId = 1L;
        final long upStationId = 1L;
        final long downStationId = 2L;
        final SectionCreationRequest request = new SectionCreationRequest(lineId, 999L, upStationId, 7);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));

        final Station upStation = new Station(upStationId, "왕십리역");
        final Station downStation = new Station(downStationId, "서울숲역");
        final List<Station> stations = List.of(
                upStation,
                downStation
        );
        given(stationDao.findAllByLineId(any(Long.class)))
                .willReturn(stations);

        given(sectionDao.findBy(any(Long.class), any(Long.class), any(Long.class)))
                .willReturn(Optional.empty());

        final Section section = new Section(1L, line, upStation, downStation, new Distance(10));
        given(sectionDao.findByLineIdAndUpStationId(any(Long.class), any(Long.class)))
                .willReturn(Optional.of(section));

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));
        given(stationDao.findById(any(Long.class)))
                .willReturn(Optional.of(upStation))
                .willReturn(Optional.of(downStation));

        // then
        assertThatCode(() -> sectionService.save(request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("하행 종점 구간을 연장한다.")
    void Create_NewDownStation_Success() {
        // given
        final long lineId = 1L;
        final long upStationId = 1L;
        final long downStationId = 2L;
        final SectionCreationRequest request = new SectionCreationRequest(lineId, downStationId, 999L, 7);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));

        final Station upStation = new Station(upStationId, "왕십리역");
        final Station downStation = new Station(downStationId, "서울숲역");
        final List<Station> stations = List.of(
                upStation,
                downStation
        );
        given(stationDao.findAllByLineId(any(Long.class)))
                .willReturn(stations);

        given(sectionDao.findBy(any(Long.class), any(Long.class), any(Long.class)))
                .willReturn(Optional.empty());

        given(sectionDao.findByLineIdAndUpStationId(any(Long.class), any(Long.class)))
                .willReturn(Optional.empty());

        final Section section = new Section(1L, line, upStation, downStation, new Distance(10));
        given(sectionDao.findByLineIdAndDownStationId(any(Long.class), any(Long.class)))
                .willReturn(Optional.of(section));

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));
        given(stationDao.findById(any(Long.class)))
                .willReturn(Optional.of(upStation))
                .willReturn(Optional.of(downStation));

        // then
        assertThatCode(() -> sectionService.save(request))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @DisplayName("[중간, 상행 종점, 하행 종점]에 위치한 구간을 삭제한다.")
    @ValueSource(longs = {2, 1, 4})
    void Delete_MiddleStation_Success(final Long stationIdToDelete) {
        // given
        final SectionDeletionRequest request = new SectionDeletionRequest(line.getId(), stationIdToDelete);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));

        final Sections sections = new Sections(List.of(
                new Section(1L, line, upStation, downStation, new Distance(5)),
                new Section(2L, line, new Station(2L, "2"), new Station(3L, "3"), new Distance(7)),
                new Section(3L, line, new Station(3L, "3"), new Station(4L, "4"), new Distance(11))
        ));
        given(sectionDao.findAllByLineId(any(Long.class)))
                .willReturn(sections);

        given(stationDao.findById(any(Long.class)))
                .willReturn(Optional.of(new Station(2L, "2")));

        // then
        assertThatCode(() -> sectionService.delete(request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("삭제하려는 구간이 마지막 하나의 구간이면 예외를 던진다.")
    void Delete_LastOneSection_ExceptionThrown() {
        // given
        final SectionDeletionRequest request = new SectionDeletionRequest(line.getId(), 999L);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));

        given(sectionDao.findAllByLineId(any(Long.class)))
                .willReturn(sections);

        given(stationDao.findById(any(Long.class)))
                .willReturn(Optional.of(upStation));

        // then
        assertThatThrownBy(() -> sectionService.delete(request))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("구간을 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("삭제하려는 역과 일치하는 구간이 존재하지 않으면 예외를 던진다.")
    void Delete_NotExistStation_ExceptionThrown() {
        // given
        final SectionDeletionRequest request = new SectionDeletionRequest(line.getId(), 1L);

        given(lineDao.findById(any(Long.class)))
                .willReturn(Optional.of(line));

        final Sections sections = new Sections(List.of(
                new Section(1L, line, upStation, downStation, new Distance(5)),
                new Section(2L, line, new Station(2L, "2"), new Station(3L, "3"), new Distance(7)),
                new Section(3L, line, new Station(3L, "3"), new Station(4L, "4"), new Distance(11))
        ));
        given(sectionDao.findAllByLineId(any(Long.class)))
                .willReturn(sections);

        given(stationDao.findById(any(Long.class)))
                .willReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> sectionService.delete(request))
                .isInstanceOf(NoSuchSectionException.class);
    }
}
