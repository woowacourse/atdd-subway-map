import LoginPage from "../../pages/member/LoginPage";
import JoinPage from "../../pages/member/JoinPage";
import MyPage from "../../pages/member/MyPage";
import MyPageEdit from "../../pages/member/MyPageEdit";

const memberRoutes = [
  {
    path: "/login",
    component: LoginPage,
  },
  {
    path: "/join",
    component: JoinPage,
  },
  {
    path: "/mypage",
    component: MyPage,
  },
  {
    path: "/mypage/edit",
    component: MyPageEdit,
  },
];

export default memberRoutes;
