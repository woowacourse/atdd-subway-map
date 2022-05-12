package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
@Import(SectionDao.class)
public class JdbcSectionDaoTest {

    @Autowired
    private SectionDao jdbcSectionDao;

    @DisplayName("구간을 생성한다")
    @Test
    void save() {
        // given
        Line 일호선 = new Line(1L, "1호선", "green");
        Station 그린론역 = new Station(1L, "그린론역");
        Station 토미역 = new Station(2L, "토미역");
        Section saveSection = new Section(일호선, 그린론역, 토미역, 10);

        // when
        Section save = jdbcSectionDao.save(saveSection);

        //then
        assertThat(save.getId()).isNotNull();
        assertThat(save.getUpStation()).isEqualTo(그린론역);
        assertThat(save.getDownStation()).isEqualTo(토미역);
    }

    @DisplayName("라인 Id로 구간들을 찾아온다.")
    @Test
    void findByLineId() {
        Line 일호선 = new Line(1L, "1호선", "green");
        Station 그린론역 = new Station(1L, "그린론역");
        Station 토미역 = new Station(2L, "토미역");
        Station 수달역 = new Station(2L, "수달역");

        // when
        jdbcSectionDao.save(new Section(일호선, 그린론역, 토미역, 10));
        jdbcSectionDao.save(new Section(일호선, 토미역, 수달역, 10));

        List<SectionEntity> sections = jdbcSectionDao.findByLineId(1L);
        // then

        assertThat(sections).hasSize(2);
    }

    @DisplayName("구간을 변경한다.")
    @Test
    void update() {
        Line 일호선 = new Line(1L, "1호선", "green");
        Station 그린론역 = new Station(1L, "그린론역");
        Station 토미역 = new Station(2L, "토미역");
        Station 수달역 = new Station(2L, "수달역");

        // when
        jdbcSectionDao.save(new Section(일호선, 그린론역, 토미역, 10));
        jdbcSectionDao.update(new Section(일호선, 그린론역, 수달역, 10));

        List<SectionEntity> sections = jdbcSectionDao.findByLineId(1L);
        // then
        assertThat(sections.get(0).getDownStationId()).isEqualTo(수달역.getId());
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void delete() {
        Line 일호선 = new Line(1L, "1호선", "green");
        Station 그린론역 = new Station(1L, "그린론역");
        Station 토미역 = new Station(2L, "토미역");
        Station 수달역 = new Station(2L, "수달역");

        // when
        jdbcSectionDao.save(new Section(일호선, 그린론역, 토미역, 10));
        jdbcSectionDao.save(new Section(일호선, 토미역, 수달역, 10));

        jdbcSectionDao.deleteById(1L);

        List<SectionEntity> sections = jdbcSectionDao.findByLineId(1L);
        // then
        assertThat(sections.get(0).getDownStationId()).isEqualTo(수달역.getId());
    }
}
