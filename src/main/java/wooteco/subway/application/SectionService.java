package wooteco.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class SectionService {

    private static final int EXIST_LINE_COUNT = 1;

    private final SectionDao<Section> sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionDao<Section> sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public Section addSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        checkExistsLineId(lineId);
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        checkExistsStationId(upStationId);
        checkExistsStationId(downStationId);
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);
        return tryToAddSection(lineId, distance, sections, upStation, downStation);
    }

    private Section tryToAddSection(Long lineId, int distance, Sections sections,
                                    Station upStation, Station downStation) {
        if (sections.checkSameStations(upStation, downStation)) {
            throw new IllegalArgumentException(
                    String.format("기존에 있는 두 역은 구간을 추가할 수 없습니다. %s, %s", upStation.getName(), downStation.getName()));
        }
        if (sections.canAddBranchSection(upStation, downStation, distance)) {
            addBranchToUpStation(lineId, distance, sections, upStation, downStation);
            addBranchToDownStation(lineId, distance, sections, upStation, downStation);
            return sectionDao.save(new Section(lineDao.findById(lineId), upStation, downStation, distance));
        }
        return addEndOfTheLine(lineId, distance, sections, upStation, downStation);
    }

    private void addBranchToDownStation(Long lineId, int distance, Sections sections, Station upStation,
                                        Station downStation) {
        if (sections.checkAddSectionInDownStation(downStation, distance)) {
            Section originSection = sections.getOriginDownStationSection(downStation.getId());
            sectionDao.updateDownStationSection(lineId, originSection.getDownStation().getId(), upStation.getId(),
                    originSection.getDistance() - distance);
        }
    }

    private void addBranchToUpStation(Long lineId, int distance, Sections sections, Station upStation,
                                      Station downStation) {
        if (sections.checkAddSectionInUpStation(upStation, distance)) {
            Section originSection = sections.getOriginUpStationSection(upStation.getId());
            sectionDao.updateUpStationSection(lineId, originSection.getUpStation().getId(), downStation.getId(),
                    originSection.getDistance() - distance);
        }
    }

    private void checkExistsLineId(Long lineId) {
        if (lineDao.notExistsById(lineId)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
    }

    private Section addEndOfTheLine(Long lineId, int distance, Sections sections, Station upStation,
                                    Station downStation) {
        if (sections.canAddEndOfTheLine(upStation, downStation)) {
            return sectionDao.save(new Section(lineDao.findById(lineId), upStation, downStation, distance));
        }
        throw new IllegalArgumentException("구간을 추가할 수 없습니다");
    }

    @Transactional
    public int deleteSection(Long lineId, Long stationId) {
        checkMinSectionCount(lineId);
        checkExistsLineId(lineId);
        Sections sections = new Sections(sectionDao.findAllByLineIdAndStationId(lineId, stationId));
        if (sections.isZeroSize()) {
            throw new IllegalArgumentException("일치하는 구간이 없습니다.");
        }
        if (sections.isTwoSections()) {
            mergeTwoSections(lineId, stationId, sections);
        }
        return sectionDao.deleteSectionById(sections.getSectionIds());
    }

    private void mergeTwoSections(Long lineId, Long stationId, Sections sections) {
        Section upStationSection = sections.getOriginUpStationSection(stationId);
        Section downStationSection = sections.getOriginDownStationSection(stationId);
        sectionDao.save(new Section(lineDao.findById(lineId), downStationSection.getUpStation(),
                upStationSection.getDownStation(), sections.getSumDistance()));
    }

    private void checkMinSectionCount(Long lineId) {
        if (sectionDao.countByLineId(lineId) == EXIST_LINE_COUNT) {
            throw new IllegalArgumentException("최소한 한 개의 구간은 있어야 합니다.");
        }
    }

    private void checkExistsStationId(Long stationId) {
        if (stationDao.nonExistsById(stationId)) {
            throw new IllegalArgumentException("존재하지 않는 id입니다.");
        }
    }
}
