package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionEntity;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.LineNotFoundException;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;
    @Mock
    private SectionDao sectionDao;
    @Mock
    private StationDao stationDao;

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() {
        // given
        final LineRequest createLineRequest = createLineRequest();

        // mocking
        given(lineDao.save(any())).willReturn(1L);
        given(stationDao.findById(1L)).willReturn(new Station(1L, "신대방역"));
        given(stationDao.findById(2L)).willReturn(new Station(2L, "선릉역"));
        given(sectionDao.save(any())).willReturn(1L);

        // when
        final LineResponse response = lineService.createLine(createLineRequest);
        final List<StationResponse> stationResponses = response.getStations();

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo(createLineRequest.getName());
            assertThat(response.getColor()).isEqualTo(createLineRequest.getColor());
            assertThat(stationResponses.get(0).getId()).isEqualTo(1L);
            assertThat(stationResponses.get(0).getName()).isEqualTo("신대방역");
            assertThat(stationResponses.get(1).getId()).isEqualTo(2L);
            assertThat(stationResponses.get(1).getName()).isEqualTo("선릉역");
        });
    }

    @Test
    @DisplayName("모든 노선과 역을 조회한다.")
    void showLines() {
        // given
        final List<Line> saveLines = List.of(new Line(1L, "신분당선", "bg-red-600"));

        // mocking
        given(lineDao.findAll()).willReturn(saveLines);
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(new SectionEntity(1L, 1L, 1L, 2L, 10)));
        given(stationDao.findById(1L)).willReturn(new Station("강남역"));
        given(stationDao.findById(2L)).willReturn(new Station("판교역"));

        // when
        final List<LineResponse> responses = lineService.showLines();

        // then
        assertAll(() -> {
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getName()).isEqualTo(saveLines.get(0).getName());
            assertThat(responses.get(0).getColor()).isEqualTo(saveLines.get(0).getColor());
        });
    }

    @Test
    @DisplayName("노선과 역을 조회한다.")
    void showLine() {
        // given
        final long id = 1L;
        final String name = "2호선";
        final String color = "bg-red-600";

        // mocking
        given(lineDao.findById(id)).willReturn(new Line(id, name, color));
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(new SectionEntity(1L, 1L, 1L, 2L, 10)));
        given(stationDao.findById(1L)).willReturn(new Station("강남역"));
        given(stationDao.findById(2L)).willReturn(new Station("판교역"));

        // when
        final LineResponse response = lineService.showLine(id);

        // then
        assertAll(() -> {
            assertThat(response.getName()).isEqualTo(name);
            assertThat(response.getColor()).isEqualTo(color);
        });
    }

    @Test
    @DisplayName("노선을 업데이트 한다.")
    void updateLine() {
        // given
        final long id = 1L;
        final String name = "2호선";
        final String color = "bg-red-600";

        given(lineDao.update(id, name, color)).willReturn(1);

        // when
        lineService.updateLine(id, createLineRequest());

        // then
        verify(lineDao).update(id, name, color);
    }

    @Test
    @DisplayName("존재하지않는 id로 노선을 업데이트하면, 예외가 발생한다.")
    void updateNotFoundException() {
        final long id = 0L;

        assertThatThrownBy(() -> lineService.updateLine(id, createLineRequest()))
                .isInstanceOf(LineNotFoundException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void deleteLine() {
        // given
        long id = 1L;

        // mocking
        given(lineDao.delete(id)).willReturn(1);

        // when
        lineService.deleteLine(id);

        // then
        verify(lineDao).delete(id);
    }

    @Test
    @DisplayName("존재하지않는 id로 노선을 삭제하면, 예외가 발생한다.")
    void deleteNotFoundException() {
        final long id = 0L;

        assertThatThrownBy(() -> lineService.deleteLine(id))
                .isInstanceOf(LineNotFoundException.class)
                .hasMessage("존재하지 않는 지하철 노선입니다.");
    }

    @Test
    @DisplayName("구간을 생성한다.")
    void createSection() {
        // given
        final Station station1 = new Station(1L, "신대방역");
        final Station station2 = new Station(2L, "선릉역");
        final Station station3 = new Station(3L, "강남역");

        // mocking
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(new SectionEntity(1L, 1L, 1L, 2L, 10)));
        given(stationDao.findById(1L)).willReturn(station1);
        given(stationDao.findById(2L)).willReturn(station2);
        given(stationDao.findById(1L)).willReturn(station1);
        given(stationDao.findById(3L)).willReturn(station3);

        // when
        lineService.createSection(1L, new SectionRequest(1L, 3L, 7));

        // then
        verify(sectionDao).findAllByLineId(1L);
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() {
        // given
        final Station station1 = new Station(1L, "신대방역");
        final Station station2 = new Station(2L, "선릉역");
        final Station station3 = new Station(3L, "강남역");
        final List<SectionEntity> sectionEntities = List.of(new SectionEntity(1L, 1L, 1L, 2L, 10),
                new SectionEntity(2L, 1L, 2L, 3L, 7));

        // mocking
        given(stationDao.findById(1L)).willReturn(station1);
        given(sectionDao.findAllByLineId(1L)).willReturn(sectionEntities);
        given(stationDao.findById(2L)).willReturn(station2);
        given(stationDao.findById(3L)).willReturn(station3);

        // when
        lineService.deleteSection(1L, 1L);

        // then
        verify(sectionDao).delete(1L);
    }

    private LineRequest createLineRequest() {
        return new LineRequest("2호선", "bg-red-600", 1L, 2L, 10);
    }
}
