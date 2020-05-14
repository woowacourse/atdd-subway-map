package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.resopnse.LineResponse;
import wooteco.subway.admin.exception.DuplicateNameException;
import wooteco.subway.admin.exception.NotFoundException;
import wooteco.subway.admin.repository.LineRepository;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;

    private Line line;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        line = Line.of(1L, "2호선", "bg-green-700", LocalTime.of(05, 30),
            LocalTime.of(22, 30), 5);
        lineService = new LineService(lineRepository);
    }

    @Test
    void saveLine() {
        when(lineRepository.save(any(Line.class))).thenReturn(line);
        Long id = lineService.save(line);
        assertThat(id).isEqualTo(line.getId());
    }

    @Test
    void updateLine() {
        when(lineRepository.findById(any(Long.class))).thenReturn(Optional.of(line));
        Line lineToUpdate = Line.withoutId("2호선", "bg-orange-700", LocalTime.of(06, 00),
            LocalTime.of(23, 30), 5);
        lineService.updateLine(1L, lineToUpdate);
    }

    @Test
    void deleteLine() {
        when(lineRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        lineService.deleteLineById(1L);
        assertThatThrownBy(() -> lineService.findLineWithoutStations(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getLine() {
        when(lineRepository.findById(any(Long.class))).thenReturn(Optional.of(line));
        LineResponse lineResponse = lineService.findLineWithoutStations(line.getId());
        assertThat(lineResponse).isEqualTo(LineResponse.from(line));
    }

    @Test
    void getLines() {
        Line thirdLine = Line.of(2L, "3호선", "bg-orange-700", LocalTime.of(06, 00),
            LocalTime.of(23, 30), 15);
        Line firstLine = Line.of(3L, "1호선", "bg-blue-500", LocalTime.of(05, 40),
            LocalTime.of(23, 40), 10);
        List<Line> lines = Arrays.asList(line, thirdLine, firstLine);
        when(lineRepository.findAll()).thenReturn(lines);
        List<LineResponse> lineResponses = lineService.findAllWithoutStations();
        assertThat(lineResponses)
            .contains(LineResponse.from(line))
            .contains(LineResponse.from(thirdLine))
            .contains(LineResponse.from(firstLine));
    }

    @Test
    void createException() {
        when(lineRepository.existsLineBy(any(String.class))).thenReturn(true);
        assertThatThrownBy(() -> lineService.save(line))
            .isInstanceOf(DuplicateNameException.class);
    }
}
