package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;

    @Mock
    private SectionService sectionService;

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        String name = "testLine";
        String color = "black";
        long upStationId = 1;
        long downStationId = 2;
        int distance = 10;
        long lineId = 1L;
        Line line = new Line(name, color);
        Line retrievedLine = new Line(lineId, name, color);

        given(lineDao.save(line)).willReturn(lineId);
        given(sectionService.createSection(upStationId, downStationId, distance, lineId)).willReturn(1L);
        given(lineDao.findById(1L)).willReturn(Optional.of(retrievedLine));

        Line savedLine = lineService.createLine(name, color, upStationId, downStationId, distance);

        assertThat(savedLine).isEqualTo(retrievedLine);
        verify(lineDao, times(1)).save(line);
        verify(lineDao, times(1)).findById(1L);
        verify(sectionService, times(1)).createSection(upStationId, downStationId, distance, lineId);
    }

    @DisplayName("노선 조회에 성공한다.")
    @Test
    void findById() {
        long id = 1;
        Line line = new Line("testLine", "black");
        given(lineDao.findById(id)).willReturn(Optional.of(line));

        Line retrievedLine = lineService.findById(id);

        assertThat(line).isEqualTo(retrievedLine);
        verify(lineDao, times(1)).findById(id);
    }

    @DisplayName("노선의 이름을 수정한다.")
    @Test
    void editLine() {
        String name = "changedName";
        String color = "black";

        lineService.editLine(1L, name, color);

        verify(lineDao, times(1)).update(1L, name, color);
    }
}
