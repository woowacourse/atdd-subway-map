package wooteco.subway.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.CommonLineDao;
import wooteco.subway.dao.CommonSectionDao;
import wooteco.subway.dao.CommonStationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.SectionDto;

@Repository
public class SectionRepository {

    private final CommonStationDao stationDao;
    private final CommonSectionDao sectionDao;
    private final CommonLineDao lineDao;

    public SectionRepository(final CommonStationDao stationDao, final CommonSectionDao sectionDao, final CommonLineDao lineDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    @Transactional(readOnly = true)
    public Line findLineById(final Long lineId) {
        final List<SectionDto> sectionDtos = sectionDao.findAllByLineId(lineId);
        final Line line = lineDao.findById(lineId);
        return addSectionsToLine(line, sectionDtos);
    }

    private Line addSectionsToLine(final Line line, final List<SectionDto> sectionDtos) {
        final List<SectionDto> sortedSectionDtos = sortSectionDto(sectionDtos, line.getUpStationId());
        for (SectionDto sectionDto : sortedSectionDtos) {
            final Station upStation = stationDao.findById(sectionDto.getUpStationId());
            final Station downStation = stationDao.findById(sectionDto.getDownStationId());
            line.addSection(new Section(upStation, downStation, sectionDto.getDistance()));
        }
        return line;
    }

    private List<SectionDto> sortSectionDto(List<SectionDto> sectionDtos, final Long upStationId) {
        final List<SectionDto> sortedSectionDtos = new ArrayList<>();
        Long sectionId = upStationId;
        while (!sectionDtos.isEmpty()) {
            final SectionDto sectionDto = findSectionDtoById(sectionDtos, sectionId);
            sortedSectionDtos.add(sectionDto);
            sectionId = sectionDto.getDownStationId();
            sectionDtos = remove(sectionDtos, sectionDto);
        }
        return sortedSectionDtos;
    }

    private SectionDto findSectionDtoById(final List<SectionDto> sectionDtos, final Long id) {
        return sectionDtos.stream()
                .filter(sectionDto -> sectionDto.getUpStationId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private List<SectionDto> remove(final List<SectionDto> sectionDtos, final SectionDto target) {
        return sectionDtos.stream()
                .filter(sectionDto -> !(sectionDto.getUpStationId().equals(target.getUpStationId())))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Station findStationById(final Long stationId) {
        return stationDao.findById(stationId);
    }

    @Transactional
    public void addSections(final Long lineId, final List<Section> sections) {
        for (Section section : sections) {
            sectionDao.save(lineId, SectionDto.from(section));
        }
    }

    @Transactional
    public void deleteSections(final Long lineId, final List<Section> sections) {
        for (Section section : sections) {
            sectionDao.save(lineId, SectionDto.from(section));
        }
    }
}
