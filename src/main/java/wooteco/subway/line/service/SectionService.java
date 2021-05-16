package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.repository.SectionRepository;
import wooteco.subway.station.repository.StationRepository;

@Service
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(final LineRepository lineRepository, final StationRepository stationRepository, final SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public void lineCreateAdd(final Long lineId, final SectionRequest sectionRequest) {
        validateLineId(lineId);
        validateStations(sectionRequest);
        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        sectionRepository.save(section);
    }

    @Transactional
    public void add(final Long lineId, final SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionRepository.getSectionsByLineId(lineId));
        validateAddRequest(lineId, sectionRequest, sections);
        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());

        if (sections.containUpStationId(section.getUpStationId())) {
            addBaseOnUpStation(section, sections);
            return;
        }
        addBaseOnDownStation(section, sections);
    }

    private void addBaseOnUpStation(final Section section, final Sections sections) {
        if (sections.containUpStationId(section.getUpStationId())) {
            Long beforeConnectedStationId = sections.getDownStationId(section.getUpStationId());
            saveSectionBetweenStationsBaseOnUpStation(section, sections, beforeConnectedStationId);
            return;
        }
        sectionRepository.save(section);
    }

    private void addBaseOnDownStation(final Section section, final Sections sections) {
        if (sections.containDownStationId(section.getDownStationId())) {
            Long beforeConnectedUpStationId = sections.getUpStationId(section.getDownStationId());
            saveSectionBetweenStationsBaseOnDownStation(section, sections, beforeConnectedUpStationId);
            return;
        }
        sectionRepository.save(section);
    }

    private void saveSectionBetweenStationsBaseOnUpStation(final Section section, final Sections sections, final Long beforeConnectedStationId) {
        int beforeDistance = sections.getDistance(section.getUpStationId(), beforeConnectedStationId);
        if (beforeDistance <= section.getDistance()) {
            throw new IllegalArgumentException("기존에 존재하는 구간의 길이가 더 짧습니다.");
        }
        sectionUpdateBetweenSaveBaseOnUpStation(section, beforeConnectedStationId, beforeDistance);
    }

    private void sectionUpdateBetweenSaveBaseOnUpStation(final Section section, final Long beforeConnectedStationId, final int beforeDistance) {
        sectionRepository.save(section);
        sectionRepository.save(new Section(section.getLineId(), section.getDownStationId(), beforeConnectedStationId, beforeDistance - section.getDistance()));
        sectionRepository.delete(section.getLineId(), section.getUpStationId(), beforeConnectedStationId);
    }

    private void saveSectionBetweenStationsBaseOnDownStation(final Section section, final Sections sections, final Long beforeConnectedUpStationId) {
        int beforeDistance = sections.getDistance(beforeConnectedUpStationId, section.getDownStationId());
        if (beforeDistance <= section.getDistance()) {
            throw new IllegalArgumentException("기존에 존재하는 구간의 길이가 더 짧습니다.");
        }
        sectionUpdateBetweenSaveBaseOnDownStation(section, beforeConnectedUpStationId, beforeDistance);
    }

    private void sectionUpdateBetweenSaveBaseOnDownStation(final Section section, final Long beforeConnectedUpStationId, final int beforeDistance) {
        sectionRepository.save(section);
        sectionRepository.delete(section.getLineId(), beforeConnectedUpStationId, section.getDownStationId());
        sectionRepository.save(new Section(section.getLineId(), beforeConnectedUpStationId, section.getUpStationId(), beforeDistance - section.getDistance()));
    }

    private void validateAddRequest(final Long lineId, final SectionRequest sectionRequest, final Sections sections) {
        validateLineId(lineId);
        validateStations(sectionRequest);
        sections.isValidateSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
    }

    private void validateStations(final SectionRequest sectionRequest) {
        if (!stationRepository.isExistId(sectionRequest.getUpStationId()) || !stationRepository.isExistId(sectionRequest.getDownStationId())) {
            throw new NotFoundException("존재하지 않는 station을 구간에 등록할 수 없습니다.");
        }
    }

    @Transactional
    public void delete(final Long lineId, final Long stationId) {
        validateDeleteRequest(lineId, stationId);
        Sections sections = new Sections(sectionRepository.getSectionsByLineId(lineId));
        if (sections.containUpStationId(stationId)) {
            deleteSectionBaseOnUpStation(lineId, stationId, sections);
            return;
        }
        sectionRepository.delete(lineId, sections.getUpStationId(stationId), stationId);
    }

    private void deleteSectionBaseOnUpStation(final Long lineId, final Long stationId, final Sections sections) {
        Long backStationId = sections.getDownStationId(stationId);

        if (sections.containDownStationId(stationId)) {
            Long frontStationId = sections.getUpStationId(stationId);
            int connectDistance = sections.getDistance(frontStationId, stationId) + sections.getDistance(stationId, backStationId);
            sectionUpdateWhenBetweenDelete(lineId, stationId, backStationId, frontStationId, connectDistance);
            return;
        }
        sectionRepository.delete(lineId, stationId, backStationId);
    }

    private void sectionUpdateWhenBetweenDelete(final Long lineId, final Long stationId, final Long backStationId, final Long frontStationId, final int connectDistance) {
        sectionRepository.delete(lineId, frontStationId, stationId);
        sectionRepository.delete(lineId, stationId, backStationId);
        sectionRepository.save(new Section(lineId, frontStationId, backStationId, connectDistance));
    }

    private void validateDeleteRequest(final Long lineId, final Long stationId) {
        validateLineId(lineId);
        Sections sections = new Sections(sectionRepository.getSectionsByLineId(lineId));
        sections.validateDeleteStation(stationId);
    }

    private void validateLineId(final Long lineId) {
        if (!lineRepository.isExistId(lineId)) {
            throw new NotFoundException("존재하지 않는 Line id 입니다.");
        }
    }
}
