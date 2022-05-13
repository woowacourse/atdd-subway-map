package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Station;

@JdbcTest
@Import(JdbcStationDao.class)
class StationDaoTest {
    private static final String STATION_NAME = "청구역";

    @Autowired
    private StationDao dao;

    @Test
    @DisplayName("역을 저장한다.")
    public void save() {
        // given
        Station station = new Station(STATION_NAME);
        // when
        final Station saved = dao.save(station);
        // then
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("중복된 이름을 저장하는 경우 예외를 던진다.")
    public void save_throwsExceptionWithDuplicatedName() {
        // given & when
        dao.save(new Station(STATION_NAME));
        // then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> dao.save(new Station(STATION_NAME)));
    }

    @Test
    @DisplayName("역 목록을 불러온다.")
    public void findAll() {
        // given & when
        final List<Station> stations = dao.findAll();
        // then
        assertThat(stations).hasSize(0);
    }

    @Test
    @DisplayName("역을 하나 추가한 뒤, 역 목록을 불러온다.")
    public void findAll_afterSaveOneStation() {
        // given
        dao.save(new Station(STATION_NAME));
        // when
        final List<Station> stations = dao.findAll();
        // then
        assertThat(stations).hasSize(1);
    }

    @Test
    @DisplayName("ID값으로 역을 삭제한다.")
    public void deleteById() {
        // given
        final Station saved = dao.save(new Station(STATION_NAME));
        // when
        final Long id = saved.getId();
        // then
        assertThat(dao.deleteById(id)).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 역을 삭제할 수 없다.")
    public void deleteById_doesNotExist() {
        assertThatThrownBy(() -> dao.deleteById(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("삭제하고자 하는 역이 존재하지 않습니다.");
    }
}