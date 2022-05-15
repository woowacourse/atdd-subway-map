package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionBufferTest {

    private SectionBuffer sectionBuffer;

    @BeforeEach
    void setUp() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Section section1 = new Section.Builder(null, station1, station2, 3)
                .build();
        Section section2 = new Section.Builder(null, station1, station2, 3)
                .build();
        Section section3 = new Section.Builder(null, station1, station2, 3)
                .build();
        Section section4 = new Section.Builder(null, station1, station2, 3)
                .build();

        sectionBuffer = new SectionBuffer();
        sectionBuffer.addToAddBuffer(section1);
        sectionBuffer.addToAddBuffer(section2);
        sectionBuffer.addToDeleteBuffer(section3);
        sectionBuffer.addToDeleteBuffer(section4);
    }

    @DisplayName("addBuffer를 가져오면 addBuffer가 초기화된다.")
    @Test
    void getAddBuffer() {
        List<Section> buffer = sectionBuffer.getAddBuffer();

        assertThat(buffer.size()).isEqualTo(2);
    }

    @DisplayName("deleteBuffer를 가져오면 deleteBuffer가 초기화된다.")
    @Test
    void getDeleteBuffer() {
        List<Section> buffer = sectionBuffer.getDeleteBuffer();

        assertThat(buffer.size()).isEqualTo(2);
    }
}
