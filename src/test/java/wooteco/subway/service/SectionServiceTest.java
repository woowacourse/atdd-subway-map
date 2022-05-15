package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class SectionServiceTest extends ServiceTest {

    @Autowired
    private SectionService service;

    @Autowired
    protected SectionDao dao;

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        private final StationEntity STATION1 = new StationEntity(1L, "강남역");
        private final StationEntity STATION2 = new StationEntity(2L, "선릉역");
        private final StationEntity STATION3 = new StationEntity(3L, "잠실역");

        @BeforeEach
        void setupStations() {
            testFixtureManager.saveStations("강남역", "선릉역", "잠실역");
            testFixtureManager.saveLine("존재하는 노선", "색깔");
        }

        @Test
        void 상행_종점_등록시_그대로_저장() {
            testFixtureManager.saveSection(1L, 2L, 3L, 10);

            service.save(1L, new CreateSectionRequest(1L, 2L, 20));
            List<SectionEntity> actual = dao.findAllByLineId(1L);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(1L, STATION1, STATION2, 20),
                    new SectionEntity(1L, STATION2, STATION3, 10));

            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        @Test
        void 하행_종점_등록시_그대로_저장() {
            testFixtureManager.saveSection(1L, 1L, 2L, 10);

            service.save(1L, new CreateSectionRequest(2L, 3L, 30));
            List<SectionEntity> actual = dao.findAllByLineId(1L);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(1L, STATION1, STATION2, 10),
                    new SectionEntity(1L, STATION2, STATION3, 30));

            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        @Test
        void 저장하려는_구간의_상행역이_이미_상행역으로_등록된_경우_저장_후_기존_구간은_수정() {
            int existingSectionDistance = 10;
            int newSectionDistance = 2;
            testFixtureManager.saveSection(1L, 1L, 3L, existingSectionDistance);

            service.save(1L, new CreateSectionRequest(1L, 2L, newSectionDistance));
            List<SectionEntity> actual = dao.findAllByLineId(1L);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(1L, STATION1, STATION2, newSectionDistance),
                    new SectionEntity(1L, STATION2, STATION3, existingSectionDistance - newSectionDistance));

            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        @Test
        void 저장하려는_구간의_히행역이_이미_하행역으로_등록된_경우_저장_후_기존_구간은_수정() {
            int existingSectionDistance = 10;
            int newSectionDistance = 3;
            testFixtureManager.saveSection(1L, 1L, 3L, existingSectionDistance);

            service.save(1L, new CreateSectionRequest(2L, 3L, newSectionDistance));
            List<SectionEntity> actual = dao.findAllByLineId(1L);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(1L, STATION1, STATION2, existingSectionDistance - newSectionDistance),
                    new SectionEntity(1L, STATION2, STATION3, newSectionDistance));

            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }
    }

    @DisplayName("delete 메서드는 노선의 특정 구간 데이터를 삭제한다")
    @Nested
    class DeleteTest {

        private final StationEntity STATION1 = new StationEntity(1L, "강남역");
        private final StationEntity STATION2 = new StationEntity(2L, "선릉역");
        private final StationEntity STATION3 = new StationEntity(3L, "잠실역");

        @Test
        void 노선의_종점을_제거하려는_경우_그와_연결된_구간만_하나_제거() {
            testFixtureManager.saveStations("강남역", "선릉역", "잠실역");
            testFixtureManager.saveLine("노선", "색깔");
            testFixtureManager.saveSection(1L, 1L, 2L);
            testFixtureManager.saveSection(1L, 2L, 3L, 10);

            service.delete(1L, 1L);
            List<SectionEntity> actual = dao.findAllByLineId(1L);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(1L, STATION2, STATION3, 10));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 노선의_중앙에_있는_역을_제거한_경우_그_사이를_잇는_구간을_새로_생성() {
            testFixtureManager.saveStations("강남역", "선릉역", "잠실역");
            testFixtureManager.saveLine("노선", "색깔");
            testFixtureManager.saveSection(1L, 1L, 2L, 5);
            testFixtureManager.saveSection(1L, 2L, 3L, 10);

            service.delete(1L, 2L);
            List<SectionEntity> actual = dao.findAllByLineId(1L);
            List<SectionEntity> expected = List.of(
                    new SectionEntity(1L, STATION1, STATION3, 15));

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 등록되지_않은_노선_id가_입력된_경우_예외발생() {
            testFixtureManager.saveStations("강남역", "선릉역", "잠실역");
            assertThatThrownBy(() -> service.delete(99999L, 1L))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        void 노선에_구간으로_등록되지_않은_지하철역_id가_입력된_경우_예외발생() {
            testFixtureManager.saveStations("강남역", "선릉역", "잠실역");
            testFixtureManager.saveLine("노선", "색깔");
            testFixtureManager.saveSection(1L, 1L, 2L);

            assertThatThrownBy(() -> service.delete(1L, 3L))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 노선의_구간이_하나_남은_경우_구간_제거_시도시_예외발생() {
            testFixtureManager.saveStations("강남역", "선릉역", "잠실역");
            testFixtureManager.saveLine("노선", "색깔");
            testFixtureManager.saveSection(1L, 1L, 2L);

            assertThatThrownBy(() -> service.delete(1L, 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
