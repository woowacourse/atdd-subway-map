package wooteco.subway.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DataDuplicationException;
import wooteco.subway.exception.DataNotExistException;
import wooteco.subway.service.dto.SectionDto;

@Service
public class LineService {

    private static final int ROW_SIZE_WHEN_NOT_DELETED = 0;

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public Line createLine(Line line, SectionDto sectionDto) {
        Optional<Line> foundLine = lineDao.findByName(line.getName());
        if (foundLine.isPresent()) {
            throw new DataDuplicationException("이미 등록된 노선입니다.");
        }
        Line newLine = lineDao.save(line);
        sectionService.createSection(sectionDto.withLineId(newLine.getId()));
        return newLine;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new DataNotExistException("존재하지 않는 노선입니다."));
    }

    public void update(Line line) {
        Optional<Line> foundLine = lineDao.findByName(line.getName());
        if (foundLine.isPresent() && !line.hasSameId(foundLine.get())) {
            throw new DataDuplicationException("이미 등록된 노선입니다.");
        }
        lineDao.update(line);
    }

    public void deleteById(Long id) {
        if (lineDao.deleteById(id) == ROW_SIZE_WHEN_NOT_DELETED) {
            throw new DataNotExistException("존재하지 않는 노선입니다.");
        }
    }
}
