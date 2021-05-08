import { SET_PATH } from "../shared/mutationTypes";

const state = {
  // pathResult: null,
  pathResult: null,
};

const getters = {
  pathResult(state) {
    return state.pathResult;
  },
};

const mutations = {
  [SET_PATH](state, pathResult) {
    state.pathResult = pathResult;
  },
};

export default {
  state,
  getters,
  mutations,
};
