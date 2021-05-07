package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.assembler.Assembler;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.dao.line.LineDaoCache;
import wooteco.subway.service.dto.LineServiceDto;
import wooteco.subway.service.LineService;

public class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        Assembler assembler = new Assembler();
        lineService = assembler.getLineService();
    }

    @Test
    @DisplayName("노선 만들기")
    void createLine() {
        // given
        String name = "부산1호선";
        String color = "주홍색";
        LineServiceDto lineServiceDto = new LineServiceDto(name, color);

        // when
        LineServiceDto createdLineServiceDto = lineService.createLine(lineServiceDto);

        // then
        assertThat(createdLineServiceDto.getName()).isEqualTo(name);
        assertThat(createdLineServiceDto.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("전체 노선 반환")
    void showAllLines() {
        // given
        List<Line> lines = Arrays.asList(
            new Line((long) 1, "대구선", "노란색"),
            new Line((long) 2, "광주선", "분홍색"),
            new Line((long) 3, "울산선", "검은색")
        );

        LineDaoCache mockLineDao = mock(LineDaoCache.class);
        when(mockLineDao.showAll()).thenReturn(lines);
        LineService lineServiceWithMock = new LineService(mockLineDao);

        List<LineServiceDto> expectedDtos = Arrays.asList(
            new LineServiceDto((long) 1, "대구선", "노란색"),
            new LineServiceDto((long) 2, "광주선", "분홍색"),
            new LineServiceDto((long) 3, "울산선", "검은색")
        );

        // when
        List<LineServiceDto> requestedDtos = lineServiceWithMock.findAll();

        // then
        assertThat(requestedDtos.get(0).getId()).isEqualTo(expectedDtos.get(0).getId());
        assertThat(requestedDtos.get(0).getName()).isEqualTo(expectedDtos.get(0).getName());
        assertThat(requestedDtos.get(0).getColor()).isEqualTo(expectedDtos.get(0).getColor());
        assertThat(requestedDtos.get(1).getId()).isEqualTo(expectedDtos.get(1).getId());
        assertThat(requestedDtos.get(1).getName()).isEqualTo(expectedDtos.get(1).getName());
        assertThat(requestedDtos.get(1).getColor()).isEqualTo(expectedDtos.get(1).getColor());
        assertThat(requestedDtos.get(2).getId()).isEqualTo(expectedDtos.get(2).getId());
        assertThat(requestedDtos.get(2).getName()).isEqualTo(expectedDtos.get(2).getName());
        assertThat(requestedDtos.get(2).getColor()).isEqualTo(expectedDtos.get(2).getColor());
    }

    @Test
    @DisplayName("특정 노선 반환")
    void findOne() {
        // given
        Line line = new Line((long) 1, "창원선", "청록색");

        LineDaoCache mockLineDao = mock(LineDaoCache.class);
        when(mockLineDao.show(any())).thenReturn(line);
        LineService lineServiceWithMock = new LineService(mockLineDao);

        // when
        LineServiceDto requestedDto = lineServiceWithMock.findOne(new LineServiceDto((long) 1));

        // then
        assertThat(requestedDto.getId()).isEqualTo(line.getId());
        assertThat(requestedDto.getName()).isEqualTo(line.getName());
        assertThat(requestedDto.getColor()).isEqualTo(line.getColor());
    }

    @Test
    @DisplayName("특정 노선 업데이트")
    void update() {
        // given
        LineServiceDto initiatedRequestDto = new LineServiceDto("문화선", "무지개색");
        LineServiceDto initiatedResponseDto = lineService.createLine(initiatedRequestDto);
        long index = initiatedResponseDto.getId();
        LineServiceDto requestDto = new LineServiceDto(index, "7호선", "녹담색");

        // when
        lineService.update(requestDto);
        LineServiceDto responseDto = lineService.findOne(requestDto);

        // then
        assertThat(responseDto.getName()).isEqualTo(requestDto.getName());
        assertThat(responseDto.getColor()).isEqualTo(requestDto.getColor());
    }

    @Test
    @DisplayName("특정 노선 삭제")
    void delete() {
        // given
        LineServiceDto initiatedRequestDto = new LineServiceDto("문화선", "무지개색");
        LineServiceDto initiatedResponseDto = lineService.createLine(initiatedRequestDto);
        long index = initiatedResponseDto.getId();
        LineServiceDto requestDto = new LineServiceDto(index, "7호선", "녹담색");

        // when
        lineService.delete(requestDto);

        // then
        assertThatThrownBy(() -> lineService.findOne(requestDto))
            .isInstanceOf(NotFoundLineException.class);
    }
}
