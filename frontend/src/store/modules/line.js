import { SET_LINE, SET_LINES } from "../shared/mutationTypes";

const state = {
  line: {},
  lines: []
};

const getters = {
  line(state) {
    return state.line;
  },
  lines(state) {
    return state.lines;
  }
};

const mutations = {
  [SET_LINE](state, line) {
    state.line = line;
  },
  [SET_LINES](state, lines) {
    state.lines = lines;
  }
};

export default {
  state,
  getters,
  mutations
};
