//package wooteco.subway.admin.acceptance;
//
//import io.restassured.RestAssured;
//import io.restassured.specification.RequestSpecification;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.test.context.jdbc.Sql;
//import wooteco.subway.admin.domain.Station;
//import wooteco.subway.admin.dto.LineResponse;
//
//import java.util.List;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Sql("/truncate.sql")
//public class LineStationAcceptanceTest {
//	@LocalServerPort
//	int port;
//
//	@BeforeEach
//	void setUp() {
//		RestAssured.port = port;
//	}
//
//	public static RequestSpecification given() {
//		return RestAssured.given().log().all();
//	}
//
//	/**
//	 * Given 지하철역이 여러 개 추가되어있다.
//	 * And 지하철 노선이 추가되어있다.
//	 * <p>
//	 * When 지하철 노선에 지하철역을 등록하는 요청을 한다.
//	 * Then 지하철역이 노선에 추가 되었다.
//	 * <p>
//	 * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
//	 * Then 지하철역 목록을 응답 받는다.
//	 * And 새로 추가한 지하철역을 목록에서 찾는다.
//	 * <p>
//	 * When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
//	 * Then 지하철역이 노선에서 제거 되었다.
//	 * <p>
//	 * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
//	 * Then 지하철역 목록을 응답 받는다.
//	 * And 제외한 지하철역이 목록에 존재하지 않는다.
//	 */
//	@DisplayName("지하철 노선에서 지하철역 추가 / 제외")
//	@Test
//	void manageLineStation() {
////        /**
////         *     Given 지하철역이 여러 개 추가되어있다.
//		createStation("종로");
//		createStation("잠실");
//		createStation("석촌");
//		createStation("암사");
////         *     And 지하철 노선이 추가되어있다.
//		createLine("1호선");
//		createLine("2호선");
//		createLine("8호선");
////         *
////         *     When 지하철 노선에 지하철역을 등록하는 요청을 한다.
//		addStationOnLine(preStationId, stationId, lineId);
////         *     Then 지하철역이 노선에 추가 되었다.
//        LineResponse lineResponse = getLineBy(lineId);
////         *
////         *     When 지하철 노선의 지하철역 목록 조회 요청을 한다.
//        //         *     Then 지하철역 목록을 응답 받는다.
//        List<LineStationResponse> lineStationResponses = getLineStaionBy(lineId);
////         *     And 새로 추가한 지하철역을 목록에서 찾는다.
//        LineStationResponse lineStationResponse = lineStationResponses.get(0);
////         *
////         *     When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
//		List<LineStationResponse> lineStationResponses = getLineStaionBy(lineId);
//		lineStationResponses.deleteStationBy(stationId); // response가 맞을까
////         *     Then 지하철역이 노선에서 제거 되었다.
//		lineStationResponses.size() == n-1;
////         *
////         *     When 지하철 노선의 지하철역 목록 조회 요청을 한다.
////         *     Then 지하철역 목록을 응답 받는다.
////         *     And 제외한 지하철역이 목록에 존재하지 않는다.
////         */
//	}
//}
