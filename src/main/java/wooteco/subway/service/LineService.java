package wooteco.subway.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.badRequest.LineInfoDuplicatedException;
import wooteco.subway.exception.notFound.LineNotFoundException;
import wooteco.subway.exception.notFound.StationNotFoundException;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.StationRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public Line createLine(Line line, Long upStationId, Long downStationId, int distance) {
        if (lineRepository.findLineByName(line.getName()).isPresent()) {
            throw new LineInfoDuplicatedException();
        }

        final Station upStation =
            stationRepository.findStationById(upStationId).orElseThrow(StationNotFoundException::new);
        final Station downStation =
            stationRepository.findStationById(downStationId).orElseThrow(StationNotFoundException::new);

        final Section section = Section.create(upStation, downStation, distance);
        line.addSection(section);

        return lineRepository.save(line);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Line findLine(Long id) {
        return lineRepository.findCompleteLineById(id).orElseThrow(LineNotFoundException::new);
    }

    @Transactional
    public void update(Long id, String name, String color) {
        final Line line = lineRepository.findCompleteLineById(id)
            .orElseThrow(LineNotFoundException::new);
        if (lineRepository.findLineByNameOrColor(name, color, id).isPresent()) {
            throw new LineInfoDuplicatedException();
        }
        line.changeInfo(name, color);
        lineRepository.update(line);
    }

    @Transactional
    public void removeLine(Long id) {
        lineRepository.removeLine(id);
    }
}
