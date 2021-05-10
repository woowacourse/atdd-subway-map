package wooteco.subway.line.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class LineServiceTest {
    @InjectMocks
    private LineService lineService;

    @Mock
    private LineRepository lineRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("노선 정상 저장된다")
    void save() {
        //given
        Section section = new Section(new Station(1L, "아마역"), new Station(2L, "마찌역"), 10);
        when(lineRepository.save("신분당선", "화이트", 1L, 2L, 10))
                .thenReturn(new Line(1L, "신분당선", "화이트", section));

        // when
        Line savedLine = lineService.save(new LineRequest("신분당선", "화이트", 1L, 2L, 10));

        // then
        assertThat(savedLine.id()).isEqualTo(1L);
        assertThat(savedLine.stations()).hasSize(2);
        assertThat(savedLine.stations().get(0).id()).isEqualTo(1L);
    }

//    @Test
//    @DisplayName("노선에 구간을 추가한다.")
//    void addSection() {
//        Long lineId = 1L;
//        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
//        when(sectionDao.save(any(Section.class))).thenReturn(null);
//        lineService.addSection(1L, sectionRequest);
//    }
}