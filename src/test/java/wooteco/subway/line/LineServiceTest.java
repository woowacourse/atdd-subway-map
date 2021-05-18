package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.CreateLineDto;
import wooteco.subway.line.dto.LineServiceDto;
import wooteco.subway.line.service.LineService;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {

    private final long UPSTATION_ID = 1;
    private final long DOWNSTATION_ID = 2;
    private final int DISTANCE = 10;

    @Mock
    private LineDao mockLineDao;
    @InjectMocks
    private LineService lineService;

    @Test
    @DisplayName("노선 만들기")
    void createLine() {
        // given
        long id = 1;
        String name = "1호선";
        String color = "파란색";

        when(mockLineDao.save(any(Line.class))).thenReturn(new Line(id, name, color));

        // when
        CreateLineDto createLineDto = new CreateLineDto(name, color, UPSTATION_ID, DOWNSTATION_ID, DISTANCE);
        Line line = new Line(name, color);
        Line createdLine = lineService.save(line);

        // then
        assertThat(createdLine.getName()).isEqualTo(name);
        assertThat(createdLine.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("전체 노선 반환")
    void showAllLines() {
        // given
        List<Line> lines = Arrays.asList(
            new Line((long) 1, "1호선", "파란색"),
            new Line((long) 2, "2호선", "초록색"),
            new Line((long) 3, "3호선", "주황색")
        );

        when(mockLineDao.showAll()).thenReturn(lines);

        // when
        List<LineServiceDto> requestedDtos = lineService.findAll();

        // then
        assertThat(requestedDtos.get(0).getId()).isEqualTo(lines.get(0).getId());
        assertThat(requestedDtos.get(0).getName()).isEqualTo(lines.get(0).getName());
        assertThat(requestedDtos.get(0).getColor()).isEqualTo(lines.get(0).getColor());
        assertThat(requestedDtos.get(1).getId()).isEqualTo(lines.get(1).getId());
        assertThat(requestedDtos.get(1).getName()).isEqualTo(lines.get(1).getName());
        assertThat(requestedDtos.get(1).getColor()).isEqualTo(lines.get(1).getColor());
        assertThat(requestedDtos.get(2).getId()).isEqualTo(lines.get(2).getId());
        assertThat(requestedDtos.get(2).getName()).isEqualTo(lines.get(2).getName());
        assertThat(requestedDtos.get(2).getColor()).isEqualTo(lines.get(2).getColor());
    }

    @Test
    @DisplayName("ID를 통한 노선 정보 반환")
    void findOne() {
        // given
        long id = 1;
        String name = "1호선";
        String color = "파란색";

        Optional<Line> candidate = Optional.ofNullable(new Line(id, name, color));
        when(mockLineDao.show(id)).thenReturn(candidate);

        // when
        Line line = lineService.show(id);

        // then
        assertThat(line.getId()).isEqualTo(id);
        assertThat(line.getName()).isEqualTo(name);
        assertThat(line.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("특정 노선 업데이트")
    void update() {
        // given
        long id = 1;
        String updateName = "7호선";
        String updateColor = "녹담색";

        Optional<Line> candidate = Optional.ofNullable(new Line(id, updateName, updateColor));
        when(mockLineDao.update(eq(id), any(Line.class))).thenReturn(1);
        when(mockLineDao.show(id)).thenReturn(candidate);

        // when
        lineService.update(new LineServiceDto(id, updateName, updateColor));

        // then
        Line updatedLine = lineService.show(id);
        assertThat(updatedLine.getId()).isEqualTo(1);
        assertThat(updatedLine.getName()).isEqualTo(updateName);
        assertThat(updatedLine.getColor()).isEqualTo("녹담색");
    }

    @Test
    @DisplayName("특정 노선 삭제")
    void delete() {
        // given
        long id = 1;
        String name = "1호선";
        String color = "파란색";

        when(mockLineDao.delete(id)).thenReturn(1);
        when(mockLineDao.show(id)).thenThrow(NotFoundLineException.class);

        //when
        lineService.delete(new LineServiceDto(id));

        // then
        assertThatThrownBy(() -> lineService.show(id))
            .isInstanceOf(NotFoundLineException.class);
    }
}

