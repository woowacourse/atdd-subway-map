package wooteco.subway.line;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        validateToCreateLine(lineRequest);
        LineEntity lineEntity = new LineEntity(lineRequest.getName(), lineRequest.getColor());
        LineEntity newLineEntity = lineDao.save(lineEntity);

        return new LineResponse(newLineEntity.getId(), newLineEntity.getName(),
            newLineEntity.getColor());
    }

    private void validateToCreateLine(LineRequest lineRequest) {
        if (lineDao.hasLineWithName(lineRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
        }
        if (lineDao.hasLineWithColor(lineRequest.getColor())) {
            throw new IllegalArgumentException("이미 존재하는 노선 색깔입니다.");
        }
    }

    public List<LineResponse> showLines() {
        List<LineEntity> lineEntities = lineDao.findAll();
        return lineEntities.stream()
            .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
            .collect(Collectors.toList());
    }

    public LineResponse showLine(Long id) {
        validateToExistId(id);
        LineEntity lineEntity = lineDao.findById(id);
        return new LineResponse(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    private void validateToExistId(Long id) {
        if (!lineDao.hasLineWithId(id)) {
            throw new IllegalArgumentException("존재하지 않는 ID입니다.");
        }
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        validateToUpdateLine(id, lineRequest);
        lineDao.updateById(id, new LineEntity(id, lineRequest.getName(), lineRequest.getColor()));
    }

    private void validateToUpdateLine(Long id, LineRequest lineRequest) {
        validateToExistId(id);
        validateNotToDuplicateName(id, lineRequest.getName());
        validateNotToDuplicateColor(id, lineRequest.getColor());
    }

    private void validateNotToDuplicateName(Long id, String name) {
        if (lineDao.hasLineWithNameAndWithoutId(id, name)) {
            throw new IllegalArgumentException("이미 존재하는 이름 입니다.");
        }
    }

    private void validateNotToDuplicateColor(Long id, String color) {
        if (lineDao.hasLineWithColorAndWithoutId(id, color)) {
            throw new IllegalArgumentException("이미 존재하는 색깔 입니다.");
        }
    }

    public void deleteLine(Long id) {
        validateToExistId(id);
        lineDao.deleteById(id);
    }
}
