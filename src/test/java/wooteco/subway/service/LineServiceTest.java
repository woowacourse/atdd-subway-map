package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.LineDao;
import wooteco.subway.repository.SectionDao;

@ExtendWith(MockitoExtension.class)
@DisplayName("노선 서비스 레이어 테스트")
class LineServiceTest {

    @Mock
    private LineDao lineDao;
    @Mock
    private SectionDao sectionDao;
    @Mock
    private SectionService sectionService;
    @InjectMocks
    private LineService lineService;

    @Test
    @DisplayName("새로운 노선을 생성한다.")
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 5);
        LineResponse lineResponse = new LineResponse(1L, "2호선", "green", new ArrayList<>());
        given(lineDao.save(any())).willReturn(1L);
        given(sectionService.createSection(1L, 1L, 2L, 5))
            .willReturn(new SectionResponse(
                1L,
                new StationResponse(1L, "강남역"),
                new StationResponse(1L, "역삼역"),
                5
            ));

        // when
        LineResponse createdLine = lineService.createLine(lineRequest);

        // then
        assertThat(createdLine.getId()).isEqualTo(lineResponse.getId());
        assertThat(createdLine.getName()).isEqualTo(lineResponse.getName());
        assertThat(createdLine.getColor()).isEqualTo(lineResponse.getColor());
    }

    @Test
    @DisplayName("생성된 노선들을 불러온다.")
    void findAll() {
        // given
        Line line1 = new Line("2호선", "green");
        Line line2 = new Line("3호선", "red");
        Sections sections = new Sections(new ArrayList<>());
        line1.setSections(sections);
        line2.setSections(sections);
        LineResponse lineResponse1 = LineResponse.of(line1);
        LineResponse lineResponse2 = LineResponse.of(line2);

        given(lineDao.findAll()).willReturn(Arrays.asList(line1, line2));
        given(sectionDao.findByLineId(any())).willReturn(sections);

        // when
        List<LineResponse> lineResponses = lineService.findAll();

        // then
        assertThat(lineResponses.get(0).getName()).isEqualTo(lineResponse1.getName());
        assertThat(lineResponses.get(0).getColor()).isEqualTo(lineResponse1.getColor());
        assertThat(lineResponses.get(1).getName()).isEqualTo(lineResponse2.getName());
        assertThat(lineResponses.get(1).getColor()).isEqualTo(lineResponse2.getColor());
    }

    @Test
    @DisplayName("아이디로 특정 노선을 조회한다.")
    void findById() {
        // given
        Line line = new Line(1L, "2호선", "green");
        Sections sections = new Sections(new ArrayList<>());
        line.setSections(sections);
        LineResponse lineResponse = LineResponse.of(line);

        given(lineDao.findById(any())).willReturn(Optional.of(line));
        given(sectionDao.findByLineId(line.getId())).willReturn(sections);

        // when
        LineResponse foundLineResponse = lineService.findById(1L);

        // then
        assertThat(foundLineResponse.getId()).isEqualTo(lineResponse.getId());
        assertThat(foundLineResponse.getName()).isEqualTo(lineResponse.getName());
        assertThat(foundLineResponse.getColor()).isEqualTo(lineResponse.getColor());

    }

    @Test
    @DisplayName("노선 정보를 수정한다.")
    void editLine() {
        // given
        LineRequest lineRequest = new LineRequest("3호선", "red", 1L, 2L, 5);

        // when
        lineService.editLine(1L, lineRequest);

        // then
        verify(lineDao, times(1))
            .findAll();
        verify(lineDao, times(1))
            .updateLine(any());
    }

    @Test
    @DisplayName("생성된 노선을 삭제한다.")
    void deleteLine() {
        // when
        lineService.deleteLine(1L);

        // then
        verify(lineDao, times(1))
            .deleteById(1L);
    }
}