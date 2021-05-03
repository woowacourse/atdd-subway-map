import { SET_STATIONS } from "../shared/mutationTypes";

const state = {
  stations: []
};

const getters = {
  stations(state) {
    return state.stations;
  }
};

const mutations = {
  [SET_STATIONS](state, stations) {
    state.stations = stations;
  }
};

export default {
  state,
  getters,
  mutations
};
