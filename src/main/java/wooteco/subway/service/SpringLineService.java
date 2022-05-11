package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.notfound.NotFoundLineException;
import wooteco.subway.exception.unknown.LineDeleteFailureException;
import wooteco.subway.exception.unknown.LineUpdateFailureException;
import wooteco.subway.exception.validation.LineColorDuplicateException;
import wooteco.subway.exception.validation.LineNameDuplicateException;
import wooteco.subway.infra.repository.LineRepository;
import wooteco.subway.service.dto.LineServiceRequest;

@Service
@Transactional
public class SpringLineService implements LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;
    private final StationService stationService;

    public SpringLineService(LineRepository lineRepository, SectionService sectionService,
                             StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @Override
    public Line save(LineServiceRequest lineServiceRequest) {
        validate(lineServiceRequest);

        final Station upStation = stationService.findById(lineServiceRequest.getUpStationId());
        final Station downStation = stationService.findById(lineServiceRequest.getDownStationId());
        final Section section = new Section(upStation, downStation, lineServiceRequest.getDistance());

        final Line saveRequest = new Line(lineServiceRequest.getName(), lineServiceRequest.getColor(),
                new Sections(List.of(section)));

        return lineRepository.save(saveRequest);
    }

    private void validate(LineServiceRequest lineServiceRequest) {
        validateDuplicateName(lineServiceRequest);
        validateDuplicateColor(lineServiceRequest);
    }

    private void validateDuplicateName(LineServiceRequest lineServiceRequest) {
        if (lineRepository.existByName(lineServiceRequest.getName())) {
            throw new LineNameDuplicateException(lineServiceRequest.getName());
        }
    }

    private void validateDuplicateColor(LineServiceRequest lineServiceRequest) {
        if (lineRepository.existByColor(lineServiceRequest.getColor())) {
            throw new LineColorDuplicateException(lineServiceRequest.getColor());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Line findById(Long id) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundLineException(id));
        final Sections sections = sectionService.findByLineId(id);

        return Line.of(line, sections);
    }

    @Override
    public void update(Long id, LineServiceRequest lineServiceRequest) {
        final Line updateRequest = new Line(id, lineServiceRequest.getName(), lineServiceRequest.getColor());
        validate(id, lineServiceRequest);

        final long affectedRow = lineRepository.update(updateRequest);
        if (affectedRow == 0) {
            throw new LineUpdateFailureException(id);
        }
    }

    private void validate(Long id, LineServiceRequest lineServiceRequest) {
        if (!lineRepository.existById(id)) {
            throw new NotFoundLineException(id);
        }
        if (lineRepository.existSameNameWithDifferentId(lineServiceRequest.getName(), id)) {
            throw new LineNameDuplicateException(lineServiceRequest.getName());
        }
    }

    @Override
    public void deleteById(Long id) {
        final long affectedRow = lineRepository.deleteById(id);

        if (affectedRow == 0) {
            throw new LineDeleteFailureException(id);
        }
    }
}
