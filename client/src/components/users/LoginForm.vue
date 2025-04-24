<template>
    <div class="outer-container">
      <div class="form-container">
        <div class="inner-container">
          <form @submit.prevent="loginUser">

            <div class="email">
                <div class="login-message-container">
                <label for="email">이메일</label>
                <div v-if="emailTouched && emailError" class="error-message">{{ errorMessage }}</div>
                </div>
                <input type="email" id="email" required v-model="formData.userEmail" @blur="checkEmail" placeholder="이메일 입력" />
            </div>
  
        <div class="password">
          <label for="password">비밀번호</label>
          <input type="password" id="password" required @keyup.enter ="loginUser" v-model="formData.userPassword" placeholder="비밀번호 입력" />
        </div>

        <div class="login-buttons">
          <button type="button" @click="loginUser" class="login">로그인</button>
          <button type="button" @click="kakaoLogin" class="kakao">
            <img src="@/assets/icons/kakao-icon.svg" alt="카카오 아이콘" class="icon"/>카카오 로그인</button>
          <button type="button" @click="googleLogin" class="google">
            <img src="@/assets/icons/google-icon.svg" alt="구글 아이콘" class="icon"/>구글 로그인</button>
        </div>

        <div class="finds">
          <div class="find-email" @click="goToFindEmail">이메일 찾기</div>
          <div>|</div>
          <div class="find-password" @click="goToPasswordReset">비밀번호 재설정</div>
          <div>|</div>
          <div class="signup" @click="goToSignup">회원가입</div>
        </div>
      </form>
    </div>
    </div>
</div>
</template>

<script setup>
import { ref, inject } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

const formData = ref({
  userEmail: '',
  userPassword: ''
});

const router = useRouter();

// emit 인자로 받음
const emit = defineEmits(['loginSuccess'])


const errorMessage = ref('');
const emailError = ref(false); // 이메일 형식 오류 상태
const emailTouched = ref(false); // 이메일 입력 폼이 처음 선택되었는지 확인

 // loginState를 inject
 const loginState = inject('loginState');

// 이메일 유효성 검사 함수
const validateEmail = (email) => {
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailPattern.test(email);
};

// 이메일 입력 필드를 벗어났을 때 유효성 검사
const checkEmail = () => {
  emailTouched.value = true; // 입력 필드를 벗어났음을 표시
  if (!validateEmail(formData.value.userEmail)) {
    emailError.value = true; // 이메일이 유효하지 않으면 오류 상태를 true로 설정
    errorMessage.value = "올바른 이메일 형식을 입력해주세요.";  
} else {
    emailError.value = false; // 유효한 이메일이면 오류 상태를 false로 설정
  }
};

// 로그인 함수
const loginUser = async () => {
  // 이메일과 비밀번호가 빈 값이 아닌지, 이메일 형식이 올바른지 확인
  if (!formData.value.userEmail || !formData.value.userPassword || emailError.value) {
    alert("아이디, 비밀번호를 확인해주세요");
    return; // 입력이 잘못되었으면 함수 종료
  }

  try {
    const response = await axios.post('/boot/users/login', {
      userEmail: formData.value.userEmail,
      userPassword: formData.value.userPassword
    }, {
      headers: {
        'Content-Type': 'application/json',
      }
    });

    console.log('HTTP 응답 상태 코드:', response.status);
    console.log('전체 응답 데이터:', response);

    const token = response.headers['authorization'];
    if (token) {
      localStorage.setItem('token', token);
      const decodedToken = jwtDecode(token);

      console.log('Decoded JWT Token:', decodedToken);
      
      // loginState 업데이트
      loginState.state.isLoggedIn = true;
      loginState.state.userNickname = decodedToken.nickname;
      
      console.log('Login successful:', decodedToken.nickname);

      alert(decodedToken.nickname+"님, 환영합니다."); // 로그인 성공 시 alert창 띄우기

      // 로그인 성공 이벤트 발생
      emit('loginSuccess', decodedToken.nickname);  // 부모 컴포넌트로 로그인 상태 전달
    } else {
      errorMessage.value = '인증 토큰이 없습니다. 로그인 실패';
      alert("아이디, 비밀번호를 확인해주세요");
    }
  } catch (error) {
    console.error('로그인 중 오류 발생:', error);
    errorMessage.value = '로그인 중 오류가 발생했습니다. 다시 시도해주세요.';
  }
};

const goToSignup = () => {
  router.push('/users/signup'); // /users/signup 경로로 이동
}

const goToPasswordReset = () => {
  router.push('/users/password'); // /users/password 경로로 이동
};

const goToFindEmail = () => {
  router.push('/users/findemail');
}

// const kakaoLogin = () => {
//   window.location.href = '/boot/oauth2/authorization/kakao';
// };

  // pkce를 위한 codeVerifier 생성
  const generateCodeVerifier = () => {
    const array = new Uint32Array(56);
    window.crypto.getRandomValues(array);
    return Array.from(array, dec => ('0' + dec.toString(16)).slice(-2)).join('');
  };

  // pkce를 위한 codeChallenge 생성
  const generateCodeChallenge = async (verifier) => {
    const encoder = new TextEncoder();
    const data = encoder.encode(verifier);
    const digest = await crypto.subtle.digest('SHA-256', data);
    return btoa(String.fromCharCode(...new Uint8Array(digest)))
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=+$/, '');
  };

  const kakaoLogin = async () => {
    const codeVerifier = generateCodeVerifier();
    sessionStorage.setItem('code_verifier', codeVerifier);
    
    const codeChallenge = await generateCodeChallenge(codeVerifier);
    
    const clientId = '6cbd43ba1b1fa2803c31d42c0b49c27f'; // 카카오 REST API 키
    const redirectUri = 'http://localhost/users/oauth/callback'; // 프론트 콜백 주소
    console.log("ㅋㅋㅋ 뭐임?")
    const kakaoAuthUrl = 
    `https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${clientId}&redirect_uri=${redirectUri}&code_challenge=${codeChallenge}&code_challenge_method=S256`;
    window.location.href = kakaoAuthUrl;
  };
const googleLogin = () => {
  window.location.href = '/boot/oauth2/authorization/google';
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

.email, .password {
  display: flex;
  flex-direction: column;
}

.login-buttons {
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-items: center;
    margin-bottom: 15px;
}

.login, .kakao, .google {
  font-size: 14px;
  width: 280px;
  height: 40px;
  border-radius: 12px;
  border-style: none;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.login {
  background-color: #01FDFE;
}

.kakao {
  background-color: #FEE500;
}

.google {
  background-color: #FFFFFF;
}

.login:hover {
  background-color: #1DEBEC;
  box-shadow: none;
  cursor: pointer;
}

.kakao:hover {
  background-color: #EAD515;
  box-shadow: none;
  cursor: pointer;
}

.google:hover {
  background-color: #EAEAEA;
  box-shadow: none;
  cursor: pointer;
}

.find-email, .find-password, .signup:hover {
  cursor: pointer;
}

label {
  font-size: 14px;
  margin-bottom: 8px;
  font-weight: bold;
}

#email, #password {
  padding-left: 10px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin-bottom: 10px;
  width: 320px;
}

#password {
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
}

  .email-message {
    display: flex;
    flex-direction: column;
  }

.login-message-container {
    display: flex;
}

.icon {
  width: 20px;
  height: 20px;
  position: absolute;
  left: 10px;
}
</style>