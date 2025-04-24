import MyPage from "@/views/users/MyPage.vue";
import MyPageEdit from "@/views/users/MyPageEdit.vue";
import LoginPage from "@/views/users/LoginPage.vue";
import AddInfoPage from "../views/users/AddInfoPage.vue";
import ResetPasswordPage from "../views/users/ResetPasswordPage.vue";
import FindEmailPage from "../views/users/FindEmailPage.vue";
import OAuthKakaoCallback from "../views/users/OAuthKakaoCallback.vue";

const UserRoutes = [
    { path: "/mypage", component: MyPage, meta: { requiresAuth: true } },
    { path: "/mypage/edit", name: 'MyPageEdit', component: MyPageEdit, props: true },
    { path: "/users/login", component: LoginPage },
    { path: "/users/addinfo", component: AddInfoPage},
    { path: "/users/password", component: ResetPasswordPage},
    { path: "/users/findemail", component: FindEmailPage},
    { path: "/users/oauth/callback", component: OAuthKakaoCallback}
];

export default UserRoutes;
