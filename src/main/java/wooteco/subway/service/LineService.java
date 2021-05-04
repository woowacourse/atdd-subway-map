package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.LineEditRequest;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.response.LineResponse;
import wooteco.subway.dao.LineJdbcDao;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String ERROR_MESSAGE_NOT_FOUND_LINE_ID = "Id에 해당하는 노선이 없습니다.";

    private final LineJdbcDao lineJdbcDao;

    public LineService(LineJdbcDao lineJdbcDao) {
        this.lineJdbcDao = lineJdbcDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        lineJdbcDao.findByName(lineRequest.getName()).ifPresent(line -> {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        });
        Line newLine = lineJdbcDao.save(lineRequest.getName(), lineRequest.getColor());
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineJdbcDao.findAll();
        return lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(Long lineId) {
        Line foundLine = lineJdbcDao.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_LINE_ID));
        return new LineResponse(foundLine.getId(), foundLine.getName(), foundLine.getName());
    }

    public long editLine(Long lineId, LineEditRequest request) {
        return lineJdbcDao.edit(lineId, request.getColor(), request.getName());
    }

    public long deleteLine(Long lineId) {
        return lineJdbcDao.deleteById(lineId);
    }
}
