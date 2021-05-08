<template>
  <v-app-bar flat app color="amber" clipped-left :height="65">
    <v-toolbar-title class="pl-0 mr-12 align-center relative bottom-2">
      <v-btn to="/" text>
        <div class="title">
          <span>RUNNINGMAP</span>
        </div>
      </v-btn>
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-btn
      v-for="navItem in navItems"
      :key="navItem._id"
      :to="navItem.link"
      text
      >{{ navItem.text }}</v-btn
    >
    <v-btn v-if="!member" to="/login" text>로그인</v-btn>
  </v-app-bar>
</template>

<script>
import { NAV_ITEMS } from "../../utils/constants";
import { mapGetters } from "vuex";

export default {
  name: "Header",
  computed: {
    ...mapGetters(["member"]),
  },
  data() {
    return {
      navItems: [...NAV_ITEMS],
    };
  },
};
</script>

<style lang="scss" scoped>
.header-logo {
  width: 25px;
}
</style>
