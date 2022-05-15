package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Station;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Sql("classpath:station.sql")
public class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("정상적으로 지하철역이 저장된 경우를 테스트한다.")
    void saveTest() {
        Station station = stationDao.save(new Station("선릉역"));

        assertThat(station.getName()).isEqualTo("선릉역");
    }

    @Test
    @DisplayName("중복된 지하철역을 저장하는 경우 예외를 발생시킨다.")
    void saveDuplicateTest() {
        stationDao.save(new Station("선릉역"));

        assertThatThrownBy(() -> stationDao.save(new Station("선릉역")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("정상적으로 전체 조회되는 경우를 테스트한다.")
    void findAllTest() {
        stationDao.save(new Station("선릉역"));
        stationDao.save(new Station("역삼역"));
        stationDao.save(new Station("강남역"));

        assertThat(stationDao.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("존재하지 않는 지하철 역을 삭제하는 경우를 테스트한다.")
    void deleteNotExistTest() {
        assertThatThrownBy(() -> {
            stationDao.deleteById(999999L);
        }).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("정상적으로 삭제되는 경우를 테스트한다.")
    void deleteTest() {
        stationDao.save(new Station("선릉역"));
        stationDao.save(new Station("역삼역"));
        final Station station = stationDao.save(new Station("강남역"));
        stationDao.deleteById(station.getId());

        assertThat(stationDao.findAll()).hasSize(2);
    }
}
