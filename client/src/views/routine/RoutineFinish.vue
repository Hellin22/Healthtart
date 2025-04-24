<template>
  <div class="level">
    <BackGround />
    <div class="content">
      <div class="emoji-container">
        <img src="@/assets/icons/emoji.svg" alt="Muscle Emoji" class="muscle-emoji" />
      </div>
      <div class="emoji-none">
        <h1>오늘도 난 한단계 강해졌다 으랏차차!!</h1>
        <div class="stars">
          <span v-for="star in 5" :key="star" @click="rate(star)">
            <img
              :src="rating >= star ? filledStar : emptyStar"
              :alt="star"
              class="star"
            />
          </span>
        </div>
        <p>오늘 운동한 시간: 1시간</p>
        <p>운동 루틴이 마음에 드신만큼 선택해 주세요 :)</p>
        <button @click="submitRating" class="submit-button">만족도 등록</button>
        <div class="foot">
          <div class="right-progress">
            <img :src="right" alt="right" class="right-icon" />
            <a href="/history" class="view-progress">내 달력 보러가기</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { jwtDecode } from 'jwt-decode';

import BackGround from '@/components/BackGround.vue';

import filledStar from '@/assets/icons/history/star.svg';
import emptyStar from '@/assets/icons/history/nonestar.svg';
import right from '@/assets/icons/right1.svg';

const rating = ref(0);
const token = localStorage.getItem('token');
const workoutInfoCode = ref(null);
const routineCode = ref(0);
const userCode = ref('');

const rate = (value) => {
  rating.value = value;
};

const router = useRouter();

const formatDateTime = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(0).padStart(2, '0');
  const minutes = String(0).padStart(2, '0');
  const seconds = String(0).padStart(2, '0');

  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
};

const findWorkoutInfoCode = async () => {
  console.log("routineCode입니다: " + routineCode.value);
  if (!routineCode.value) {
    console.error('Invalid routineCode');
    return;
  }
  try {
    const response = await fetch(`/boot/workoutInfos/detail/${routineCode.value}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    workoutInfoCode.value = data.workoutInfoCode;
    console.log("넘어온 routineCode로 확인한 workoutInfoCode: " + workoutInfoCode.value);
  } catch (error) {
    console.error('Error fetching workout info:', error);
  }
};

const submitRating = async () => {
  if (rating.value === 0) {
    alert('별점을 선택해주세요.');
    return;
  }
  
  try {
    const currentDate = new Date();
    const formattedDate = formatDateTime(currentDate);
    
    console.log('Submitting rating:', {
      routineRatings: rating.value,
      workoutInfoCode: workoutInfoCode.value,
      dayOfExercise: formattedDate // 포맷팅된 날짜 추가
    });
    
    const response = await fetch(`/boot/history/register`, {    
      method: 'POST',
      headers: {
        'Content-Type': 'application/json; charset=UTF-8',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify({
        routineRatings: rating.value,
        workoutInfoCode: workoutInfoCode.value,
        dayOfExercise: formattedDate
      })
    });
    
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Network response was not ok: ${response.status} ${errorText}`);
    }
    
    const result = await response.json();
    console.log('Rating submitted successfully:', result);
    
    
    alert('평가와 운동 기록이 성공적으로 제출되었습니다.');
    
    router.push('/history');
  } catch (error) {
    console.error('Error submitting rating or recording workout:', error);
    alert('평가 제출 또는 운동 기록 중 오류가 발생했습니다. 다시 시도해 주세요.');
  }
};

onMounted(async () => {
  routineCode.value = Number(router.currentRoute.value.query.routineCode) || 0;
  console.log("routineCode 확인:", routineCode.value);
  await findWorkoutInfoCode();

  const decodedToken = jwtDecode(token);
  userCode.value = decodedToken.sub;
  
  await findWorkoutInfoCode();
});
</script>

<style scoped>
 .level {
  background-color: black;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: calc(100vh - 65px);
  position: relative;
  z-index: 1;
}

.background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0; 
}
  
  .content {
    text-align: center;
    color: white;
  }
  
  .emoji-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    margin-bottom: 20px;
  }
  
  .emoji-none {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    margin-bottom: 20px;
  }
  
  .muscle-emoji {
    width: 400px; 
    height: auto;
  }
  
  h1 {
    color: #00f9ff;
    font-size: 3.5rem;
    margin-bottom: 20px;
  }
  
  .stars {
    display: flex;
    justify-content: center;
    margin: 20px 0;
  }
  
  .star {
    width: 90px;
    cursor: pointer;
  }
  
  p {
    font-size: 1.2rem;
    margin: 20px 0;
  }
  
  .right-progress {
    display: flex;
    align-items: center;
  }
  
  .right-icon {
    width: 20px;
    margin-right: 10px;
  }
  
  .foot {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .view-progress {
    color: #00f9ff;
    font-size: 1.2rem;
    text-decoration: none;
    margin-top: 0; 
  }
  </style>