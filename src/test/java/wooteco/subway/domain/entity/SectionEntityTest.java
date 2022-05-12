package wooteco.subway.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionEntityTest {

    @DisplayName("id를 up에서 down으로 정렬된 순서로 뽑는다.")
    @Test
    void extractStationIds() {
        SectionEntity sectionEntity1 = SectionEntity.of(1L, 1L, 3L, 4L, 1);
        SectionEntity sectionEntity2 = SectionEntity.of(1L, 1L, 1L, 2L, 1);
        SectionEntity sectionEntity3 = SectionEntity.of(1L, 1L, 5L, 6L, 1);
        SectionEntity sectionEntity4 = SectionEntity.of(1L, 1L, 2L, 3L, 1);
        SectionEntity sectionEntity5 = SectionEntity.of(1L, 1L, 4L, 5L, 1);
        SectionEntity sectionEntity6 = SectionEntity.of(1L, 1L, 10L, 1L, 1);

        List<SectionEntity> sectionEntities = List.of(sectionEntity1, sectionEntity2, sectionEntity3, sectionEntity4, sectionEntity5, sectionEntity6);

        List<Long> stationIds = SectionEntity.extractStationIds(sectionEntities);
        assertThat(stationIds.get(0)).isEqualTo(10L);
        assertThat(stationIds.get(1)).isEqualTo(1L);
        assertThat(stationIds.get(2)).isEqualTo(2L);
        assertThat(stationIds.get(3)).isEqualTo(3L);
        assertThat(stationIds.get(4)).isEqualTo(4L);
        assertThat(stationIds.get(5)).isEqualTo(5L);
        assertThat(stationIds.get(6)).isEqualTo(6L);
    }
}
