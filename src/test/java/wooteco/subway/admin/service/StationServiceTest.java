package wooteco.subway.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.subway.admin.dto.request.StationCreateRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class StationServiceTest {
    @Autowired
    private StationService stationService;

    private Constructor<StationCreateRequest> declaredConstructor;
    private Field declaredField;
    private StationCreateRequest request;

    @BeforeEach
    void setUp() throws Exception {
        declaredConstructor = StationCreateRequest.class.getDeclaredConstructor();
        declaredField = StationCreateRequest.class.getDeclaredField("name");
        declaredConstructor.setAccessible(true);
        declaredField.setAccessible(true);

        request = declaredConstructor.newInstance();
    }

    @DisplayName("잘못된 역 이름이 입력된 경우")
    @ParameterizedTest
    @ValueSource(strings = {"", "잠실1역", " "})
    void createStation(String stationName) throws Exception {
        declaredField.set(request, stationName);

        assertThatThrownBy(() -> stationService.createStation(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}