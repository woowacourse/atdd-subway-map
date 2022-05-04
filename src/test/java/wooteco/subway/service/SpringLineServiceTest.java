package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@SpringBootTest
@Transactional
class SpringLineServiceTest {

    private static final LineRequest LINE_FIXTURE = new LineRequest("2호선", "bg-color-600");
    private static final LineRequest LINE_FIXTURE2 = new LineRequest("3호선", "bg-color-700");
    private static final LineRequest LINE_FIXTURE3 = new LineRequest("4호선", "bg-color-800");

    @Autowired
    private LineService lineService;

    @Nested
    @DisplayName("새로운 노선을 저장할 때")
    class SaveTest {

        @Test
        @DisplayName("노선 이름이 중복되지 않으면 저장할 수 있다.")
        void save_Success_If_Not_Exists() {
            assertThatCode(() -> lineService.save(LINE_FIXTURE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("노선 이름이 중복되면 예외가 발생한다.")
        void save_Fail_If_Exists() {
            lineService.save(LINE_FIXTURE);
            assertThatThrownBy(() -> lineService.save(LINE_FIXTURE))
                    .isInstanceOf(DuplicateKeyException.class);
        }
    }

    @Test
    @DisplayName("전체 지하철 노선을 조회할 수 있다")
    void findAll() {
        lineService.save(LINE_FIXTURE);
        lineService.save(LINE_FIXTURE2);
        lineService.save(LINE_FIXTURE3);

        assertThat(lineService.findAll()).extracting("name")
                .isEqualTo(List.of(LINE_FIXTURE.getName(), LINE_FIXTURE2.getName(), LINE_FIXTURE3.getName()));
    }

    @Test
    @DisplayName("아이디로 지하철 노선을 조회할 수 있다")
    void findById() {
        final Line line = lineService.save(LINE_FIXTURE);
        final Line found = lineService.findById(line.getId());

        assertThat(line).isEqualTo(found);
    }

    @Test
    @DisplayName("아이디로 지하철노선을 삭제할 수 있다")
    void deleteById() {
        final Line line = lineService.save(LINE_FIXTURE);
        final List<Line> lines = lineService.findAll();
        lineService.deleteById(line.getId());
        final List<Line> afterDelete = lineService.findAll();

        assertThat(lines).isNotEmpty();
        assertThat(afterDelete).isEmpty();
    }

    @Test
    @DisplayName("노선 이름과 색상을 변경할 수 있다")
    void update() {
        final Line line = lineService.save(LINE_FIXTURE);
        final Long id = line.getId();
        final LineRequest lineRequest = new LineRequest("22호선", "bg-color-777");

        lineService.update(id, lineRequest);
        final Line updated = lineService.findById(id);

        assertAll(
                () -> assertThat(updated.getId()).isEqualTo(id),
                () -> assertThat(updated.getName()).isEqualTo("22호선"),
                () -> assertThat(updated.getColor()).isEqualTo("bg-color-777")
        );
    }
}
