package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public Line save(LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        validateDuplicateName(line);
        Line savedLine = lineDao.save(line);
        sectionService.save(Section.of(savedLine, lineRequest));
        return lineDao.save(line);
    }

    private void validateDuplicateName(Line line) {
        Optional<Line> optionalLine = lineDao.findByName(line.getName());
        if (optionalLine.isPresent()) {
            throw new IllegalArgumentException("같은 이름의 노선은 등록할 수 없습니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        Optional<Line> line = lineDao.findById(id);
        return line.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line.getName(), line.getColor());
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
