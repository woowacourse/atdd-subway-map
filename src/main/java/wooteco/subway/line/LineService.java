package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.line.exception.LineExistenceException;
import wooteco.subway.line.exception.LineNotFoundException;
import wooteco.subway.section.SectionDto;
import wooteco.subway.section.SectionService;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public Line createLine(LineRequest lineRequest) {
        if (isExistingLine(lineRequest.getName())) {
            throw new LineExistenceException();
        }
        Line savedLine = lineDao.save(lineRequest.getName(), lineRequest.getColor());
        sectionService.save(SectionDto.of(savedLine.getId(), lineRequest));
        return savedLine;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(LineNotFoundException::new);
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        if (lineDao.update(id, lineRequest.getName(), lineRequest.getColor()) == 0) {
            throw new LineNotFoundException();
        }
    }

    public void deleteLine(Long id) {
        if (lineDao.delete(id) == 0) {
            throw new LineNotFoundException();
        }
    }

    private boolean isExistingLine(String name) {
        return lineDao.findByName(name).isPresent();
    }
}
