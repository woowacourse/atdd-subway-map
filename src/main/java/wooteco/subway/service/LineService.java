package wooteco.subway.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DataDuplicationException;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.service.dto.LineDto;
import wooteco.subway.service.dto.SectionDto;

@Service
public class LineService {

    private static final int ROW_SIZE_WHEN_NOT_DELETED = 0;

    private final LineDao lineDao;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineService(LineDao lineDao, SectionService sectionService,
        StationService stationService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @Transactional
    public Line createLine(LineDto lineDto) {
        Optional<Line> foundLine = lineDao.findByName(lineDto.getName());
        if (foundLine.isPresent()) {
            throw new DataDuplicationException("이미 등록된 노선입니다.");
        }
        Line newLine = lineDao.save(lineDto.toLine());
        sectionService.createSection(SectionDto.of(newLine.getId(), lineDto));

        return new Line(newLine,
            List.of(stationService.findById(lineDto.getUpStationId()),
                stationService.findById(lineDto.getDownStationId())));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new DataNotFoundException("존재하지 않는 노선입니다."));
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
            throw new DataNotFoundException("존재하지 않는 노선입니다.");
        }
    }
}
