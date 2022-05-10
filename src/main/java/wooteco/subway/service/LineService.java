package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.info.LineInfo;

@Service
public class LineService {
    private static final String ERROR_MESSAGE_DUPLICATE_NAME = "중복된 지하철 노선 이름입니다.";
    private static final String ERROR_MESSAGE_NOT_EXISTS_ID = "존재하지 않는 지하철 노선 id입니다.";

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Transactional
    public LineInfo save(LineInfo lineInfo) {
        validateNameDuplication(lineInfo.getName());
        Line line = new Line(lineInfo.getName(), lineInfo.getColor());
        Line newLine = lineDao.save(line);
        return new LineInfo(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private void validateNameDuplication(String name) {
        if (lineDao.existByName(name)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_DUPLICATE_NAME);
        }
    }

    public List<LineInfo> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
            .map(it -> new LineInfo(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
    }

    public LineInfo find(Long id) {
        validateNotExists(id);
        Line line = lineDao.find(id);
        return new LineInfo(line.getId(), line.getName(), line.getColor());
    }

    public void update(LineInfo lineInfo) {
        Long id = lineInfo.getId();
        String name = lineInfo.getName();

        validateNotExists(id);
        validateNameDuplication(name);
        Line line = new Line(id, name, lineInfo.getColor());
        lineDao.update(line);
    }

    public void delete(Long id) {
        validateNotExists(id);
        lineDao.delete(id);
    }

    private void validateNotExists(Long id) {
        if (!lineDao.existById(id)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_EXISTS_ID);
        }
    }
}
