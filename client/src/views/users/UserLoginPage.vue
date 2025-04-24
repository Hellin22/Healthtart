<template>
    <div class="login-container">
      <h2>Login</h2>
      
      <!-- Email 입력 필드 -->
      <div class="input-group">
        <label for="email">Email:</label>
        <input 
          type="email" 
          v-model="email" 
          placeholder="Enter your email" 
          required 
          @keyup.enter="loginUser"
        />
      </div>
  
      <!-- Password 입력 필드 -->
      <div class="input-group">
        <label for="password">Password:</label>
        <input 
          type="password" 
          v-model="password" 
          placeholder="Enter your password" 
          required 
          @keyup.enter="loginUser"
        />
      </div>
  
      <!-- 로그인 버튼 -->
      <button @click="loginUser" class="login-button">Login</button>
  
      <!-- 에러 메시지 출력 -->
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, inject } from 'vue';
  import axios from 'axios';
  import { useRouter } from 'vue-router';
  import { jwtDecode } from 'jwt-decode';
  
  const email = ref('');
  const password = ref('');
  const errorMessage = ref('');
  const router = useRouter();
  
  // loginState를 inject
  const loginState = inject('loginState');
  
  // 로그인 처리 함수
  const loginUser = async () => {
    errorMessage.value = '';
  
    try {
      const response = await axios.post('/boot/users/login', {
        userEmail: email.value,
        userPassword: password.value,
      }, {
        headers: {
          'Content-Type': 'application/json',
        }
      });
  
      const token = response.headers['authorization'];
      if (token) {
        localStorage.setItem('token', token);
        const decodedToken = jwtDecode(token);
        
        // loginState 업데이트
        loginState.state.isLoggedIn = true;
        loginState.state.userNickname = decodedToken.nickname;
        
        console.log('Login successful:', decodedToken.nickname);
        
        // 로그인 성공 이벤트 발생
        loginState.checkLoginStatus();
        
        router.push('/');
      }
    } catch (error) {
      errorMessage.value = 'Login failed. Please check your credentials.';
      console.error('Login error:', error);
    }
  };
  </script>
  
  <style scoped>
  .login-container {
    width: 300px;
    margin: 100px auto;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 5px;
    background-color: #f9f9f9;
  }
  
  .input-group {
    margin-bottom: 15px;
  }
  
  .input-group label {
    display: block;
    margin-bottom: 5px;
  }
  
  .input-group input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 3px;
  }
  
  .login-button {
    width: 100%;
    padding: 10px;
    background-color: #007bff;
    color: #fff;
    border: none;
    border-radius: 3px;
    cursor: pointer;
  }
  
  .login-button:hover {
    background-color: #0056b3;
  }
  
  .error-message {
    color: red;
    margin-top: 10px;
  }
  </style>
  