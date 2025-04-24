<template>
  <div class="inbody-ranking">
    <BackGround class="background-component" />
    <h2 class="inbodyrankingtitle">InBody Ranking</h2>
    <div class="icon-button-container">
      <button @click="openFilterModal" class="icon-button">
        <img src="@/assets/icons/filtericon.svg" alt="Filter" />
      </button>
      <button @click="openInbodyInfoModal" class="icon-button">
        <img src="@/assets/icons/usericon.svg" alt="User" />
      </button>
    </div>
    <div class="table-container">
      <table>
        <thead>
          <tr class="inbodytrtitle">
            <th>랭킹</th>
            <th>이름</th>
            <th>성별</th>
            <th>키</th>
            <th>체중</th>
            <th>골격근량</th>
            <th>체지방률</th>
            <th>기초대사량</th>
            <th>인바디 점수</th>
          </tr>
        </thead>
        <tbody>
          <tr class="inbodytr" v-for="(user, index) in paginatedUsers" :key="index">
            <td>{{ (currentPage - 1) * itemsPerPage + index + 1 }}</td>
            <td>{{ user.userNickname }}</td>
            <td>{{ user.userGender }}</td>
            <td>{{ user.height }}</td>
            <td>{{ user.weight }}</td>
            <td>{{ user.muscleWeight }}</td>
            <td>{{ user.fatPercentage }}</td>
            <td>{{ user.basalMetabolicRate }}</td>
            <td>{{ user.inbodyScore }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="pagination">
      <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">이전</button>
      <button 
        v-for="page in displayedPages" 
        :key="page" 
        @click="changePage(page)"
        :class="{ active: currentPage === page }"
      >
        {{ page }}
      </button>
      <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">다음</button>
    </div>

    <InbodyFilterModal 
      v-if="isFilterModalOpen" 
      @close="isFilterModalOpen = false" 
      @applyFilters="applyFilters" 
    />
    <InbodyInfoModal 
      v-if="isInbodyInfoModalOpen" 
      @close="isInbodyInfoModalOpen = false" 
      :inbodyData="inbodyData" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import BackGround from '../../components/BackGround.vue';
import InbodyFilterModal from '../../components/modal/inbody/InbodyFilterModal.vue';
import InbodyInfoModal from '../../components/modal/inbody/InbodyInfoModal.vue';
import { jwtDecode } from 'jwt-decode';

const inbodyUsers = ref([]);
const currentPage = ref(1);
const itemsPerPage = ref(10);
const isFilterModalOpen = ref(false);
const isInbodyInfoModalOpen = ref(false);
const inbodyData = ref(null);

const fetchInbodyData = async () => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('/boot/inbody/inbody_ranking', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    inbodyUsers.value = response.data;
  } catch (error) {
    console.error('Error fetching InBody data:', error);
  }
};

onMounted(() => {
  fetchInbodyData();
});

const openInbodyInfoModal = async () => {
  try {
    const token = localStorage.getItem('token');
    const decodedToken = jwtDecode(token);
    const userCode = decodedToken.sub;
    
    const response = await axios.get('/boot/inbody/my-inbody', {
      params: { userCode: userCode },
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    
    if (!response.data || (Array.isArray(response.data) && response.data.length === 0)) {
      alert("등록된 인바디 정보가 없습니다. MyPage에서 인바디 정보를 등록해주세요.");
    } else {
      inbodyData.value = Array.isArray(response.data) ? response.data : [response.data];
      isInbodyInfoModalOpen.value = true;
    }
  } catch (error) {
    console.error('Error fetching user Inbody data:', error);
    if (error.response && error.response.status === 404) {
      alert("인바디 정보를 불러오는 중 오류가 발생했습니다. 다시 시도해 주세요.");
    } 
  }
};

const openFilterModal = () => {
  isFilterModalOpen.value = true;
};

const applyFilters = async (appliedFilters) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.post('/boot/inbody/filter', appliedFilters, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    inbodyUsers.value = response.data;
    currentPage.value = 1;

    inbodyUsers.value.sort((a, b) => b.inbodyScore - a.inbodyScore);
  } catch (error) {
    console.error('Error applying filters:', error);
  }
};

const totalItems = computed(() => inbodyUsers.value.length);
const totalPages = computed(() => Math.ceil(totalItems.value / itemsPerPage.value));

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return inbodyUsers.value.slice(start, end);
});

const displayedPages = computed(() => {
  const range = 2;
  let start = Math.max(1, currentPage.value - range);
  let end = Math.min(totalPages.value, currentPage.value + range);

  if (end - start + 1 < range * 2 + 1) {
    if (start === 1) {
      end = Math.min(start + range * 2, totalPages.value);
    } else {
      start = Math.max(end - range * 2, 1);
    }
  }

  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};
</script>

<style scoped>
.inbody-ranking {
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 65px);
  padding: 20px;
  position: relative;
}

.background-component {
  position: fixed;
  width: 100%;
  height: 100%;
  margin-top: 32px;
  z-index: -1;
}

.inbodyrankingtitle {
  text-align: center;
  font-size: 40px;
  margin-bottom: 20px;
  color: #fff;
  position: relative;
  z-index: 2;
}

.icon-button-container {
  display: flex;
  justify-content: right;
  margin-bottom: 10px;
  padding: 0 15%;
  gap : 10px;
}

.icon-button {
  display: flex;
  align-items: end;
  background-color: #ffffff;
  border: 2px solid #ffffff;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding : 3px 5px;
}

.icon-button:hover {
  background-color: #01FEAE;
}

.icon-button img {
  width: 20px;
  height: 20px;
}

.table-container {
  display: flex;
  justify-content: center;
  position: relative;
  z-index: 2;
}

table {
  width: 70%;
  border-collapse: collapse;
  background-color: rgba(0, 0, 0, 0.7);
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
}

.inbodytrtitle, .inbodytr {
  border: 1px solid #ffffff;
  padding: 8px;
  text-align: center;
  transition: background-color 0.3s;
}

.inbodytrtitle {
  background-color: rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

th {
  font-weight: bold;
  padding: 8px;
  font-size: 14px;
  color: #000;
}

td {
  height: 40px;
  font-size: 14px;
  color: #fff;
  font-weight: bold;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
  color: #fff;
}

.pagination button {
  background-color: #fff;
  color: #000;
  border: none;
  padding: 5px 10px;
  margin: 0 5px;
  cursor: pointer;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.pagination button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.pagination button.active {
  background-color: #00ffff;
  color: black;
}

.button-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.button-container button {
  background-color: #00ffff;
  color: #000;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  transition: background-color 0.3s;
}

.button-container button:hover {
  background-color: #008080;
}
</style>
