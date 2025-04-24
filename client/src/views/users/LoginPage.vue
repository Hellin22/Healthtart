<template>
    <LoginBackground/>
    <LoginForm @loginSuccess="handleLoginSuccess"/>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { inject } from 'vue';
import LoginBackground from '@/components/users/LoginBackground.vue';
import LoginForm from '@/components/users/LoginForm.vue';

// Vue Router 사용
const router = useRouter();
const loginState = inject('loginState');

// 로그인 성공 처리 함수
const handleLoginSuccess = (userNickname) => {
  loginState.state.isLoggedIn = true;
  // 로그인 성공 시 메인 페이지로 이동
  router.push({ path: '/' });
  // 필요 시, 추가적인 처리 (로그인 성공 메시지 등)
};
</script>

<style scoped>

</style>