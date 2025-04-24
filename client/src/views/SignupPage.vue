<template>
  <SignupStep1 v-if="currentStep === 1" @nextStep="goToStep2" />
  <SignupStep2 v-if="currentStep === 2" @submit="submitForm" />
  <LoginBackground/>
</template>

<script setup>
import SignupStep1 from '@/components/users/SignupStep1.vue';
import SignupStep2 from '@/components/users/SignupStep2.vue';
import LoginBackground from '@/components/users/LoginBackground.vue';
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

// 현재 스텝 추적
const currentStep = ref(1);

// 폼 데이터
const formData = ref({
  userEmail: '',
  userPassword: '',
  userName: '',
  userPhone: '',
  userNickname: '',
  userAddress: '',
  userGender: '',
  userAge: '',
  userHeight: '',
  userWeight: '',
  gymCode: ''
});

// 첫 번째 페이지에서 두 번째 페이지로 이동
const goToStep2 = (dataFromStep1) => {
  formData.value = { ...formData.value, ...dataFromStep1 }; // 받은 데이터 저장
  currentStep.value = 2; // 스텝을 2로 변경
};

// 최종 제출
const submitForm = (dataFromStep2) => {
  formData.value = { ...formData.value, ...dataFromStep2 }; // 나머지 데이터 합치기

  // 여기에 서버로 데이터를 보내는 로직 추가
  console.log('최종 데이터: ', formData.value);

  axios.post("/boot/users/signup", formData.value)
    .then((response) => {
      console.log('서버 응답: ', response.data);
      alert('가입 성공!');
      // 가입 성공 후 홈페이지로 리다이렉트
      router.push('/');
    })
    .catch((error) => {
      console.error('서버 오류: ', error.response || error.message);
      alert('가입 실패! 다시 시도하세요');
    });
};
</script>

<style scoped>
</style>