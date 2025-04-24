<template>
  <div id="app" style="background-color: black;">

    <AppHeader :is-logged-in="loginState.isLoggedIn" :user-nickname="loginState.userNickname" @logout="logout" />
  </div>
  <main>
    <RouterView />
  </main>
</template>

<script setup>
import './assets/css/style.css';
import { ref, onMounted, provide, watch, reactive } from 'vue';
import { RouterView, useRoute, useRouter } from 'vue-router';
import AppHeader from '@/components/AppHeader.vue';
import { jwtDecode } from 'jwt-decode';

  const router = useRouter();

  const route = useRoute();
  const isMainPage = ref(false);
  const isLoginPage = ref(false);
  const isHistoryPage = ref(false);
  const isSignupPage = ref(false);
  const isGymPage = ref(false);
  const isMyPage = ref(false);
  const isRankingPage = ref(false);
  const isAddInfoPage = ref(false);
  const isResetPasswordPage = ref(false);

const loginState = reactive({
  isLoggedIn: false,
  userNickname: ''
});

const checkLoginStatus = () => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const decodedToken = jwtDecode(token);
      const currentTime = Date.now() / 1000;
      if (decodedToken.exp > currentTime) {
        loginState.isLoggedIn = true;

        // userNickname을 토큰에서 추출하거나, 이미 설정된 값을 유지
        if (!loginState.userNickname) {
          loginState.userNickname = decodedToken.nickname;  // 토큰에 nickname이 있으면 설정
        }

        // loginState.userNickname = decodedToken.nickname;
        console.log(decodedToken.nickname);
      } else {
        logout();
      }
    } catch (error) {
      console.error('Error decoding token:', error);
      logout();
    }
  } else {
    loginState.isLoggedIn = false;
    loginState.userNickname = '';
  }
};

watch(() => route?.path, (newPath) => {
    console.log("Route Path: ", newPath);
    isMainPage.value = newPath === '/';
    isLoginPage.value = newPath === '/login';
    isHistoryPage.value = newPath === '/history';
    isSignupPage.value = newPath === '/users/signup';
    isMyPage.value = newPath === '/mypage';
    isGymPage.value = newPath === '/gym';
    isRankingPage.value = newPath === '/ranking';
    isAddInfoPage.value = newPath === '/users/addinfo';
    isResetPasswordPage.value = newPath === '/users/password'; 
  },
  {
    immediate: true
  });
  
// 로그아웃 함수
const logout = () => {
  localStorage.removeItem('token');
  loginState.isLoggedIn = false;
  loginState.userNickname = '';
  router.push('/');
};

onMounted(() => {
  checkLoginStatus();
  // 주기적으로 로그인 상태 확인 (예: 1분마다)
  setInterval(checkLoginStatus, 60000);
});

// 로그인 상태와 관련 함수들을 provide
provide('loginState', {
  state: loginState,
  logout,
  checkLoginStatus
});
</script>

<style scoped>

</style>
