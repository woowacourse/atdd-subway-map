package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.BusinessException;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
public class StationDaoTest {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;
    private StationDao dao;

    @BeforeEach
    void setUp() {
        dao = new StationDao(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    @DisplayName("지하철 역 저장")
    void save() {
        String name = "선릉역";
        Station station = dao.save(name);

        assertThat(station.getId()).isNotNull();
        assertThat(station.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("지하철역 조회")
    void findById() {
        Station expected = dao.save("선릉역");

        Station result = dao.findById(expected.getId());

        assertThat(result.getName()).isEqualTo("선릉역");
    }

    @Test
    @DisplayName("존재하지 않는 지하철 역 조회")
    void findByWrongId() {
        assertThatThrownBy(() -> dao.findById(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("존재하지 않는 지하철역입니다.");
    }

    @Test
    @DisplayName("노선별 지하철 조회")
    void findByLineId() {
        Station up = dao.save("선릉역");
        Station down = dao.save("구의역");
        jdbcTemplate.execute("INSERT INTO LINE(name,color) VALUES('2호선','green')");
        jdbcTemplate.execute("INSERT INTO SECTION(line_id,up_station_id,down_station_id,distance) " +
                "VALUES (1L," + up.getId() + "," + down.getId() + ",5)");

        List<Station> stations = dao.findByLineId(1L);

        List<Long> ids = stations.stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());
        List<String> names = stations.stream()
                .map(s -> s.getName())
                .collect(Collectors.toList());

        assertThat(ids).containsOnly(up.getId(), down.getId());
        assertThat(names).containsOnly("선릉역", "구의역");
    }

    @Test
    @DisplayName("지하철 역 전체 조회")
    void findAll() {
        dao.save("선릉역");
        dao.save("구의역");

        List<Station> response = dao.findAll();
        List<String> names = response.stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        assertThat(response.size()).isEqualTo(2);
        assertThat(names).containsAll(List.of("선릉역", "구의역"));
    }

    @Test
    @DisplayName("지하철역이 존재하는 경우 삭제 가능")
    void delete() {
        Station station = dao.save("선릉역");

        assertDoesNotThrow(() -> dao.delete(station.getId()));
        assertThat(dao.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("지하철역이 존재하지 않는 경우 삭제 불가능")
    void deleteEmpty() {
        assertThatThrownBy(() -> dao.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("존재하지 않는 지하철역입니다.");
    }

    @Test
    @DisplayName("존재하는 이름인 경우 true 반환")
    void isExistNameWhenTrue() {
        dao.save("선릉역");
        assertThat(dao.isExistName("선릉역")).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이름인 경우 false 반환")
    void isExistNameWhenFalse() {
        assertThat(dao.isExistName("선릉역")).isFalse();
    }

}
