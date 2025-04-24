<template>
  <div class="addinfo-outer-container">
    <div class="addinfo-form-container">
      <div class="addinfo-inner-container">
        <form @submit.prevent="submitAddinfo">
          <div class="addinfo-phone-form">
            <label for="phone"> 전화번호 *</label>
            <input type="string" id="phone" required v-model="formData.userPhone" placeholder="전화번호 입력" />
          </div>
          
          <div class="addinfo-nickname-form">
            <label for="nickname">닉네임 *</label>
            <div class="addinfo-nickname-container">
              <input type="string" id="nickname" required v-model="formData.userNickname" @input="resetNicknameCheck" placeholder="닉네임 입력" />
              <button type="button" @click="checkNickname" class="nickname-check-btn" :disabled="!formData.userNickname">중복체크</button>
            </div>
            <div class="nickname-message">
              <div v-if="nicknameError" class="error-message">중복된 닉네임입니다. 다른 닉네임을 선택해주세요.</div>
              <div v-if="nicknameChecked && !nicknameError" class="success-message">사용 가능한 닉네임입니다.</div>
            </div>
          </div>

          <div class="addinfo-postalCode-form">
            <div class="left-postalcode">
              <label for="postalCode"> 주소 (우편번호) *</label>
              <input type="number" id="postalCode" required v-model="formData.postalCode" placeholder="우편번호 입력" />
            </div>
            <div class="right-postalcode">
              <div class="blank"></div>
              <button type="button" @click="findPostalCode" required class="search">검색</button>
            </div>
          </div>
  
        <div class="address-form">
          <label for="detailAddress">주소 (상세주소) *</label>
          <input type="text" id="detailAddress" required v-model="formData.detailAddress" placeholder="상세주소 입력" />
        </div>

        <div class="physical-info">
          <div class="height-form">
            <label for="height">키 (cm) *</label>
            <input type="number" id="height" required min="0" step="0.1" v-model="formData.userHeight" placeholder="키 입력" />
          </div>

          <div class="weight-form">
            <label for="weight">몸무게 (kg) *</label>
            <input type="number" id="weight" required min="0" step="0.1" v-model="formData.userWeight" placeholder="몸무게 입력" />
          </div>

          <div class="gender-form">
            <label for="gender">성별 *</label>
            <select v-model="formData.userGender" required id="gender" placeholder="성별 선택">
              <option value="M">남자</option>
              <option value="F">여자</option>
            </select>
          </div>
        </div>
        <div class="personal-info">
          
  
          <div class="age-form">
            <label for="age">나이 *</label>
            <input type="number" id="age" min="0" required v-model="formData.userAge" placeholder="나이 입력" />
          </div>

          <div class="gym-form">
            <label for="gym">헬스장</label>
            <input type="text" id="gym" v-model="formData.gymCode" placeholder="헬스장 검색" />
          </div>
        </div>

          <button type="submit" class="addinfo-done">입력 완료</button>
      </form>
    </div>
    </div>
</div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import axios from 'axios';

  const nicknameError = ref(false);  // 닉네임 중복 여부
  const nicknameChecked = ref(false); // 닉네임 중복 체크 완료 여부

  // 닉네임 중복 체크 상태 초기화 (닉네임이 변경되면 호출)
const resetNicknameCheck = () => {
  nicknameError.value = false;
  nicknameChecked.value = false;
};


  // 폼 데이터 상태
const formData = ref({
  userPhone: '',
  userNickname: '',
  userAddress: '',
  userGender: '',
  userAge: '',
  userHeight: '',
  userWeight: '',
  gymCode: ''
});

const findPostalCode = () => {
        new daum.Postcode({
            oncomplete: (data) => {
                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                // 각 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                let addr = ''; // 주소 변수
                let extraAddr = ''; // 참고항목 변수

                //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    addr = data.roadAddress;
                } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    addr = data.jibunAddress;
                }

                // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
                if(data.userSelectedType === 'R'){
                    // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                    // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                    if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                        extraAddr += data.bname;
                    }
                    // 건물명이 있고, 공동주택일 경우 추가한다.
                    if(data.buildingName !== '' && data.apartment === 'Y'){
                        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    }
                    // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                    if(extraAddr !== ''){
                        extraAddr = ' (' + extraAddr + ')';
                    }

                    // formData에 우편번호, 주소, 참고주소 업데이트
                    formData.value.postalCode = data.zonecode; // 우편번호
                    formData.value.detailAddress = `${addr} ${extraAddr}`; // 참고항목을 상세주소로
                
                } else {
                    formData.value.postalCode = '';
                }
                // 커서를 상세주소 필드로 이동한다.
                document.getElementById("detailAddress").focus();
            }
        }).open();
    };

console.log(formData.value);

// setup에서 emit을 인자로 받아옵니다
const emit = defineEmits(['submit']); // 이벤트 정의

// 닉네임 중복 체크 함수
const checkNickname = async () => {
  if (formData.value.userNickname) {
    try {
      const response = await axios.get('/boot/users/nickname/check', {
        params: { userNickname: formData.value.userNickname }
      });

      // 서버로부터 닉네임 중복 체크 결과 처리
      console.log(response.data.isDuplicate)

      if (response.data.isDuplicate) {
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

// 최종 제출 함수
const submitAddinfo = () => {
  console.log(nicknameError.value);
  console.log(nicknameChecked.value);
  if(nicknameError.value === false && nicknameChecked.value === true){
      
    formData.value.userAddress = `${formData.value.postalCode} ${formData.value.detailAddress}`;

    // LocalStorage에서 추가 정보를 가져옵니다
    // 토큰은 안줘도 됩니다.
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');
    const provider = localStorage.getItem('provider');
    const providerId = localStorage.getItem('providerId');

    // 추가 정보를 formData에 추가
    const finalData = {
      ...formData.value,
      userName,
      userEmail,
      provider,
      providerId
    };
    emit('submit', finalData); // 부모 컴포넌트로 데이터 전달
  }else {
    console.log("닉네임 체크를 해주세요!");
  }
};
  </script>
  
  <style scoped>
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap');
  
  * {
    font-family: 'Inter', sans-serif;
  }
  
  .addinfo-outer-container {
    width: 100vw;
    height: calc(100vh - 65px);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .addinfo-form-container {
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
  
  .addinfo-postalCode-form {
    display: flex;
    justify-content: space-between;
  }
  
  .left-postalcode, .right-postalcode, .addinfo-nickname-form, .addinfo-phone-form, .address-form, .height-form, .weight-form, .gender-form, .age-form, .gym-form {
    display: flex;
    flex-direction: column;
  }
  
  .blank {
    margin-top: 26.4px;
  }
  
  label {
    font-size: 14px;
    margin-bottom: 8px;
    font-weight: bold;
  }
  
  #phone, #nickname, #postalCode, #detailAddress, #gender, #age, #weight, #height, #gym {
    padding-left: 10px;
    padding-top: 5px;
    padding-bottom: 5px;
    margin-bottom: 15px;
    width: 320px;
  }
  
  input, select {
    font-size: 14px;
    width: 300px;
    height: 40px;
    border-radius: 12px;
    border-style: none;
    box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.15);
  }
  
  #nickname, #postalCode {
    width: 250px;
  }
  
  .search, .nickname-check-btn {
    background-color: #01FDFE;
    width: 60px;
    height: 35px;
    border-style: none;
    border-radius: 10px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2);
    font-weight: bold;
  }
  
  .search:hover, .nickname-check-btn:hover {
    background-color: #1DEBEC;
    box-shadow: none;
    cursor: pointer;
  }
  
  .addinfo-done {
    background-color: #01FDFE;
    width: 320px;
    height: 45px;
    border-radius: 20px;
    border-style: none;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2); 
    font-weight: bold;
    margin-top: 10px;
  }
  
  .addinfo-done:hover {
    background-color: #1DEBEC;
    box-shadow: none;
    cursor: pointer;
  }
  
  .physical-info, .personal-info {
    display: flex;
    justify-content: space-between;
  }
  
  #height, #weight, #gender {
    width: 100px;
  }
  
  #age, #gym {
    width: 150px;
  }
  
  .addinfo-nickname-form {
    position: relative;
    margin-bottom: 20px;
  }
  
  .addinfo-nickname-container {
    display: flex;
    gap: 10px;
  }
  
  .nickname-message {
    position: absolute;
    left: 0;
    top: 100%;
    width: 100%;
  }
  
  .error-message, .success-message {
    font-size: 12px;
    margin-top: 2px;
  }
  
  .error-message {
    color: red;
  }
  
  .success-message {
    color: green;
  }
  
  .addinfo-postalCode-form, .address-form, .physical-info, .personal-info {
    margin-top: 10px;
  }
  </style>