<template>
  <div class="mypageedit-layout">
    <MyPageSideMenu />
    <main class="mypageedit-content" v-if="formData">
      <BackGround class="background-component" />
      <h2 class="last-updated">마지막으로 수정한 날짜: {{ formData.updatedAt }}</h2>
      <form class="mypageedit-form" @submit.prevent="updateProfile">
        <div><label><span></span></label></div>
        <div class="mypageedit-form-group-check password-change-group">
          <label for="passwordChangeCheckbox" class="password-change-label">
            <span>비밀번호 변경</span>
            <input
              id="passwordChangeCheckbox"
              class="mypageedit-form-checkbox"
              type="checkbox"
              v-model="isPasswordChangeEnabled"
              @change="triggerAnimation"
            />
          </label>
        </div>
        <div><label><span></span></label></div>
        <div class="mypageedit-form-group password-animated" v-if="isPasswordChangeEnabled">
          <label for="currentPassword">현재 비밀번호</label>
          <input
            class="mypageedit-input"
            type="password"
            id="currentPassword"
            v-model="currentPassword"
          />
        </div>
        <div class="mypageedit-form-group password-animated" v-if="isPasswordChangeEnabled">
          <label for="newPassword">새 비밀번호</label>
          <input
            class="mypageedit-input"
            type="password"
            id="newPassword"
            v-model="newPassword"
          />
        </div>
        <div class="mypageedit-form-group password-animated" v-if="isPasswordChangeEnabled">
          <label for="confirmPassword">비밀번호 확인</label>
          <input
            class="mypageedit-input"
            type="password"
            id="confirmPassword"
            v-model="confirmPassword"
          />
        </div>
        <button class="update-btn" :class="{ animated: animationTriggered }" type="submit">
          완료
        </button>
      </form>
      <div class="extra-content">
        <div class="extra-section" :class="{ animated: animationTriggered }">
          <div class="extra-button-group">
            <span class="label-text">등록 헬스장</span>
            <button class="add-btn" @click="openGymModal" v-if="!selectedGym">추가</button>
            <button v-if="selectedGym" @click="confirmDelete('gym')" class="remove-btn">삭제</button>
          </div>
          <div v-if="selectedGym" class="selected-gym">
            <div class="gym-info new-design">
              <div class="new-design-header">
                <img src="@/assets/icons/gym.svg" alt="헬스장" class="gymicon new-design-icon" />
                <h3 class="new-design-title">{{ selectedGym.gymName }}</h3>
              </div>
              <div class="new-design-actions">
                <button class="new-design-action-btn">운동기구 보기</button>
                <button class="new-design-action-btn">주소 보기</button>
              </div>
            </div>
          </div>
        </div>
        <div class="extra-section" :class="{ animated: animationTriggered }">
          <div class="extra-button-group">
            <span class="label-text">등록 라이벌</span>
            <button v-if="!registeredRival" class="add-btn" @click="openRivalModal">추가</button>
            <button v-if="registeredRival" @click="confirmDelete('rival')" class="remove-btn">
              삭제
            </button>
          </div>
          <div v-if="registeredRival" class="selected-rival">
            <div class="rival-info new-design">
              <div class="new-design-header-rival">
                <img src="@/assets/icons/usericon.svg" alt="유저" class="usericon new-design-icon" />
                <h3 class="new-design-title">{{ registeredRival.userNickname }}</h3>
              </div>
              <div class="new-design-actions">
                <button class="new-design-action-btn" @click="viewRivalWorkoutRecord(registeredRival.rivalUserCode)">
                  운동기록 보기
                </button>
                <button class="new-design-action-btn" @click="viewRivalInbody(registeredRival.rivalUserCode)">
                  인바디 보기
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <RegisterGymModal
        :isOpen="isGymModalOpen"
        @close="closeGymModal"
        @selectGym="handleGymSelection"
      />
      <RegisterRivalModal
        :isOpen="isRivalModalOpen"
        @close="closeRivalModal"
        @register="handleRivalRegistration"
      />
      <DeleteModal
        :isOpen="isDeleteModalOpen"
        @close="closeDeleteModal"
        @confirm="deleteItem"
        :itemType="deleteItemType"
      />
    </main>
    <RightSide />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';
import RightSide from '@/components/RightSide.vue';
import BackGround from '@/components/BackGround.vue';
import RegisterGymModal from '@/components/modal/gym/RegisterGymModal.vue';
import RegisterRivalModal from '@/components/modal/rival/RegisterRivalModal.vue';
import DeleteModal from '@/components/modal/DeleteModal.vue';
import '@/assets/css/user/MyPageEdit.css';
import MyPageSideMenu from '../../components/MyPageSideMenu.vue';
import { jwtDecode } from 'jwt-decode';

const router = useRouter();
const route = useRoute();
const formData = ref(null);
const currentPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const isGymModalOpen = ref(false);
const isRivalModalOpen = ref(false);
const isDeleteModalOpen = ref(false);
const deleteItemType = ref('');
const isPasswordChangeEnabled = ref(false);
const selectedGym = ref(null);
const registeredRival = ref(null);
const animationTriggered = ref(false);

const triggerAnimation = () => {
  animationTriggered.value = true;
};

onMounted(async () => {
  try {
    await fetchUserData();
  } catch (error) {
    console.error('Error initializing data:', error);
  }
});

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

    if (!selectedGym.value && response.data.gymCode && response.data.gymName) {
      selectedGym.value = {
        gymCode: response.data.gymCode,
        gymName: response.data.gymName,
      };
    }

    if (!registeredRival.value && response.data.rivalUserCode && response.data.rivalNickname) {
      registeredRival.value = {
        rivalUserCode: response.data.rivalUserCode,
        userNickname: response.data.rivalNickname
      };
    }

    return response.data;
  } catch (error) {
    console.error('Error fetching user data:', error);
    throw error;
  }
};

const updateProfile = async () => {
  try {
    const token = localStorage.getItem('token');
    const decodedToken = jwtDecode(token);
    const userCode = decodedToken.sub;

    if (isPasswordChangeEnabled.value) {
      if (newPassword.value !== confirmPassword.value) {
        alert('새 비밀번호가 일치하지 않습니다.');
        return;
      }

      await axios.patch('/boot/users/mypage/edit/password', {
        currentPassword: currentPassword.value,
        newPassword: newPassword.value
      }, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });
    }

    if (selectedGym.value && selectedGym.value.businessNumber) {
      await axios.post('/boot/users/register-gym', {
        userCode: userCode,
        businessNumber: selectedGym.value.businessNumber,
      }, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });
    }

    alert('프로필이 성공적으로 수정되었습니다.');
    router.push('/mypage');
  } catch (error) {
    console.error('Error updating profile:', error);
    alert('프로필 수정에 실패했습니다.');
  }
};

const confirmDelete = (itemType) => {
  deleteItemType.value = itemType;
  isDeleteModalOpen.value = true;
};

const deleteItem = async () => {
  const token = localStorage.getItem('token');
  const decodedToken = jwtDecode(token);
  const userCode = decodedToken.sub;

  try {
    if (deleteItemType.value === 'gym') {
      await axios.post('/boot/users/remove-gym', {
        userCode: userCode,
      }, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });
      selectedGym.value = null;
    } else if (deleteItemType.value === 'rival') {
      const rivalResponse = await axios.get(`/boot/rival`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      const rivalMatchCode = rivalResponse.data.rivalMatchCode;

      if (!rivalMatchCode) {
        throw new Error('라이벌 매치 코드를 찾을 수 없습니다.');
      }

      await axios.delete(`/boot/rival/${rivalMatchCode}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });
      
      registeredRival.value = null;
    }
    
    alert(`성공적으로 삭제되었습니다.`);
    closeDeleteModal();
  } catch (error) {
    console.error('Error deleting item:', error);
    if (error.message === '라이벌 매치 코드를 찾을 수 없습니다.') {
      alert('라이벌 정보를 찾을 수 없습니다.');
    } else {
      alert('삭제에 실패했습니다.');
    }
  }
};

const openGymModal = () => {
  isGymModalOpen.value = true;
  document.body.style.overflow = 'hidden';
};

const closeGymModal = () => {
  isGymModalOpen.value = false;
  document.body.style.overflow = '';
};

const handleGymSelection = (gym) => {
  selectedGym.value = gym;
  closeGymModal();
};

const openRivalModal = () => {
  isRivalModalOpen.value = true;
  document.body.style.overflow = 'hidden';
};

const closeRivalModal = () => {
  isRivalModalOpen.value = false;
  document.body.style.overflow = '';
};

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false;
  deleteItemType.value = '';
};

const handleRivalRegistration = (rival) => {
  registeredRival.value = rival;
  closeRivalModal();
};
</script>