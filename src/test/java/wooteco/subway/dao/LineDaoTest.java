package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Line;

public class LineDaoTest {

    private final LineDao lineDao = LineDao.getInstance();

    @AfterEach
    void tearDown() {
        lineDao.deleteAll();
    }

    @Test
    @DisplayName("Line 을 저장한다.")
    void save() {
        //given
        Line line = new Line("가산디지털", "khaki");

        //when
        Line actual = lineDao.save(line);

        //then
        assertThat(equals(actual, line)).isTrue();
    }

    private boolean equals(Line lineA, Line lineB) {
        return lineA.getColor().equals(lineB.getColor()) && lineA.getName().equals(lineB.getName());
    }

    @Test
    @DisplayName("전체 Line 목록을 조회한다.")
    void findAll() {
        //given
        Line line1 = new Line("가산디지털", "blue");
        Line line2 = new Line("중곡", "khaki");
        lineDao.save(line1);
        lineDao.save(line2);

        //when
        List<Line> actual = lineDao.findAll();

        //then
        assertAll(
            () -> assertThat(equals(actual.get(0), line1)).isTrue(),
            () -> assertThat(equals(actual.get(1), line2)).isTrue()
        );
    }

    @Test
    @DisplayName("단일 Line 을 id 로 조회한다.")
    void findById() {
        //given
        Line line = new Line("가산디지털", "khaki");
        Line savedLine = lineDao.save(line);

        //when
        Line actual = lineDao.findById(savedLine.getId()).get();

        //then
        assertThat(equals(actual, line)).isTrue();
    }

    @Test
    @DisplayName("이름으로 station 을 조회한다.")
    void findByName() {
        //given
        String name = "중곡";
        String color = "khaki";
        Line savedLine = lineDao.save(new Line(name, color));

        //when
        Line actual = lineDao.findByName(name).get();

        //then
        assertThat(equals(actual, savedLine)).isTrue();
    }

    @Test
    @DisplayName("Line 의 이름과 색깔을 변경한다.")
    void update() {
        //given
        Line line = new Line("가산디지털", "blue");
        Line savedLine = lineDao.save(line);

        //when
        Line updatedLine = new Line("중곡", "khaki");
        lineDao.update(savedLine.getId(), updatedLine.getName(), updatedLine.getColor());

        //then
        Line actual = lineDao.findById(savedLine.getId()).get();
        assertThat(equals(actual, updatedLine)).isTrue();
    }

    @Test
    @DisplayName("Line 을 삭제한다.")
    void deleteById() {
        //given
        Line line = new Line("가산디지털", "blue");
        Line savedLine = lineDao.save(line);

        //when
        lineDao.deleteById(savedLine.getId());

        //then
        assertThat(lineDao.findById(savedLine.getId())).isEmpty();
    }
}
