package wooteco.subway.service;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.ClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new FakeLineDao());
    }

    @AfterEach
    void finish() {
        List<LineResponse> lines = lineService.findLines();
        for (LineResponse line : lines) {
            lineService.deleteLine(line.getId());
        }
    }

    @DisplayName("노선 저장")
    @Test
    void save() {
        LineRequest line = new LineRequest("4호선", "green");
        LineResponse newLine = lineService.createLine(line);

        assertThat(line.getName()).isEqualTo(newLine.getName());
    }

    @DisplayName("중복된 노선 저장시 예외")
    @Test
    void duplicateLine() {
        LineRequest line = new LineRequest("3호선", "green");
        LineRequest duplicateLine = new LineRequest("3호선", "red");
        lineService.createLine(line);

        assertThatThrownBy(() -> lineService.createLine(duplicateLine))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("이미 등록된 지하철노선입니다.");
    }


    @DisplayName("노선 정보 전체 조회")
    @Test
    void findAll() {
        lineService.createLine(new LineRequest("5호선", "green"));
        lineService.createLine(new LineRequest("7호선", "red"));

        List<LineResponse> lines = lineService.findLines();
        lines.stream()
                .filter(line -> line.getName().equals("5호선"))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("노선 정보가 없습니다."));
    }

    @Test
    @DisplayName("노선 정보 삭제")
    void delete() {
        LineRequest line = new LineRequest("4호선", "red");
        LineResponse newLine = lineService.createLine(line);

        assertThat(lineService.deleteLine(newLine.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("노선 정보 업데이트")
    void update() {
        LineRequest line = new LineRequest("4호선", "red");
        LineResponse newLine = lineService.createLine(line);

        assertThat(lineService.updateLine(newLine.getId(), line)).isEqualTo(1);
    }

    @Test
    @DisplayName("노선 정보 조회")
    void find() {
        LineRequest line = new LineRequest("4호선", "red");
        LineResponse newLine = lineService.createLine(line);

        assertThat(lineService.findLine(newLine.getId()).getName()).isEqualTo(line.getName());
    }
}
