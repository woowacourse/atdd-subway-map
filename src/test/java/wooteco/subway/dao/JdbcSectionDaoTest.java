package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class JdbcSectionDaoTest {
    private final Long lineId = 1L;

    private JdbcSectionDao sectionDao;
    private Section section1;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);

        jdbcTemplate.execute("drop table SECTION if exists");
        jdbcTemplate.execute("drop table LINE if exists");
        jdbcTemplate.execute("create table if not exists LINE(\n"
                + "    id bigint auto_increment not null,\n"
                + "    name varchar(255) not null unique,\n"
                + "    color varchar(20) not null,\n"
                + "    primary key(id)\n"
                + ");");
        jdbcTemplate.execute("create table if not exists SECTION(\n" +
                "    id              bigint auto_increment not null,\n" +
                "    line_id         bigint                not null,\n" +
                "    up_station_id   bigint                not null,\n" +
                "    down_station_id bigint                not null,\n" +
                "    distance        int,\n" +
                "    primary key (id),\n" +
                "    constraint line_id foreign key (line_id) references LINE (id) on delete cascade\n" +
                ");");
        jdbcTemplate.execute("insert into LINE (name, color) values ('2호선', 'bg-green-600')");
        section1 = new Section(1L, lineId, 1L, 2L, 20);
        sectionDao.save(section1);
    }

    @Test
    @DisplayName("지하철 구간을 저장한다.")
    void save() {
        final String sql = "select count(*) from SECTION";
        final int expected = 1;

        final int actual = jdbcTemplate.queryForObject(sql, Integer.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("지하철 구간을 조회한다.")
    void findById() {
        final Section expected = new Section(lineId, 2L, 3L, 10);
        final Long id = sectionDao.save(expected);

        final Section actual = sectionDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "번에 해당하는 구간이 존재하지 않습니다."));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("해당 노선의 지하철 구간을 모두 조회한다.")
    void findByLineId() {
        final Section expected = new Section(lineId, 2L, 3L, 10);
        sectionDao.save(expected);

        final List<Section> actual = sectionDao.findByLineId(lineId);

        assertThat(actual).containsAll(List.of(section1, expected));
    }

    @Test
    @DisplayName("지하철 구간을 삭제한다.")
    void delete() {
        sectionDao.delete(List.of(section1));

        List<Section> actual = sectionDao.findByLineId(lineId);

        assertThat(actual).doesNotContain(section1);
    }
}
