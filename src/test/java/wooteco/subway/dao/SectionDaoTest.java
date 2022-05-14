package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;

@SuppressWarnings("NonAsciiCharacters")
class SectionDaoTest extends DaoTest {

    private final StationEntity STATION1 = new StationEntity(1L, "이미 존재하는 역 이름");
    private final StationEntity STATION2 = new StationEntity(2L, "선릉역");
    private final StationEntity STATION3 = new StationEntity(3L, "잠실역");

    @Autowired
    private SectionDao dao;

    @Test
    void findAllByLineId_메서드는_lineId에_해당하는_모든_구간_데이터를_조회() {
        List<SectionEntity> actual = dao.findAllByLineId(1L);

        List<SectionEntity> expected = List.of(
                new SectionEntity(1L, STATION1, STATION2, 10),
                new SectionEntity(1L, STATION2, STATION3, 5));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_정보인_경우_데이터_생성() {
            dao.save(new SectionEntity(3L, STATION3, STATION1, 10));

            boolean created = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM section WHERE "
                            + "id = 4 AND line_id = 3 AND up_station_id = 3 AND down_station_id = 1 AND distance = 10",
                    Integer.class) > 0;

            assertThat(created).isTrue();
        }

        @Test
        void 중복되는_정보로_생성하려는_경우_예외발생() {
            SectionEntity existingSection = new SectionEntity(1L, STATION1, STATION2, 10);

            assertThatThrownBy(() -> dao.save(existingSection))
                    .isInstanceOf(DataAccessException.class);
        }
    }


    @DisplayName("delete 메서드는 데이터를 삭제한다")
    @Nested
    class DeleteTest {

        @Test
        void delete_메서드는_노선과_상행역_하행역에_부합하는_데이터를_삭제() {
            dao.delete(new SectionEntity(2L, STATION1, STATION3, 10));

            boolean exists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM section WHERE line_id = 2", Integer.class) > 0;

            assertThat(exists).isFalse();
        }

        @Test
        void 거리_정보가_틀리더라도_성공적으로_데이터_삭제() {
            dao.delete(new SectionEntity(2L, STATION1, STATION3, 99999999));

            boolean exists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM section WHERE line_id = 2", Integer.class) > 0;

            assertThat(exists).isFalse();
        }

        @Test
        void 존재하지_않는_구간_정보가_입력되더라도_결과는_동일하므로_예외_미발생() {
            SectionEntity nonExistingSection = new SectionEntity(99999L, STATION1, STATION2, 10);
            assertThatNoException()
                    .isThrownBy(() -> dao.delete(nonExistingSection));
        }
    }

    @Test
    void deleteAllByLineId_메서드는_노선에_해당되는_모든_구간_데이터를_삭제() {
        dao.deleteAllByLineId(1L);

        boolean exists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM section WHERE line_id = 1", Integer.class) > 0;

        assertThat(exists).isFalse();
    }
}
