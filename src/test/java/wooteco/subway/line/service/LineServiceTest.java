package wooteco.subway.line.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LineServiceTest {
    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("노선에 구간을 추가한다.")
    void addSection() {
        Long lineId = 1L;
        Station station1 = new Station(1L, "아마역");
        Station station3 = new Station(3L, "잠실역");
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 5);
        lineService.addSection(lineId, sectionRequest);
    }
}