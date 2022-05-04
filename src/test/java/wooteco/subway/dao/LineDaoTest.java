package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        Line line = LineDao.save(new Line("2호선", "bg-red-600"));
        assertThat(line.getName()).isEqualTo("2호선");
    }

    @Test
    @DisplayName("노선을 저장할 때 id가 1씩 증가한다.")
    void increaseId() {
        Line line1 = LineDao.save(new Line("2호선", "bg-red-600"));
        Line line2 = LineDao.save(new Line("3호선", "bg-blue-600"));
        assertThat(line2.getId() - line1.getId()).isEqualTo(1L);
    }
}
