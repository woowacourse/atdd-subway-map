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
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;

@Service
@Transactional
public class LineService {

    public static final String DUPLICATE_MESSAGE = "%s는 중복된 노선 이름입니다.";
    public static final String NOT_FOUND_MESSAGE = "%d와 동일한 ID의 노선이 없습니다.";

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationService stationService;
    private final LineDao lineDao;

    public LineService(LineRepository lineRepository,
                       SectionRepository sectionRepository,
                       StationService stationService, LineDao lineDao) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
        this.lineDao = lineDao;
    }

    public LineResponse save(LineRequest request) {
        if (lineRepository.existByName(request.getName())) {
            throw new DuplicateException(String.format(DUPLICATE_MESSAGE, request.getName()));
        }

        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        sectionRepository.save(new Section(line, upStation, downStation, request.getDistance()));
        return queryById(line.getId());
    }

    public LineResponse update(Long id, LineRequest request) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));

        if (isDuplicateName(line, request.getName())) {
            throw new DuplicateException(String.format(DUPLICATE_MESSAGE, request.getName()));
        }
        lineRepository.update(new Line(id, request.getName(), request.getColor()));

        return queryById(id);
    }

    private boolean isDuplicateName(Line line, String name) {
        return !line.isSameName(name) && lineRepository.existByName(name);
    }

    public void deleteById(Long id) {
        if (!lineRepository.existById(id)) {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
        sectionRepository.deleteByLineId(id);
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse queryById(Long id) {
        return lineDao.queryById(id)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> queryAll() {
        return lineDao.queryAll();
    }
}
