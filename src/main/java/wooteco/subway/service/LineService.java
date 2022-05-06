package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.ClientException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        try {
            Line newLine = lineDao.save(lineRequest);
            return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        } catch (DataAccessException exception) {
            throw new ClientException("이미 등록된 지하철노선입니다.");
        }
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineDao.find(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public int updateLine(Long id, LineRequest lineRequest) {
        try {
            return lineDao.update(id, lineRequest);
        } catch (DataAccessException exception) {
            throw new ClientException("등록된 지하철노선으로 변경할 수 없습니다.");
        }
    }

    public int deleteLine(Long id) {
        return lineDao.delete(id);
    }
}
