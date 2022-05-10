package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import wooteco.subway.domain.Section;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        Section savedSection = sectionService.save(section);

        //then
        assertThat(savedSection.getDistance()).isEqualTo(section.getDistance());
    }

    @DisplayName("[ERROR] 정확히 일치하는 구간이 있으면 새로 등록할 수 없다.")
    @Test
    void saveSameSectionThenThrowException() {
        //given
        Section section = new Section(10, 2L, 1L, 2L);
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);


        given(sectionDao.findAll())
                .willReturn(List.of(existedSection1, existedSection2));

        //when & then
        assertThatThrownBy(() -> {
            sectionService.save(section);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기존에 존재하는 노선 구간은 등록할 수 없습니다.");
    }

    @DisplayName("[ERROR] 정확히 일치하지 않더라도 구간이 연결되어 있으면 새로 등록할 수 없다.")
    @Test
    void saveLinearlySameSectionThenThrowException() {
        //given
        Section section = new Section(10, 2L, 1L, 3L);
        Section existedSection1 = new Section(10, 2L, 1L, 2L);
        Section existedSection2 = new Section(10, 2L, 2L, 3L);


        given(sectionDao.findAll())
                .willReturn(List.of(existedSection1, existedSection2));

        //when & then
        assertThatThrownBy(() -> {
            sectionService.save(section);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기존에 존재하는 노선 구간은 등록할 수 없습니다.");
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
        List<Long> stationIds = sectionService.getStationIds(1L);

        //then
        assertThat(stationIds.size()).isEqualTo(6);
        assertThat(stationIds).isEqualTo(List.of(1L, 2L, 3L, 4L, 6L, 7L));
    }

}
