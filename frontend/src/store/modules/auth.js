import { SET_ACCESS_TOKEN } from "../shared/mutationTypes";

const state = {
  accessToken: null,
};

const getters = {
  accessToken(state) {
    return state.accessToken;
  },
};

const mutations = {
  [SET_ACCESS_TOKEN](state, accessToken) {
    state.accessToken = accessToken;
  },
};

export default {
  state,
  getters,
  mutations,
};
