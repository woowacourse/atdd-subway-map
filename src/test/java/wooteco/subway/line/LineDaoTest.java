package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DuplicateException;

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

    @Test
    @DisplayName("중복된 이름을 갖는 노선은 저장이 안된다.")
    void duplicateSaveValidate() {
        Line line = new Line("2호선", "bg-green-600");
        lineDao.save(line);

        assertThatThrownBy(() -> {
            lineDao.save(line);
        }).isInstanceOf(DuplicateException.class);
    }

}