package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@SuppressWarnings("NonAsciiCharacters")
@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("classpath:schema-test.sql")
class SectionDaoTest {

    private SectionDao dao;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        dao = new SectionDao(jdbcTemplate);
    }

    @Test
    void save_메서드는_데이터를_저장한다() {
        Section actual = dao.save(
            new Section(1L, new Station(1L, "가천대역"), new Station(2L, "태평역"), 10));

        Section expected = new Section(1L, 1L, new Station(1L, "가천대역"), new Station(2L, "태평역"), 10);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void existByLineId_메서드_해당_Line_id로_존재_하는지_확인() {
        dao.save(new Section(1L, new Station(1L, "문정역"), new Station(2L, "장지역"), 10));

        assertThat(dao.existByLineId(1L)).isTrue();
    }

    @Test
    void deleteByLineId_메서드는_Line_id로_해당_데이터를_삭제한다() {
        dao.save(new Section(1L, new Station(1L, "문정역"), new Station(2L, "장지역"), 10));
        dao.deleteByLineId(1L);

        assertThat(dao.existByLineId(1L)).isFalse();
    }

    @Test
    void saveSections_메서드는_구간들을_저장한다() {
        Sections sections = new Sections(List.of(
            new Section(1L, new Station(1L, "복정역"), new Station(2L, "가천대역"), 10),
            new Section(2L, new Station(2L, "가천대역"), new Station(3L, "태평역"), 4)
        ));
        Line line = new Line(1L, "분당선", "노란색", sections);

        dao.saveSections(line);

        assertThatCode(() -> dao.saveSections(line)).doesNotThrowAnyException();
    }
}
