import Vue from "vue";
import Vuex from "vuex";
import station from "./modules/station";
import line from "./modules/line";
import member from "./modules/member";
import auth from "./modules/auth";
import snackbar from "./modules/snackbar";

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    snackbar,
    station,
    line,
    member,
    auth,
  },
});
