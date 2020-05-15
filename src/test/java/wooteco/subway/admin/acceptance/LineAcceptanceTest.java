package wooteco.subway.admin.acceptance;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.admin.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선을 관리한다")
	@Test
	void manageLine() {
		// when
		createLine("신분당선");
		createLine("1호선");
		createLine("2호선");
		createLine("3호선");
		// then
		List<LineResponse> lines = getLines();
		assertThat(lines.size()).isEqualTo(4);

		// when
		LineResponse firstLineResponse = getLine(lines.get(0).getId());
		Long firstLineId = firstLineResponse.getId();

		// then
		assertThat(firstLineResponse.getId()).isNotNull();
		assertThat(firstLineResponse.getName()).isNotNull();
		assertThat(firstLineResponse.getStartTime()).isNotNull();
		assertThat(firstLineResponse.getEndTime()).isNotNull();
		assertThat(firstLineResponse.getIntervalTime()).isNotNull();

		assertThatThrownBy(() -> createLine("3호선"));

		// when
		LocalTime startTime = LocalTime.of(8, 0);
		LocalTime endTime = LocalTime.of(22, 0);
		updateLine(firstLineId, startTime, endTime);

		//then
		LineResponse updatedLineResponse = getLine(firstLineResponse.getId());
		Long updatedLineId = firstLineResponse.getId();
		assertThat(updatedLineResponse.getStartTime()).isEqualTo(startTime);
		assertThat(updatedLineResponse.getEndTime()).isEqualTo(endTime);

		// when
		deleteLine(updatedLineId);
		List<LineResponse> linesAfterDelete = getLines();

		//then
		assertThat(linesAfterDelete.size()).isEqualTo(3);
		assertThat(getLineNames(linesAfterDelete)).containsExactly("1호선", "2호선", "3호선");

		//when
		long nonExistLineId = Long.MAX_VALUE;

		//then
		assertThatThrownBy(() -> deleteLine(nonExistLineId));
	}

	private List<String> getLineNames(List<LineResponse> linesAfterDelete) {
		return linesAfterDelete.stream().map(LineResponse::getName).collect(toList());
	}
}
