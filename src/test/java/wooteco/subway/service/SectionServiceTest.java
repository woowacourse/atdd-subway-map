package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class SectionServiceTest extends ServiceTest{

    @Autowired
    private SectionService service;

    @Autowired
    protected SectionDao dao;

    @BeforeEach
    void cleanseAndSetUp() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("section_test_fixture.sql"));
        }
    }

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        private final Long LINE_ID = 1L;
        private final int CURRENT_SECTION_DISTANCE = 5;
        private final SectionEntity EXISTING_SECTION_AT_LINE1 = new SectionEntity(LINE_ID, 3L, 1L,
                CURRENT_SECTION_DISTANCE);

        @Test
        void 상행_종점_등록시_그대로_저장() {
            service.save(LINE_ID, new CreateSectionRequest(2L, 3L, 10));

            List<SectionEntity> actual = dao.findAllByLineId(LINE_ID);
            List<SectionEntity> expected = List.of(EXISTING_SECTION_AT_LINE1,
                    new SectionEntity(LINE_ID, 2L, 3L, 10));

            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        @Test
        void 하행_종점_등록시_그대로_저장() {
            service.save(LINE_ID, new CreateSectionRequest(1L, 2L, 10));

            List<SectionEntity> actual = dao.findAllByLineId(LINE_ID);
            List<SectionEntity> expected = List.of(EXISTING_SECTION_AT_LINE1,
                    new SectionEntity(LINE_ID, 1L, 2L, 10));

            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        @Test
        void 저장하려는_구간의_상행역이_이미_상행역으로_등록된_경우_저장_후_기존_구간은_수정() {
            int newSectionDistance = 2;
            service.save(LINE_ID, new CreateSectionRequest(3L, 2L, newSectionDistance));

            List<SectionEntity> actual = dao.findAllByLineId(LINE_ID);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(LINE_ID, 3L, 2L, newSectionDistance),
                    new SectionEntity(LINE_ID, 2L, 1L, CURRENT_SECTION_DISTANCE - newSectionDistance));

            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        @Test
        void 저장하려는_구간의_히행역이_이미_하행역으로_등록된_경우_저장_후_기존_구간은_수정() {
            int newSectionDistance = 1;
            service.save(LINE_ID, new CreateSectionRequest(2L, 1L, newSectionDistance));

            List<SectionEntity> actual = dao.findAllByLineId(LINE_ID);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(LINE_ID, 3L, 2L, CURRENT_SECTION_DISTANCE - newSectionDistance),
                    new SectionEntity(LINE_ID, 2L, 1L, newSectionDistance));

            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        @DisplayName("거리 유효성 검증")
        @Nested
        class ValidateDistanceTest {

            @Test
            void 종점_등록시_거리는_1이상이면_무조건_유효() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(2L, 3L, 999999);
                assertThatNoException()
                        .isThrownBy(() -> service.save(LINE_ID, sectionRequest));
            }

            @Test
            void 거리가_0인_경우_예외발생() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(2L, 3L, 0);
                assertThatThrownBy(() -> service.save(LINE_ID, sectionRequest))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void 상행역을_기준으로_기존_구간_사이에_끼려는_경우_기존_구간과_거리가_같으면_예외발생() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(3L, 2L, CURRENT_SECTION_DISTANCE);
                assertThatThrownBy(() -> service.save(LINE_ID, sectionRequest))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void 상행역을_기준으로_기존_구간_사이에_끼려는_경우_기존_구간보다_거리가_크면_예외발생() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(3L, 2L, 99999);
                assertThatThrownBy(() -> service.save(LINE_ID, sectionRequest))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void 하행역을_기준으로_기존_구간_사이에_끼려는_경우_기존_구간과_거리가_같으면_예외발생() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(2L, 1L, CURRENT_SECTION_DISTANCE);
                assertThatThrownBy(() -> service.save(LINE_ID, sectionRequest))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void 하행역을_기준으로_기존_구간_사이에_끼려는_경우_기존_구간보다_거리가_크면_예외발생() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(2L, 1L, 99999);
                assertThatThrownBy(() -> service.save(LINE_ID, sectionRequest))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("지하철역 유효성 검증")
        @Nested
        class ValidateStationsTest {

            @Test
            void 동일한_지하철역_두개를_입력한_경우_예외발생() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(2L, 2L, 5);
                assertThatThrownBy(() -> service.save(LINE_ID, sectionRequest))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void 존재하지_않는_지하철역을_입력한_경우_예외발생() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(2L, 99999L, 5);
                assertThatThrownBy(() -> service.save(LINE_ID, sectionRequest))
                        .isInstanceOf(NotFoundException.class);
            }

            @Test
            void 존재하더라도_두_개의_지하철_역_모두_해당_노선에_구간으로_등록되지_않은_경우_예외발생() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(4L, 5L, 5);
                assertThatThrownBy(() -> service.save(LINE_ID, sectionRequest))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void 존재하더라도_두_개의_지하철_역_모두_해당_노선에_이미_구간으로_등록된_경우_예외발생() {
                CreateSectionRequest sectionRequest = new CreateSectionRequest(1L, 3L, 5);
                assertThatThrownBy(() -> service.save(LINE_ID, sectionRequest))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

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
