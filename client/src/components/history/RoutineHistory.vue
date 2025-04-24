<template>
  <div class="routine-calendar-container">
    <div class="calendar-header">
      <h2>{{ currentMonthName }} {{ currentYear }}</h2>
    </div>
    <div class="calendar-layout">
      <table class="calendar-table">
        <thead>
          <tr>
            <th>M</th>
            <th>T</th>
            <th>W</th>
            <th>T</th>
            <th>F</th>
            <th>S</th>
            <th>S</th>
          </tr>
        </thead>
        <tbody>
        <tr v-for="(week, weekIndex) in weeks" :key="weekIndex">
          <td v-for="(day, dayIndex) in week" :key="dayIndex" class="day-cell" @click="openModal(day)">
            <div class="workout-icon">
              <img v-if="day.workout" :src="getWorkoutIcon(day.workout)" :alt="day.workout" />
            </div>
            <div class="day-number" :class="{ 'current-day': day.date === currentDate }">
              <span v-if="day.date === currentDate" class="current-date-circle">{{ formatDay(day.date) }}</span>
              <span v-else>{{ formatDay(day.date) }}</span>
            </div>
          </td>
        </tr>
      </tbody>
      </table>
      <div class="legend">
        <div class="legend-item" v-for="(legend, index) in workoutLegend" :key="index">
          <img :src="getWorkoutIcon(legend.workout)" alt="운동 아이콘" class="legend-icon" />
          <span style="font-size: 20px;">{{ legend.name }}</span>
        </div>
      </div>
    </div>

    <RoutineHistoryModal 
  v-if="isModalOpen" 
  :isOpen="isModalOpen" 
  :workoutDetails="selectedWorkoutDetails || []" 
  :workoutTime="selectedWorkoutTime || 0" 
  :satisfaction="selectedSatisfaction || 5" 
  :date="selectedDay?.date || ''"  
  @close="closeModal" 
/>


  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { jwtDecode } from 'jwt-decode';
import '@/assets/css/history/historyroutine.css';
import RoutineHistoryModal from './RoutineHistoryModal.vue';

const currentDate = new Date();
const currentYear = ref(currentDate.getFullYear());
const currentMonth = ref(currentDate.getMonth());

const weeks = ref([]);

const isModalOpen = ref(false);
const selectedDay = ref(null);
const selectedWorkoutDetails = ref([]);
const selectedWorkoutTime = ref(0);
const selectedSatisfaction = ref(0);

const workoutData = ref({});

const currentMonthName = computed(() => {
  const monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];
  return monthNames[currentMonth.value];
}); 

const getUserCodeFromToken = () => {
  const token = localStorage.getItem('token');
  if (token) {
    const decodedToken = jwtDecode(token);
    return decodedToken.sub;
  }
  return null;
};

const fetchWorkoutDetailsForRoutine = async (routineCode) => {
  try {
    const token = localStorage.getItem('token');
    const response = await fetch(`/boot/workout-per-routine/detail/${routineCode}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });


    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`운동 루틴 조회 오류 상태: ${response.status} ${errorText}`);
    }

    const workoutDetails = await response.json();
    return workoutDetails;
  } catch (error) {
    console.error('루틴 코드별 운동 루틴 조회 실패:', error);
    return null;
  }
};

const bodyPartClassification = {
  가슴: 'chest',
  등: 'back',
  어깨: 'shoulders',
  코어: 'back',
  삼두: 'chest',
  유산소: 'legs',
  하체: 'legs',
  전완근: 'back',
  이두: 'chest',
};

const categorizeBodyPart = (bodyPart) => {
  if (!bodyPart) return 'other';
  const category = bodyPartClassification[bodyPart] || 'other';
  return category;
};

const getWorkoutIcon = (workout) => {
  let iconUrl='';
  switch (workout) {
    case 'chest':
      iconUrl = new URL('@/assets/icons/history/chest.svg', import.meta.url).href;
      break;
    case 'back':
      iconUrl = new URL('@/assets/icons/history/back.svg', import.meta.url).href;
      break;
    case 'legs':
      iconUrl = new URL('@/assets/icons/history/lowerbody.svg', import.meta.url).href;
      break;
    case 'shoulders':
      iconUrl = new URL('@/assets/icons/history/shoulder.svg', import.meta.url).href;
      break;
    default:
      iconUrl = new URL('@/assets/icons/equipment.svg', import.meta.url).href;
      break;
  }
  return iconUrl;
};

const fetchBodyPartForEquipment = async (exerciseEquipmentCode) => {
  try {
    const token = localStorage.getItem('token');
    const response = await fetch(`/boot/exercise_equipment/${exerciseEquipmentCode}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`운동 기구 조회 오류 상태: ${response.status} ${errorText}`);
    }

    const workoutDetails = await response.json();

    const categorizedPart = categorizeBodyPart(workoutDetails.bodyPart);
    return categorizedPart;

  } catch (error) {
    console.error('운동 부위 조회 실패:', error);
    return 'other';
  }
};

const formatDateTime = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = 0;
  const minutes = 0;
  const seconds = 0;

  return `${year}-${month}-${day}T${hours}0:${minutes}0:${seconds}0`;
};

const fetchAllWorkoutRecords = async () => {
  try {
    const token = localStorage.getItem('token');
    const userCode = getUserCodeFromToken();
    
    const response = await fetch(`/boot/recordperuser/${userCode}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      }
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Error fetching all workout records: ${response.status} ${errorText}`);
    }

    const workoutRecords = await response.json();
    console.log(workoutRecords);
    
    return workoutRecords;
  } catch (error) {
    console.error('Error fetching workout records:', error);
    return [];
  }
};

const fetchWorkoutHistoryForMonth = async (userCode, year, month) => {
  const allRecords = await fetchAllWorkoutRecords(userCode);
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  
  for (let day = 1; day <= daysInMonth; day++) {
    const formattedDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    const dailyRecords = allRecords.filter(record => {
      const recordDate = new Date(record.dayOfExercise);
      return (
        recordDate.getFullYear() === year &&
        recordDate.getMonth() === month &&
        recordDate.getDate() === day
      );
    });

    if (dailyRecords.length > 0) {
      const routineCode = dailyRecords[0].routineCode;
      const routineDetails = await fetchWorkoutDetailsForRoutine(routineCode);

      const details = await Promise.all(
        routineDetails.map(async (workoutInfo) => {
          const equipmentCode = workoutInfo?.exerciseEquipmentCode?.exerciseEquipmentCode;
          const bodyPart = equipmentCode ? await fetchBodyPartForEquipment(equipmentCode) : 'other';
        
          return {
            ...workoutInfo,
            bodyPart,
            workout: bodyPart,
          };
        })
      );

      workoutData.value[formattedDate] = {
        details: details  || [{ name: '운동 정보 없음', sets: '-', reps: '-', weight: '-' }],
        time: dailyRecords[0].exerciseDuration || 0,
        satisfaction: dailyRecords[0].satisfaction || 0,
      };
    }
  }
};


const fetchWorkoutHistoryForDay = async (userCode, dayOfExercise) => {
  try {
    const token = localStorage.getItem('token');

    const formattedDayOfExercise = formatDateTime(new Date(dayOfExercise));

    const response = await fetch(`/boot/recordperuser/${userCode}/${formattedDayOfExercise}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(`Error fetching workout history: ${response.status} ${errorText}`);
      return;
    }

    const workoutInfo = await response.json();
    if (!workoutInfo || workoutInfo.length === 0) {
      console.log('운동 기록이 없습니다.');
      workoutData.value[dayOfExercise] = {
        details: [{ name: '운동 정보 없음', sets: '-', reps: '-', weight: '-' }],
        time: 0,
        satisfaction: 0,
      };
      return;
    }

    const routineCode = workoutInfo[0]?.routineCode;
    const routineDetails = await fetchWorkoutDetailsForRoutine(routineCode);

    if (!routineDetails || routineDetails.length === 0) {
      workoutData.value[dayOfExercise] = {
        details: [{ name: '운동 정보 없음', sets: '-', reps: '-', weight: '-' }],
        time: 0,
        satisfaction: 0,
      };
      return;
    }

    const details = await Promise.all(
      routineDetails.map(async (workoutInfo) => {
        const equipmentCode = workoutInfo?.exerciseEquipmentCode?.exerciseEquipmentCode;
        const bodyPart = equipmentCode ? await fetchBodyPartForEquipment(equipmentCode) : 'other';
        return {
          name: workoutInfo?.exerciseEquipmentCode?.exerciseEquipmentName || '운동명 없음',
          sets: workoutInfo?.weightSet || '0',
          reps: workoutInfo?.numberPerSet || '0',
          weight: workoutInfo?.weightPerSet || '0',
          bodyPart, 
          workout: bodyPart,
        };
      })
    );

    workoutData.value[dayOfExercise] = {
      details,
      time: routineDetails.reduce((totalTime, workoutInfo) => totalTime + (workoutInfo.workoutTime || 0), 0),
      satisfaction: workoutInfo[0]?.satisfaction || 0,
    };
  } catch (error) {
    console.error('Error in fetchWorkoutHistoryForDay:', error);
    workoutData.value[dayOfExercise] = {
      details: [{ name: '운동 정보 없음', sets: '-', reps: '-', weight: '-' }],
      time: 0,
      satisfaction: 0,
    };
  }
};

const openModal = async (day) => {
  if (!day?.date) return;

  const userCode = getUserCodeFromToken();
  if (!userCode) {
    console.error('유저 코드를 가져올 수 없습니다.');
    return;
  }

  const formattedDate = `${currentYear.value}-${String(10).padStart(2, '0')}-${String(day.date).padStart(2, '0')}`;
  await fetchWorkoutHistoryForDay(userCode, formattedDate);

  const workout = workoutData.value[formattedDate];

  selectedWorkoutDetails.value = workout?.details || [{ name: '운동 정보 없음', sets: '-', reps: '-', weight: '-' }];
  selectedWorkoutTime.value = workout?.time || 0;
  selectedSatisfaction.value = workout?.satisfaction || 0;
  selectedDay.value = { date: formattedDate };

  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const workoutLegend = ref([
  { workout: 'chest', name: '가슴' },
  { workout: 'back', name: '등' },
  { workout: 'legs', name: '하체' },
  { workout: 'shoulders', name: '어깨' },
]);

const formatDay = (day) => {
  return day ? String(day).padStart(2, '0') : '';
};

const generateCalendar = () => {
  weeks.value = [];
  const days = new Date(currentYear.value, currentMonth.value + 1, 0).getDate();
  const firstDay = new Date(currentYear.value, currentMonth.value, 1).getDay();

  let week = Array(firstDay).fill({ date: null });

  for (let day = 1; day <= days; day++) {
    const formattedDate = `${currentYear.value}-${String(currentMonth.value + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    const workout = workoutData.value[formattedDate];
    
    week.push({
      date: day,
      workout: workout ? workout.details[0]?.workout : null
    });

    if (week.length === 7) {
      weeks.value.push(week);
      week = [];
    }
  }

  if (week.length > 0) {
    while (week.length < 7) {
      week.push({ date: null });
    }
    weeks.value.push(week);
  }
};

onMounted(async () => {
  const userCode = getUserCodeFromToken();
  if (userCode) {
    await fetchWorkoutHistoryForMonth(userCode, currentYear.value, currentMonth.value);
    generateCalendar();
  } else {
    console.error('유저 코드를 가져올 수 없습니다.');
  }
});
</script>