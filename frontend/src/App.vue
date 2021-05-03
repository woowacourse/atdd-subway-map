<template>
  <v-app v-cloak>
    <Header />
    <v-main>
      <router-view />
    </v-main>
    <Snackbar />
  </v-app>
</template>

<script>
  import Header from "./pages/shared/Header";
  import Snackbar from "./components/snackbar/Snackbar";

  export default {
    name: "App",
    components: { Snackbar, Header },
    data() {
      return {};
    }
  };
</script>
