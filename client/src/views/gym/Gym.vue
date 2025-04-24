<template>
  <GymBackGround />
  <div class="gym-page">
    <div v-if="error" class="error-message">{{ error }}</div>
    <div class="gym-container" v-if="gymData">
      <div class="gym-header">
        <h1 class="gym-title">{{ gymData.gymName }}</h1>
        <h2 class="equipment-title">보유 운동기구</h2>
      </div>
      <div class="equipment-table">
        <div class="table-row">
          <div v-for="category in topCategories" :key="category" class="table-column">
            <div class="header-item">{{ category }}</div>
            <div class="column-body">
              <button
                v-for="equipment in gymEquipment[category]"
                :key="equipment.exerciseEquipment.exerciseEquipmentCode"
                class="equipment-button"
                @click="showEquipmentInfo(equipment.exerciseEquipment.exerciseEquipmentCode)"
              >
                {{ equipment.exerciseEquipment.exerciseEquipmentName }}
              </button>
            </div>
          </div>
        </div>
        <div class="table-row bottom-row">
          <div v-for="category in bottomCategories" :key="category" class="table-column">
            <div class="header-item">{{ category }}</div>
            <div class="column-body">
              <button
                v-for="equipment in gymEquipment[category]"
                :key="equipment.exerciseEquipment.exerciseEquipmentCode"
                class="equipment-button"
                @click="showEquipmentInfo(equipment.exerciseEquipment.exerciseEquipmentCode)"
              >
                {{ equipment.exerciseEquipment.exerciseEquipmentName }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <EquipmentInfoModal
      v-if="selectedEquipment"
      :equipment="selectedEquipment"
      :equipmentImage="selectedEquipment ? selectedEquipment.exerciseImage : ''"
      @close="closeEquipmentModal"
      @confirm="closeEquipmentModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import '@/assets/css/gym/Gym.css';
import EquipmentInfoModal from '@/components/modal/exerciseequipment/EquipmentInfoModal.vue';
import defaultEquipmentImage from '@/assets/icons/equipment.svg';
import GymBackGround from '../../components/gym/GymBackGround.vue';

const gymData = ref(null);
const gymEquipment = ref({});
const selectedEquipment = ref(null);
const error = ref(null);

const topCategories = computed(() => ['등', '가슴', '어깨', '하체', '코어']);
const bottomCategories = computed(() => ['이두', '삼두', '유산소', '전완근']);

const fetchGymData = async () => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('/boot/users/mypage', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (response.data && response.data.gymCode) {
      gymData.value = {
        gymCode: response.data.gymCode,
        gymName: response.data.gymName
      };
      await fetchEquipmentData(response.data.gymCode);
    } else {
      error.value = '헬스장 정보가 없습니다.';
    }
  } catch (err) {
    console.error('Error fetching gym data:', err);
    error.value = '데이터를 불러오는 중 오류가 발생했습니다: ' + err.message;
  }
};

const fetchEquipmentData = async (gymCode) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get(`/boot/equipment_per_gym/equipment_per_gym_list`, {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      params: {
        gymCode: gymCode
      }
    });
    
    const equipmentList = response.data;
    if (Array.isArray(equipmentList) && equipmentList.length > 0) {
      gymEquipment.value = equipmentList.reduce((acc, item) => {
        const bodyPart = item.exerciseEquipment.bodyPart;
        if (!acc[bodyPart]) {
          acc[bodyPart] = [];
        }
        acc[bodyPart].push(item);
        return acc;
      }, {});
    } else {
      gymEquipment.value = {};
      error.value = '운동 기구 정보가 없습니다.';
    }
  } catch (err) {
    console.error('Error fetching equipment data:', err);
    error.value = '운동 기구 정보를 불러오는데 실패했습니다: ' + err.message;
    gymEquipment.value = {};
  }
};

const showEquipmentInfo = async (exerciseEquipmentCode) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get(`/boot/exercise_equipment/${exerciseEquipmentCode}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    selectedEquipment.value = {
      name: response.data.exerciseEquipmentName,
      part: response.data.bodyPart,
      videoUrl: response.data.recommendedVideo,
      description: response.data.exerciseDescription,
      exerciseImage: response.data.exerciseImage || defaultEquipmentImage
    };
    
  } catch (err) {
    console.error('Error fetching equipment data:', err);
    error.value = '운동 기구 정보를 불러오는데 실패했습니다: ' + err.message;
  }
};

const closeEquipmentModal = () => {
  selectedEquipment.value = null;
};

onMounted(() => {
  fetchGymData();
});
</script>