package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.entity.LineEntity;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NoLineFoundException;
import wooteco.subway.service.dto.LineServiceRequest;

@Service
public class SpringLineService implements LineService {

    private final LineRepository lineRepository;

    public SpringLineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    @Override
    public Line save(LineServiceRequest lineServiceRequest) {
        LineEntity lineENtity = new LineEntity(null, lineServiceRequest.getName(), lineServiceRequest.getColor());

        final LineEntity lineEntity = lineRepository.save(lineENtity);

        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Line> findAll() {
        final List<LineEntity> lineEntities = lineRepository.findAll();

        return lineEntities.stream()
                .map(entity -> new Line(entity.getId(), entity.getName(), entity.getColor()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Line findById(Long id) {
        final LineEntity lineEntity = lineRepository.findById(id)
                .orElseThrow(NoLineFoundException::new);

        return new Line(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor());
    }

    @Transactional
    @Override
    public void update(Long id, LineServiceRequest lineServiceRequest) {
        final LineEntity lineEntity = new LineEntity(id, lineServiceRequest.getName(), lineServiceRequest.getColor());
        final long affectedRow = lineRepository.updateById(lineEntity);

        if (affectedRow == 0) {
            throw new NoSuchElementException();
        }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        final long affectedRow = lineRepository.deleteById(id);

        if (affectedRow == 0) {
            throw new NoSuchElementException();
        }
    }
}
