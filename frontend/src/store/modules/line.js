import { SET_LINE, SET_LINES } from "../shared/mutationTypes";

const state = {
  line: {},
  lines: [
    {
      id: "sdfsd32",
      name: "1호선",
      color: "pink lighten-1",
      upStationId: "123QWE",
      downStationId: "456RTY",
      distance: "1",
      extraFare: "",
      stations: [
        {
          id: "123QWE",
          name: "aaaa",
        },
        {
          id: "456RTY",
          name: "bbb",
        },
      ],
    },
  ],
};

const getters = {
  line(state) {
    return state.line;
  },
  lines(state) {
    return state.lines;
  },
};

const mutations = {
  [SET_LINE](state, line) {
    state.line = line;
  },
  [SET_LINES](state, lines) {
    state.lines = lines;
  },
};

export default {
  state,
  getters,
  mutations,
};
