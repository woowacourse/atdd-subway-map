package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DataNotExistException;

public class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new FakeLineDao());
    }

    @Test
    @DisplayName("Line 을 저장한다.")
    void create() {
        //given
        Line line = new Line("7호선", "khaki");

        //when
        Line createdLine = lineService.createLine(line);

        //then
        assertThat(equals(createdLine, line)).isTrue();
    }

    private boolean equals(Line lineA, Line lineB) {
        return lineA.getColor().equals(lineB.getColor()) && lineA.getName().equals(lineB.getName());
    }

    @Test
    @DisplayName("중복된 Line 을 저장할 수 없다.")
    void createDuplicateName() {
        //given
        Line line = new Line("7호선", "khaki");
        lineService.createLine(line);

        //then
        assertThatThrownBy(() -> lineService.createLine(line))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 등록된 노선입니다.");
    }

    @Test
    @DisplayName("전체 Line 목록을 조회한다.")
    void findAll() {
        //given
        Line line1 = new Line("2호선", "green");
        Line line2 = new Line("7호선", "khaki");
        lineService.createLine(line1);
        lineService.createLine(line2);

        //when
        List<Line> actual = lineService.findAll();

        //then
        assertAll(
            () -> assertThat(equals(actual.get(0), line1)).isTrue(),
            () -> assertThat(equals(actual.get(1), line2)).isTrue()
        );
    }

    @Test
    @DisplayName("id로 Line 을 조회한다.")
    void findById() {
        //given
        Line line = new Line("7호선", "khaki");
        Line createdLine = lineService.createLine(line);

        //when
        Line actual = lineService.findById(createdLine.getId());

        //then
        assertThat(equals(actual, line)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 id 를 조회할 경우 예외를 던진다.")
    void findByIdNotExists() {
        //given
        Line line = new Line("4호선", "sky-blue");
        Long id = lineService.createLine(line).getId();

        //then
        assertThatThrownBy(() -> lineService.findById(id + 1))
            .isInstanceOf(DataNotExistException.class)
            .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("Line 을 수정한다.")
    void update() {
        //given
        Line line = new Line("4호선", "sky-blue");
        Line createdLine = lineService.createLine(line);

        //when
        Line expected = new Line(createdLine.getId(), "4호선", "khaki");
        lineService.update(expected);
        Line actual = lineService.findById(createdLine.getId());

        //then
        assertThat(equals(actual, expected)).isTrue();
    }

    @Test
    @DisplayName("중복된 이름으로 수정하면 예외를 던진다.")
    void updateWithDuplicatedName() {
        //given
        Line line2 = new Line("2호선", "sky-blue");
        lineService.createLine(line2);
        Line line4 = new Line("4호선", "sky-blue");
        Line createdLine = lineService.createLine(line4);
        Line duplicatedLine = new Line(createdLine.getId(), "2호선", "sky-blue");

        //then
        assertThatThrownBy(() -> lineService.update(duplicatedLine))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 등록된 노선입니다.");
    }

    @Test
    @DisplayName("Line 을 삭제한다.")
    void delete() {
        //given
        Line line = new Line("4호선", "sky-blue");
        Long id = lineService.createLine(line).getId();

        //when
        lineService.deleteById(id);

        //then
        assertThatThrownBy(() -> lineService.findById(id))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 노선입니다.");
    }
}
