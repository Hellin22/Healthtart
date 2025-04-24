<template>
    <LoginBackground/>
    <ResetPasswordForm1 v-if="currentStep === 1" @next-step="goToStep2" @update-email="updateEmail" />
    <ResetPasswordForm2 v-if="currentStep === 2" :userEmail="userEmail" />
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import LoginBackground from '../../components/users/LoginBackground.vue';
  import ResetPasswordForm1 from '../../components/users/ResetPasswordForm1.vue';
  import ResetPasswordForm2 from '../../components/users/ResetPasswordForm2.vue';
  
  // 현재 단계와 이메일 상태값
  const currentStep = ref(1);
  const userEmail = ref('');
  
  // 단계 전환
  const goToStep2 = () => {
    currentStep.value = 2;
  };
  
  // 자식 컴포넌트에서 이메일을 업데이트
  const updateEmail = (newEmail) => {
    userEmail.value = newEmail;
    console.log(userEmail.value);
  };
  </script>
  
<style scoped>
</style>