<template>
  <div class="modal-overlay" v-if="isOpen" @click.self="closeModal">
    <div class="modal-content">
      <div class="header-row">
        <h3>{{ formattedDate }}</h3> <!-- 포맷팅된 날짜 표시 -->
        <p class="workout-time">운동 시간 : {{ formattedWorkoutTime }}</p> <!-- 운동 시간 표시 -->
      </div>
      <table class="workout-table">
        <thead>
          <tr>
            <th>운동명</th>
            <th>세트 수</th>
            <th>세트 당 개수</th>
            <th>세트 당 무게</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(exercise, index) in workoutDetails" :key="index">
            <td style="height: 20px; font-size: 16px;">{{ exercise.name || '정보 없음' }}</td> <!-- 기본값 처리 -->
            <td style="height: 20px; font-size: 18px;">{{ exercise.sets || '-' }}</td> <!-- 기본값 처리 -->
            <td style="height: 20px; font-size: 18px;">{{ exercise.reps || '-' }}</td> <!-- 기본값 처리 -->
            <td style="height: 20px; font-size: 18px;">{{ exercise.weight || '-' }}</td> <!-- 기본값 처리 -->
          </tr>
        </tbody>
      </table>
      <div class="satisfaction">
        <div class="box">
          <div class="stars">
            <span>만족도 &nbsp;</span>
            <img v-for="n in 5" :key="n" :src="n <= satisfaction ? activeStar : inactiveStar"
              :class="n <= satisfaction ? 'star activestar' : 'star inactivestar'" />
          </div>
          <button class="confirm-btn" @click="closeModal">확인</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import '@/assets/css/history/historyroutine.css';

const activeStar = new URL('@/assets/icons/history/star.svg', import.meta.url).href;
const inactiveStar = new URL('@/assets/icons/history/nonestar.svg', import.meta.url).href;

const props = defineProps({
  isOpen: Boolean,
  workoutDetails: {
    type: Array,
    default: () => [],  // 기본값으로 빈 배열 설정
  },
  workoutTime: {
    type: Number,
    default: 0,  // 기본값으로 0 설정
  },
  satisfaction: {
    type: Number,
    default: 0,  // 기본값으로 0 설정
  },
  date: {
    type: String,
    default: '',  // 기본값으로 빈 문자열 설정
  },
});

// 날짜 포맷팅 함수
const formattedDate = computed(() => {
  if (!props.date) return '';
  const date = new Date(props.date);
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
});

// 운동 시간을 보기 좋게 변환하는 함수
const formattedWorkoutTime = computed(() => {
  const time = props.workoutTime || 0;
  return convertMinutesToTime(time);
});

const convertMinutesToTime = (minutes) => {
  if (isNaN(minutes) || minutes <= 0) {
    return '0분';
  }
  const hours = Math.floor(minutes / 60);
  const remainingMinutes = minutes % 60;
  return hours > 0 ? `${hours}시간 ${remainingMinutes}분` : `${remainingMinutes}분`;
};

const emit = defineEmits(['close']);

// 모달 닫는 함수
const closeModal = () => {
  emit('close');
};
</script>
