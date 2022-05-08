package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDaoImpl;
import wooteco.subway.dao.SectionDaoImpl;
import wooteco.subway.dao.StationDaoImpl;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;

public class LineServiceTest {
    private final LineService lineService =
            new LineService(LineDaoImpl.getInstance(), StationDaoImpl.getInstance(), SectionDaoImpl.getInstance());

    private Station station1;
    private Station station2;
    private Station station3;

    @BeforeEach
    void setUp() {
        final List<Line> lines = LineDaoImpl.getInstance().findAll();
        lines.clear();
        final List<Station> stations = StationDaoImpl.getInstance().findAll();
        stations.clear();
        final List<Section> sections = SectionDaoImpl.getInstance().findAll();
        sections.clear();

        station1 = new Station("애플역");
        station2 = new Station("갤럭시역");
        station3 = new Station("옵티머스역");
    }

    @Test
    @DisplayName("이미 존재하는 노선을 생성하려고 하면 에러를 발생한다.")
    void save_duplicate_station() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);

        final LineRequest lineRequest1 =
                new LineRequest("신분당선", "bg-red-600", id1, id2, 20);
        final LineRequest lineRequest2 =
                new LineRequest("신분당선", "bg-green-600", id1, id3, 15);

        lineService.saveLine(lineRequest1);

        Assertions.assertThatThrownBy(() -> lineService.saveLine(lineRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 이름의 노선이 존재합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 접근하려고 하면 에러를 발생한다.")
    void not_exist_station() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final LineRequest lineRequest =
                new LineRequest("신분당선", "bg-red-600", id1, id2, 20);

        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final Long invalidLineId = lineResponse.getId() + 1L;

        Assertions.assertThatThrownBy(() -> lineService.deleteLine(invalidLineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당하는 노선이 존재하지 않습니다.");
    }
}
