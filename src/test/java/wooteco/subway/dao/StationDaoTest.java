package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.entity.StationEntity;

@JdbcTest
class StationDaoTest {

    private static final String STATION_NAME = "청구역";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    private StationDao dao;

    @BeforeEach
    void setUp() {
        dao = new JdbcStationDao(dataSource, jdbcTemplate);
    }

    @Test
    @DisplayName("역을 저장한다.")
    public void save() {
        // given
        StationEntity entity = new StationEntity("청구역");
        // when
        final Long id = dao.save(entity);
        // then
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("중복된 이름을 저장하는 경우 빈 Optional을 돌려준다.")
    public void save_throwsExceptionWithDuplicatedName() {
        // given
        final StationEntity entity = new StationEntity("청구역");
        // when
        dao.save(entity);

        // then
        assertThatExceptionOfType(DuplicateKeyException.class)
            .isThrownBy(() -> dao.save(new StationEntity("청구역")));
    }

    @Test
    @DisplayName("ID값으로 역을 불러온다.")
    public void findById() {
        // given
        final Long savedId = dao.save(new StationEntity(STATION_NAME));

        // when
        final Optional<StationEntity> foundEntity = dao.findById(savedId);

        // then
        assertThat(foundEntity).isPresent();
    }

    @Test
    @DisplayName("역 목록을 불러온다.")
    public void findAll() {
        // given & when
        final List<StationEntity> stations = dao.findAll();
        // then
        assertThat(stations).hasSize(0);
    }

    @Test
    @DisplayName("역을 하나 추가한 뒤, 역 목록을 불러온다.")
    public void findAll_afterSaveOneStation() {
        // given
        dao.save(new StationEntity(STATION_NAME));
        // when
        final List<StationEntity> stations = dao.findAll();
        // then
        assertThat(stations).hasSize(1);
    }

    @Test
    @DisplayName("ID값으로 역을 삭제한다.")
    public void deleteById() {
        // given
        final Long id = dao.save(new StationEntity(STATION_NAME));
        // when
        final Long deletedId = dao.delete(id);
        // then
        assertThat(deletedId).isEqualTo(id);
    }

    @Test
    @DisplayName("존재하지 않는 역을 삭제하는 경우 null을 반환한다.")
    public void deleteById_doesNotExist() {
        // given
        final long id = 1L;

        // when
        final Long deletedId = dao.delete(id);

        // then
        assertThat(deletedId).isNull();
    }
}