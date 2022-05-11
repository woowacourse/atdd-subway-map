package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationsTest {

    @Test
    @DisplayName("역 Id에 따라 정렬하는 기능")
    void sortBy() {
        // given
        Stations stations = new Stations(
                List.of(
                        new Station(1L, "강남역"), new Station(2L, "노원역"), new Station(3L, "선릉역")
                )
        );

        // when
        List<Station> sorted = stations.sortBy(List.of(3L, 1L, 2L));
        List<Long> sortedIds = sorted.stream()
                .map(Station::getId)
                .collect(Collectors.toList());
        // then
        assertThat(sortedIds).containsExactly(3L, 1L, 2L);
    }
}
