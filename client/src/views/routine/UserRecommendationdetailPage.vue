<template>
    <div class="container">
        <div class="recommendation-main-container">
            <div class="title-info-container">
                <label class="routine-title">{{title}}</label>
                <div class="info-container">
                    <p class="registration-time">등록 시간: {{ createdAt }}</p>
                    <p class="total-time">전체 운동 시간: {{ totalTime }}분</p>
                </div>
            </div>
            <div class="board-container">
                <div class="detail-board-header">
                    <div class="board-header-number">순서</div>
                    <div class="board-header-workout">운동</div>
                    <div class="board-header-set">세트 수</div>
                    <div class="board-header-num">세트 별 개수</div>
                    <div class="board-header-weight">무게</div>
                    <div class="board-header-time">시간</div>
                    <div class="board-header-link">영상 링크</div>
                </div>
            <div class="board-body">
                    <div class="board-row" v-for="(routine, index) in routines" :key="routine.number">
                        <div class="board-row-number">{{ routine.number }}</div>
                        <div class="board-row-workout">{{ routine.title }}</div>
                        <div class="board-row-set">{{ routine.set }}</div>
                        <div class="board-row-num">{{ routine.num }}</div>
                        <div class="board-row-weight">{{ routine.weight }}</div>
                        <div class="board-row-time">{{ routine.time }}</div>
                        <div class="board-row-link">{{ routine.link }}</div>
                    </div>
                </div>
            </div>
            <div class="start-button-container">
                <button class="start-button" @click="routeStartWorkoutPage">운동 시작</button>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue';
    import { useRoute,useRouter } from 'vue-router';

    const route = useRoute();
    const router = useRouter();
    const title = ref(route.query.title);
    const createdAt = ref(null);
    const totalTime = ref(0);
    const routines = ref([]);
    const workoutInfoCode = ref(route.query.workoutInfoCode);

    const fetchRoutineDetails = async () => {
        try {
            const response = await fetch(`/boot/workoutInfos/${workoutInfoCode.value}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                },
            });

            if (!response.ok) {
                throw new Error('운동 정보 조회 오류');
            }

            const workoutInfo = await response.json();

            const routineResponse = await fetch(`/boot/workout-per-routine/detail/${workoutInfo.routineCode}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                },
            });

            if (!routineResponse.ok) {
                throw new Error('운동 루틴 상세 정보 조회 오류');
            }

            const routineInfo = await routineResponse.json();


            title.value = workoutInfo.title;

            const year = workoutInfo.createdAt[0];
            const month = workoutInfo.createdAt[1];
            const day = workoutInfo.createdAt[2];

            createdAt.value = `${year}년 ${month}월 ${day}일`;
            totalTime.value = workoutInfo.time;

            routines.value = routineInfo.map((routine, index) => ({
                number: routine.workoutOrder,
                title: routine.workoutName,
                set: routine.weightSet,
                num: routine.numberPerSet,
                weight: routine.weightPerSet,
                link: routine.link,
                time: routine.workoutTime,
            }));


        } catch (error) {
            console.error('오류 발생:', error);
        }
    };

    const routeStartWorkoutPage = () => {
        router.push({ 
            path: '/start-workout', 
            query: { 
                title: title.value, 
                workoutInfoCode: workoutInfoCode.value 
            } 
        });
    };

    

    onMounted(() => {
        fetchRoutineDetails();
    });
</script>


<style scoped>
    .container {
        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: black;
        color: white;
        height: calc(100vh - 65px);
        padding-top: 41.6px;
    }

    .recommendation-main-container {
        width: 80%;
        height: 90%; 
        padding: 40px;
        background-color: rgba(255, 255, 255, 0.3);
        border: 2px solid rgba(255, 255, 255, 0);
        border-radius: 15px;
    }

    .title-info-container {
        display: flex;
        align-items: center; 
    }

    .routine-title {
        padding: 10px;
        background-color: hwb(161 0% 0% / 0.5);
        text-align: center;
        font-size: 24px;
        font-weight: bold;
        margin: 0;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    }

    .info-container {
        margin-left: 20px; 
        text-align: left;
    }

    .registration-time, .total-time {
        font-weight: 500;
        font-size: 14px;
    }

    .board-container {
        margin-top: 20px;
    }

    .detail-board-header {
        display: grid;
        grid-template-columns: 1fr 2fr 1fr 1fr 1fr 1fr 3fr;
        padding: 20px 16px;
        font-weight: bold;
        border-bottom: 2px solid rgb(1, 254, 174, 0.5);
        text-align: center;
        background-color: rgba(255, 255, 255, 0);
    }

    .board-body {
        display: flex;
        flex-direction: column;
        max-height: 300px; 
        overflow-y: auto; 
    }

    .board-row {
        display: grid;
        grid-template-columns: 1fr 2fr 1fr 1fr 1fr 1fr 3fr;
        padding: 10px 16px;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    }

    .board-row-number, .board-row-workout, .board-row-set, .board-row-num, .board-row-weight, .board-row-link {
        text-align: center; 
    
    }

    .start-button-container {
        display: flex;
        justify-content: flex-end;
        margin-top: 20px; 
    }

    .start-button {
        padding: 3px 15px;
        background-color: #01FDFE;
        border: none;
        border-radius: 10px;
        color: black;
        font-weight: bold;
        cursor: pointer;
    }

    .start-button-container button:hover {
        background-color: #ffffff;
    }
</style>
