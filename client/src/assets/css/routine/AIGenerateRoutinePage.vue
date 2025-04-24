<template>
    <div class="container">
        <div class="ai-main-container">
            <div class="div-title">
                    <h1 class="routine-title">{{ routine.title }}</h1>
                </div>
            <div class="routine-contents">
                <div class="routine-insert-info">
                    <p class="routine-date">날짜: {{routine.date}}</p> 
                    <p class="routine-workoutPart">운동 부위: {{ routine.bodyPart }} | 키: {{routine.height}}cm | 몸무게: {{routine.weight}}kg | 성별: {{routine.gender}} | 운동 시간: {{ routine.totalTime }}분</p>
                    <br>
                </div>
                <br>
                <div class="exercise-container">
                    <div v-for="(exercise, index) in routine.exercises" :key="index" class="exercise-card">
                        <p class="routine-workoutName"><strong>{{ exercise.name }}</strong> <button class="x-button" @click="openDeleteModal(index)"><img class="x-icon" src="@/assets/icons/xbutton.svg" alt=""></button></p>
                        <p><span class="set-span">세트:</span> <span @click="toggleEdit(index, 'sets')" class="editable">{{ exercise.sets }}</span>세트</p>
                        <p><span class="rep-span">세트 당 횟수:</span> <span @click="toggleEdit(index, 'reps')" class="editable">{{ exercise.reps }}</span>회</p>
                        <p><span class="ex-span">운동 설명:</span> <br><span v-html="formatExerciseExplanation(exercise.explanation)"></span>.</p>
                        <p><span class="weight-span">중량:</span> <span @click="toggleEdit(index, 'weight')" class="editable">{{ exercise.weight }}</span>kg</p>
                        <p><span class="video-span">추천 영상:</span> {{ exercise.video }}</p>
                        <br>
                    </div>
                    <div class="recommend-music">
                        <p><span class="music-span">추천 Music: {{ routine.musicList }}</span></p>
                    </div>
                </div>
            
            </div>
            <div class="button-container">
                <button class="regenerate-routine-button" @click="regenerateRoutine()"><img class="regenerate-icon" src="@/assets/icons/regenerate.svg" alt="루틴 재생성 버튼">루틴 재생성 하기</button>
                <button class="start-routine-button" @click="setActiveButton()"><img class="start-icon"src="@/assets/icons/start.svg" alt="운동 시작 버튼">운동 시작하기</button>
            </div>
        </div>
    </div>
    <DeleteModal
        :is-open="isDeleteModalOpen" 
        @close="closeDeleteModal" 
        @confirm="confirmDelete"
    />
</template>

<script setup>
    import { ref, onMounted } from 'vue';
    import { useRouter } from 'vue-router';
    import { jwtDecode } from 'jwt-decode';
    import DeleteModal from '../../components/modal/DeleteModal.vue';

    const router = useRouter(); 
    const routine = ref({ exercises: [], bodyPart: '', date: '' });

    const token = localStorage.getItem('token');
    const userCode = jwtDecode(token).sub;

    const fetchUserData = async () => {
        try {
            const response = await fetch(`/boot/users/usercode/${userCode}`,{
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });
            const userData = await response.json();
            routine.value.height = userData.userHeight; 
            routine.value.weight = userData.userWeight; 
            routine.value.gender = userData.userGender;
            
        } catch (error) {
            console.error('Error fetching User data:', error);
        }

    }

    const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0'); 
        return `${year}년 ${month}월 ${day}일`;
    };

    const regenerateRoutine = async () => {

        try {
            const response = await fetch(`/boot/api/gpt/generate-routine?userCode=${userCode}&bodyPart=${routine.value.bodyPart}&time=${routine.value.totalTime}`, {    
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify({
                    userCode: userCode,
                    bodyPart: routine.value.bodyPart,
                    time: routine.value.totalTime,
                })
            });
            
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const newRoutine = await response.json();

             routine.value = {
                ...routine.value,
                title: newRoutine.title,
                totalTime: newRoutine.totalTime,
                musicList: newRoutine.musicList,
                bodyPart: newRoutine.bodyPart,
                exercises: [],
            };

            let i = 1;
            while (newRoutine[`workoutName${i}`]) {
                routine.value.exercises.push({
                    name: newRoutine[`workoutName${i}`],
                    explanation: newRoutine[`exerciseExplanation${i}`],
                    video: newRoutine[`exerciseVideo${i}`],
                    sets: newRoutine[`weightSet${i}`],
                    reps: newRoutine[`numberPerSet${i}`],
                    weight: newRoutine[`weightPerSet${i}`],
                    time: newRoutine[`workoutTime${i}`]
                });
                i++;
            }

        } catch (error) {
            console.error('Error regenerating routine:', error);
        }
    }


    onMounted(() => {

        const data = router.currentRoute.value.query.routineData;

        if (data) {
            const parsedData = JSON.parse(data);
            routine.value.title = parsedData.title;
            routine.value.totalTime = parsedData.totalTime;
            routine.value.musicList = parsedData.musicList;
            routine.value.bodyPart = router.currentRoute.value.query.bodyPart; 
            
            const today = new Date();
            routine.value.date = formatDate(today); 
            
            let i = 1;
            routine.value.exercises = [];
            while (parsedData[`workoutName${i}`]) {
                routine.value.exercises.push({
                    name: parsedData[`workoutName${i}`],
                    explanation: parsedData[`exerciseExplanation${i}`],
                    video: parsedData[`exerciseVideo${i}`],
                    sets: parsedData[`weightSet${i}`],
                    reps: parsedData[`numberPerSet${i}`],
                    weight: parsedData[`weightPerSet${i}`],
                    time: parsedData[`workoutTime${i}`]
                });
                i++;
            }
        }
        fetchUserData();
    });

    const formatExerciseExplanation = (explanation) => {
        return explanation.split('.').map(sentence => sentence.trim()).filter(sentence => sentence).join('.<br>');
    };

    const toggleEdit = (index, field) => {
        const exercise = routine.value.exercises[index];
        const newValue = prompt(`${field} 변경:`, exercise[field]);
        if (newValue !== null) {
            exercise[field] = newValue;
        }
    };

    const setActiveButton = () => {
        router.push({ 
            path: '/start-workout', 
            query: { 
                routineData: JSON.stringify(routine.value) 
            } 
        });
    };


    const isDeleteModalOpen = ref(false);
    const currentDeleteIndex = ref(null);

    const openDeleteModal = (index) => {
    currentDeleteIndex.value = index;
    isDeleteModalOpen.value = true;
    };

    const closeDeleteModal = () => {
        isDeleteModalOpen.value = false;
        currentDeleteIndex.value = null;
    };

    const confirmDelete = () => {
        if (currentDeleteIndex.value !== null) {
            routine.value.exercises.splice(currentDeleteIndex.value, 1);
        }
        closeDeleteModal();
    };

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

    .ai-main-container {
        width: 80%;
        height: 90%; 
        padding: 0px 40px 20px ;
        border: 2px solid #01FDFE;
        border-radius: 15px;
        display: flex; 
        flex-direction: column; 
        justify-content: space-between;
        position: relative;
        overflow-y: auto;
        
    }

    .div-title {
        position: sticky; 
        top: 0;
        background-color: #000000;
        border-bottom: 2px solid #01FDFE;
        padding: 10px;
        z-index: 10;
    }

    .routine-insert-info {
        margin-top: 20px;
        margin-bottom: 10px;
        border-bottom: 1px solid #9c9d9d;
    }

    .routine-workoutName {
        color: #01FDFE;
        font-size: 20px;
        display: flex;
        align-items: center;
        margin-bottom: 5px;
    }

    .x-button {
        background-color: transparent;
        border: none;
        cursor: pointer;
        margin-top:4px;
        margin-left: 4px;
        width: 15px;
        height: 15px;;
    }

    .x-icon {
        width: 15px;
    }

    .button-container {
        display: flex;
        gap: 20px;
        margin-top: 20px;
    }

    .regenerate-routine-button, .start-routine-button {
        color: white;
        font-size: 20px;
        padding: 10px;
        width: 240px;
        border: 2px solid #01FDFE;
        border-radius: 100px;
        background-color: black;
        display: flex;
        align-items: center;
        justify-content: flex-start;
        cursor: pointer;
    }

    .regenerate-icon {
        width:20px;
        margin-right: 10px;
        margin-left: 10px;
        margin-top: 2px;
    }

    .start-icon {
        width:16px;
        margin-left: 10px;
        margin-right: 25px;
        margin-top: 2px;
    }

    .bluemonster-icon {
        width:100px;
        height: auto;
        margin-top: auto;
        right: 20px; 
        bottom: 20px;
    }

    .editable {
        cursor: pointer;
        text-decoration: underline;
    }

    .set-span, .rep-span, .ex-span, .weight-span, .video-span {
        font-weight: 600;
    }

</style>