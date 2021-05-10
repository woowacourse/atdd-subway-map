package wooteco.subway.line.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.line.domain.Line;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LineRepositoryTest {
    private static final Long EXIST_ID = 1L;
    private static final Station EXIST_UP_STATION = new Station(1L, "아마역");
    @Autowired
    private LineRepository lineRepository;

    @Test
    @DisplayName("노선 id로 노선을 찾는다.")
    void findById() {
        Line line = lineRepository.findById(EXIST_ID);

        assertThat(line.sections()).isNotEmpty();
        assertThat(line.sections().get(0).upStation().id()).isNotNull();
        assertThat(line.sections().get(0).downStation().name()).isNotNull();
        assertThat(line.sections().get(0).upStation()).isEqualTo(EXIST_UP_STATION);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        Line line = lineRepository.save("신분당선", "화이트", 1L, 2L, 10);

        assertThat(line.id()).isEqualTo(2L);
        assertThat(line.stations()).hasSize(2);
        assertThat(line.stations().get(0).id()).isEqualTo(1L);
    }
}