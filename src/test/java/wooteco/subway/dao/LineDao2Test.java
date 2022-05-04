package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;

@SpringBootTest
@Transactional
public class LineDao2Test {

    @Autowired
    private LineDao2 lineDao2;

    @Test
    @DisplayName("이름이 일치하는 노선 조회한다.")
    void findByName() {
        //given
        String color = "bg-red-600";
        String name = "7호선";
        final Line line = new Line(name, color);
        lineDao2.save(line);

        //when
        Line foundLine = lineDao2.findByName(name);

        //then
        assertThat(foundLine.getName()).isEqualTo(name);
        assertThat(foundLine.getColor()).isEqualTo(color);
    }

    @Test
    @DisplayName("노선을 저장하면 저장된 노선 정보를 반환한다.")
    void save() {
        // given
        final Line line = new Line("7호선", "bg-red-600");

        // when
        Line savedLine = lineDao2.save(line);

        // then
        assertThat(savedLine.getName()).isEqualTo(line.getName());
        assertThat(savedLine.getColor()).isEqualTo(line.getColor());
    }
}
