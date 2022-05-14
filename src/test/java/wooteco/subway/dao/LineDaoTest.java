package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.entity.LineEntity;

@JdbcTest
class LineDaoTest {

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;

    private Line testLine1;
    private Line testLine1SameName;
    private Line testLine2;
    private Line testLine3;

    @Autowired
    private LineDaoTest(JdbcTemplate jdbcTemplate) {
        this.lineDao = new LineDao(jdbcTemplate);
        this.stationDao = new StationDao(jdbcTemplate);
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @BeforeEach
    public void setUp() {
        station1 = stationDao.save(new Station("강남역"));
        station2 = stationDao.save(new Station("역삼역"));
        station3 = stationDao.save(new Station("선릉역"));
        station4 = stationDao.save(new Station("삼성역"));
        station5 = stationDao.save(new Station("종합운동장역"));
        station6 = stationDao.save(new Station("잠실새내역"));

        testLine1 = new Line("testName", "black", station1, station2, 10L);
        testLine1SameName = new Line("testName", "black", station1, station2, 10L);
        testLine2 = new Line("testName2", "white", station3, station4, 10L);
        testLine3 = new Line("testName3", "black", station5, station6, 10L);
    }

    @DisplayName("중복되는 노선 이름이 없을 때 성공적으로 저장되는지 테스트")
    @Test
    void save_success() {
        List<LineEntity> findLines = lineDao.findAll();
        int beforeSize = findLines.size();

        lineDao.save(testLine2);

        assertThat(lineDao.findAll().size()).isEqualTo(beforeSize + 1);
    }

    @DisplayName("중복되는 노선 이름이 있을 때 예외 반환 테스트")
    @Test
    void save_fail() {
        lineDao.save(testLine1);
        assertThatThrownBy(() -> lineDao.save(testLine1SameName))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("존재하는 노선 id가 있으면 삭제되는지 테스트")
    @Test
    void deleteById_exist() {
        LineEntity line = lineDao.save(testLine1);
        Long deleteId = lineDao.deleteById(line.getId());
        assertThat(lineDao.findById(deleteId).isEmpty()).isTrue();
    }

    @DisplayName("존재하는 노선 id가 없으면 삭제되지 않는지 테스트")
    @Test
    void deleteById_not_exist() {
        lineDao.save(testLine1);
        lineDao.deleteById(-1L);
        assertThat(lineDao.findAll().isEmpty()).isFalse();
    }

    @DisplayName("존재하는 노선 id가 있으면 결과값이 존재하는지 테스트")
    @Test
    void findById_exist() {
        LineEntity line = lineDao.save(testLine1);
        Optional<LineEntity> result = lineDao.findById(line.getId());
        assertThat(result.get()).isNotNull();
    }

    @DisplayName("존재하는 노선 id가 없으면 빈 옵셔널 반환하는지 테스트")
    @Test
    void findById_not_exist() {
        lineDao.save(testLine1);
        assertThat(lineDao.findById(-1L).isEmpty()).isTrue();
    }

    @DisplayName("바뀐 이름이 중복될 때 예외가 발생하는지 테스트")
    @Test
    void changeLineName_duplicate() {
        LineEntity line = lineDao.save(testLine1);
        lineDao.save(testLine3);
        assertThatThrownBy(() -> lineDao.changeLineName(line.getId(), testLine3.getName()))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("id가 있고 바뀐 이름이 중복되지 않을 때 예외가 발생하지 않는지 테스트")
    @Test
    void changeLineName_success() {
        LineEntity line = lineDao.save(testLine1);
        lineDao.save(testLine3);

        lineDao.changeLineName(line.getId(), "testName4");

        assertThat(lineDao.findById(line.getId()).get().getName()).isEqualTo("testName4");
    }

    @DisplayName("원래 자신의 이름으로 바꿨을 때 예외가 발생하지 않는지 테스트")
    @Test
    void changeLineName_self_loop() {
        LineEntity line = lineDao.save(testLine1);
        lineDao.save(testLine3);
        lineDao.changeLineName(line.getId(), "testName");
        assertThat(line.getName()).isEqualTo("testName");
    }
}
