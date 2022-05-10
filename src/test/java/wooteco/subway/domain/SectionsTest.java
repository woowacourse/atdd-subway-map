package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.NotFoundException;

class SectionsTest {
    private static Section section1;
    private static Section section2;
    private static Section section3;


    @BeforeAll
    static void setUp(){
         section1 = Section.of(1L, 2L, 10);
         section2 = Section.of(2L, 3L, 10);
         section3 = Section.of(5L, 4L, 10);
    }

    @Test
    @DisplayName("상행과 하행이 이미 등록된 경우 에러를 발생시킨다")
    void checkSectionErrorByAlreadyExist() {
        Sections sections = new Sections(List.of(section1, section2));

        assertThatThrownBy(() -> sections.checkSection(section1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.");
    }

    @Test
    @DisplayName("상행과 하행이 존재하지 않는 경우 에러를 발생시킨다")
    void checkSectionErrorByNotExist() {

        Sections sections = new Sections(List.of(section1, section2));

        assertThatThrownBy(() -> sections.checkSection(section3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 모두 노선에 등록되어 있지 않습니다.");
    }

    @Test
    @DisplayName("해당 노선에 해당하는 지하철 역이 없는 경우 에러를 발생시킨다.")
    void findByStationIdNotFound() {
        Sections sections = new Sections(List.of(section1, section2));

        assertThatThrownBy(() -> sections.findByStationId(4L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 노선에서는 입력한 지하철 역을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("sections의 길이가 1인 경우 에러를 발생시킨다.")
    void checkCanDeleteErrorBySizeOne() {
        Sections sections = new Sections(List.of(section1));

        assertThatThrownBy(sections::checkCanDelete)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선은 더 삭제할 수 없습니다.");
    }
}