package wooteco.subway.line;

import org.junit.jupiter.api.Test;
import wooteco.subway.AppConfig;
import wooteco.subway.station.Station;

import static org.assertj.core.api.Assertions.assertThat;

public class LineDaoTest {
    private LineRepository lineRepository = AppConfig.lineRepository();

    @Test
    void create() {
        Line line = new Line("2호선", new Station("강남역"), new Station("역삼역"));
        lineRepository.save(line);
        assertThat(lineRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void findAll() {
        Line line1 = new Line("1호선", new Station("동탄역"), new Station("의정부역"));
        Line line2 = new Line("9호선", new Station("가양역"), new Station("석촌역"));
        lineRepository.save(line1);
        lineRepository.save(line2);
        assertThat(lineRepository.findAll().size()).isEqualTo(2);
    }
}
