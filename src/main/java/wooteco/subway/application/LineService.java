package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.exception.DuplicateLineNameException;
import wooteco.subway.application.exception.NotFoundLineException;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionEdge;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationService stationService;
    private final LineDao lineDao;

    public LineService(LineRepository lineRepository,
                       SectionRepository sectionRepository,
                       StationService stationService,
                       LineDao lineDao) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
        this.lineDao = lineDao;
    }

    @Transactional
    public Line save(LineRequest request) {
        if (lineRepository.existByName(request.getName())) {
            throw new DuplicateLineNameException(request.getName());
        }

        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        SectionEdge edge = new SectionEdge(upStation.getId(), downStation.getId(),
            request.getDistance());
        Section section = new Section(line.getId(), edge);
        sectionRepository.save(section);
        return line;
    }

    public Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NotFoundLineException(id));
    }

    @Transactional
    public Line update(Long id, LineRequest request) {
        Line line = findById(id);

        if (isDuplicateName(line, request.getName())) {
            throw new DuplicateLineNameException(request.getName());
        }

        return lineRepository.update(new Line(id, request.getName(), request.getColor()));
    }

    private boolean isDuplicateName(Line line, String name) {
        return !line.isSameName(name) && lineRepository.existByName(name);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!lineRepository.existById(id)) {
            throw new NotFoundLineException(id);
        }
        sectionRepository.deleteByLineId(id);
        lineRepository.deleteById(id);
    }

    public LineResponse getById(Long id) {
        return lineDao.queryById(id, new UpwardSorter())
            .orElseThrow(() -> new NotFoundLineException(id));
    }

    public List<LineResponse> getAll() {
        return lineDao.queryAll(new UpwardSorter());
    }
}
