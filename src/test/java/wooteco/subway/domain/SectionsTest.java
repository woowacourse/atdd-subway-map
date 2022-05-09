package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.utils.exception.SectionCreateException;

public class SectionsTest {

    @DisplayName("이미 존재하는 구간 등록시 예외를 발생한다.")
    @Test
    void duplicateSectionException() {
        Section section = createSection("신당역", "동묘앞역");
        Sections sections = new Sections(List.of(section));
        assertThatThrownBy(() -> sections.add(createSection("신당역", "동묘앞역")))
                .isInstanceOf(SectionCreateException.class);
    }

    @DisplayName("상행 역 혹은 하행역이 상행, 혹은 하행에 존재하지 않으면 예외를 발생한다.")
    @Test
    void stationNotExistException() {
        Section section = createSection("신당역", "동묘앞역");
        Sections sections = new Sections(List.of(section));
        assertThatThrownBy(() -> sections.add(createSection("안암역", "보문역")))
                .isInstanceOf(SectionCreateException.class);
    }

    private Section createSection(String upName, String downName) {
        return new Section(1L, new Station(1L, upName), new Station(2L, downName), 2);
    }
}
