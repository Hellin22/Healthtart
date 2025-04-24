<template>
  <div class="outer-container">
    <div class="form-container">
      <form @submit.prevent="next">
        <div class="reset_pwd_email-container">
          <div class="reset_pwd_email-form">
            <div class="reset_pwd_left-email">
              <div class="reset_pwd_label-error-container">
                <label for="email">이메일 *</label>
                <div v-if="emailTouched && emailError" class="reset_pwd_error-message">올바른 이메일 형식을 입력해주세요.</div>
              </div>

              <div class="reset_pwd_email-input-group">
                <input class="reset_pwd_inputinfo" type="text" id="email" required v-model="email" @blur="checkEmail" placeholder="이메일 입력" />
                <button type="button" @click="sendVerificationEmail" class="reset_pwd_auth" :disabled="emailError || !email || isEmailSent">
                  인증
                </button>
              </div>

              <div class="reset_pwd_verification-form">
                <label for="verificationCode">인증번호 *</label>
                <div class="reset_pwd_verification-input-group">
                  <input class="reset_pwd_inputinfo" type="text" id="verificationCode" placeholder="5분내로 인증번호를 입력해주세요" v-model="verificationCode" :disabled="!isEmailSent" />
                  <button type="button" @click="verifyCode" class="reset_pwd_auth" :disabled="!isEmailSent || isVerified">확인</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <button type="submit" class="reset_pwd_next-btn" :disabled="!isVerified" @click="next">다음</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

// 이메일과 인증번호 관련 상태값
const email = ref('');
const verificationCode = ref('');
const isEmailSent = ref(false);
const isVerified = ref(false);
const emailError = ref(false);
const emailTouched = ref(false);

// 유효성 검사
const validateEmail = (email) => {
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailPattern.test(email);
};

// 이메일 입력 필드를 벗어났을 때 유효성 검사
const checkEmail = () => {
  emailTouched.value = true;
  emailError.value = !validateEmail(email.value);
};

// 이메일 인증 요청
const sendVerificationEmail = async () => {
  if (!emailError.value && email.value) {
    try {
      const response = await axios.post('/boot/users/verification-email/password', { email: email.value });

      if (response.data.success) {
        alert('인증번호를 전송하였습니다.');
        isEmailSent.value = true;
      } else {
        alert('인증번호 전송에 실패했습니다.');
      }
    } catch (error) {
      alert('인증번호 전송 중 오류가 발생했습니다.');
    }
  }
};

// 인증번호 확인
const verifyCode = async () => {
  if (verificationCode.value) {
    try {
      const response = await axios.post('/boot/users/verification-email/confirmation', {
        email: email.value,
        code: verificationCode.value
      });

      if (response.data.success) {
        alert('인증번호 확인이 완료되었습니다.');
        isVerified.value = true;
      } else {
        alert('인증번호가 일치하지 않습니다.');
      }
    } catch (error) {
      alert('인증번호 확인에 실패했습니다.');
    }
  }
};

const emit = defineEmits(['next-step', 'update-email']);

// 다음 단계로 넘어가는 함수
const next = () => {
  emit('update-email', email.value);  // 부모에게 이메일 전송
  emit('next-step');
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
  width: 270px;
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