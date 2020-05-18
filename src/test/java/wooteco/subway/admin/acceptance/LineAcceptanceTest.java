package wooteco.subway.admin.acceptance;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.admin.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

	private static final String LINE_NAME_NEW_BUNDANG = "신분당선";
	private static final String LINE_NAME_ONE = "1호선";
	private static final String LINE_NAME_TWO = "2호선";
	private static final String LINE_NAME_THREE = "3호선";
	private static final long NOT_EXIST_LINE_ID = Long.MAX_VALUE;

	@DisplayName("노선 추가/정보수정/삭제/목록조회/상세조회를 수행한다. 존재하는 노선을 추가, 존재하지 않는 노선을 삭제시, 예외발생")
	@Test
	void manageLine() {
		//when
		createLine(LINE_NAME_NEW_BUNDANG);
		createLine(LINE_NAME_ONE);
		createLine(LINE_NAME_TWO);
		createLine(LINE_NAME_THREE);
		//then
		List<LineResponse> lines = getLines();
		assertThat(lines.size()).isEqualTo(4);

		//when
		LineResponse firstLineResponse = getLine(lines.get(0).getId());
		Long firstLineId = firstLineResponse.getId();
		//then
		assertThat(firstLineResponse.getId()).isNotNull();
		assertThat(firstLineResponse.getName()).isNotNull();
		assertThat(firstLineResponse.getStartTime()).isNotNull();
		assertThat(firstLineResponse.getEndTime()).isNotNull();
		assertThat(firstLineResponse.getIntervalTime()).isNotNull();

		//when 	//then
		assertThatThrownBy(() -> createLine(LINE_NAME_THREE));

		// when
		LocalTime updateStartTime = LocalTime.of(8, 0);
		LocalTime updateEndTime = LocalTime.of(22, 0);
		updateLine(firstLineId, updateStartTime, updateEndTime);
		//then
		LineResponse updatedLine = getLine(firstLineResponse.getId());
		Long updatedLineId = firstLineResponse.getId();
		assertThat(updatedLine.getStartTime()).isEqualTo(updateStartTime);
		assertThat(updatedLine.getEndTime()).isEqualTo(updateEndTime);

		// when
		deleteLine(updatedLineId);
		List<LineResponse> linesAfterDelete = getLines();
		//then
		assertThat(linesAfterDelete.size()).isEqualTo(3);
		assertThat(getLineNames(linesAfterDelete)).containsExactly("1호선", "2호선", "3호선");

		//when	//then
		assertThatThrownBy(() -> deleteLine(NOT_EXIST_LINE_ID));
	}

	private List<String> getLineNames(List<LineResponse> linesAfterDelete) {
		return linesAfterDelete.stream()
			.map(LineResponse::getName)
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}
}
