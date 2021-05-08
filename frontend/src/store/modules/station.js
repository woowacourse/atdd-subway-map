import { SET_STATIONS } from "../shared/mutationTypes";

const MOCK_DATA = [
  {
    id: "123QWE",
    name: "aaaa",
  },
  {
    id: "456RTY",
    name: "bbb",
  },
  {
    id: "789RTY",
    name: "ccc",
  },
  {
    id: "098RTY",
    name: "ddd",
  },
];

const state = {
  stations: [...MOCK_DATA],
};

const getters = {
  stations(state) {
    return state.stations;
  },
};

const mutations = {
  [SET_STATIONS](state, stations) {
    state.stations = stations;
  },
};

export default {
  state,
  getters,
  mutations,
};
