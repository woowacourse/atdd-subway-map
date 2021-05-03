import Vue from "vue";
import VueRouter from "vue-router";
import stationRoutes from "./modules/station";
import lineRoutes from "./modules/line";

Vue.use(VueRouter);

export default new VueRouter({
  mode: "history",
  routes: [...stationRoutes, ...lineRoutes]
});
