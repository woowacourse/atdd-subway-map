package wooteco.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
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

    public LineService(LineRepository lineRepository,
                       SectionRepository sectionRepository,
                       StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public Line save(LineRequest request) {
        if (lineRepository.existByName(request.getName())) {
            throw new DuplicateException(String.format(DUPLICATE_MESSAGE, request.getName()));
        }

        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        Section section = sectionRepository.save(
            new Section(line, upStation, downStation, request.getDistance()));

        line.addSection(section);
        return line;
    }

    public Line save(String name, String color) {
        if (lineRepository.existByName(name)) {
            throw new DuplicateException(String.format(DUPLICATE_MESSAGE, name));
        }
        return lineRepository.save(new Line(name, color));
    }

    @Transactional(readOnly = true)
    public Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }

    public Line update(Long id, String name, String color) {
        Line line = findById(id);

        if (isDuplicateName(line, name)) {
            throw new DuplicateException(String.format(DUPLICATE_MESSAGE, name));
        }

        return lineRepository.update(new Line(id, name, color));
    }

    private boolean isDuplicateName(Line line, String name) {
        return !line.isSameName(name) && lineRepository.existByName(name);
    }

    public void deleteById(Long id) {
        if (!lineRepository.existById(id)) {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
        lineRepository.deleteById(id);
    }
}
