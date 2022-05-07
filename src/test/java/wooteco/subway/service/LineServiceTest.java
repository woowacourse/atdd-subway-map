package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@Transactional
@Sql("classpath:dao_test_db.sql")
class LineServiceTest {

    @Autowired
    private LineService service;

    @Autowired
    private LineDao dao;

    @Test
    void findAll_메서드는_모든_데이터를_id_순서대로_조회() {
        List<LineResponse> actual = service.findAll();

        List<LineResponse> expected = List.of(
                new LineResponse(1L, "이미 존재하는 노선 이름", "노란색"),
                new LineResponse(2L, "신분당선", "빨간색"),
                new LineResponse(3L, "2호선", "초록색")
        );

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("find 메서드는 특정 id의 데이터를 조회한다.")
    @Nested
    class FindTest {

        @Test
        void 존재하는_데이터의_id가_입력된_경우_성공() {
            LineResponse actual = service.find(1L);
            LineResponse excepted = new LineResponse(1L, "이미 존재하는 노선 이름", "노란색");

            assertThat(actual).isEqualTo(excepted);
        }

        @Test
        void 존재하지_않는_데이터의_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> service.find(99999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_이름인_경우_성공() {
            LineResponse actual = service.save(new LineRequest("새로운 노선", "분홍색"));

            LineResponse expected = new LineResponse(4L, "새로운 노선", "분홍색");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름인_경우_예외발생() {
            assertThatThrownBy(() -> service.save(new LineRequest("이미 존재하는 노선 이름", "노란색")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("update 메서드는 데이터를 수정한다")
    @Nested
    class UpdateTest {

        @Test
        void 중복되지_않는_이름으로_수정_가능() {
            service.update(1L, new LineRequest("새로운 노선 이름", "노란색"));

            String actual = dao.findById(1L).get().getName();
            String expected = "새로운 노선 이름";

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 색상은_자유롭게_수정_가능() {
            LineRequest validLineRequest = new LineRequest("새로운 노선 이름", "새로운 색상");
            service.update(1L, validLineRequest);

            String actual = dao.findById(1L).get().getColor();
            String expected = "새로운 색상";

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름으로_수정하려는_경우_예외발생() {
            LineRequest duplicateLineNameRequest = new LineRequest("이미 존재하는 노선 이름", "노란색");
            assertThatThrownBy(() -> service.update(2L, duplicateLineNameRequest))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 존재하지_않는_노선을_수정하려는_경우_예외발생() {
            LineRequest validLineRequest = new LineRequest("새로운 노선 이름", "노란색");
            assertThatThrownBy(() -> service.update(999999999L, validLineRequest))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("delete 메서드는 특정 데이터를 삭제한다")
    @Nested
    class DeleteTest {

        @Test
        void 존재하는_데이터의_id가_입력된_경우_삭제성공() {
            service.delete(1L);

            boolean notFound = dao.findById(1L).isEmpty();

            assertThat(notFound).isTrue();
        }

        @Test
        void 존재하지_않는_데이터의_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> service.delete(99999L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
