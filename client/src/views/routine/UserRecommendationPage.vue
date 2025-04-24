<template>
    <div class="user-recommendation">
        <RoutineInnerTab>
            <div class="board-container">
                <div class="board-header">
                    <div class="board-header-number">번호</div>
                    <div class="board-header-title">제목</div>
                    <div class="board-header-rating">평균 별점</div>
                    <div class="board-header-time">운동 시간</div>
                    <div class="board-header-start">시작하기</div>
                </div>
                <div class="board-body">
                    <div class="board-row" v-for="(routine, index) in routines" :key="routine.number">
                        <div class="board-row-number">{{ routine.number }}</div>
                        <div class="board-row-title" @click="setActiveTitle(routine)">{{ routine.title }}</div>
                        <div class="board-row-rating">
                            <span v-for="star in Math.max(0, Math.round(routine.averageRating))" :key="star" class="star-filled">★</span>
                            <span v-for="star in (5 - Math.max(0, Math.round(routine.averageRating)))" :key="star">☆</span>
                        </div>
                        <div class="board-row-time">{{ routine.workoutTime }}</div>
                        <div class="board-row-start">
                            <button @click="routeStartWorkoutPage(routine)">운동 시작</button>
                        </div>
                    </div>
                </div>
            </div>
        </RoutineInnerTab>
    </div>
</template>

<script setup>
    import '@/assets/css/routine/UserRecommendationPage.css';
    import RoutineInnerTab from '@/components/routine/RoutineInnerTab.vue';

    import { ref, onMounted } from 'vue';
    import { useRouter } from 'vue-router';

    const router = useRouter();

    const routines = ref([]);

    const setActiveTitle = (routine) => {
        router.push({ path: '/routine-detail', query: { title: routine.title, workoutInfoCode: routine.workoutInfoCode } });
    };

    const routeStartWorkoutPage = (routine) => {
        console.log(`운동을 시작합니다: ${routine.title}`);
        router.push({ 
            path: '/start-workout', 
            query: { 
                title: routine.title, 
                workoutInfoCode: routine.workoutInfoCode 
            } 
        });
    };

    const fetchRoutines = async () => {
        try {
            const response = await fetch('/boot/history/ratings', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                },
            });

            if (!response.ok) {
                throw new Error('서버 응답 오류');
            }

            const data = await response.json();

            routines.value = await Promise.all(data.map(async (item, index) => {
                const averageRating = item.routineRatings ? Math.round(item.routineRatings * 2) / 2 : 0;
                const workoutInfoResponse = await fetch(`/boot/workoutInfos/${item.workoutInfoCode}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    },
                });

                if (!workoutInfoResponse.ok) {
                    throw new Error('운동 정보 조회 오류');
                }

                const workoutInfo = await workoutInfoResponse.json();

                return {
                    number: index + 1,
                    title: workoutInfo.title,
                    averageRating: averageRating,
                    workoutTime: workoutInfo.time,
                    workoutInfoCode: workoutInfo.workoutInfoCode
                };
            }));
        
        } catch (error) {
            console.error('오류 발생:', error);
        }
    };

    onMounted(() => {
        fetchRoutines();
    });

</script>