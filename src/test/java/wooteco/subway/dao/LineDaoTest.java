package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.domain.Line;

class LineDaoTest {

    @DisplayName("이름값을 받아 해당 이름값을 가진 노선이 있는지 확인한다.")
    @ParameterizedTest
    @CsvSource({"2호선, red, true", "신분당선, blue, true", "신분당선, red, true", "2호선, blue, false"})
    void exists(String name, String color, boolean expected) {
        Line line = new Line("신분당선", "red");
        LineDao.save(line);

        boolean actual = LineDao.exists(new Line(name, color));

        assertThat(actual).isEqualTo(expected);

        LineDao.deleteById(line.getId());
    }

    @DisplayName("id에 해당하는 노선의 정보를 바꾼다.")
    @Test
    void update() {
        Line line = new Line("신분당선", "red");
        LineDao.save(line);
        Line updatingLine = new Line("7호선", "darkGreen");
        LineDao.update(line.getId(), updatingLine);

        Optional<Line> updated = LineDao.findById(line.getId());

        Line updatedLine = null;
        if (updated.isPresent()) {
            updatedLine = updated.get();
        }

        assertThat(updatedLine).isNotNull();
        assertThat(updatedLine.getId()).isEqualTo(updatingLine.getId());
        assertThat(updatedLine.getName()).isEqualTo(updatingLine.getName());
        assertThat(updatedLine.getColor()).isEqualTo(updatingLine.getColor());

        LineDao.deleteById(line.getId());
    }
}