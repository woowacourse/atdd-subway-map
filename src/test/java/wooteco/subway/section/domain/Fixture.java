package wooteco.subway.section.domain;

import wooteco.subway.station.fixture.StationFixture;

public class Fixture {
    public static final Section FIRST_SECTION = new Section(1L, StationFixture.GANGNAM_STATION, StationFixture.JAMSIL_STATION, 10);
    public static final Section SECOND_SECTION = new Section(2L, StationFixture.JAMSIL_STATION, StationFixture.JONGHAP_STATION, 10);
    public static final Section THIRD_SECTION = new Section(3L, StationFixture.JONGHAP_STATION, StationFixture.SADANG_STATION, 10);
    public static final Section FOURTH_SECTION = new Section(4L, StationFixture.SADANG_STATION, StationFixture.DDUK_SUM_STATION, 10);

    //whenEndStationsSizeNotOne
    public static final Section DOUBLE_END_UPSTATION_SECTION = new Section(5L, StationFixture.JONGHAP_STATION, StationFixture.JAMSIL_STATION, 10);

    //whenNewSectionTooLong
    public static final Section TOO_LONG_SECTION = new Section(6L, StationFixture.JAMSIL_STATION, StationFixture.SAMSUNG_STATION, 10);

    //addSectionTest
    public static final Section BEFORE_FIRST_SECTION = new Section(7L, StationFixture.ANYANG_STATION, StationFixture.GANGNAM_STATION, 10);
    public static final Section FIFTH_SECTION = new Section(8L, StationFixture.DDUK_SUM_STATION, StationFixture.BANGBAE_STATION, 10);
    public static final OrderedSections BEFORE_FIRST_SECTION_RESULT = new OrderedSections(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, FOURTH_SECTION, BEFORE_FIRST_SECTION);
    public static final OrderedSections FIFTH_SECTION_RESULT = new OrderedSections(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, FOURTH_SECTION, FIFTH_SECTION);
    public static final Section BETWEEN_THIRD_AND_FOURTH_SECTION = new Section(9L, StationFixture.SADANG_STATION, StationFixture.GYODAE_STATION, 4);
    public static final Section MUTATE_FOURTH_SECTION = new Section(FOURTH_SECTION.getId(), StationFixture.GYODAE_STATION, StationFixture.DDUK_SUM_STATION, THIRD_SECTION.getDistance() - BETWEEN_THIRD_AND_FOURTH_SECTION.getDistance());
    public static final OrderedSections BETWEEN_THIRD_AND_FOURTH_RESULT = new OrderedSections(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, MUTATE_FOURTH_SECTION, BETWEEN_THIRD_AND_FOURTH_SECTION);

    public static final Section BETWEEN_SECOND_AND_FIRST_SECTION = new Section(10L, StationFixture.GYODAE_STATION, StationFixture.JAMSIL_STATION, 3);
    public static final Section MUTATE_FIRST_SECTION = new Section(FIRST_SECTION.getId(), StationFixture.GANGNAM_STATION, StationFixture.GYODAE_STATION, FIFTH_SECTION.getDistance() - BETWEEN_SECOND_AND_FIRST_SECTION.getDistance());
    public static final OrderedSections BETWEEN_SECOND_AND_FIRST_RESULT = new OrderedSections(MUTATE_FIRST_SECTION, BETWEEN_SECOND_AND_FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, FOURTH_SECTION);

    //removeTest
    public static final Section FIRST_AND_SECOND_MERGED_SECTION = new Section(FIRST_SECTION.getId(), FIRST_SECTION.getUpStation(), SECOND_SECTION.getDownStation(), FIRST_SECTION.getDistance() + SECOND_SECTION.getDistance());

    public static final int TEST_DISTANCE = 10;

    public OrderedSections getFirstToFourthSections() {
        return firstToFourthSections;
    }

    private final OrderedSections firstToFourthSections = new OrderedSections(FIRST_SECTION, SECOND_SECTION, THIRD_SECTION, FOURTH_SECTION);
}
