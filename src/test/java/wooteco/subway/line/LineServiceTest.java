package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.line.dao.LineDaoH2;
import wooteco.subway.line.dto.LineDto;
import wooteco.subway.line.dto.LineIdDto;
import wooteco.subway.line.dto.NonIdLineDto;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {

    @Mock
    private LineDaoH2 mockDao;

    @InjectMocks
    private LineService lineService;
    
    @Test
    @DisplayName("노선 만들기")
    void createLine() {
        // given
        String name = "부산1호선";
        String color = "주홍색";
        NonIdLineDto nonIdlineDto = new NonIdLineDto(name, color);
        when(mockDao.save(any())).thenReturn(new Line(1L, name, color));
        // when
        LineDto createdLineDto = lineService.createLine(nonIdlineDto);

        // then
        assertThat(createdLineDto.getName()).isEqualTo(name);
        assertThat(createdLineDto.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("전체 노선 반환")
    void showAllLines() {
        //given
        List<Line> lines = Arrays.asList(
            new Line(1L, "대구선", "노란색"),
            new Line(2L, "광주선", "분홍색"),
            new Line(3L, "울산선", "검은색")
        );

        when(mockDao.showAll()).thenReturn(lines);
        LineService lineServiceWithMock = new LineService(mockDao);

        List<LineDto> expectedDtos = Arrays.asList(
            new LineDto(1L, "대구선", "노란색"),
            new LineDto(2L, "광주선", "분홍색"),
            new LineDto(3L, "울산선", "검은색")
        );

        //when
        List<LineDto> requestedDtos = lineServiceWithMock.findAll();

        //then
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
        //given
        Line line = new Line(1L, "창원선", "청록색");

        when(mockDao.show(any())).thenReturn(line);
        LineService lineServiceWithMock = new LineService(mockDao);

        //when
        LineDto requestedDto = lineServiceWithMock.findOne(new LineIdDto((long) 1));

        //then
        assertThat(requestedDto.getId()).isEqualTo(line.getId());
        assertThat(requestedDto.getName()).isEqualTo(line.getName());
        assertThat(requestedDto.getColor()).isEqualTo(line.getColor());
    }

}
