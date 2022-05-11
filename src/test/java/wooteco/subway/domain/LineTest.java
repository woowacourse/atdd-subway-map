package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineTest {

    @DisplayName("상행선부터 하행선까지 지하철역을 반환한다.")
    @Test
    void getTrainsFromUp() {
        Station 강남 = new Station("강남");
        Station 양재 = new Station("양재");
        Station 양재시민의숲 = new Station("양재시민의숲");
        Station 청계산입구 = new Station("청계산입구");
        List<Section> sections = List.of(
                new Section(강남, 양재, 2),
                new Section(양재, 양재시민의숲, 2),
                new Section(양재시민의숲, 청계산입구, 2)
        );
        Line line = new Line("신분당선", "red", sections);
        assertThat(line.getTrainsFromUpLine()).containsExactly(강남, 양재, 양재시민의숲, 청계산입구);
    }
}
