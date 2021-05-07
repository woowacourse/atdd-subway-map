package wooteco.subway.line.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepositoryImpl;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepositoryImpl lineRepository;

    @Test
    void allLines() {
        given(lineRepository.allLines()).willReturn(Arrays.asList(
                new Line(1L, "a", "a"),
                new Line(2L, "a", "a"),
                new Line(3L, "a", "a")
        ));


        List<Line> lines = lineService.allLines();

        List<Line> expected = Arrays.asList(
                new Line(1L, "a", "a"),
                new Line(2L, "a", "a"),
                new Line(3L, "a", "a")
        );

        assertThat(lines).isEqualTo(expected);
    }

    @Test
    void findById() {
        given(lineRepository.findById(1L)).willReturn(new Line(1L, "a", "b"));

        Line line = lineService.findById(1L);

        Line expected = new Line(1L, "a", "b");

        assertThat(line).isEqualTo(expected);
    }

    @Test
    void update() {
        //How can i do?
    }

    @Test
    void deleteById() {
        //How can i do?
    }

}