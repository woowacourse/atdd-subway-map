package wooteco.subway.line.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.exception.line.LineDuplicatedNameException;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.line.Line;
import wooteco.subway.line.dao.JdbcLineDao;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.line.dto.response.LineStationsResponse;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static wooteco.subway.line.LineFixture.*;
import static wooteco.subway.section.SectionFixture.*;
import static wooteco.subway.station.StationFixture.*;

@DisplayName("지하철 노선 비즈니스 로직 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    private final LineDao lineDao = Mockito.mock(JdbcLineDao.class);

    @Mock
    private StationService stationService;

    @Mock
    private SectionService sectionService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("노선 생성 및 초기 구간 생성")
    @Test
    void create() {
        // given
        given(lineDao.save(any(Line.class))).willReturn(이호선);
        given(stationService.findById(1L)).willReturn(왕십리역);
        given(stationService.findById(2L)).willReturn(잠실역);
        given(sectionService.add(any(Section.class))).willReturn(이호선_왕십리_잠실_거리10);
        LineCreateRequest request =
                new LineCreateRequest("2호선", "green", 1L, 2L, 10);

        // when
        LineCreateResponse response = lineService.create(request);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new LineCreateResponse(2L, "2호선", "green", new StationResponse(왕십리역),
                        new StationResponse(잠실역), 10));
    }

    @DisplayName("노선 생성시 초기 구간의 양 역이 같은 경우 예외처리")
    @Test
    void createWithSameInitStation() {
        // given
        given(lineDao.save(any(Line.class))).willReturn(이호선);
        given(stationService.findById(1L)).willReturn(왕십리역);
        LineCreateRequest request =
                new LineCreateRequest("2호선", "green", 1L, 1L, 10);

        // when & then
        assertThatThrownBy(() -> lineService.create(request))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("노선에 구간을 추가하기")
    @Test
    void addSection() {
        // given
        given(lineDao.findById(anyLong())).willReturn(이호선);
        given(sectionService.findByLine(이호선)).willReturn(
                new Sections(Arrays.asList(
                        이호선_왕십리_잠실_거리10,
                        이호선_잠실_강남_거리5,
                        이호선_강남_구의_거리7
                ))
        );
        given(stationService.findById(1L)).willReturn(왕십리역);
        given(stationService.findById(6L)).willReturn(한양대역);

        // when
        lineService.addSection(2L, new SectionCreateRequest(1L, 6L, 3));

        // then
        verify(sectionService).updateSectionsInLine(이호선);
    }

    @DisplayName("모든 노선 조회 ")
    @Test
    void findAll() {
        // given
        given(lineDao.findAll())
                .willReturn(Arrays.asList(
                        신분당선, 이호선, 사호선
                ));

        // when
        List<LineResponse> results = lineService.findAll();
        List<Line> lines = results.stream()
                .map(response -> new Line(response.getName(), response.getColor()))
                .collect(Collectors.toList());

        // then
        assertThat(lines).usingRecursiveFieldByFieldElementComparator()
                .containsAll(Arrays.asList(
                        new Line("신분당선", "yellow"),
                        new Line("2호선", "green"),
                        new Line("4호선", "sky")
                ));
        verify(lineDao).findAll();
    }

    @DisplayName("노선 하나 조회")
    @Test
    void findBy() {
        // given
        given(lineDao.findById(anyLong())).willReturn(이호선);
        given(sectionService.findByLine(이호선)).willReturn(
                new Sections(Arrays.asList(
                        이호선_왕십리_잠실_거리10,
                        이호선_잠실_강남_거리5,
                        이호선_강남_구의_거리7
                ))
        );
        given(stationService.findById(1L)).willReturn(왕십리역);
        given(stationService.findById(2L)).willReturn(잠실역);
        given(stationService.findById(3L)).willReturn(강남역);
        given(stationService.findById(4L)).willReturn(구의역);

        // when
        LineStationsResponse response = lineService.findBy(2L);

        // then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new LineStationsResponse(2L, "2호선", "green",
                        Arrays.asList(
                                new StationResponse(왕십리역), new StationResponse(잠실역),
                                new StationResponse(강남역), new StationResponse(구의역)
                        )));
    }

    @DisplayName("노선 정보 수정")
    @Test
    void update() {
        // given
        given(lineDao.findById(2L))
                .willReturn(이호선);
        given(lineDao.existByNameAndNotInOriginalName("분당선", "2호선"))
                .willReturn(false);

        // when
        lineService.update(2L, new LineUpdateRequest("분당선", "green"));

        // then
        verify(lineDao).update(any(Line.class));
    }

    @DisplayName("존재하지 않는 노선 수정")
    @Test
    void updateNotExist() {
        // given
        given(lineDao.findById(1L))
                .willThrow(LineNotFoundException.class);

        // when & then
        assertThatThrownBy(() -> lineService.update(1L, new LineUpdateRequest("2호선", "green")))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("이미 존재하는 노선이름으로 노선 정보 수정")
    @Test
    void updateDuplicatedName() {
        // given
        given(lineDao.findById(1L))
                .willReturn(new Line(1L, "분당선", "red"));
        given(lineDao.existByNameAndNotInOriginalName("2호선", "분당선"))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> lineService.update(1L, new LineUpdateRequest("2호선", "green")))
                .isInstanceOf(LineDuplicatedNameException.class);
    }

    @DisplayName("노선 삭제")
    @Test
    void delete() {
        // given
        Long id = 1L;

        // when
        lineService.delete(id);

        // then
        verify(lineDao).delete(any(Long.class));
    }

    @DisplayName("노선 구간 안의 Station 삭제")
    @Test
    void deleteStationInSection() {
        // given
        given(lineDao.findById(anyLong())).willReturn(이호선);
        given(sectionService.findByLine(이호선)).willReturn(
                new Sections(Arrays.asList(
                        이호선_왕십리_잠실_거리10,
                        이호선_잠실_강남_거리5,
                        이호선_강남_구의_거리7
                )));
        given(stationService.findById(2L)).willReturn(잠실역);

        // when
        lineService.deleteStationInSection(1L, 2L);

        // then
        verify(sectionService).updateSectionsInLine(any(Line.class));
    }

}