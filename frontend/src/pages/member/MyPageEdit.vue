<template>
  <v-sheet class="d-flex flex-column justify-center mt-12">
    <div class="d-flex justify-center relative">
      <v-card width="400" class="card-border px-3 pt-3 pb-5">
        <v-form ref="memberEditForm" v-model="valid" @submit.prevent>
          <v-card-title class="font-weight-bold justify-center">
            나의 정보 수정
          </v-card-title>
          <v-card-text class="px-4 pt-4 pb-0">
            <div class="d-flex">
              <v-text-field
                color="grey darken-1"
                label="이메일을 입력해주세요."
                v-model="editingMember.email"
                prepend-inner-icon="mdi-email"
                dense
                outlined
                :rules="rules.member.email"
              ></v-text-field>
            </div>
            <div class="d-flex mt-2">
              <v-text-field
                color="grey darken-1"
                label="나이를 입력해주세요."
                v-model="editingMember.age"
                prepend-inner-icon="mdi-account"
                dense
                outlined
                :rules="rules.member.age"
              ></v-text-field>
            </div>
            <div class="d-flex mt-2">
              <v-text-field
                color="grey darken-1"
                label="비밀번호를 입력해주세요."
                v-model="editingMember.password"
                prepend-inner-icon="mdi-lock"
                type="password"
                dense
                outlined
                :rules="rules.member.password"
              ></v-text-field>
            </div>
            <div class="d-flex mt-2">
              <v-text-field
                color="grey darken-1"
                label="비밀번호를 한번 더 입력해주세요."
                type="password"
                prepend-inner-icon="mdi-lock"
                dense
                outlined
                v-model="editingMember.confirmPassword"
                :rules="[
                  (editingMember.password &&
                    editingMember.password === editingMember.confirmPassword) ||
                    '비밀번호가 일치하지 않습니다.',
                ]"
              ></v-text-field>
            </div>
          </v-card-text>
          <v-card-actions class="px-4 pb-4">
            <v-spacer></v-spacer>
            <v-btn text @click="$router.go(-1)">
              취소
            </v-btn>
            <v-btn
              @click.prevent="onEditMember"
              :disabled="!valid"
              color="amber"
              depressed
            >
              확인
            </v-btn>
          </v-card-actions>
        </v-form>
      </v-card>
    </div>
  </v-sheet>
</template>

<script>
import { mapGetters, mapMutations } from "vuex";
import { SHOW_SNACKBAR } from "../../store/shared/mutationTypes";
import { SNACKBAR_MESSAGES } from "../../utils/constants";
import validator from "../../utils/validator";

export default {
  name: "MypageEdit",
  computed: {
    ...mapGetters(["member"]),
  },
  created() {
    const { email, age } = this.member;
    this.editingMember = {
      email,
      age,
      password: "",
      confirmPassword: "",
    };
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    isValid() {
      return this.$refs.memberEditForm.validate();
    },
    async onEditMember() {
      try {
        // TODO member 정보를 update하는 API를 추가해주세요
        // const { email, age, password } = this.editingMember;
        // const updateMemberView = { email, age, password };
        // await fetch("/api/users", updateMemberView)
        this.showSnackbar(SNACKBAR_MESSAGES.MEMBER.EDIT.SUCCESS);
        this.$router.replace("/mypage");
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.MEMBER.EDIT.FAIL);
        throw new Error(e);
      }
    },
  },
  data() {
    return {
      editingMember: {},
      valid: false,
      rules: { ...validator },
    };
  },
};
</script>
