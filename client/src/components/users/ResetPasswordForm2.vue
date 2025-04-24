<template>
    <div class="outer-container">
      <div class="form-container">
        <form @submit.prevent="next">
          <div class="reset_pwd_email-container">
            <div class="reset_pwd_email-form">
              <div class="reset_pwd_left-email">
                <div class="reset_pwd_label-error-container">
                  <label for="userPassword1">비밀번호 *</label>
                  <div v-if="passwordTouched && passwordError" class="reset_pwd_error-message">비밀번호가 일치하지 않습니다.</div>
                </div>
  
                <div class="reset_pwd_email-input-group">
                  <input class="reset_pwd_inputinfo" type="password" id="userPassword1" required v-model="userPassword1"
                    placeholder="비밀번호 입력" />
                </div>
                
                <div class="reset_pwd_verification-form">
                  <label for="userPassword2">비밀번호 확인 *</label>
                  <div class="reset_pwd_verification-input-group">
                    <input class="reset_pwd_inputinfo" type="password" id="userPassword2"
                      placeholder="비밀번호 확인" v-model="userPassword2" />
                  </div>
                </div>
              </div>
            </div>
          </div>
  
          <button type="submit" class="reset_pwd_next-btn" @click="checkPasswordMatch">다음</button>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
    import { ref, watch } from 'vue';
    import axios from 'axios';
    import { useRouter } from 'vue-router';

  const props = defineProps({
  userEmail: {
    type: String,
    required: true
  }})  // 부모로부터 이메일을 받음

  const userEmail = ref(props.userEmail);
  console.log(userEmail);
  const userPassword1 = ref('');  // 첫 번째 비밀번호
  const userPassword2 = ref('');  // 두 번째 비밀번호
  const passwordError = ref(true);  // 비밀번호 일치 여부
  const passwordTouched = ref(false); // 비밀번호 입력 여부
  const router = useRouter();  // Vue Router 사용
  
  // 비밀번호 일치 여부 확인 함수
  const checkPasswordMatch = async () => {
    passwordTouched.value = true;
    if (userPassword1.value !== userPassword2.value) {
      passwordError.value = true;
    } else {
      passwordError.value = false;
      console.log(userPassword1, userPassword2, passwordError.value); 
      await sendPasswordReset();
    }
  };

    // 비밀번호 재설정 POST 요청 함수
  const sendPasswordReset = async () => {
    try {
        console.log(userEmail.value);
        console.log(userPassword1.value);
        const response = await axios.post('/boot/users/password', {
            userEmail: userEmail.value,  // 부모로부터 받은 이메일
            userPassword: userPassword1.value
        });

        // 성공적으로 요청이 처리되면 알림 표시 및 리다이렉트
        alert('비밀번호 재설정 완료!');
        router.push('/');  // 메인 페이지로 이동
    } catch (error) {
    console.error('비밀번호 재설정 실패:', error);
    alert('비밀번호 재설정 실패. 다시 시도해주세요.');
    }
};
  
  </script>
  
  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap');
  
  * {
    font-family: 'Inter', sans-serif;
  }
  
  .outer-container {
    width: 100%;
    height: calc(100vh - 65px);;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
  }
  
  .form-container {
    background-color: #f0f4f8;
    padding: 20px;
    width: 380px;
    margin-left: 880px;
    margin-top: auto;
    margin-bottom: auto;
    border-radius: 20px;
    box-shadow: -20px 20px 20px rgba(0, 255, 255, 0.5);
    position: relative;
  }
  
  .reset_pwd_email-form {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .reset_pwd_left-email {
    display: flex;
    flex-direction: column;
  }
  
  label {
    font-size: 14px;
    margin-bottom: 8px;
    font-weight: bold;
  }
  
  .reset_pwd_inputinfo {
    font-size: 14px;
    width: 340px;
    height: 40px;
    border-radius: 12px;
    border: none;
    box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.15);
    padding-left: 10px;
  }
  
  .reset_pwd_auth {
    background-color: #01FDFE;
    width: 60px;
    height: 40px;
    border: none;
    border-radius: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
    font-weight: bold;
  }
  
  .reset_pwd_auth:hover {
    background-color: #1DEBEC;
    cursor: pointer;
  }
  
  .reset_pwd_next-btn {
    background-color: #01FDFE;
    width: 100%;
    height: 45px;
    border-radius: 20px;
    border: none;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
    font-weight: bold;
    margin-top: 20px;
  }
  
  .reset_pwd_next-btn:hover {
    background-color: #1DEBEC;
    cursor: pointer;
  }
  
  
  .reset_pwd_email-input-group, .reset_pwd_verification-input-group {
    display: flex;
    gap: 10px;
    margin-bottom: 15px;
    width: 100%;
  }
  
  .reset_pwd_email-input-group input, .reset_pwd_verification-input-group input {
    flex: 1;
  }
  
  .reset_pwd_label-error-container {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }
  
  .reset_pwd_label-error-container label {
    margin-right: 10px;
    margin-bottom: 0;
  }
  
  .reset_pwd_error-message {
    color: red;
    font-size: 12px;
  }
  </style>