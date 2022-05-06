package wooteco.subway.ui;

import wooteco.subway.dto.info.LineInfo;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;

public class LineConverter {
    static LineInfo toInfo(LineRequest lineRequest) {
        return new LineInfo(lineRequest.getName(), lineRequest.getColor());
    }

    static LineInfo toInfo(Long id, LineRequest lineRequest) {
        return new LineInfo(id, lineRequest.getName(), lineRequest.getColor());
    }

    static LineResponse toResponse(LineInfo lineInfo) {
        return new LineResponse(lineInfo.getId(), lineInfo.getName(), lineInfo.getColor());
    }
}
