<template>
  <LoginBackground/>
  <AddInfoForm @submit="handleAddinfoSuccess"/>
</template>

<script setup>
  import { useRoute, useRouter } from 'vue-router';
  import { inject, onMounted } from 'vue';
  import axios from 'axios';
  import { jwtDecode } from 'jwt-decode';
  import AddInfoForm from '@/components/users/AddInfoForm.vue';
  import LoginBackground from '@/components/users/LoginBackground.vue';

  const router = useRouter();
  const route = useRoute();
  const loginState = inject('loginState'); // loginState를 상위 컴포넌트에서 inject로 받음

  // 컴포넌트가 마운트되었을 때 실행되는 함수
  onMounted(() => {
    // 쿼리 스트링에서 필요한 정보를 가져옴
    const query = route.query;
    console.log(query);
    // 쿼리 파라미터로 전달된 정보를 로컬 스토리지에 저장
    if (query.token && query.userName && query.userEmail && query.provider && query.providerId) {
      localStorage.setItem('token', query.token);
      localStorage.setItem('username', query.userName);
      localStorage.setItem('userEmail', query.userEmail);
      localStorage.setItem('provider', query.provider);
      localStorage.setItem('providerId', query.providerId);
      

      // URL에서 쿼리 스트링을 제거하기 위해 리다이렉트
      router.replace({ path: '/users/addinfo' });
    }
  });

// AddInfoForm에서 받은 finalData를 처리하는 함수
const handleAddinfoSuccess = (finalData) => {
  console.log('Received finalData:', finalData); // 전달된 데이터를 로그로 출력

  // 토큰 가져오기
  const token = localStorage.getItem('token');
  console.log(token);

  axios
    .post(
      "/boot/users/oauth2",
      finalData,
      {
        headers: {
          Authorization: `Bearer ${token}`, // Bearer 토큰 추가
          'Content-Type': 'application/json' // JSON 형식으로 데이터 전송
        }
      }
    )
    .then((response) => {
      console.log('서버 응답: ', response.data);
      alert('가입 성공!');

      // 로그인 상태 갱신
      loginState.state.isLoggedIn = true;
      loginState.state.userNickname = finalData.userNickname;

      console.log('Login successful:', finalData.userNickname);

      // 로컬 스토리지에서 토큰만 남기고 나머지 삭제
      localStorage.clear(); // 로컬 스토리지 전체 삭제
      localStorage.setItem('token', token); // 토큰만 다시 저장

      router.push({ path: '/' }); // 가입 성공 후 / 경로로 이동
    })
    .catch((error) => {
      console.error('서버 오류: ', error.response || error.message);
      alert('가입 실패! 다시 시도하세요');
    });
};

</script>

<style scoped>

</style>