package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;
    private final LineDao lineDao;

    public LineService(LineRepository lineRepository,
                       SectionRepository sectionRepository,
                       StationRepository stationRepository,
                       LineDao lineDao) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
        this.lineDao = lineDao;
    }

    public LineResponse save(LineRequest request) {
        if (lineRepository.existByName(request.getName())) {
            throw new DuplicateLineNameException(request.getName());
        }

        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        sectionRepository.save(new Section(line, upStation, downStation, request.getDistance()));

        return queryById(line.getId());
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(() -> new NotFoundStationException(stationId));
    }

    public LineResponse update(Long id, LineRequest request) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new NotFoundLineException(id));

        if (isDuplicateName(line, request.getName())) {
            throw new DuplicateLineNameException(request.getName());
        }
        lineRepository.update(new Line(id, request.getName(), request.getColor()));

        return queryById(id);
    }

    private boolean isDuplicateName(Line line, String name) {
        return !line.isSameName(name) && lineRepository.existByName(name);
    }

    public void deleteById(Long id) {
        if (!lineRepository.existById(id)) {
            throw new NotFoundLineException(id);
        }
        sectionRepository.deleteByLineId(id);
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse queryById(Long id) {
        return lineDao.queryById(id)
            .orElseThrow(() -> new NotFoundLineException(id));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> queryAll() {
        return lineDao.queryAll();
    }
}
