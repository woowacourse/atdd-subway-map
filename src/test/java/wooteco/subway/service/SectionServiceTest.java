package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.SubwayException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@DisplayName("구간 Service 테스트")
public class SectionServiceTest extends ServiceTest {
    @InjectMocks
    private SectionService sectionService;

    @DisplayName("새 구간을 생성한다.")
    @Test
    void save() {
        //given
        Section section = new Section(10, 1L, 1L, 2L);

        given(sectionDao.save(section))
                .willReturn(section);

        //when
        Section savedSection = sectionService.init(section);

        //then
        assertThat(savedSection.getDistance()).isEqualTo(section.getDistance());
    }

    @DisplayName("[ERROR] 정확히 일치하는 구간이 있으면 새로 등록할 수 없다.")
    @Test
    void saveSameSectionThenThrowException() {
        //given
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);

        Line line = new Line(2L, "2호선", "green");
        SectionRequest request = new SectionRequest(1L, 2L, 10);


        given(sectionDao.findByLineId(anyLong()))
                .willReturn(List.of(existedSection1, existedSection2));

        //when & then
        assertThatThrownBy(() -> {
            sectionService.add(line, request);
        }).isInstanceOf(SubwayException.class)
                .hasMessageContaining("추가할 수 없는 구간입니다.");
    }

    @DisplayName("[ERROR] 정확히 일치하지 않더라도 구간이 연결되어 있으면 새로 등록할 수 없다.")
    @Test
    void saveLinearlySameSectionThenThrowException() {
        //given
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);

        Line line = new Line(2L, "2호선", "green");
        SectionRequest request = new SectionRequest(1L, 3L, 10);



        given(sectionDao.findByLineId(any()))
                .willReturn(List.of(existedSection1, existedSection2));

        //when & then
        assertThatThrownBy(() -> {
            sectionService.add(line, request);
        }).isInstanceOf(SubwayException.class)
                .hasMessageContaining("추가할 수 없는 구간입니다.");
    }

    @DisplayName("구간에 포함된 지하철 역 ID 리스트를 조회한다.")
    @Test
    void getStationIds() {
        //given
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);
        Section existedSection3 = new Section(10, 2L, 3L, 4L);
        Section existedSection4 = new Section(10, 2L, 6L, 7L);

        given(sectionDao.findByLineId(anyLong()))
                .willReturn(List.of(existedSection1, existedSection2, existedSection3, existedSection4));

        //when
        Set<Long> stationIds = sectionService.getStationIds(1L);

        //then
        assertThat(stationIds.size()).isEqualTo(6);
        assertThat(stationIds).isEqualTo(Set.of(1L, 2L, 3L, 4L, 6L, 7L));
    }
}
