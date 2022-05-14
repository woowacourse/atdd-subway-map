package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.dto.SectionDto;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Sections loadSections(long lineId) {
        List<Section> sections = new ArrayList<>();
        List<SectionDto> sectionDtos = getSectionsByLineId(lineId).collect(Collectors.toList());
        for (SectionDto sectionDto : sectionDtos) {
            long id = sectionDto.getId();
            Station upStation = stationDao.findById(sectionDto.getUpStationId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
            Station downStation = stationDao.findById(sectionDto.getDownStationId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
            int distance = sectionDto.getDistance();
            sections.add(new Section(id, lineId, upStation, downStation, distance));
        }
        return new Sections(sections);
    }

    public List<StationResponse> create(long lineId, SectionRequest sectionRequest) {
        Sections sections = loadSections(lineId);
        sections.validateDistance(sectionRequest.getDistance());
        Station upStation = stationDao.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        Station downStation = stationDao.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        if (sections.isPresent()) {
            sections.validateStations(upStation, downStation);
        }
        sections.updateSection(upStation, downStation, sectionRequest.getDistance());
        updateSections(sections);

        return Stream.of(upStation, downStation)
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private Stream<SectionDto> getSectionsByLineId(long lineId) {
        return sectionDao.findAll().stream()
                .filter(sectionDto -> sectionDto.getLineId() == lineId);
    }

    private void updateSections(Sections sections) {
        List<SectionDto> sectionDtos = new ArrayList<>();
        for (Section section : sections.getSections()) {
            long lineId = section.getLineId();
            long upStationId = section.getUpStation().getId();
            long downStationId = section.getDownStation().getId();
            int distance = section.getDistance();
            sectionDtos.add(new SectionDto(lineId, upStationId, downStationId, distance));
        }
        sectionDao.deleteByLineId(sections.getLineId());
        sectionDao.update(sectionDtos);
    }

    public void delete(long lineId, long stationId) {
        Sections sections = loadSections(lineId);
        Station station = stationDao.findById(stationId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        sections.deleteSection(station);
        updateSections(sections);
    }
}
