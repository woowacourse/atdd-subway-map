package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.line.exception.LineExistenceException;
import wooteco.subway.line.exception.LineNotFoundException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDto;
import wooteco.subway.section.SectionService;
import wooteco.subway.section.Sections;

import java.util.Collections;
import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public Line create(LineRequest lineRequest) {
        if (isExistingLine(lineRequest.getName())) {
            throw new LineExistenceException();
        }
        Line savedLine = lineDao.save(lineRequest.getName(), lineRequest.getColor());
        Section savedSection = sectionService.initialize(SectionDto.of(savedLine.getId(), lineRequest));
        savedLine.setSections(new Sections(Collections.singletonList(savedSection)));
        return savedLine;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(LineNotFoundException::new);
    }

    public void modify(Long id, LineRequest lineRequest) {
        if (lineDao.update(id, lineRequest.getName(), lineRequest.getColor()) == 0) {
            throw new LineNotFoundException();
        }
    }

    public void delete(Long id) {
        if (lineDao.delete(id) == 0) {
            throw new LineNotFoundException();
        }
    }

    private boolean isExistingLine(String name) {
        return lineDao.findByName(name).isPresent();
    }
}
