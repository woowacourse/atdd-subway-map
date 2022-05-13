package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import org.springframework.context.annotation.Import;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
@Import({JdbcLineDao.class, JdbcStationDao.class})
class LineDaoTest {
    private static final String LINE_NAME = "신분당선";
    private static final String LINE_COLOR = "bg-red-600";

    @Autowired
    private LineDao linDao;
    @Autowired
    private StationDao stationDao;

    private Section section;

    @BeforeEach
    void setUp() {
        Station upTermination = new Station(1L, "상행종점역");
        Station downTermination = new Station(2L, "하행종점역");
        stationDao.save(upTermination);
        stationDao.save(downTermination);
        section = new Section(upTermination, downTermination, 10);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    public void save() {
        // given
        Line Line = new Line(LINE_NAME, LINE_COLOR, section);
        // when
        final Line saved = linDao.save(Line);
        // then
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("중복된 이름을 저장하는 경우 예외를 던진다.")
    public void save_throwsExceptionWithDuplicatedName() {
        // given & when
        linDao.save(new Line(LINE_NAME, LINE_COLOR, section));
        // then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> linDao.save(new Line(LINE_NAME, LINE_COLOR, section)));
    }

    @Test
    @DisplayName("전체 노선을 조회한다.")
    public void findAll() {
        // given & when
        List<Line> lines = linDao.findAll();
        // then
        assertThat(lines).hasSize(0);
    }

    @Test
    @DisplayName("노선을 하나 추가한 뒤, 전체 노선을 조회한다")
    public void findAll_afterSaveOneLine() {
        // given
        linDao.save(new Line(LINE_NAME, LINE_COLOR, section));
        // when
        List<Line> lines = linDao.findAll();
        // then
        assertThat(lines).hasSize(1);
    }

    @Test
    @DisplayName("ID 값으로 노선을 조회한다")
    public void findById() {
        // given
        final Line saved = linDao.save(new Line(LINE_NAME, LINE_COLOR, section));
        // when
        final Line found = linDao.findById(saved.getId());
        // then
        Assertions.assertAll(
                () -> assertThat(found.getId()).isEqualTo(saved.getId()),
                () -> assertThat(found.getName()).isEqualTo(saved.getName()),
                () -> assertThat(found.getColor()).isEqualTo(saved.getColor())
        );
    }

    @Test
    @DisplayName("존재하지 않는 ID 값으로 노선을 조회하면 예외를 던진다")
    public void findById_invalidID() {
        // given & when
        linDao.save(new Line(LINE_NAME, LINE_COLOR, section));
        // then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> linDao.findById(2L));
    }

    @Test
    @DisplayName("노선 정보를 수정한다.")
    public void update() {
        // given & when
        final Line saved = linDao.save(new Line(LINE_NAME, LINE_COLOR, section));
        // then
        assertThat(linDao.update(new Line(saved.getId(), "구분당선", LINE_COLOR))).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 ID값을 수정하는 경우 예외를 던진다.")
    public void update_throwsExceptionWithInvalidId() {
        // given
        linDao.save(new Line(LINE_NAME, LINE_COLOR, section));
        // when
        Line updateLine = new Line(100L, "사랑이넘치는", "우테코");
        // then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> linDao.update(updateLine));
    }

    @Test
    @DisplayName("ID값으로 노선을 삭제한다.")
    public void delete() {
        // given & when
        Line saved = linDao.save(new Line(LINE_NAME, LINE_COLOR, section));
        // then
        assertThat(linDao.delete(saved.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지않는 ID값을 삭제하는 경우 예외를 던진다.")
    public void delete_throwsExceptionWithInvalidId() {
        // given
        linDao.save(new Line(LINE_NAME, LINE_COLOR, section));
        // when
        Long deleteId = 100L;
        // then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> linDao.delete(deleteId));
    }
}