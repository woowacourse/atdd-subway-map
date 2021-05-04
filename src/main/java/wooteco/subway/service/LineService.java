package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.LineEditRequest;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.response.LineResponse;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String ERROR_MESSAGE_NOT_FOUND_LINE_ID = "Id에 해당하는 노선이 없습니다.";

    public LineResponse createLine(LineRequest lineRequest) {
        Line newLine = LineDao.save(lineRequest.getName(), lineRequest.getColor());
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> showLines() {
        List<Line> lines = LineDao.findAll();
        return lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(Long lineId) {
        Line foundLine = LineDao.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_LINE_ID));
        return new LineResponse(foundLine.getId(), foundLine.getName(), foundLine.getName());
    }

    public long editLine(Long lineId, LineEditRequest request) {
        return LineDao.edit(lineId, request.getColor(), request.getName());
    }

    public long deleteLine(Long lineId) {
        return LineDao.deleteById(lineId);
    }
}
