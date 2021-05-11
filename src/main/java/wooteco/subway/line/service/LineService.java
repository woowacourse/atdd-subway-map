package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.station.domain.Station;

import java.util.List;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public Line save(final LineRequest lineRequest) {
        validateDuplicate(lineRequest.getName());
        return lineRepository.save(lineRequest.getName(), lineRequest.getColor(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    private void validateDuplicate(final String name) {
        if (lineRepository.findByName(name).isPresent()) {
            throw new IllegalStateException("[ERROR] 이미 존재하는 노선입니다.");
        }
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Line findById(final Long id) {
        return lineRepository.findById(id);
    }

    @Transactional
    public void update(final Long id, LineRequest lineRequest) {
        lineRepository.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    @Transactional
    public void delete(final Long lineId, final Long stationId) {
        Line line = lineRepository.findById(lineId);
        if (line.hasOnlyOneSection()) {
            throw new IllegalStateException("[ERROR] 구간이 하나만 존재하므로 삭제할 수 없습니다.");
        }
        List<Section> sections = line.sectionsWhichHasStation(new Station(stationId));
        lineRepository.delete(lineId, stationId);
        if (sections.size() == 2) {
            lineRepository.addSection(
                    lineId,sections.get(0).upStation().id(), sections.get(1).downStation().id(),sections.get(0).addDistance(sections.get(1)));
        }
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId);
        Section toAddSection = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Station targetStation = line.registeredStation(toAddSection);
        if (toAddSection.hasUpStation(targetStation)) {
            Section targetSection = line.findSectionWithUpStation(targetStation);
            checkAbleToAddByDistance(toAddSection, targetSection);
            lineRepository.updateSection(lineId,
                    new Section(targetSection.id(), lineId, toAddSection.downStation(), targetSection.downStation(), targetSection.subtractDistance(toAddSection)));
        }
        if (toAddSection.hasDownStation(targetStation)) {
            Section targetSection = line.findSectionWithDownStation(targetStation);
            checkAbleToAddByDistance(toAddSection, targetSection);
            lineRepository.updateSection(lineId,
                    new Section(targetSection.id(), lineId, targetSection.upStation(), toAddSection.upStation(), targetSection.subtractDistance(toAddSection)));
        }
        lineRepository.addSection(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    private void checkAbleToAddByDistance(Section addSection, Section targetSection) {
        if (targetSection.lessDistanceThan(addSection)) {
            throw new IllegalArgumentException("[ERROR] 기존 구간 길이보다 크거나 같으면 등록할 수 없습니다.");
        }
    }
}
