package wooteco.subway.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("새로 등록할 구간의 상행역과 하행역 중 노선에 이미 등록되어있는 역을 기준으로 새로운 구간을 추가한다.")
    @Test
    public void addNewSection() {
        //given
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(1L, 1L, 2L, 3L, 1));
        final Sections sections = new Sections(sectionList);

        // when & then
        final Section section = new Section(2L, 1L, 1L, 2L, 1);
        assertDoesNotThrow(() -> sections.addSection(section));
    }
}