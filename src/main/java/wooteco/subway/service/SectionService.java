package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.service.dto.SectionDto;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(final SectionDao sectionDao,
                          final LineRepository lineRepository,
                          final StationRepository stationRepository) {
        this.sectionDao = sectionDao;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public void save(final Long lineId, final SectionRequest request) {
        final Line line = lineRepository.findById(lineId);
        final List<Section> previousSections = new ArrayList<>(line.getSections());
        final Section newSection = makeSection(request);

        line.addSection(newSection);
        final List<Section> addSections = line.getAddSections(previousSections);
        final List<Section> deletedSections = line.getDeletedSections(previousSections);

        deleteSections(lineId, deletedSections);
        addSections(lineId, addSections);
    }

    private Section makeSection(final SectionRequest request) {
        final Station upStation = stationRepository.findById(request.getUpStationId());
        final Station downStation = stationRepository.findById(request.getDownStationId());
        return new Section(upStation, downStation, request.getDistance());
    }

    public void delete(final Long lineId, final Long stationId) {
        final Line line = lineRepository.findById(lineId);
        final Station target = stationRepository.findById(stationId);
        final List<Section> previousSections = new ArrayList<>(line.getSections());

        line.deleteSection(target);
        final List<Section> addSections = line.getAddSections(previousSections);
        final List<Section> deletedSections = line.getDeletedSections(previousSections);

        deleteSections(lineId, deletedSections);
        addSections(lineId, addSections);
    }

    private void addSections(final Long lineId, final List<Section> sections) {
        for (Section section : sections) {
            sectionDao.save(lineId, SectionDto.from(section));
        }
    }

    private void deleteSections(final Long lineId, final List<Section> sections) {
        for (Section section : sections) {
            sectionDao.deleteById(lineId, section.getUpStationId());
        }
    }

}
