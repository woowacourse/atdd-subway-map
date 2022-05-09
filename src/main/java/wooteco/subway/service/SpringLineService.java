package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.notfound.NotFoundLineException;
import wooteco.subway.exception.unknown.LineDeleteFailureException;
import wooteco.subway.exception.unknown.LineUpdateFailureException;
import wooteco.subway.exception.validation.LineColorDuplicateException;
import wooteco.subway.exception.validation.LineNameDuplicateException;
import wooteco.subway.infra.dao.LineDao;
import wooteco.subway.infra.entity.LineEntity;
import wooteco.subway.service.dto.LineServiceRequest;

@Service
@Transactional(readOnly = true)
public class SpringLineService implements LineService {

    private final LineDao lineRepository;
    private final StationService stationService;

    public SpringLineService(LineDao lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    @Override
    public Line save(LineServiceRequest lineServiceRequest) {
        validate(lineServiceRequest);

        LineEntity lineEntity = new LineEntity(lineServiceRequest.getName(), lineServiceRequest.getColor());
        final LineEntity saved = lineRepository.save(lineEntity);

        return new Line(saved.getId(), saved.getName(), saved.getColor());
    }

    private void validate(LineServiceRequest lineServiceRequest) {
        validateDuplicateName(lineServiceRequest);
        validateDuplicateColor(lineServiceRequest);
    }

    private void validateDuplicateName(LineServiceRequest lineServiceRequest) {
        if (lineRepository.existsByName(lineServiceRequest.getName())) {
            throw new LineNameDuplicateException(lineServiceRequest.getName());
        }
    }

    private void validateDuplicateColor(LineServiceRequest lineServiceRequest) {
        if (lineRepository.existsByColor(lineServiceRequest.getColor())) {
            throw new LineColorDuplicateException(lineServiceRequest.getColor());
        }
    }

    @Override
    public List<Line> findAll() {
        final List<Line> lines = findAllLines();
        final List<Station> stations = stationService.findAll();
        return lines;
    }

    private List<Line> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(entity -> new Line(entity.getId(), entity.getName(), entity.getColor()))
                .collect(Collectors.toList());
    }

    @Override
    public Line findById(Long id) {
        final LineEntity lineEntity = lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundLineException(id));

        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    @Transactional
    @Override
    public void update(Long id, LineServiceRequest lineServiceRequest) {
        final LineEntity lineEntity = new LineEntity(id, lineServiceRequest.getName(), lineServiceRequest.getColor());
        final long affectedRow = lineRepository.updateById(lineEntity);

        if (affectedRow == 0) {
            throw new LineUpdateFailureException(id);
        }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        final long affectedRow = lineRepository.deleteById(id);

        if (affectedRow == 0) {
            throw new LineDeleteFailureException(id);
        }
    }
}
