import Vue from "vue";
import VueRouter from "vue-router";
import stationRoutes from "./modules/station";
import lineRoutes from "./modules/line";
import mainRoutes from "./modules/main";
import sectionRoutes from "./modules/section";
import memberRoutes from "./modules/member";
import pathRoutes from "./modules/path";

Vue.use(VueRouter);

export default new VueRouter({
  mode: "history",
  routes: [
    ...stationRoutes,
    ...lineRoutes,
    ...mainRoutes,
    ...sectionRoutes,
    ...pathRoutes,
    ...memberRoutes,
  ],
});
