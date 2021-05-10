package wooteco.subway.line;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    LineService lineService;

    @Test
    void createLine() {
        //given
        String lineName = "2호선";
        String lineColor = "Green";
        //when
        final LineResponse lineResponse = lineService.createLine(1, 2, lineName, lineColor);
        //then
        assertThat(lineResponse.getName()).isEqualTo(lineName);
        assertThat(lineResponse.getColor()).isEqualTo(lineColor);
    }

    @Test
    void createLineDuplicateName() {
        //given
        String lineName = "2호선";
        String lineColor = "Green";
        lineService.createLine(1, 2, lineName, lineColor);
        //when & then
        assertThatThrownBy(() -> lineService.createLine(1, 2, lineName, lineColor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선 이름이 중복됩니다.");
    }

    @Test
    void showLines() {
        //given
        String lineName1 = "2호선";
        String lineColor1 = "Green";
        lineService.createLine(1, 2, lineName1, lineColor1);
        String lineName2 = "3호선";
        String lineColor2 = "Orange";
        lineService.createLine(3, 4, lineName2, lineColor2);
        //when
        final List<LineResponse> lineResponses = lineService.showLines();
        //then
        final List<String> nameList = lineResponses.stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());
        assertThat(nameList).containsExactly(lineName1, lineName2);
        final List<String> colorList = lineResponses.stream()
                .map(LineResponse::getColor)
                .collect(Collectors.toList());
        assertThat(colorList).containsExactly(lineColor1, lineColor2);
    }

    @Test
    void showLine() {
        //given
        String lineName = "2호선";
        String lineColor = "Green";
        final LineResponse lineResponse = lineService.createLine(1, 2, lineName, lineColor);
        //when
        final LineResponse shownLine = lineService.showLine(lineResponse.getId());
        //then
        assertThat(shownLine.getName()).isEqualTo(lineName);
        assertThat(shownLine.getColor()).isEqualTo(lineColor);
    }

    @Test
    void updateLine() {
        //given
        String lineName = "2호선";
        String lineColor = "Green";
        final LineResponse lineResponse = lineService.createLine(1, 2, lineName, lineColor);
        //when
        String updateName = "3호선";
        String updateColor = "Orange";
        lineService.updateLine(lineResponse.getId(), updateName, updateColor);
        //then
        final LineResponse updatedLine = lineService.showLine(lineResponse.getId());
        assertThat(updatedLine.getName()).isEqualTo(updateName);
        assertThat(updatedLine.getColor()).isEqualTo(updateColor);
    }

    @Test
    void deleteLine() {
        //given
        String lineName = "2호선";
        String lineColor = "Green";
        final LineResponse lineResponse = lineService.createLine(1, 2, lineName, lineColor);
        //when
        lineService.deleteLine(lineResponse.getId());
        //then
        assertThatThrownBy(() -> lineService.deleteLine(lineResponse.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 id에 대응하는 노선이 없습니다.");
    }
}