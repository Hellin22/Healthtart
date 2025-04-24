<template>
  <div class="modal-overlay" v-if="isOpen">
    <div class="rival-modal-content">
      <h2 class="modal-title">라이벌 등록</h2>
      <div class="input-container">
        <input 
          type="text" 
          v-model="searchQuery" 
          @input="filterUsers" 
          placeholder="라이벌 닉네임 검색" 
          class="modal-input" 
        />
      </div>
      <div class="rival-list" v-if="filteredUsers.length">
        <div 
          v-for="user in filteredUsers" 
          :key="user.userCode" 
          class="rival-item"
          @click="selectRival(user)"
        >
          {{ user.userNickname }}
        </div>
      </div>
      <div v-else-if="searchQuery && !filteredUsers.length" class="no-results">
        검색 결과가 없습니다.
      </div>
      <div class="modal-actions">
        <button class="modal-cancel-btn" @click="closeModal">취소</button>
        <button 
          class="modal-confirm-btn" 
          @click="registerRival" 
          :disabled="!selectedRival"
        >
          등록
        </button>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const searchQuery = ref('');
const allUsers = ref([]);
const filteredUsers = ref([]);
const selectedRival = ref(null);

const props = defineProps({
  isOpen: Boolean,
});
const emit = defineEmits(['close', 'register']);


const closeModal = () => {
  searchQuery.value = '';
  filteredUsers.value = [];
  selectedRival.value = null;
  emit('close');
};

const fetchAllUsers = async () => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('/boot/users', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    allUsers.value = response.data;
  } catch (error) {
    console.error('Error fetching users:', error);
  }
};

const filterUsers = () => {
  if (searchQuery.value.length < 1) {
    filteredUsers.value = [];
    return;
  }
  
  filteredUsers.value = allUsers.value.filter(user => 
    user.userNickname.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
};

const selectRival = (user) => {
  selectedRival.value = user;
  searchQuery.value = user.userNickname;
};

const registerRival = async () => {
  if (selectedRival.value) {
    try {
      const token = localStorage.getItem('token');
      await axios.post(`/boot/rival/${selectedRival.value.userCode}`, {}, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      
      emit('register', selectedRival.value);
      closeModal();
    } catch (error) {
      console.error('Error registering rival:', error);
      alert('라이벌 등록에 실패했습니다.');
    }
  }
};

onMounted(() => {
  fetchAllUsers();
});
</script>
  
<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.rival-modal-content {
  background-color: #000000;
  color: #ffffff;
  padding: 20px;
  border-radius: 20px;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
  width: 300px;
  text-align: center;
}

.modal-title {
  font-size: 20px;
  margin-bottom: 20px;
}

.input-container {
  margin-bottom: 20px;
}

.modal-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ffffff;
  border-radius: 10px;
  background-color: transparent;
  color: #ffffff;
  font-size: 16px;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
}

.modal-cancel-btn, .modal-confirm-btn {
  padding: 6px 20px;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  cursor: pointer;
}

.modal-cancel-btn {
  background-color: #cccccc;
  color: #000000;
}

.modal-confirm-btn {
  background-color: #00ffff;
  color: #000000;
}

.rival-list {
  max-height: 200px;
  overflow-y: auto;
  margin-bottom:10px;
}

.rival-item {
  padding: 8px;
  cursor: pointer;
}

.rival-item:hover {
  background-color: #01FEAE;
  color:black;
}

.no-results {
  margin-top: 10px;
  text-align: center;
  color: #888;
}
</style>
