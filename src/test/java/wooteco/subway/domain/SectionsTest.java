package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.utils.exception.SectionCreateException;

public class SectionsTest {

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        Sections sections = createInitialSections("신당역", "동묘앞역");
        sections.add(createSection("동묘앞역", "창신역", 1));
        sections.add(createSection("창신역", "보문역", 1));

        assertThat(sections.getValues()).hasSize(3);
    }

    @DisplayName("이미 존재하는 구간 등록시 예외를 발생한다.")
    @Test
    void duplicateSectionException() {
        Sections sections = createInitialSections("신당역", "동묘앞역");
        assertThatThrownBy(() -> sections.add(createSection("신당역", "동묘앞역", 1)))
                .isInstanceOf(SectionCreateException.class)
                .hasMessageContaining("이미 존재하는 구간입니다.");
    }

    @DisplayName("상행 역 혹은 하행역이 상행, 혹은 하행에 존재하지 않으면 예외를 발생한다.")
    @Test
    void stationNotExistException() {
        Sections sections = createInitialSections("신당역", "동묘앞역");
        assertThatThrownBy(() -> sections.add(createSection("안암역", "보문역", 1)))
                .isInstanceOf(SectionCreateException.class)
                .hasMessageContaining("구간이 연결되지 않습니다");
    }

    @DisplayName("이미 연결된 역이 등록될 시 예외를 발생한다.")
    @Test
    void sectionAlreadyExistException() {
        Sections sections = createInitialSections("신당역", "동묘앞역");
        sections.add(createSection("동묘앞역", "창신역", 2));

        assertThatThrownBy(() -> sections.add(createSection("신당역", "창신역", 1)))
                .isInstanceOf(SectionCreateException.class)
                .hasMessageContaining("이미 존재하는 구간입니다.");
    }

    @DisplayName("구간이 존재하면 사이에 역을 등록한다.")
    @Test
    void cutIntSection() {
        Sections sections = createInitialSections("신당역", "창신역");
        sections.add(createSection("동묘앞역", "창신역", 2));

        assertAll(
                () -> assertThat(sections.getValues()).contains(createSection("신당역", "동묘앞역", 3)),
                () -> assertThat(sections.getValues()).contains(createSection("동묘앞역", "창신역", 2))
        );
    }

    @DisplayName("사이 거리보다 길이가 길면 역을 등록할 수 없다.")
    @Test
    void cutInException() {
        Sections sections = createInitialSections("신당역", "창신역");
        assertThatThrownBy(() -> sections.add(createSection("동묘앞역", "창신역", 6)))
                .isInstanceOf(SectionCreateException.class)
                .hasMessageContaining("기존의 구간보다 긴 구간은 넣을 수 없습니다.");
    }

    private Section createSection(String upName, String downName, int distance) {
        return new Section(1L, new Station(1L, upName), new Station(2L, downName), distance);
    }

    private Sections createInitialSections(String upName, String downName) {
        List<Section> initialSections = new ArrayList<>();
        Section section = createSection(upName, downName, 5);
        initialSections.add(section);
        return new Sections(initialSections);
    }
}
