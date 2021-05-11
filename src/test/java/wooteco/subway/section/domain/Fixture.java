package wooteco.subway.section.domain;

import wooteco.subway.station.domain.Station;

public class Fixture {
    static final Station GANGNAM_STATION = new Station(1L, "강남역");
    static final Station JAMSIL_STATION = new Station(2L, "잠실역");
    static final Station JONGHAP_STATION = new Station(3L, "종합운동장역");
    static final Station SADANG_STATION = new Station(4L, "사당역");
    static final Station DDUK_SUM_STATION = new Station(5L, "뚝섬역");
    static final Station BANGBAE_STATION = new Station(6L, "방배역");
    static final Station SAMSUNG_STATION = new Station(7L, "삼성역");
    static final Station GYODAE_STATION = new Station(8L, "교대역");
    static final Station ANYANG_STATION = new Station(9L, "안양역");
    static final Station YEOKSAM_STATION = new Station(10L, "역삼역");

    static final Section FIRST_SECTION = new Section(1L, GANGNAM_STATION, JAMSIL_STATION, 10);
    static final Section SECOND_SECTION = new Section(2L, JAMSIL_STATION, JONGHAP_STATION, 10);
    static final Section THIRD_SECTION = new Section(3L, JONGHAP_STATION, SADANG_STATION, 10);
    static final Section FOURTH_SECTION = new Section(4L, SADANG_STATION, DDUK_SUM_STATION, 10);

    //whenEndStationsSizeNotOne
    static final Section DOUBLE_END_UPSTATION_SECTION = new Section(5L, JONGHAP_STATION, JAMSIL_STATION, 10);

    //whenNewSectionTooLong
    static final Section TOO_LONG_SECTION = new Section(6L, JAMSIL_STATION, SAMSUNG_STATION, 10);

    //addSectionTest
    static final Section BEFORE_FIRST_SECTION = new Section(7L, ANYANG_STATION, GANGNAM_STATION, 10);
    static final Section FIFTH_SECTION = new Section(8L, DDUK_SUM_STATION, BANGBAE_STATION, 10);
    static final OrderedSections BEFORE_FIRST_SECTION_RESULT = new OrderedSections(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, FOURTH_SECTION, BEFORE_FIRST_SECTION);
    static final OrderedSections FIFTH_SECTION_RESULT = new OrderedSections(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, FOURTH_SECTION, FIFTH_SECTION);
    static final Section BETWEEN_THIRD_AND_FOURTH_SECTION = new Section(9L, SADANG_STATION, GYODAE_STATION, 4);
    static final Section MUTATE_FOURTH_SECTION = new Section(FOURTH_SECTION.getId(), GYODAE_STATION, DDUK_SUM_STATION, THIRD_SECTION.getDistance() - BETWEEN_THIRD_AND_FOURTH_SECTION.getDistance());
    static final OrderedSections BETWEEN_THIRD_AND_FOURTH_RESULT = new OrderedSections(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, MUTATE_FOURTH_SECTION, BETWEEN_THIRD_AND_FOURTH_SECTION);

    static final Section BETWEEN_SECOND_AND_FIRST_SECTION = new Section(10L, GYODAE_STATION, JAMSIL_STATION, 3);
    static final Section MUTATE_FIRST_SECTION = new Section(FIRST_SECTION.getId(), GANGNAM_STATION, GYODAE_STATION, FIFTH_SECTION.getDistance() - BETWEEN_SECOND_AND_FIRST_SECTION.getDistance());
    static final OrderedSections BETWEEN_SECOND_AND_FIRST_RESULT = new OrderedSections(MUTATE_FIRST_SECTION, BETWEEN_SECOND_AND_FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, FOURTH_SECTION);

    //removeTest
    static final Section FIRST_AND_SECOND_MERGED_SECTION = new Section(FIRST_SECTION.getId(), FIRST_SECTION.getUpStation(), SECOND_SECTION.getDownStation(), FIRST_SECTION.getDistance() + SECOND_SECTION.getDistance());

    public OrderedSections getFirstToFourthSections() {
        return firstToFourthSections;
    }
    private final OrderedSections firstToFourthSections = new OrderedSections(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, FOURTH_SECTION);
}
