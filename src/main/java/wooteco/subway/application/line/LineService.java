package wooteco.subway.application.line;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.exception.line.DuplicateLineException;
import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.Sections;
import wooteco.subway.domain.line.value.line.LineColor;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.line.LineName;
import wooteco.subway.domain.station.StationRepository;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository; //Is it possible? it makes dependency with Station package!!!

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Line save(Line line) {
        validateLineExisting(line);
        validateStationIdExisting(line);

        return lineRepository.save(line);
    }

    private void validateLineExisting(Line line) {
        if (lineRepository.contains(line)) {
            throw new DuplicateLineException();
        }
    }

    private void validateStationIdExisting(Line line) {
        line.getUnorderedStationIds().stream()
                .filter(stationRepository::contains)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("stationId가 존재하지 않습니다."));

    }

    public List<Line> allLines() {
        return lineRepository.allLines();
    }

    public Line findById(final Long id) {
        return lineRepository.findById(id);
    }

    @Transactional
    public void update(final Line line) {
        List<Section> sections = lineRepository.findById(line.getLineId()).getSections();
        lineRepository.update(
                new Line(
                        new LineId(line.getLineId()),
                        new LineName(line.getLineName()),
                        new LineColor(line.getLineColor()),
                        new Sections(sections)
                )
        );
    }

    public void deleteById(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addNewSection(Long lineId, Section section) {
        Line line = lineRepository.findById(lineId);
        line.addSection(section);

        lineRepository.update(line);
    }

}
