<template>
  <div class="d-flex justify-center align-center app-container">
    <div class="text-center">
      <img src="../../assets/main_logo.png" class="main-logo mx-auto" />
      <p class="mt-2 text-gray-700">
        RunningMap Service 페이지입니다. <br />
        제공되는 템플릿을 활용해 프로젝트를 완성해 보세요!
      </p>
      <ul class="text-blue-700">
        <li
          v-for="navItem in navItems"
          :key="navItem._id"
          class="d-inline-block p-1"
        >
          <router-link :to="navItem.link">
            <v-btn text>{{ navItem.text }}</v-btn>
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { NAV_ITEMS } from "../../utils/constants";

export default {
  name: "MainPage",
  data() {
    return {
      navItems: [...NAV_ITEMS],
    };
  },
};
</script>

<style lang="scss" scoped>
.app-container {
  height: calc(100vh - 100px);
}

.main-logo {
  width: 150px;
}

ul li {
  list-style: none;
  a {
    text-decoration: none;
  }
}
</style>
