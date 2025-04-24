<template>
  <div class="mypage-layout">
    <MyPageSideMenu />
    <main class="mypage-content">
      <BackGround class="background-component" />
      <div v-if="formData">
        <h2 class="last-updated">마지막으로 수정한 날짜: {{ formData.updatedAt }}</h2>
        <form class="mypage-form">
          <div class="form-group">
            <label for="name">이름</label>
            <input type="text" id="name" v-model="formData.userName" readonly />
          </div>
          <div class="form-group">
            <label for="email">이메일</label>
            <input type="text" id="email" v-model="formData.userEmail" readonly />
          </div>
          <div class="form-group">
            <label for="password">비밀번호</label>
            <input type="password" id="password" value="********" readonly />
          </div>
          <div class="form-group">
            <label for="phone">전화번호</label>
            <input type="text" id="phone" v-model="formData.userPhone" readonly />
          </div>
          <div class="form-group">
            <label for="nickname">닉네임</label>
            <input type="text" id="nickname" v-model="formData.userNickname" readonly />
          </div>
          <div class="form-group">
            <label for="gender">성별</label>
            <input type="text" id="gender" v-model="formData.userGender" readonly />
          </div>
          <div class="form-group">
            <label for="height">키</label>
            <input type="text" id="height" v-model="formData.userHeight" readonly />
          </div>
          <div class="form-group">
            <label for="weight">몸무게</label>
            <input type="text" id="weight" v-model="formData.userWeight" readonly />
          </div>
          <div class="button-group">
            <button type="button" class="inbody-btn" @click="openInbodyModal">내 인바디 등록</button>
            <button type="button" class="edit-btn" @click="goToEditPage">수정</button>
          </div>
        </form>
        <RegisterInbodyModal
          :isOpen="isInbodyModalOpen"
          :closeModal="closeModal"
          :updateUserData="updateUserData"
        />
        <div class="extra-content">
          <div class="extra-section">
            <div class="extra-button-group">
              <span class="label-text">등록 헬스장</span>
            </div>
            <div v-if="selectedGym" class="selected-gym">
              <div class="gym-info new-design">
                <div class="new-design-header">
                  <img src="@/assets/icons/gym.svg" alt="헬스장" class="gymicon new-design-icon" />
                  <h3 class="new-design-title">{{ selectedGym.gymName }}</h3>
                </div>
                <div class="new-design-actions">
                  <button class="new-design-action-btn" @click="showGymEquipment">운동기구 보기</button>
                  <button class="new-design-action-btn" @click="showGymAddress">주소 보기</button>
                </div>
              </div>
            </div>
          </div>
          <div class="extra-section">
            <div class="extra-button-group">
              <span class="label-text">등록 라이벌</span>
            </div>
            <div v-if="registeredRival && registeredRival.rivalMatchCode" class="selected-rival">
              <div class="rival-info new-design">
                <div class="new-design-header-rival">
                  <img src="@/assets/icons/usericon.svg" alt="유저" class="usericon new-design-icon" />
                  <h3 class="new-design-title">{{ registeredRival.userNickname }}</h3>
                </div>
                <div class="new-design-actions">
                  <button class="new-design-action-btn" @click="viewRivalWorkoutRecord(registeredRival.rivalUserCode)">운동기록 보기</button>
                  <button class="new-design-action-btn" @click="viewRivalInbody(registeredRival.rivalUserCode)">인바디 보기</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    <RightSide />
  </div>
</template>

<script setup>
import { ref, onMounted, provide } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import BackGround from '@/components/BackGround.vue';
import '@/assets/css/user/MyPage.css';
import RegisterInbodyModal from '@/components/modal/inbody/RegisterInbodyModal.vue';
import RightSide from '@/components/RightSide.vue';
import MyPageSideMenu from '../../components/MyPageSideMenu.vue';
import { jwtDecode } from 'jwt-decode';

const route = useRoute();
const router = useRouter();
const formData = ref(null);
const selectedGym = ref(null);
const registeredRival = ref(null);
const isInbodyModalOpen = ref(false);

provide('selectedGym', selectedGym);
provide('registeredRival', registeredRival);

const fetchUserData = async () => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('/boot/users/mypage', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    formData.value = response.data;

    if (response.data.gymCode && response.data.gymName) {
      selectedGym.value = {
        gymCode: response.data.gymCode,
        gymName: response.data.gymName,
      };
    } else {
      selectedGym.value = null;
    }
  } catch (error) {
    console.error('유저 정보 조회 중 오류가 발생했습니다.:', error);
  }
};

const fetchRivalNickname = async (rivalUserCode) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get(`/boot/users/usercode/${rivalUserCode}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    return response.data.userNickname;
  } catch (error) {
    console.error(`라이벌 닉네임 조회 중 오류 발생: ${rivalUserCode}`, error);
    return null;
  }
};

const fetchRival = async () => {
  try {
    const token = localStorage.getItem('token');
    const decodedToken = jwtDecode(token);
    const userCode = decodedToken.sub;

    const response = await axios.get('/boot/rival', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    const rivalData = response.data;

    if (rivalData && rivalData.rivalUserCode) {
      const userNickname = await fetchRivalNickname(rivalData.rivalUserCode);
      registeredRival.value = {
        ...rivalData,
        userNickname,
      };
    } else {
      registeredRival.value = null;
    }

  } catch (error) {
    console.error('라이벌 조회 중 오류가 발생했습니다:', error);
    registeredRival.value = null;
  }
};

const updateUserData = (updatedUserData) => {
  formData.value.userHeight = updatedUserData.height;
  formData.value.userWeight = updatedUserData.weight;
};

const goToEditPage = async () => {
  router.push({
    name: 'MyPageEdit',
    params: {
      selectedGym: selectedGym.value,
      registeredRival: registeredRival.value
    }
  });
};

const openInbodyModal = () => {
  isInbodyModalOpen.value = true;
};

const handleInbodyModalClose = () => {
  isInbodyModalOpen.value = false;
  fetchUserData();
};

const closeModal = () => {
  isInbodyModalOpen.value = false;
  handleInbodyModalClose();
};

onMounted(() => {
  fetchUserData();
  fetchRival();
});
</script>
