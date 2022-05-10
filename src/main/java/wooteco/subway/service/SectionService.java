package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionRepository;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@Transactional
@Service
public class SectionService {

    private static final int NEED_MERGE_SIZE = 2;

    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public void create(final Long lineId, final SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionRepository.findByLineId(lineId));

        Station upStation = stationRepository.findById(sectionRequest.getUpStationId());
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId());
        Section section = new Section(lineId, upStation, downStation, sectionRequest.getDistance());
        sections.add(section);

        sectionRepository.save(section);
        sections.pickUpdate(sectionRepository
                        .findByLineId(lineId))
                .ifPresent(sectionRepository::update);
    }

    public void delete(final Long lineId, final Long stationId) {
        Sections sections = new Sections(sectionRepository.findByLineId(lineId));
        Station station = stationRepository.findById(stationId);

        List<Section> deletedSections = sections.delete(station);
        sectionRepository.deleteSections(deletedSections);

        if (deletedSections.size() == NEED_MERGE_SIZE) {
            Section mergedSection = deletedSections.get(0).merge(deletedSections.get(1));
            sectionRepository.save(mergedSection);
        }
    }
}
