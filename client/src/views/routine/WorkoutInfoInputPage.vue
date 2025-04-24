<template>
    <div class="workout-info-input">
        <RoutineInnerTab>
            <div class="workout-time-section">
            <label for="workout-time">운동 예정 시간:</label>

            <!-- v-model="selectedTime" 추가하기 -->
            <select id="workout-time" v-model="selectedTime" class="dropdown"> 
                <option value="60">60</option>
                <option value="90">90</option>
                <option value="120">120</option>
                <option value="120">150</option>

            </select>
            <span>분</span>
        </div>

        <div class="body-parts-section">
                <div class="button-grid">
                    <button v-for="part in bodyParts":key="part"@click="toggleSelection(part)":class="{'selected': selectedParts.includes(part)}"class="body-part-button">
                        {{part}}
                    </button>
                </div>
                <label class="select-label">운동 부위를 선택해 주세요. ( 중복 선택 불가 )</label>
            </div>
            <button class="generate-routine-button" @click="generateRoutine">운동 루틴 생성하기</button>
        </RoutineInnerTab>
        <LoadingScreen v-if="loading"/>
        <ConfirmModal :isOpen="isModalOpen" @close="isModalOpen = false" @confirm="isModalOpen = false" />
        
    </div>
 </template>
 
 <script setup>
    import { ref } from 'vue';
    import { jwtDecode } from 'jwt-decode';
    import { useRouter } from 'vue-router';

    import RoutineInnerTab from '@/components/routine/RoutineInnerTab.vue';
    import '@/assets/css/routine/WorkoutInfoInputPage.css';
    import LoadingScreen from './LoadingScreen.vue';
    import ConfirmModal from '../../components/modal/ConfirmModal.vue';

    const bodyParts = ['등', '가슴', '어깨', '코어', '삼두', '유산소', '하체', '전완근', '이두'];
    const selectedParts = ref([]);
    const selectedTime = ref(60);
    const loading = ref(false);
    const isModalOpen = ref(false);
    const router = useRouter();

    const toggleSelection = (part) => {
        if (!selectedParts.value.includes(part)) {
            selectedParts.value=[];
            selectedParts.value.push(part);
        };
    }

    const generateRoutine = async () => {
    if (selectedParts.value.length === 0) {
        isModalOpen.value = true;
        return;
    }
    loading.value = true; 
    const bodyPart = selectedParts.value[0];

    try {
        const token = localStorage.getItem('token');
        const userCode = jwtDecode(token).sub;
        
        const response = await fetch(`/boot/api/gpt/generate-routine`, {    
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({
                userCode: userCode,
                bodyPart: bodyPart,
                time: selectedTime.value,
            }),
        });

        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }
        const data = await response.json();

        localStorage.setItem('tempRoutineData', JSON.stringify(data));

        router.push({ 
            name: 'GenerateRoutine',
            params: { 
                bodyPart: bodyPart,
                time: selectedTime.value
            },
            state: { routineDataStored: true }
        });
    } catch (error) {
        console.error('오류 발생:', error);
    } finally {
        loading.value = false;
    }
};

 </script>
