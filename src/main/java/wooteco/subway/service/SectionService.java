package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.repository.SectionRepository;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(final SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void saveSection(final Long lineId, final SectionRequest request) {
        final Line line = sectionRepository.findLineById(lineId);
        final List<Section> previousSections = new ArrayList<>(line.getSections());
        final Section newSection = makeSection(request);

        line.addSection(newSection);
        final List<Section> addSections = line.getAddSections(previousSections);
        final List<Section> deletedSections = line.getDeletedSections(previousSections);

        sectionRepository.deleteSections(lineId, deletedSections);
        sectionRepository.addSections(lineId, addSections);
    }

    private Section makeSection(final SectionRequest request) {
        final Station upStation = sectionRepository.findStationById(request.getUpStationId());
        final Station downStation = sectionRepository.findStationById(request.getDownStationId());
        return new Section(upStation, downStation, request.getDistance());
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        final Line line = sectionRepository.findLineById(lineId);
        final Station target = sectionRepository.findStationById(stationId);
        final List<Section> previousSections = new ArrayList<>(line.getSections());

        line.deleteSection(target);
        final List<Section> addSections = line.getAddSections(previousSections);
        final List<Section> deletedSections = line.getDeletedSections(previousSections);

        sectionRepository.deleteSections(lineId, deletedSections);
        sectionRepository.addSections(lineId, addSections);
    }
}
