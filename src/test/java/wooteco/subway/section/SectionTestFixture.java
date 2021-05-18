package wooteco.subway.section;

public class SectionTestFixture {
    public static Section 강남_역삼 = new Section(1L, 2L, 10);
    public static Section 역삼_선릉 = new Section(2L, 3L, 6);
    public static Section 강남_선릉 = new Section(1L, 3L, 16);
    public static Section 선릉_삼성 = new Section(3L, 4L, 8);
    public static Section 교대_강남 = new Section(5L, 1L, 10);
    public static Section 교대_서초_불가능1 = new Section(5L, 6L, 10);
    public static Section 교대_서초_불가능2 = new Section(5L, 6L, 14);
    public static Section 합정_당산 = new Section(10L, 11L, 12);
}
