<template>
  <v-sheet class="d-flex flex-column justify-center mt-12">
    <div class="d-flex justify-center relative">
      <v-card width="500" max-width="600" max-height="600" class="card-border">
        <v-card-title class="font-weight-bold justify-center relative">
          구간 관리
        </v-card-title>
        <v-card-text
          class="relative px-0 line-list-container d-flex flex-column"
        >
          <div class="relative px-5 pb-5">
            <v-select
              v-model="activeLineId"
              :items="lineNamesViews"
              @change="onChangeLine"
              label="노선 선택"
              width="400"
              color="grey darken-1"
              item-color="amber darken-3"
              outlined
              dense
            ></v-select>
          </div>
          <v-divider />
          <div class="d-flex justify-end mr-4 section-create-button-container">
            <SectionCreateButton />
          </div>
          <div class="mt-10 overflow-y-auto">
            <v-card v-if="activeLine.id" class="mx-5" outlined>
              <v-toolbar :color="activeLine.color" dense flat>
                <v-toolbar-title>{{ activeLine.name }}</v-toolbar-title>
              </v-toolbar>
              <v-card-text class="overflow-y-auto py-0">
                <v-list dense class="max-height-300px">
                  <template
                    v-if="activeLine.stations && activeLine.stations.length > 0"
                  >
                    <v-list-item
                      v-for="(station, index) in activeLine.stations"
                      :key="index"
                    >
                      <v-list-item-content>
                        <v-list-item-title
                          v-text="station.name"
                        ></v-list-item-title>
                      </v-list-item-content>
                      <v-list-item-action class="flex-row">
                        <SectionDeleteButton
                          :line-id="activeLine.id"
                          :station-id="station.id"
                        />
                      </v-list-item-action>
                    </v-list-item>
                  </template>
                  <template v-else>
                    <v-list-item>
                      <v-list-item-content>
                        <v-list-item-title
                          >아직 추가된 역이 없습니다.</v-list-item-title
                        >
                      </v-list-item-content>
                    </v-list-item>
                  </template>
                </v-list>
              </v-card-text>
            </v-card>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </v-sheet>
</template>

<script>
import { mapGetters, mapMutations } from "vuex";
import { SHOW_SNACKBAR } from "../../store/shared/mutationTypes";
import { SNACKBAR_MESSAGES } from "../../utils/constants";
import SectionCreateButton from "./components/SectionCreateButton";
import SectionDeleteButton from "./components/SectionDeleteButton";

export default {
  name: "SectionPage",
  components: { SectionDeleteButton, SectionCreateButton },
  async created() {
    // TODO 초기 노선 데이터를 불러오는 API를 추가해주세요.
    // const lines = await fetch("/api/lines");
    // this.setLines([lines]);
    this.initLinesView();
  },
  computed: {
    ...mapGetters(["lines", "line"]),
  },
  watch: {
    line() {
      if (this.activeLine.id === this.line.id) {
        this.activeLine = { ...this.line };
      }
    },
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    initLinesView() {
      try {
        if (this.lines.length < 1) {
          return;
        }
        this.lineNamesViews = this.lines.map(({ name, id }) => {
          return {
            text: name,
            value: id,
          };
        });
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL);
        throw new Error(e);
      }
    },
    async onChangeLine() {
      try {
        // TODO 선택한 노선 데이터를 불러오는 API를 추가해주세요.
        // this.activeLine = await fetch("/lines/{id}");
        this.activeLine = this.lines.find(({ id }) => id === this.activeLineId); // TODO 위 API를 추가한 후에는 이 코드를 제거해 주세요.
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL);
        throw new Error(e);
      }
    },
  },
  data() {
    return {
      lineNamesViews: [],
      activeLineId: {},
      activeLine: {},
    };
  },
};
</script>

<style lang="scss" scoped>
.section-create-button-container {
  height: 25px;
}
</style>
