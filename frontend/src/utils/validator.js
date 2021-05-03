const validator = {
  stations: {
    name: [
      v => !!v || "이름 입력이 필요합니다.",
      v => v.length > 0 || "이름은 1글자 이상 입력해야 합니다."
    ]
  },
  line: {
    name: [v => !!v || "이름 입력이 필요합니다."],
    color: [v => !!v || "색상 입력이 필요합니다."]
  },
  section: {
    upStationId: [v => !!v || "상행역을 선택하세요."],
    downStationId: [v => !!v || "하행역을 선택하세요."],
    distance: [v => !!v || "거리 입력이 필요합니다."]
  }
};

export default validator;
