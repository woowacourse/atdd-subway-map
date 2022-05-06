package wooteco.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistException;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository,
                       StationRepository stationRepository,
                       SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public Line save(LineRequest request) {
        if (lineRepository.existByName(request.getName())) {
            throw new DuplicateException(String.format("%s는 중복된 노선 이름입니다.", request.getName()));
        }

        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow();
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow();

        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        Section section = sectionRepository.save(
            new Section(line, upStation, downStation, request.getDistance()));
        line.addSection(section);
        return line;
    }

    public Line save(String name, String color) {
        if (lineRepository.existByName(name)) {
            throw new DuplicateException(String.format("%s는 중복된 노선 이름입니다.", name));
        }
        return lineRepository.save(new Line(name, color));
    }

    @Transactional(readOnly = true)
    public Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new NotExistException(String.format("%d와 동일한 ID의 노선이 없습니다.", id)));
    }

    public Line update(Long id, String name, String color) {
        Line line = findById(id);

        if (isDuplicateName(line, name)) {
            throw new DuplicateException(String.format("%s는 중복된 노선 이름입니다.", name));
        }

        return lineRepository.update(new Line(id, name, color));
    }

    private boolean isDuplicateName(Line line, String name) {
        return !line.isSameName(name) && lineRepository.existByName(name);
    }

    public void deleteById(Long id) {
        if (!lineRepository.existById(id)) {
            throw new NotExistException(String.format("%d와 동일한 ID의 노선이 없습니다.", id));
        }
        lineRepository.deleteById(id);
    }
}
