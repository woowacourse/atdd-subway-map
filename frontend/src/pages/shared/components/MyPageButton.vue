<template>
  <router-link to="/mypage">
    <v-list-item class="cursor-pointer">
      <v-list-item-title>
        <v-icon class="mr-1 button">mdi-account</v-icon>
        <span class="relative top-1">나의 페이지</span>
      </v-list-item-title>
    </v-list-item>
  </router-link>
</template>

<script>
export default {
  name: "MyPageButton",
};
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
</style>
