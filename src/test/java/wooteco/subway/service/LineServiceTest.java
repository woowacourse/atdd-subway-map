package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.service.dto.line.LineRequestDto;
import wooteco.subway.service.dto.line.LineResponseDto;

class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        this.lineService = new LineService(new FakeLineDao());
    }

    @DisplayName("지하철 노선을 저장한다.")
    @Test
    void create() {
        LineResponseDto line = lineService.create("신분당선", "bg-red-600");
        assertThat(line.getId()).isGreaterThan(0);
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("이미 존재하는 이름으로 지하철 노선을 생성할 수 없다.")
    @Test
    void duplicateNameException() {
        lineService.create("신분당선", "bg-red-600");

        assertThatThrownBy(() -> lineService.create("신분당선", "bg-blue-600"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 이름의 지하철 노선이 이미 존재합니다");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAll() {
        lineService.create("신분당선", "bg-red-600");
        lineService.create("2호선", "bg-red-601");
        lineService.create("분당선", "bg-red-602");
        List<LineResponseDto> lines = lineService.findAll();
        assertThat(lines).hasSize(3);
    }

    @DisplayName("id로 지하철 노선을 조회한다.")
    @Test
    void findOne() {
        LineResponseDto line = lineService.create("신분당선", "bg-red-600");
        LineResponseDto foundLine = lineService.findOne(line.getId());
        assertThat(foundLine.getId()).isEqualTo(line.getId());
        assertThat(foundLine.getName()).isEqualTo(line.getName());
        assertThat(foundLine.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        LineResponseDto line = lineService.create("신분당선", "bg-red-600");
        lineService.update(line.getId(),
                new LineRequestDto("분당선", "bg-blue-600", 1L, 2L, 10));

        LineResponseDto updatedLine = lineService.findOne(line.getId());
        assertThat(updatedLine.getId()).isEqualTo(line.getId());
        assertThat(updatedLine.getName()).isEqualTo("분당선");
        assertThat(updatedLine.getColor()).isEqualTo("bg-blue-600");
    }

    @DisplayName("id로 지하철 노선을 삭제한다.")
    @Test
    void remove() {
        LineResponseDto line = lineService.create("신분당선", "bg-red-600");
        lineService.remove(line.getId());
        assertThat(lineService.findAll()).isEmpty();
    }
}
