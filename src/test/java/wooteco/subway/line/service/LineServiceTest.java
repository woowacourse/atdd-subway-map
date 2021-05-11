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


//    @Test
//    @DisplayName("노선 정상 저장된다")
//    void save() {
//        //given
//        Section section = new Section(new Station(1L, "아마역"), new Station(2L, "마찌역"), 10);
//        when(lineRepository.save("신분당선", "화이트", 1L, 2L, 10))
//                .thenReturn(new Line(1L, "신분당선", "화이트", section));
//
//        // when
//        Line savedLine = lineService.save(new LineRequest("신분당선", "화이트", 1L, 2L, 10));
//
//        // then
//        assertThat(savedLine.id()).isEqualTo(1L);
//        assertThat(savedLine.stations()).hasSize(2);
//        assertThat(savedLine.stations().get(0).id()).isEqualTo(1L);
//    }

    /**
     *     public void addSection(final Long lineId, final SectionRequest sectionRequest) {
     *         Line originLine = lineRepository.findById(lineId);
     *         Line addedLine = originLine.addedSectionLine(
     *                 new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance()));
     *         Section toUpdateSection = addedLine.affectedSection(originLine);
     *         lineRepository.updateSection(lineId, toUpdateSection);
     *     }
     */

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