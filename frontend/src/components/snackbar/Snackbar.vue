<template>
  <v-snackbar v-model="snackbar" :timeout="2000" multi-line>
    <div class="text-center width-100 font-size-15">{{ snackbarMsg }}</div>
  </v-snackbar>
</template>

<script>
import { mapGetters, mapMutations } from "vuex";
import { HIDE_SNACKBAR } from "../../store/shared/mutationTypes";

export default {
  name: "Snackbar",
  computed: {
    ...mapGetters(["isShow", "message"])
  },
  watch: {
    isShow() {
      if (this.isShow) {
        this.showSnackbar(this.message);
        setTimeout(() => {
          this.hideSnackbar();
        }, 0);
      }
    }
  },
  methods: {
    ...mapMutations([HIDE_SNACKBAR]),
    showSnackbar(msg) {
      this.snackbarMsg = msg;
      this.snackbar = true;
    }
  },
  data() {
    return {
      snackbar: false,
      snackbarMsg: ""
    };
  }
};
</script>
