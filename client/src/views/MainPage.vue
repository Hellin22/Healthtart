<template>
  <div class="app">
    <main>
      <div class="content">
        <div class="left-content">
          <h1 class="h1content">
            헬린이들,<br />
            헬스장이 두렵나?
          </h1>
          <p class="pcontent">
            <span style="font-weight: 700;">헬스장 첫 발걸음이 막막하시다면, 걱정 마세요!</span><br />
            AI가 당신만을 위한 운동 루틴과 헬스 기구<br />
            사용법을 편리하게 알려주고, 신나는 맞춤<br />
            플레이리스트까지 함께합니다.
          </p>
          <div class="buttons">
            <button class="primary-btn" @click="routeRoutinePage">운동 루틴 추천받기</button>
            <button class="secondary-btn" @click="routeRecommendationPage">추천 루틴 리스트 보기</button>
          </div>
        </div>
        <div class="right-content">
          <img src="@/assets/icons/mainImage.svg" alt="헬스장 내부" class="main-image" />
          <img src="@/assets/icons/blueLabelFirst.svg" alt="블루라벨" class="blue-label" />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted, inject } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { jwtDecode } from 'jwt-decode';  // jwt-decode 임포트
import '@/assets/css/Main/MainPage.css';

const router = useRouter();
const route = useRoute();
const loginState = inject('loginState'); // loginState를 상위 컴포넌트에서 inject로 받음

const routeRoutinePage = () => {
  router.push({ path: '/routine' });
  };
  
const routeRecommendationPage = () => {
  router.push({ path: '/user-recommendation' });
  };
  
onMounted(() => {
  const blueWave = document.createElement('div');
  blueWave.className = 'blue-wave';
  document.querySelector('.app').appendChild(blueWave);

  const token = localStorage.getItem('token');

  if (token) {
    // jwt-decode를 사용해 닉네임 추출
    const decodedToken = jwtDecode(token);
    const userNickname = decodedToken.nickname;

    // 로그인 상태 업데이트
    loginState.state.isLoggedIn = true;
    loginState.state.userNickname = userNickname;

    router.replace('/');
  }
});
</script>
