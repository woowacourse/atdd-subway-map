package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.domain.Section;

import static org.assertj.core.api.Assertions.*;
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

    @DisplayName("구간 생성 시 기존 일치하는 구간이 존재하는 경우 경우 예외를 발생한다.")
    @Test
    void saveSameSectionThenThrowException() {
        //given
        Section section1 = new Section(10, 1L, 1L, 2L);
        Section section2 = new Section(10, 2L, 1L, 2L);

        //when & then
        assertThatThrownBy(() -> {
            sectionService.save(section2);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기존에 존재하는 노선은 등록할 수 없습니다.");
    }

}
