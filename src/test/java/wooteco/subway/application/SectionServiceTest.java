package wooteco.subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.dao.MemorySectionDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.constant.TerminalStation;
import wooteco.subway.exception.constant.SectionNotRegisterException;

import java.util.LinkedList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionServiceTest {

    private SectionDao sectionDao;
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        sectionDao = new MemorySectionDao();
        sectionService = new SectionService(sectionDao);
    }

    @DisplayName("정렬을 할 수 있다")
    @Test
    void can_sort_stations() {
        // 3 -> 2 -> 5 -> 7
        sectionDao.save(new Section(2L, 5L, 40, 10L));
        sectionDao.save(new Section(3L, 2L, 30, 10L));
        sectionDao.save(new Section(5L, 7L, 50, 10L));

        LinkedList<Long> sortedStations = sectionService.findSortedStationIds(10L);
        assertThat(sortedStations).containsExactly(3L, 2L, 5L, 7L);
    }

    @DisplayName("상행 종점역과 하행종점역을 찾을 수 있다")
    @Test
    void can_find_up_and_down_terminal_station() {
        // 3 -> 2 -> 5 -> 7
        sectionDao.save(new Section(2L, 5L, 40, 10L));
        sectionDao.save(new Section(3L, 2L, 30, 10L));
        sectionDao.save(new Section(5L, 7L, 50, 10L));

        Map<TerminalStation, Long> terminalStationMap = sectionService.findTerminalStations(10L);
        assertThat(terminalStationMap.get(TerminalStation.UP)).isEqualTo(3L);
        assertThat(terminalStationMap.get(TerminalStation.DOWN)).isEqualTo(7L);
    }

    @DisplayName("[구간 등록]")
    @Nested
    class register {

        @DisplayName("상행 종점 등록을 한다")
        @Test
        void create_first_terminal_station() {
            // given
            long savedSectionId = firstCreateSection(1L, 1L, 2L, 3);
            long savedSectionId2 = sectionService.createSection2(new Section(3L, 1L, 4, 1L));

            // when
            Section foundSection = sectionDao.findById(savedSectionId).get();
            Section foundSection2 = sectionDao.findById(savedSectionId2).get();

            // then
            assertThat(foundSection.getUpStationId()).isEqualTo(1L);
            assertThat(foundSection.getDownStationId()).isEqualTo(2L);
            assertThat(foundSection2.getUpStationId()).isEqualTo(3L);
            assertThat(foundSection2.getDownStationId()).isEqualTo(1L);
        }

        @DisplayName("하행 종점 등록을 한다")
        @Test
        void create_last_terminal_station() {
            // given
            long savedSectionId = firstCreateSection(1L, 1L, 2L, 3);
            long savedSectionId2 = sectionService.createSection2(new Section(2L, 3L, 4, 1L));

            // when
            Section foundSection = sectionDao.findById(savedSectionId).get();
            Section foundSection2 = sectionDao.findById(savedSectionId2).get();

            // then
            assertThat(foundSection.getUpStationId()).isEqualTo(1L);
            assertThat(foundSection.getDownStationId()).isEqualTo(2L);
            assertThat(foundSection2.getUpStationId()).isEqualTo(2L);
            assertThat(foundSection2.getDownStationId()).isEqualTo(3L);
        }

        @DisplayName("[갈림길 방지]")
        @Nested
        class preventForkJoin {
            @DisplayName("상행역이 같을때 변경된 구간을 검증한다")
            @Test
            void prevent_forked_road_same_up_station() {
                // given
                long oldSectionId = firstCreateSection(1L, 1L, 2L, 7);
                long newSectionId = sectionService.createSection2(new Section(1L, 3L, 4, 1L));

                Section oldSection = sectionDao.findById(oldSectionId).get();
                Section newSection = sectionDao.findById(newSectionId).get();

                assertThat(oldSection.getUpStationId()).isEqualTo(3L);
                assertThat(oldSection.getDownStationId()).isEqualTo(2L);
                assertThat(oldSection.getDistance()).isEqualTo(3);

                assertThat(newSection.getUpStationId()).isEqualTo(1L);
                assertThat(newSection.getDownStationId()).isEqualTo(3L);
                assertThat(newSection.getDistance()).isEqualTo(4);
            }

            @DisplayName("하행역이 같을때 변경된 구간을 검증한다")
            @Test
            void prevent_forked_road_same_down_station() {
                // given
                long oldSectionId = firstCreateSection(1L, 1L, 2L, 7);
                long newSectionId = sectionService.createSection2(new Section(3L, 2L, 4, 1L));

                Section oldSection = sectionDao.findById(oldSectionId).get();
                Section newSection = sectionDao.findById(newSectionId).get();

                assertAll(
                        () -> assertThat(oldSection.getUpStationId()).isEqualTo(1L),
                        () -> assertThat(oldSection.getDownStationId()).isEqualTo(3L),
                        () -> assertThat(oldSection.getDistance()).isEqualTo(3),

                        () -> assertThat(newSection.getUpStationId()).isEqualTo(3L),
                        () -> assertThat(newSection.getDownStationId()).isEqualTo(2L),
                        () -> assertThat(newSection.getDistance()).isEqualTo(4)
                );
            }

            @DisplayName("[예외] 역과 역사이의 길이가 기존보다 길다면 구간을 추가할 수 없다")
            @ParameterizedTest
            @CsvSource(value = {"1 - 3", "3 - 2"}, delimiterString = " - ")
            void can_not_register_if_new_distance_is_longer(long upStationId, long downStationId) {
                firstCreateSection(1L, 1L, 2L, 7);
                assertThatThrownBy(() -> sectionService.createSection2(new Section(upStationId, downStationId, 11, 1L)))
                        .isInstanceOf(SectionNotRegisterException.class);
            }

            @DisplayName("[예외] 상행역과 하행역 모두 존재하는 것을 등록하려 한다면 구간을 추가할 수 없다 (구간 1개인 경우)")
            @Test
            void can_not_register_if_all_same_up_and_down_station_when_1_section() {
                long upStationId = 1L;
                long downStationId = 2L;
                firstCreateSection(1L, upStationId, downStationId, 7);
                assertThatThrownBy(() -> sectionService.createSection2(new Section(upStationId, downStationId, 11, 1L)))
                        .isInstanceOf(SectionNotRegisterException.class);
            }

            @DisplayName("[예외] 상행역과 하행역 모두 존재하는 것을 등록하려 한다면 구간을 추가할 수 없다 (구간 2개인 경우)")
            @Test
            void can_not_register_if_all_same_up_and_down_station_when_2_section() {
                long upStationId = 1L;
                long downStationId = 3L;
                firstCreateSection(1L, upStationId, 2L, 7);
                sectionService.createSection(1L, 2L, downStationId, 7);
                assertThatThrownBy(() -> sectionService.createSection2(new Section(upStationId, downStationId, 11, 1L)))
                        .isInstanceOf(SectionNotRegisterException.class);
            }

            @DisplayName("[예외] 상행역과 하행역 모두 존재하지 않는다면 구간을 추가할 수 없다")
            @Test
            void can_not_register_if_up_and_down_all_exist() {
                firstCreateSection(1L, 1L, 2L, 7);
                assertThatThrownBy(() -> sectionService.createSection2(new Section(5L, 6L, 11, 1L)))
                        .isInstanceOf(SectionNotRegisterException.class);
            }
        }
    }

    private long firstCreateSection(Long lineId, long upStationId, long downStationId, Integer distance) {
        return sectionDao.save(new Section(upStationId, downStationId, distance, lineId));
    }
}
