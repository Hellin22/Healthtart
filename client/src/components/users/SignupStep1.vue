<template>
  <div class="outer-container">
    <div class="form-container">
      <form @submit.prevent="next">
        <div class="email-container">
          <div class="email-form">
            <div class="left-email">

              <div class="message-container">
                <label for="email">이메일 *</label>
                <div v-if="emailTouched && emailError" class="error-message">올바른 이메일 형식을 입력해주세요.</div>
              </div>

              <div class="email-input-group">
                <input class="inputinfo" type="text" id="email" required v-model="formData.userEmail" @blur="checkEmail" placeholder="이메일 입력" />
                <button type="button" @click="sendVerificationEmail" class="auth" :disabled="emailError || !formData.userEmail || isEmailSent">
                  인증
                </button>
              </div>
              
              <div class="verification-form">
                <label for="verificationCode">인증번호 *</label>
                <div class="verification-input-group">
                  <input class="inputinfo" type="text" id="verificationCode" placeholder="   5분내로 인증번호를 입력해주세요" v-model="verificationCode" :disabled="!isEmailSent" />
                  <button type="button" @click="verifyCode" class="auth" :disabled="!isEmailSent || isVerified">확인</button>
                </div>
              </div>

              <div class="nickname-form">
                <label for="nickname">닉네임 *</label>
                <div class="nickname-input-group">
                  <input class="inputinfo" type="text" id="nickname" required v-model="formData.userNickname" @input="resetNicknameCheck" placeholder="닉네임 입력" />
                  <button type="button" @click="checkNickname" class="auth" :disabled="!formData.userNickname">중복체크</button>
                </div>
                <div v-if="nicknameError" class="error-message">중복되거나 닉네임 길이를 벗어났습니다.(한글 7글자, 영어 15글자)</div>
                <div v-if="nicknameChecked && !nicknameError" class="success-message">사용 가능한 닉네임입니다.</div>
              </div>

            </div>
          </div>
        </div>

        <div class="password-form">
          <label for="password">비밀번호 *</label>
          <input type="password" id="password" required v-model="formData.userPassword" placeholder="비밀번호 입력" />
        </div>

        <div class="name-form">
          <label for="name">이름 *</label>
          <input type="text" required v-model="formData.userName" id="name" placeholder="이름 입력" />
        </div>

        <div class="phonenumber-form">
          <label for="phonenumber">전화번호 *</label>
          <input type="text" id="phonenumber" required v-model="formData.userPhone" placeholder="전화번호 입력" />
        </div>

        <!-- "다음" 버튼은 이메일 인증 및 닉네임 중복 체크가 완료되면 활성화 -->
        <button type="submit" class="next-btn" :disabled="!isVerified || !nicknameChecked || nicknameError">다음</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

// 폼 데이터 상태
const formData = ref({
  userEmail: '',
  userPassword: '',
  userName: '',
  userPhone: '',
  userNickname: ''
});

const verificationCode = ref('');
const isEmailSent = ref(false);
const isVerified = ref(false);
const emailError = ref(false);
const emailTouched = ref(false);
const nicknameError = ref(false);  // 닉네임 중복 여부
const nicknameChecked = ref(false); // 닉네임 중복 체크 완료 여부

// 이메일 유효성 검사 함수
const validateEmail = (email) => {
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailPattern.test(email);
};

// 이메일 입력 필드를 벗어났을 때 유효성 검사
const checkEmail = () => {
  emailTouched.value = true;
  if (!validateEmail(formData.value.userEmail)) {
    emailError.value = true;
  } else {
    emailError.value = false;
  }
};

// 닉네임 중복 체크 상태 초기화 (닉네임이 변경되면 호출)
const resetNicknameCheck = () => {
  nicknameError.value = false;
  nicknameChecked.value = false;
};

// 닉네임 중복 체크 함수
const checkNickname = async () => {
  if (formData.value.userNickname) {
    try {
      const response = await axios.get('/boot/users/nickname/check', {
        params: { userNickname: formData.value.userNickname }
      });

      // 서버로부터 닉네임 중복 체크 결과 처리
      if (!response.data.isValid) {
        nicknameError.value = true; // 중복됨
        nicknameChecked.value = false; // 체크 실패
      } else {
        nicknameError.value = false; // 중복 아님
        nicknameChecked.value = true; // 체크 완료
      }
    } catch (error) {
      alert('닉네임 중복 체크 중 오류가 발생했습니다.');
      console.error('Error:', error);
      nicknameError.value = true;
      nicknameChecked.value = false;
    }
  }
};

// 인증 메일 전송
const sendVerificationEmail = async () => {
  if (!emailError.value && formData.value.userEmail) {
    try {
      const response = await axios.post('/boot/users/verification-email', {
        email: formData.value.userEmail
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (response.data.success === true) {
        alert('인증번호를 전송하였습니다.');
        isEmailSent.value = true;
      } else if (response.data.error.statusCode === 409) {
        alert('중복된 이메일입니다. 다른 이메일을 사용해주세요.');
      } else {
        alert('인증번호 전송에 실패했습니다.');
      }
    } catch (error) {
      alert('인증번호 전송 중 오류가 발생했습니다.');
      console.error('Error:', error);
    }
  }
};

// 인증번호 확인
const verifyCode = async () => {
  if (verificationCode.value) {
    try {
      const response = await axios.post('/boot/users/verification-email/confirmation', {
        email: formData.value.userEmail,
        code: verificationCode.value
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      console.log(response.data);
      if (response.data.success === true) {
        alert('인증번호 확인이 완료되었습니다.');
        isVerified.value = true;
      }else{
        alert('인증번호가 일치하지 않습니다.');
      }
    } catch (error) {
      alert('인증번호 확인에 실패했습니다.');
      console.error('Error:', error);
    }
  }
};

// 폼 제출 함수
const emit = defineEmits(['nextStep']);
const next = () => {
  if (emailError.value || !isVerified.value || !nicknameChecked.value || nicknameError.value) {
    alert('이메일 인증 및 닉네임 중복 체크가 필요합니다.');
    return;
  }
  emit('nextStep', formData.value);
};
</script>
  
  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap');

  * {
    font-family: 'Inter', sans-serif;
  }
  .outer-container {
    width: 100vw;
    height: calc(100vh - 65px);;
    display: flex;
    justify-content: center;
    align-items: center;
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

  .inputinfo {
    max-width: 260px;
  }

  .email-form {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .left-email, .password-form, .name-form, .phonenumber-form, .nickname-form {
    display: flex;
    flex-direction: column;
    position: relative;
  }

  label {
    font-size: 14px;
    margin-bottom: 8px;
    font-weight: bold;
  }

  #email, #password, #name, #phonenumber, #nickname, #verificationCode {
    padding-left: 10px;
    padding-top: 5px;
    padding-bottom: 5px;
    margin-bottom: 15px;
    width: 320px;
  }

  #email {
    margin-bottom: 5px;
  }

  .form-group {
    margin-bottom: 15px;
  }

  input {
    font-size: 14px;
    width: 300px;
    height: 40px;
    border-radius: 12px;
    border-style: none;
    box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.15);
  }

  #email {
    width: 250px;
  }

  .auth {
    background-color: #01FDFE;
    width: 60px;
    height: 35px;
    border-style: none;
    border-radius: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
    font-weight: bold;
  }

  .auth:hover {
    background-color: #1DEBEC;
    box-shadow: none;
    cursor: pointer;
  }

  .next-btn {
    background-color: #01FDFE;
    width: 320px;
    height: 45px;
    border-radius: 20px;
    border-style: none;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2); 
    font-weight: bold;
    margin-top: 10px;
  }

  .next-btn:hover {
    background-color: #1DEBEC;
    box-shadow: none;
    cursor: pointer;
  }

  .error-message {
  color: red;
  font-size: 11px;
  margin-left: 10px;
}

  .email-message {
    display: flex;
    flex-direction: column;
  }

  .email-container {
    margin-bottom: 15px;
  }

  .message-container {
    display: flex;
  }

  .email-input-group, .nickname-input-group {
    display: flex;
    gap: 10px;
    margin-bottom: 15px;
}

  .verification-input-group {
    display: flex;
    gap: 10px;
    margin-bottom: 8px;
  }

  

.email-input-group input, .verification-input-group input, .nickname-input-group input {
  flex: 1;
  width: auto;
}

.auth {
  width: 60px;
  height: 40px; /* 높이를 input과 맞춤 */
  flex-shrink: 0;
}

#email, #verificationCode, #nickname {
  flex: 1;
}

.success-message {
  position: absolute;
  color: green;
  font-size: 12px;
  margin-left: 10px;
  top: 75px;
}

.verification-form label {
  display: block;
  margin-bottom: 8px;
}

  </style>
  