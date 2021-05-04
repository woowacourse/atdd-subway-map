package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineDaoTest {

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao();
    }

    @Test
    @DisplayName("노선 한 개가 저장된다.")
    void save() {
        Line line = lineDao.save(new Line("2호선", "bg-green-600"));
        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

}