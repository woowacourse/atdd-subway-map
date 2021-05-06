package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

@JdbcTest
public class StationDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        this.stationDao = new StationDao(jdbcTemplate);
    }


    @DisplayName("station 저장 테스트")
    @Test
    void save() {
        assertThat(stationDao.save(new Station("송파역"))).isEqualTo(1L);
    }

    @DisplayName("station 저장 실패 테스트")
    @Test
    void failSave() {
        stationDao.save(new Station("송파역"));
        assertThatThrownBy(() -> {
            stationDao.save(new Station("송파역"));
        })
            .isInstanceOf(DuplicateKeyException.class);
    }
}
