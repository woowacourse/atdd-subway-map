package wooteco.subway.admin.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import wooteco.subway.admin.domain.Line;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;


    @Test
    void findByName() {
        Line line = new Line(1L, "haha", LocalTime.now(), LocalTime.now(), 10, "red");
        Line save = lineRepository.save(line);
        assertThat(save.getName()).isEqualTo("haha");
        Line haha = lineRepository.findByName("haha");
        assertThat(haha.getName()).isEqualTo(save.getName());
        assertThat(haha.getId()).isEqualTo(save.getId());
    }


}
