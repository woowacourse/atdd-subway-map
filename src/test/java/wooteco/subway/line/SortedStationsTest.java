package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.SortedStations;
import wooteco.subway.domain.Section;
import wooteco.subway.web.dto.StationResponse;

public class SortedStationsTest {

    @Test
    void sortTest() {
        final List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
        final List<StationResponse> stations = inputStations(ids);

        SortedStations sortedStations = new SortedStations(inputSections(), stations);
        assertThat(sortedStations.get()).containsAll(stations);
    }

    private List<StationResponse> inputStations(List<Long> ids) {
        List<StationResponse> stations = ids.stream()
                .map(id -> new StationResponse(id, "역"))
                .collect(Collectors.toList());
        Collections.shuffle(stations);
        return stations;
    }

    private List<Section> inputSections() {
        int x = 1;
        List<Section> sections = Arrays.asList(
                new Section(7L, 8L, x),
                new Section(2L, 3L, x),
                new Section(5L, 6L, x),
                new Section(3L, 4L, x),
                new Section(8L, 9L, x),
                new Section(4L, 5L, x),
                new Section(1L, 2L, x),
                new Section(6L, 7L, x)
        );
        Collections.shuffle(sections);
        return sections;
    }
}
