package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateNameException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(final LineRequest lineRequest) {
        validateDuplicate(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public int updateLine(final Long id, final LineRequest lineRequest) {
        validateExist(id);
        validateDuplicate(lineRequest);
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        return lineDao.update(id, line);
    }

    private void validateDuplicate(final LineRequest lineRequest) {
        if (hasDuplicateLine(lineRequest)) {
            throw new DuplicateNameException("이미 등록된 지하철 노선이름 입니다.");
        }
    }

    private boolean hasDuplicateLine(final LineRequest lineRequest) {
        return lineDao.findAll()
                .stream()
                .anyMatch(line -> line.getName().equals(lineRequest.getName()));
    }

    public List<LineResponse> findLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        try {
            Line line = lineDao.findById(id);
            return new LineResponse(line.getId(), line.getName(), line.getColor());
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException("존재하지 않는 노선입니다.");
        }
    }

    public int deleteLine(final Long id) {
        validateExist(id);
        return lineDao.delete(id);
    }

    private void validateExist(final long id) {
        try {
            lineDao.findById(id);
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException("존재하지 않는 노선입니다.");
        }
    }
}
