<template>
  <v-sheet class="d-flex flex-column justify-center mt-12">
    <div class="d-flex justify-center relative">
      <v-card v-if="member" width="400" class="card-border px-3 pt-3 pb-5">
        <v-card-title class="font-weight-bold justify-center">
          나의 정보
        </v-card-title>
        <v-card-text class="px-4 pt-4 pb-0">
          <v-row>
            <v-col cols="12">
              <label class="font-weight-regular">email</label>
              <div class="text-left subtitle-1 text-dark font-weight-bold">
                {{ member.email }}
              </div>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <label class="font-weight-regular">age</label>
              <div class="text-left subtitle-1 text-dark font-weight-bold">
                {{ member.age }}
              </div>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions class="px-4 pb-4">
          <v-spacer></v-spacer>
          <v-btn @click="onDeleteAccount" text>
            탈퇴
          </v-btn>
          <v-btn to="/mypage/edit" color="amber" depressed>
            수정
          </v-btn>
        </v-card-actions>
      </v-card>
    </div>
    <ConfirmDialog ref="confirm" />
  </v-sheet>
</template>

<script>
import { mapGetters, mapMutations } from "vuex";
import { SET_MEMBER, SHOW_SNACKBAR } from "../../store/shared/mutationTypes";
import ConfirmDialog from "../../components/dialogs/ConfirmDialog";
import { SNACKBAR_MESSAGES } from "../../utils/constants";

export default {
  name: "MyPage",
  components: { ConfirmDialog },
  computed: {
    ...mapGetters(["member"]),
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR, SET_MEMBER]),
    async onDeleteAccount() {
      const confirm = await this.$refs.confirm.open(
        "회원 탈퇴",
        `정말로 탈퇴 하시겠습니까? 탈퇴 후에는 복구할 수 없습니다.`,
        {
          color: "red lighten-1",
        }
      );
      if (!confirm) {
        return;
      }
      try {
        // TODO 유저를 삭제하는 API를 추가해주세요
        // await fetch("/api/users")
        this.setMember(null);
        this.showSnackbar(SNACKBAR_MESSAGES.MEMBER.DELETE.SUCCESS);
        this.$router.replace("/");
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.MEMBER.DELETE.FAIL);
        throw new Error(e);
      }
    },
  },
};
</script>
