import { SET_MEMBER } from "../shared/mutationTypes";

const state = {
  member: null,
};

const getters = {
  member(state) {
    return state.member;
  },
};

const mutations = {
  [SET_MEMBER](state, member) {
    state.member = member;
  },
};

export default {
  state,
  getters,
  mutations,
};
