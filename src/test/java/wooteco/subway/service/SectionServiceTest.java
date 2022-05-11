package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class SectionServiceTest extends ServiceTest {

    @Autowired
    private SectionService service;

    @Autowired
    protected SectionDao dao;

    @DisplayName("delete 메서드는 노선의 특정 구간 데이터를 삭제한다")
    @Nested
    class DeleteTest {

        @Test
        void 노선의_종점을_제거하려는_경우_그와_연결된_구간만_하나_제거() {
            service.delete(2L, 1L);

            List<SectionEntity> actual = dao.findAllByLineId(2L);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(2L, 2L, 3L, 5));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 노선의_중앙에_있는_역을_제거한_경우_그_사이를_잇는_구간을_새로_생성() {
            service.delete(2L, 2L);

            List<SectionEntity> actual = dao.findAllByLineId(2L);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(2L, 1L, 3L, 10));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 등록되지_않은_노선_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> service.delete(99999L, 1L))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        void 노선에_구간으로_등록되지_않은_지하철역_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> service.delete(1L, 2L))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 노선의_구간이_하나_남은_경우_구간_제거_시도시_예외발생() {
            assertThatThrownBy(() -> service.delete(1L, 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
