<template>
  <div class="outer-container">
    <div class="form-container">
      <div class="inner-container">
        <form @submit.prevent="loginUser">
          <div class="input-group">
              <div class="login-message-container">
                <label for="phone">핸드폰 번호</label>
              </div>
              <div class="input-with-button">
                <input type="string" id="phone" required v-model="formData.userPhone" placeholder="핸드폰 번호 입력" />
                <button type="button" class="verification-btn" @click="handleSendSms">인증번호 받기</button>
              </div>
              <div class="error-message">{{ phoneErrorMessage }}</div>
            </div>
  
        <div class="input-group">
          <label for="verification-code">인증번호</label>
          <div class="input-with-button">
            <input type="string" id="verification-code" required v-model="formData.verificationCode" placeholder="인증번호 입력" />
            <button type="button" class="verification-btn" @click="handleVerifyCode">인증</button>
        </div>
        <div class="error-message">{{ verificationErrorMessage }}</div>
      </div>
      <div class="email-message">{{ emailMessage }}</div>
      </form>
    </div>
  </div>
</div>
</template>

<script setup>
  import { ref } from 'vue';
  import axios from 'axios';


  const formData = ref({
    userPhone: '',
    verificationCode: ''
  });

  const phoneErrorMessage = ref('');
  const verificationErrorMessage = ref('');
  const emailMessage = ref('');
  const verificationSent = ref(false);
  const verified = ref(false);


      // 인증번호 받기 버튼 클릭
  const handleSendSms = async () => {
      if (!formData.value.userPhone) {
        phoneErrorMessage.value = '핸드폰 번호를 입력해주세요.';
        return;
      }
      console.log('핸드폰 번호:', formData.value.userPhone); // 로그 추가
      phoneErrorMessage.value = '';  // 오류 메시지 초기화
      sendVerificationCode();
    };

  const sendVerificationCode = async () => {
    try {
        const response = await axios.post('/boot/users/send-sms', {
            userPhone: formData.value.userPhone,
        });
        console.log('SMS 전송 응답:', response.data); // 로그 추가
        if (response.status === 200) {
            verificationSent.value = true;
            alert('인증번호가 발송되었습니다.');
            // 인증번호 발송 후 버튼 비활성화
            document.querySelector('.verification-btn').disabled = true;
        }
    } catch (error) {
        phoneErrorMessage.value = '인증번호 발송에 실패했습니다. 다시 시도해주세요.';
        console.error('SMS 전송 오류:', error); // 로그 추가
    }
  };

    // 인증 버튼 클릭
    const handleVerifyCode = async () => {
      if (!formData.value.verificationCode) {
        verificationErrorMessage.value = '인증번호를 입력해주세요.';
        return;
      }
      console.log('입력된 인증번호:', formData.value.verificationCode); // 로그 추가
      verificationErrorMessage.value = '';  // 오류 메시지 초기화
      verifyCode();
    };

    // 인증번호 검증 후 이메일 확인
  const verifyCode = async () => {
      try {
        console.log('인증번호 검증 요청:', formData.value); // 로그 추가
        const response = await axios.post('/boot/users/verify-code', {
          userPhone: formData.value.userPhone,
          verificationCode: formData.value.verificationCode,
        });

        console.log('인증번호 검증 응답:', response.data); // 로그 추가
        if (response.status === 200 && response.data) {
          verified.value = true;
          emailMessage.value = response.data;
          verificationErrorMessage.value = '';  // 성공 시 오류 메시지 제거
        } else {
            verificationErrorMessage.value = '인증에 실패했습니다. 다시 시도해주세요.';
        }
      } catch (error) {
        console.error('인증번호 검증 오류:', error); // 로그 추가
        if (error.response && error.response.status === 400) {
          verificationErrorMessage.value = '인증번호가 잘못되었습니다. 다시 시도해주세요.';
        } else {
          verificationErrorMessage.value = '인증 요청 중 오류가 발생했습니다.';
        }
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
  max-width: 380px;
  margin-left: 880px;
  margin-top: auto;
  margin-bottom: auto;
  border-radius: 20px;
  box-shadow: -20px 20px 20px rgba(0, 255, 255, 0.5);
  position: relative;
}

.phone, .verification-code {
  display: flex;
  flex-direction: column;
}

.input-group {
  margin-bottom: 15px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: bold;
}

.input-with-button {
  display: flex;
  align-items: stretch;
}

input {
  flex-grow: 1;
  margin-right: 10px;
  height: 40px; /* button과 동일한 높이 */
  padding: 0 10px; /* 좌우 패딩 추가 */
  font-size: 14px;
  border-radius: 12px;
  border: none;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.15);
}

.verification-btn {
  font-size: 14px;
  width: 120px;
  height: 40px;
  border-radius: 12px;
  border-style: none;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.15);
  background-color: #01FDFE;
  white-space: nowrap;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-buttons {
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-items: center;
    margin-bottom: 15px;
}

.verification-btn {
  font-size: 14px;
  width: 100px;
  height: 40px;
  border-radius: 12px;
  border-style: none;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.15);
  background-color: #01FDFE;
}

.verification-btn:hover {
  background-color: #1DEBEC;
  box-shadow: none;
}

label {
  font-size: 14px;
  margin-bottom: 8px;
  font-weight: bold;
}

#phone, #verification-code {
  padding-left: 10px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin-bottom: 10px;
  width: 280px;
}

#verification-code {
  margin-bottom: 25px;
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

.finds {
  display: flex;
  justify-content: space-evenly;
  font-size: 12px;
  color: #767676;
}

.error-message {
  color: red;
  font-size: 12px;
  margin-left: 10px;
  margin-top: 5px;
}

.email-message {
  font-size: 14px;
  display: flex;
  flex-direction: column;
}

.login-message-container {
    display: flex;
}
</style>