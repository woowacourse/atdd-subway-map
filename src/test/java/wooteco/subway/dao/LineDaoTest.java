package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.LineDuplicateException;

@DisplayName("Line Dao를 통해서")
class LineDaoTest {

    private static final Line LINE_FIXTURE = new Line(1L, "line1", "color");
    private static final Line LINE_FIXTURE2 = new Line(2L, "line2", "color");
    private static final Line LINE_FIXTURE3 = new Line(3L, "line3", "color");

    @BeforeEach
    void setup() {
        LineDao.deleteAll();
    }

    @Nested
    @DisplayName("새로운 노선을 저장할 때")
    class SaveTest {

        @Test
        @DisplayName("노선 이름이 중복되지 않으면 저장할 수 있다.")
        void save_Success_If_Not_Exists() {
            LineDao.deleteAll();
            assertThatCode(() -> LineDao.save(LINE_FIXTURE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("노선 이름이 중복되면 예외가 발생한다.")
        void save_Fail_If_Exists() {
            LineDao.deleteAll();
            LineDao.save(LINE_FIXTURE);
            assertThatThrownBy(() -> LineDao.save(LINE_FIXTURE))
                    .isInstanceOf(LineDuplicateException.class)
                    .hasMessage("이미 존재하는 노선입니다.");
        }
    }


    @Test
    @DisplayName("전체 지하철 노선을 조회할 수 있다")
    void findAll() {
        LineDao.save(LINE_FIXTURE);
        LineDao.save(LINE_FIXTURE2);
        LineDao.save(LINE_FIXTURE3);

        assertThat(LineDao.findAll()).isEqualTo(List.of(LINE_FIXTURE, LINE_FIXTURE2, LINE_FIXTURE3));
    }

    @Test
    @DisplayName("아이디로 지하철 노선을 조회할 수 있다")
    void findById() {
        final Line line = LineDao.save(LINE_FIXTURE);
        final Line found = LineDao.findById(line.getId());

        assertThat(line).isEqualTo(found);
    }

    @Test
    @DisplayName("아이디로 지하철노선을 삭제할 수 있다")
    void deleteById() {
        final Line line = LineDao.save(LINE_FIXTURE);
        final List<Line> lines = LineDao.findAll();
        LineDao.deleteById(line.getId());
        final List<Line> afterDelete = LineDao.findAll();

        assertThat(lines).isNotEmpty();
        assertThat(afterDelete).isEmpty();
    }
}
