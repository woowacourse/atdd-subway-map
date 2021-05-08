<template>
  <Dialog :width="500" :close="close">
    <template slot="trigger">
      <v-btn @click="initEditingLine" icon>
        <v-icon color="grey lighten-1">mdi-pencil</v-icon>
      </v-btn>
    </template>
    <template slot="title">
      <div class="width-100 text-center mt-6">노선 수정</div>
    </template>
    <template slot="text">
      <v-form ref="lineEditForm" v-model="valid" @submit.prevent>
        <v-text-field
          v-model="lineEditForm.name"
          :rules="rules.line.name"
          color="grey darken-1"
          label="노선 이름"
          placeholder="노선 이름"
          outlined
        ></v-text-field>
        <div class="d-flex">
          <v-text-field
            v-model="lineEditForm.extraFare"
            color="grey darken-1"
            label="추가 요금"
            placeholder="(선택) 추가 요금"
            outlined
          ></v-text-field>
        </div>
        <div>
          <v-text-field
            v-model="lineEditForm.color"
            :rules="rules.line.color"
            :value="lineEditForm.color"
            label="노선 색상"
            filled
            disabled
          ></v-text-field>
          <p>
            노선의 색상을 아래 팔레트에서 선택해주세요.
          </p>
          <div class="d-flex justify-center">
            <div>
              <template v-for="(option, index) in lineColors">
                <v-btn
                  :key="option._id"
                  small
                  class="color-button ma-1"
                  depressed
                  min-width="30"
                  :color="option.color"
                  @click="setLineColor(option.color)"
                ></v-btn>
                <br
                  v-if="index === 8 || index % 9 === 8"
                  :key="`${option._id}-${index}`"
                />
              </template>
            </div>
          </div>
        </div>
      </v-form>
    </template>
    <template slot="action">
      <v-btn
        :disabled="!valid"
        @click.prevent="onEditLine"
        color="amber"
        depressed
        >확인</v-btn
      >
    </template>
  </Dialog>
</template>

<script>
import dialog from "../../../mixins/dialog";
import { mapGetters, mapMutations } from "vuex";
import Dialog from "../../../components/dialogs/Dialog";
import { LINE_COLORS, SNACKBAR_MESSAGES } from "../../../utils/constants";
import { SHOW_SNACKBAR } from "../../../store/shared/mutationTypes";
import validator from "../../../utils/validator";
import shortid from "shortid";

export default {
  name: "LineEditButton",
  props: {
    line: {
      type: Object,
      required: true,
    },
  },
  components: { Dialog },
  mixins: [dialog],
  computed: {
    ...mapGetters(["lines"]),
  },
  created() {
    this.lineEditForm = { ...this.line };
    this.lineColors = LINE_COLORS.map((color) => {
      return {
        _id: shortid.generate(),
        color,
      };
    });
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    setLineColor(color) {
      this.lineEditForm.color = color;
    },
    initEditingLine() {
      this.lineEditForm = { ...this.line };
    },
    async onEditLine() {
      try {
        // TODO Line을 수정하는 API를 추가해주세요.
        // await fetch("/api/lines/{id}", { data: this.lineEditForm })
        // TODO 전체 Line 데이터를 불러오는 API를 추가해주세요.
        // const lines = await fetch("/api/lines")
        // this.setLines([...lines])
        this.closeDialog();
        this.showSnackbar(SNACKBAR_MESSAGES.LINE.UPDATE.SUCCESS);
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.LINE.UPDATE.FAIL);
        throw new Error(e);
      }
    },
  },
  data() {
    return {
      rules: { ...validator },
      lineEditForm: {
        name: "",
        color: "",
        distance: "",
        extraFare: "",
      },
      valid: false,
      lineColors: [...LINE_COLORS],
    };
  },
};
</script>
<style lang="scss" scoped></style>
