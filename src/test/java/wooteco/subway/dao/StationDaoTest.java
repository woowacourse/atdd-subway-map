package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import wooteco.subway.domain.Station;

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

    private StationDao dao;

    @BeforeEach
    void setUp() {
        dao = new StationDao(dataSource);
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
    @DisplayName("지하철 역 조회")
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
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessageContaining("존재하지 않는 지하철역입니다.");
    }

    @Test
    @DisplayName("존재하는 이름인 경우 true 반환")
    void isExistNameWhenTrue(){
        dao.save("선릉역");
        assertThat(dao.isExistName("선릉역")).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이름인 경우 false 반환")
    void isExistNameWhenFalse(){
        assertThat(dao.isExistName("선릉역")).isFalse();
    }

}
