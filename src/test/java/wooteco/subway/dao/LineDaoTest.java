package wooteco.subway.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineDaoTest {

    @AfterEach
    void tearDown() {
        List<Long> lineIds = LineDao.findAll().stream()
                .map(Line::getId)
                .collect(Collectors.toList());

        for (Long lineId : lineIds) {
            LineDao.deleteById(lineId);
        }
    }

    @Test
    void save() {
        Line line = new Line("신분당선", "bg-red-600");
        Line savedLine = LineDao.save(line);

        assertThat(savedLine).isNotNull();
    }

    @Test
    void findAll() {
        Line line1 = new Line("신분당선", "bg-red-600");
        Line line2 = new Line("분당선", "bg-green-600");

        LineDao.save(line1);
        LineDao.save(line2);

        assertThat(LineDao.findAll()).containsAll(List.of(line1, line2));
    }

    @Test
    void findById() {
        Line line1 = new Line("신분당선", "bg-red-600");
        Line line2 = new Line("분당선", "bg-green-600");

        LineDao.save(line1);
        Line savedLine = LineDao.save(line2);

        assertThat(LineDao.findById(savedLine.getId())).isEqualTo(line2);
    }

    @Test
    void update() {
        Line line1 = new Line("신분당선", "bg-red-600");
        Line savedLine = LineDao.save(line1);
        Long savedId = savedLine.getId();

        String newLineName = "새로운 노선";
        String newLineColor = "bg-red-500";
        Line newLine = new Line(newLineName, newLineColor);
        LineDao.update(savedId, newLine);

        assertAll(
                () -> assertThat(LineDao.findById(savedId).getName()).isEqualTo(newLineName),
                () -> assertThat(LineDao.findById(savedId).getColor()).isEqualTo(newLineColor)
        );
    }

    @Test
    void deleteById() {
        Line line1 = new Line("신분당선", "bg-red-600");
        Line savedLine = LineDao.save(line1);

        LineDao.deleteById(savedLine.getId());

        assertThat(LineDao.findAll()).hasSize(0);
    }
}
