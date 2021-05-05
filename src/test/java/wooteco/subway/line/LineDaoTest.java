package wooteco.subway.line;

import org.junit.jupiter.api.Test;
import wooteco.subway.AppConfig;
import wooteco.subway.station.Station;

import static org.assertj.core.api.Assertions.assertThat;

public class LineDaoTest {
    private LineRepository lineRepository = AppConfig.lineRepository();

    @Test
    void create() {
        // given
        Line line = new Line("2호선", new Station("강남역"), new Station("역삼역"));

        // when
        lineRepository.save(line);

        // then
        assertThat(lineRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void findAll() {
        // given
        Line line1 = new Line("1호선", new Station("동탄역"), new Station("의정부역"));
        Line line2 = new Line("9호선", new Station("가양역"), new Station("석촌역"));
        int sizeBefore = lineRepository.findAll().size();

        // when
        lineRepository.save(line1);
        lineRepository.save(line2);

        // then
        assertThat(lineRepository.findAll().size()).isEqualTo(sizeBefore + 2);
    }

    @Test
    void findByIdTest() {
        // given
        Line line = new Line("7호선", new Station("장암역"), new Station("부평구청역"));

        // when
        Line expected = lineRepository.save(line);
        Line found = lineRepository.findById(expected.getId());

        // then
        assertThat(expected).isEqualTo(found);
    }
}
