package wooteco.subway.line.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepositoryImpl;
import wooteco.util.LineFactory;

import java.util.Arrays;
import java.util.Collections;
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
                LineFactory.create(1L, "a", "a", Collections.emptyList()),
                LineFactory.create(2L, "a", "a", Collections.emptyList()),
                LineFactory.create(3L, "a", "a", Collections.emptyList())
        ));


        List<Line> lines = lineService.allLines();

        List<Line> expected = Arrays.asList(
                LineFactory.create(1L, "a", "a", Collections.emptyList()),
                LineFactory.create(2L, "a", "a", Collections.emptyList()),
                LineFactory.create(3L, "a", "a", Collections.emptyList())
        );

        assertThat(lines).isEqualTo(expected);
    }

    @Test
    void findById() {
        given(lineRepository.findById(1L))
                .willReturn(LineFactory.create(1L, "a", "b", Collections.emptyList()));

        Line line = lineService.findById(1L);
        Line expected = LineFactory.create(1L, "a", "b", Collections.emptyList());

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