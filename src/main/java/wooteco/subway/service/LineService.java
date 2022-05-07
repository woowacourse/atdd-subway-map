package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.exception.BadRequestException;
import wooteco.subway.exception.NotFoundException;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse create(LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        validateDuplicateNameAndColor(line.getName(), line.getColor());
        return LineResponse.from(lineDao.save(line));
    }

    public LineResponse showById(Long id) {
        return LineResponse.from(findBy(id));
    }

    public List<LineResponse> showAll() {
        return lineDao.findAll().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public void updateById(Long id, LineUpdateRequest request) {
        validateDuplicateNameAndColor(request.getName(), request.getColor());
        Line line = findBy(id);
        line.update(request.getName(), request.getColor());
        lineDao.modifyById(id, line);
    }

    public void removeById(Long id) {
        lineDao.deleteById(id);
    }

    private Line findBy(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new NotFoundException("조회하려는 id가 존재하지 않습니다."));
    }

    private void validateDuplicateNameAndColor(String name, String color) {
        if (lineDao.existByNameAndColor(name, color)) {
            throw new BadRequestException("노선이 이름과 색상은 중복될 수 없습니다.");
        }
    }
}
