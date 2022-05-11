package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class StationServiceTest extends ServiceTest {

    @Autowired
    private StationService service;

    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void cleanseAndSetUp() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("service_test_fixture.sql"));
        }
    }

    @Test
    void findAll_메서드는_모든_데이터를_id_순서대로_조회() {
        List<StationResponse> actual = service.findAll();

        List<StationResponse> expected = List.of(
                new StationResponse(1L, "이미 존재하는 역 이름"),
                new StationResponse(2L, "선릉역"),
                new StationResponse(3L, "잠실역"));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("save 메서드는 데이터를 저장한다")
    @Nested
    class SaveTest {

        @Test
        void 중복되지_않는_이름인_경우_성공() {
            StationResponse actual = service.save(new StationRequest("새로운 지하철역"));

            StationResponse expected = new StationResponse(4L, "새로운 지하철역");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 중복되는_이름을_입력한_경우_예외발생() {
            assertThatThrownBy(() -> service.save(new StationRequest("이미 존재하는 역 이름")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("delete 메서드는 데이터를 삭제한다")
    @Nested
    class DeleteTest {

        @Test
        void 존재하는_데이터의_id가_입력된_경우_삭제성공() {
            service.delete(1L);

            boolean notFound = stationDao.findById(1L).isEmpty();

            assertThat(notFound).isTrue();
        }

        @Test
        void 존재하지_않는_데이터의_id가_입력된_경우_예외발생() {
            assertThatThrownBy(() -> service.delete(99999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
