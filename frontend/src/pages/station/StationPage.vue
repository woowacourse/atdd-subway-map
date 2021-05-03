<template>
  <v-sheet class="d-flex flex-column justify-center mt-12">
    <div class="d-flex justify-center relative">
      <v-card width="500" max-width="600" max-height="200" class="card-border">
        <v-card-title class="font-weight-bold justify-center">
          지하철 역 관리
        </v-card-title>
        <v-card-text>
          <v-form ref="stationForm" v-model="valid" @submit.prevent>
            <div class="d-flex">
              <v-text-field
                color="grey darken-1"
                class="mr-4"
                @keydown.enter="onCreateStation"
                label="지하철 역 이름을 입력해주세요."
                v-model="stationName"
                prepend-inner-icon="mdi-subway"
                dense
                outlined
                :rules="rules.stationName"
                autofocus
              ></v-text-field>
              <v-btn
                :disabled="!valid"
                color="amber"
                @click.prevent="onCreateStation"
                >추가</v-btn
              >
            </div>
          </v-form>
        </v-card-text>
      </v-card>
    </div>
    <div class="d-flex justify-center relative mt-4">
      <v-card width="500" height="500px" class="overflow-y-auto pl-3">
        <v-list>
          <template v-for="station in stations">
            <v-list-item :key="station._id">
              <v-list-item-content>
                <v-list-item-title v-text="station.name"></v-list-item-title>
              </v-list-item-content>
              <v-list-item-action>
                <v-btn @click="onDeleteStation(station._id)" icon>
                  <v-icon color="grey lighten-1">mdi-delete</v-icon>
                </v-btn>
              </v-list-item-action>
            </v-list-item>
          </template>
        </v-list>
      </v-card>
    </div>
  </v-sheet>
</template>

<script>
import validator from "../../utils/validator";
import { SNACKBAR_MESSAGES } from "../../utils/constants";
import { mapMutations } from "vuex";
import { SHOW_SNACKBAR } from "../../store/shared/mutationTypes";
import shortid from "shortid";

export default {
  name: "StationPage",
  created() {
    //TODO 초기 역 데이터를 불러오는 API를 추가해주세요.
    this.stations = [];
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    isValid() {
      return this.$refs.stationForm.validate();
    },
    onCreateStation() {
      if (!this.isValid()) {
        return;
      }
      try {
        //TODO 역을 추가하는 API를 추가해주세요.
        const newStation = {
          _id: shortid.generate(),
          name: this.stationName
        };
        this.stations.push(newStation);
        this.stationName = "";
        this.$refs.stationForm.resetValidation();
        this.showSnackbar(SNACKBAR_MESSAGES.STATION.CREATE.SUCCESS);
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.STATION.CREATE.FAIL);
        throw new Error(e);
      }
    },
    async onDeleteStation(stationId) {
      try {
        //TODO 역을 삭제하는 API를 추가해주세요.
        const idx = this.stations.findIndex(
          station => station._id === stationId
        );
        this.stations.splice(idx, 1);
        this.showSnackbar(SNACKBAR_MESSAGES.STATION.DELETE.SUCCESS);
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.STATION.DELETE.FAIL);
      }
    }
  },
  data() {
    return {
      rules: { ...validator },
      valid: false,
      stationName: "",
      stations: []
    };
  }
};
</script>

<style lang="scss">
.card-border {
  border-top: 8px solid #ffc107 !important;
}
</style>
