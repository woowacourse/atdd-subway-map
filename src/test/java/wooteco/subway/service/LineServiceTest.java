package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.error.exception.NotFoundException;

@SpringBootTest
@Sql("/truncate.sql")
class LineServiceTest {

    private final LineService lineService;
    private final StationDao stationDao;

    @Autowired
    public LineServiceTest(LineService lineService, StationDao stationDao) {
        this.lineService = lineService;
        this.stationDao = stationDao;
    }

    @DisplayName("노선 저장과 관련된 기능")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromStationSave() {
        Station upStation = generateStation("선릉역");
        Station downStation = generateStation("잠실역");

        return Stream.of(
                dynamicTest("노선을 저장한다.", () -> {
                    String name = "2호선";
                    String color = "bg-green-600";
                    LineRequest lineRequest = new LineRequest(
                            name, color, upStation.getId(), downStation.getId(), 10);

                    LineResponse lineResponse = lineService.save(lineRequest);

                    assertAll(
                            () -> assertThat(lineResponse.getName()).isEqualTo(name),
                            () -> assertThat(lineResponse.getColor()).isEqualTo(color),
                            () -> assertThat(lineResponse.getStations().size()).isEqualTo(2)
                    );
                }),

                dynamicTest("중복된 이름의 노선을 저장할 경우 예외를 발생시킨다.", () -> {
                    String name = "2호선";
                    String color = "bg-green-600";
                    LineRequest lineRequest = new LineRequest(
                            name, color, upStation.getId(), downStation.getId(), 10);

                    assertThatThrownBy(() -> lineService.save(lineRequest))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("노선의 저장 시 상행과 하행이 같은 지하철역인 경우 예외를 던진다.", () -> {
                    LineRequest lineRequest = new LineRequest(
                            "2호선", "bg-green-600", 1L, 1L, 10);

                    assertThatThrownBy(() -> lineService.save(lineRequest))
                            .isInstanceOf(IllegalArgumentException.class);
                })
        );
    }

    @DisplayName("존재하지 않는 지하철역을 활용하여 노선을 등록할 경우 예외를 던진다.")
    @Test
    void 존재하지_않는_지하철역으로_노선_등록_예외발생() {
        LineRequest lineRequest = new LineRequest(
                "2호선", "bg-green-600", 1L, 2L, 10);

        assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("노선 조작과 관련된 기능")
    @TestFactory
    Stream<DynamicTest> dynamicTestFromStationManipulate() {
        Station upStation = generateStation("선릉역");
        Station downStation = generateStation("잠실역");

        String name1 = "2호선";
        String color1 = "bg-green-600";
        Integer distance1 = 7;
        LineResponse lineResponse1 = lineService.save(
                new LineRequest(name1, color1, upStation.getId(), downStation.getId(), distance1));

        Station upStation2 = generateStation("중동역");
        Station downStation2 = generateStation("신도림역");

        String name2 = "1호선";
        String color2 = "bg-blue-600";
        Integer distance2 = 10;
        LineResponse lineResponse2 = lineService.save(
                new LineRequest(name2, color2, upStation2.getId(), downStation2.getId(), distance2));

        return Stream.of(
                dynamicTest("노선을 조회한다.", () -> {
                    assertAll(
                            () -> assertThat(lineResponse1.getName()).isEqualTo(name1),
                            () -> assertThat(lineResponse1.getColor()).isEqualTo(color1)
                    );
                }),

                dynamicTest("다수의 노선을 조회한다.", () -> {
                    List<LineResponse> lines = lineService.findAll();

                    assertThat(lines.size()).isEqualTo(2);
                }),

                dynamicTest("존재하는 노선을 수정한다.", () -> {
                    String updateColor = "bg-blue-600";
                    LineRequest updateRequest = new LineRequest(name1, updateColor);
                    lineService.update(lineResponse1.getId(), updateRequest);

                    LineResponse updatedResponse = lineService.findById(lineResponse1.getId());
                    assertAll(
                            () -> assertThat(updatedResponse.getName()).isEqualTo(name1),
                            () -> assertThat(updatedResponse.getColor()).isEqualTo(updateColor)
                    );
                }),

                dynamicTest("중복된 이름을 가진 노선으로 수정할 경우 예외를 던진다.", () -> {
                    String updateName = "2호선";
                    String updateColor = "bg-green-600";
                    LineRequest updateRequest = new LineRequest(updateName, updateColor);

                    assertThatThrownBy(() -> lineService.update(lineResponse2.getId(), updateRequest))
                            .isInstanceOf(IllegalArgumentException.class);
                }),

                dynamicTest("노선을 삭제한다.", () -> {
                    lineService.deleteById(lineResponse1.getId());
                    lineService.deleteById(lineResponse2.getId());

                    assertThat(lineService.findAll().size()).isEqualTo(0);
                })
        );
    }

    @DisplayName("존재하지 않는 노선을 조회할 경우 예외를 발생시킨다.")
    @Test
    void 존재하지_않는_노선_조회_예외발생() {
        assertThatThrownBy(() -> lineService.findById(0L))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("존재하지 않는 노선을 수정하는 경우 예외를 던진다.")
    @Test
    void 존재하지_않는_노선_수정_예외발생() {
        String name = "2호선";
        String updateColor = "bg-blue-600";
        LineRequest updateRequest = new LineRequest(name, updateColor);

        assertThatThrownBy(() -> lineService.update(0L, updateRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("존재하지 않는 노선을 삭제하는 경우 예외를 던진다.")
    @Test
    void 존재하지_않는_노선_삭제_예외발생() {
        assertThatThrownBy(() -> lineService.deleteById(0L))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("특정 노선의 구간을 추가한다.")
    @Test
    void 구간_추가() {
        Station upStation1 = generateStation("선릉역");
        Station downStation1 = generateStation("잠실역");
        Integer distance1 = 10;
        LineResponse line = generateLine(
                "2호선", "bg-green-600", upStation1.getId(), downStation1.getId(), distance1);

        Station upStation2 = generateStation("신대방역");
        Station downStation2 = upStation1;
        Integer distance2 = 7;
        SectionRequest sectionRequest2 = new SectionRequest(upStation2.getId(), downStation2.getId(), distance2);

        lineService.addSection(line.getId(), sectionRequest2);

        LineResponse lineResponse = lineService.findById(line.getId());
        assertThat(lineResponse.getStations().size()).isEqualTo(3);
    }

    @DisplayName("특정 노선에 모두 존재하는 구간 추가 시 예외가 발생한다.")
    @Test
    void 존재하는_구간_추가_예외발생() {
        Station upStation1 = generateStation("선릉역");
        Station downStation1 = generateStation("잠실역");
        Integer distance1 = 10;
        LineResponse line = generateLine(
                "2호선", "bg-green-600", upStation1.getId(), downStation1.getId(), distance1);

        Station upStation2 = downStation1;
        Station downStation2 = upStation1;
        Integer distance2 = 7;
        SectionRequest sectionRequest2 = new SectionRequest(upStation2.getId(), downStation2.getId(), distance2);

        assertThatThrownBy(() -> lineService.addSection(line.getId(), sectionRequest2))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("특정 노선에 존재하지 않는 구간 추가 시 예외가 발생한다.")
    @Test
    void 존재하지_않는_구간_추가_예외발생() {
        Station upStation1 = generateStation("선릉역");
        Station downStation1 = generateStation("잠실역");
        Integer distance1 = 10;
        LineResponse line = generateLine(
                "2호선", "bg-green-600", upStation1.getId(), downStation1.getId(), distance1);

        Station upStation2 = generateStation("신대방역");
        Station downStation2 = generateStation("신림역");
        Integer distance2 = 7;
        SectionRequest sectionRequest2 = new SectionRequest(upStation2.getId(), downStation2.getId(), distance2);

        assertThatThrownBy(() -> lineService.addSection(line.getId(), sectionRequest2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private LineResponse generateLine(String name, String color, Long upStationId, Long downStationId,
                                      Integer distance) {
        return lineService.save(new LineRequest(name, color, upStationId, downStationId, distance));
    }

    private Station generateStation(String name) {
        return stationDao.save(new Station(name));
    }
}
