package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

public class LineDaoTest {

    @DisplayName("노선을 저장한다.")
    @Test
    void save() {
        Line line = new Line("2호선", "green");
        Line savedLine = LineDao.save(line);

        assertThat(line.getName()).isEqualTo(savedLine.getName());
    }

    @DisplayName("같은 이름의 노선을 저장하는 경우 예외가 발생한다.")
    @Test
    void saveExistingName() {
        Line line = new Line("2호선", "green");
        LineDao.save(line);

        assertThatThrownBy(() -> {
            LineDao.save(line);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 이름의 노선은 등록할 수 없습니다.");
    }

    @AfterEach
    void reset() {
        List<Line> lines = LineDao.findAll();
        lines.clear();
    }
}
