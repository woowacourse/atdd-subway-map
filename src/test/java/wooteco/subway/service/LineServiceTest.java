package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineServiceTest {

    private final LineService lineService = LineService.getInstance();

    @AfterEach
    void tearDown() {
        LineDao.getInstance().deleteAll();
    }

    @Test
    @DisplayName("Line 을 저장한다.")
    void save() {
        //given
        Line line = new Line("가산디지털단지", "blue");

        //when
        Line savedLine = lineService.save(line);

        //then
        assertThat(equals(savedLine, line)).isTrue();
    }

    private boolean equals(Line lineA, Line lineB) {
        return lineA.getColor().equals(lineB.getColor()) && lineA.getName().equals(lineB.getName());
    }

    @Test
    @DisplayName("중복된 Line 을 저장할 수 없다.")
    void saveDuplicateName() {
        //given
        Line line = new Line("중곡", "khaki");
        lineService.save(line);

        //then
        assertThatThrownBy(() -> lineService.save(line))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 등록된 노선입니다.");
    }

    @Test
    @DisplayName("전체 Line 목록을 조회한다.")
    void findAll() {
        //given
        Line line1 = new Line("중곡", "khaki");
        Line line2 = new Line("가산디지털", "khaki");
        lineService.save(line1);
        lineService.save(line2);

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
        Line line = new Line("중곡", "khaki");
        Line savedLine = lineService.save(line);

        //when
        Line actual = lineService.findById(savedLine.getId());

        //then
        assertThat(equals(actual, line)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 id 를 조회할 경우 예외를 던진다.")
    void findByIdNotExists() {
        //given
        Line line = new Line("이수", "sky-blue");
        Long id = lineService.save(line).getId();

        //then
        assertThatThrownBy(() -> lineService.findById(id + 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("Line 을 수정한다.")
    void update() {
        //given
        Line line = new Line("이수", "sky-blue");
        Line savedLine = lineService.save(line);

        //when
        Line expected = new Line(savedLine.getId(), "가산디지털", "blue");
        lineService.update(expected);
        Line actual = lineService.findById(savedLine.getId());

        //then
        assertThat(equals(actual, expected)).isTrue();
    }

    @Test
    @DisplayName("중복된 이름으로 수정하면 예외를 던진다.")
    void updateWithDuplicatedName() {
        //given
        Line line = new Line("이수", "sky-blue");
        Line savedLine = lineService.save(line);
        Line duplicatedLine = new Line(savedLine.getId(), "이수", "sky-blue");

        //then
        assertThatThrownBy(() -> lineService.update(duplicatedLine))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 등록된 노선입니다.");
    }

    @Test
    @DisplayName("Line 을 삭제한다.")
    void delete() {
        //given
        Line line = new Line("이수", "sky-blue");
        Long id = lineService.save(line).getId();

        //when
        lineService.deleteById(id);

        //then
        assertThatThrownBy(() -> lineService.findById(id))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 노선입니다.");
    }
}
