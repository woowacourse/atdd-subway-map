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
                depressed
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
            <v-list-item :key="station.id">
              <v-list-item-content>
                <v-list-item-title v-text="station.name"></v-list-item-title>
              </v-list-item-content>
              <v-list-item-action>
                <v-btn @click="onDeleteStation(station.id)" icon>
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
import { mapGetters, mapMutations } from "vuex";
import { SET_STATIONS, SHOW_SNACKBAR } from "../../store/shared/mutationTypes";

export default {
  name: "StationPage",
  computed: {
    ...mapGetters(["stations"]),
  },
  created() {
    // TODO 초기 역 데이터를 불러오는 API를 추가해주세요.
    this.setStations([...this.stations]); // stations 데이터를 단 한개 존재하는 저장소에 등록
  },
  methods: {
    ...mapMutations([SET_STATIONS, SHOW_SNACKBAR]),
    isValid() {
      return this.$refs.stationForm.validate();
    },
    async onCreateStation() {
      if (!this.isValid()) {
        return;
      }
      try {
        // TODO 역을 추가하는 API Sample
        const response = await fetch("/api/stations", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            name: this.stationName,
          }),
        });
        if (!response.ok) {
          throw new Error(`${response.status}`);
        }
        const newStation = await response.json();

        this.setStations([...this.stations, newStation]);
        this.initStationForm();
        this.showSnackbar(SNACKBAR_MESSAGES.STATION.CREATE.SUCCESS);
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.STATION.CREATE.FAIL);
        throw new Error(e);
      }
    },
    initStationForm() {
      this.stationName = "";
      this.$refs.stationForm.resetValidation();
    },
    async onDeleteStation(stationId) {
      try {
        // TODO 역을 삭제하는 API를 추가해주세요.
        // await fetch("/api/stations/{id}");
        const idx = this.stations.findIndex(
          (station) => station.id === stationId
        );
        this.stations.splice(idx, 1);
        this.showSnackbar(SNACKBAR_MESSAGES.STATION.DELETE.SUCCESS);
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.STATION.DELETE.FAIL);
        throw new Error(e);
      }
    },
  },
  data() {
    return {
      rules: { ...validator },
      valid: false,
      stationName: "",
    };
  },
};
</script>

<style lang="scss">
.card-border {
  border-top: 8px solid #ffc107 !important;
}
</style>
