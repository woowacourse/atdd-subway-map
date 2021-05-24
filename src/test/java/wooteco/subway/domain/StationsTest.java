package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[도메인] Stations")
class StationsTest {
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 수서역 = new Station(2L, "수서역");
    private final Station 잠실역 = new Station(3L, "잠실역");
    private final Station 동탄역 = new Station(4L, "동탄역");
    private final Section 강남_수서 = new Section(강남역, 수서역, 10);
    private final Section 수서_잠실 = new Section(수서역, 잠실역, 10);
    private final Section 잠실_동탄 = new Section(잠실역, 동탄역, 10);

    @DisplayName("구간 순서대로 역 보여주기")
    @Test
    void convertToSortedStations() {
        List<Section> setting = Arrays.asList(수서_잠실, 강남_수서, 잠실_동탄);
        Sections sections = new Sections(setting);

        List<Station> stations = new Stations(sections).getStations();

        assertThat(stations).hasSize(4);
        assertThat(stations).containsExactly(강남역, 수서역, 잠실역, 동탄역);
    }
}