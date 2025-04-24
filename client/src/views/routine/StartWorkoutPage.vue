<template>
    <div class="container">
        <div class="routine-container">
            <div class="routine-info">
                <div class="exercise-list">
                    <div v-for="(exercise, index) in routine.exercises" :key="index">
                        <p class="name-p"><span class="name-span">{{ exercise.name }}</span> - {{ exercise.sets }}세트, {{ exercise.reps }}회</p>
                        <p v-if="getExplanation(exercise)">
                            <span v-html="formatExerciseExplanation(getExplanation(exercise))"></span>.
                        </p><br>
                    </div>
                </div>
            </div>
        </div>
        <div class="timer-border">
            <div class="timer">{{ formattedTime }}</div>
            <div class="timer-button">
                <button class="start-button" @click="handleStart" :disabled="isStarted">
                    <img class="start-icon" src="@/assets/icons/playbutton.svg" alt="시작 버튼">
                </button>
                <button class="pause-button" @click="stopTimer" :disabled="!isStarted">
                    <img class="pause-icon" src="@/assets/icons/pausebutton.svg" alt="중지 버튼">
                </button>
                <button class="stop-button" @click="handleStop">
                    <img class="stop-icon" src="@/assets/icons/stopbutton.svg" alt="끝 버튼">
                </button>
            </div>
            <p class="timer-instruction">추천 운동 시간: {{ routine.totalTime }}분</p>
        </div>
        <SuccessModal
            v-if="showModal"
            :isOpen="showModal"
            @close="closeModal"
            @cancel="handleCancel"
            @confirm="handleModalAction"
            />
    </div>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { jwtDecode } from 'jwt-decode';
    import SuccessModal from '../../components/modal/SuccessModal.vue';

    const route = useRoute();
    const router = useRouter();
    const routine = ref(JSON.parse(route.query.routineData || '{"exercises":[], "totalTime": 0}')); 
    const token = localStorage.getItem('token');
    const workoutInfoCode = route.query.workoutInfoCode || null;
    const workoutRoutineCode = ref(0);
    const userInfo = ref({
        height: '',
        weight: '',
        gender: '',
        age: ''
    });

    const seconds = ref(0);
    let timerInterval = null;

    const isStarted = ref(false);
    const showModal = ref(false);
    const modalAction = ref('record');

    const formattedTime = computed(() => {
        const minutes = String(Math.floor(seconds.value / 60)).padStart(2, '0');
        const secs = String(seconds.value % 60).padStart(2, '0');
        return `${minutes}:${secs}`;
    });

    const selectedRoutineCode = computed(() => {
        if (workoutInfoCode) {
            return workoutRoutineCode.value;
        } else {
            return routine.value.routineCode;
        }
    });

    const handleStart = () => {
        if (!isStarted.value) {
            startTimer();
            if (!workoutInfoCode) {
                saveRoutine();
            }
            isStarted.value = true;
        }
    };

    const startTimer = () => {
        if (!timerInterval) {
            timerInterval = setInterval(() => {
                seconds.value++;
            }, 1000);
        }
    };

    const stopTimer = () => {
        clearInterval(timerInterval);
        timerInterval = null;
    };

    const handleStop = () => {
        stopTimer();
        showModal.value = true; 
        modalAction.value = 'record';
    };

    const formatDateTime = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(0).padStart(2, '0');
  const minutes = String(0).padStart(2, '0');
  const seconds = String(0).padStart(2, '0');

  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
};

    const fetchUserInfo = async () => {
        try {
            const decodedToken = jwtDecode(token);
            const userCode = decodedToken.sub;
            const response = await fetch(`/boot/users/usercode/${userCode}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error('사용자 정보 조회 실패');
            }

            const data = await response.json();
            userInfo.value = {
                height: data.userHeight,
                weight: data.userWeight,
                gender: data.userGender,
                age: data.userAge
            };
        } catch (error) {
            console.error('사용자 정보 조회 오류:', error);
        }
    };

    const saveRoutine = async () => {
        const currentDate = new Date().toLocaleDateString('ko-KR');
        const routineContent = `
오늘의 운동 루틴
제목: ${routine.value.title}
날짜: ${currentDate}
운동 부위: ${routine.value.bodyPart}
키: ${userInfo.value.height}cm
몸무게: ${userInfo.value.weight}kg
성별: ${userInfo.value.gender}
나이: ${userInfo.value.age}세
운동 시간: ${routine.value.totalTime}분

오늘의 운동 루틴을 추천해 드립니다:
${routine.value.exercises.map((exercise, index) => `
${index + 1}. ${exercise.name}
- 세트 및 반복: ${exercise.sets}세트 x ${exercise.reps}회
- 운동별 시간: ${exercise.time || ''}분
- 운동 설명: ${formatExerciseExplanation(getExplanation(exercise))}
- 중량: ${exercise.weight > 0 ? exercise.weight + 'kg' : '맨몸운동입니다.'}
- 추천 영상: [${exercise.name} 운동 영상](${exercise.video})
`).join('')}
추천 MusicList: ${routine.value.musicList || ''}`;

        const formattedResponse = {
            choices: [
                {
                    message: {
                        content: routineContent.trim()
                    }
                }
            ]
        };

        try {
            const response = await fetch('/boot/api/gpt/process-routine', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(formattedResponse)
            });

            if (!response.ok) {
                throw new Error('루틴 저장 실패');
            }

            const result = await response.json();
            console.log('운동 루틴 저장 결과:', result);

            if (result.routineCode) {
                routine.value.routineCode = result.routineCode;
            }
        } catch (error) {
            console.error('루틴 저장 오류:', error);
        }
    };

    const recordWorkout = async () => {
    try {
        const now = new Date();

        // toISOString() 사용 후 필요 없는 부분 제거
        const formatDate = (date) => {
        return date.toISOString().split('.')[0]; // 밀리초와 'Z' 제거
        };

        const createdAt = formatDate(now);
        const updatedAt = formatDate(now);
        const currentDate = new Date();
        const dayOfExercise = formatDateTime(currentDate);

        console.log(dayOfExercise);  // 로그 출력
        console.log(createdAt);      // 로그 출력
        console.log(updatedAt);      // 로그 출력

        const exerciseDuration = seconds.value; // 타이머로 측정한 시간 (초 단위)
        const decodedToken = jwtDecode(token);
        const userCode = decodedToken.sub;
        const response = await fetch('/boot/recordperuser/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({      
            userRecordCode: null,
            dayOfExercise: dayOfExercise,
            exerciseDuration: exerciseDuration,
            recordFlag: true,
            userCode: userCode,
            routineCode: selectedRoutineCode.value,
            createdAt: createdAt,
            updatedAt: updatedAt
        })
        });

        if (!response.ok) {
        const errorText = await response.text();
        throw new Error('운동 기록 실패: ' + errorText);
        }

        console.log('운동이 성공적으로 기록되었습니다.');
        
    } catch (error) {
        console.error('운동 기록 오류:', error);
    }
    };


    const handleModalAction = async () => {
        if (modalAction.value === 'record') {
            await recordWorkout();
            closeModal();
            resetTimer();
            await router.push({path: '/finished-routine', query: { routineCode: selectedRoutineCode.value} });
            console.log("보내는 query: "+selectedRoutineCode.value);
        }
    };

    const resetTimer = () => {
        stopTimer();
        seconds.value = 0;
        isStarted.value = false;
    };

    const closeModal = () => {
    showModal.value = false;
    };

    const handleCancel = async () => {
        closeModal();
        console.log('취소 버튼 클릭됨. workoutInfoCode:', workoutInfoCode, 'routineCode:', routine.value.routineCode);

        const isAIGeneratedRoutine = !workoutInfoCode && routine.value.routineCode;
        console.log('AI 생성 루틴 여부:', isAIGeneratedRoutine);

        if (isAIGeneratedRoutine) {
            console.log('AI 생성 루틴 삭제 시도 중...');
            try {
            const response = await fetch(`/boot/routines/${routine.value.routineCode}`, {
                method: 'DELETE',
                headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
                },
            });

            const responseData = await response.text();
            console.log('서버 응답:', response.status, responseData);

            if (!response.ok) {
                throw new Error(`루틴 삭제 실패: ${response.status} ${responseData}`);
            }

            console.log('AI 생성 루틴 삭제 성공');
            } catch (error) {
            console.error('루틴 삭제 중 오류 발생:', error);
            }
        } else {
            console.log('기존 루틴이므로 삭제하지 않습니다.');
        }

        console.log('메인 화면으로 이동 중...');
        await router.push('/');
        };


    const formatExerciseExplanation = (explanation) => {
        return explanation.split('.').map(sentence => sentence.trim()).filter(sentence => sentence).join('.');
    };

    const getExplanation = (exercise) => {
        if (exercise.explanation) {
            return exercise.explanation;
        } else if (exercise.exerciseEquipmentCode && exercise.exerciseEquipmentCode.exerciseDescription) {
            return exercise.exerciseEquipmentCode.exerciseDescription;
        }
        return '';
    };

    const fetchRoutineDetails = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                console.error('토큰이 없습니다.');
                return;
            }

            const workoutInfoCode = route.query.workoutInfoCode;
            if (!workoutInfoCode) {
                return;
            }

            const response = await fetch(`/boot/workoutInfos/${workoutInfoCode}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error('운동 정보 조회 오류');
            }

            const workoutInfo = await response.json();
            console.log('Workout Info:', workoutInfo);

            const routineResponse = await fetch(`/boot/workout-per-routine/detail/${workoutInfo.routineCode}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (!routineResponse.ok) {
                throw new Error('운동 루틴 상세 정보 조회 오류');
            }

            const routineInfo = await routineResponse.json();
            console.log('Routine Info:', routineInfo);

            workoutRoutineCode.value = workoutInfo.routineCode;

            routine.value = {
                title: workoutInfo.title,
                totalTime: workoutInfo.time,
                routineCode: workoutInfo.routineCode, 
                exercises: routineInfo.map((exercise) => ({
                    name: exercise.workoutName,
                    sets: exercise.weightSet,
                    reps: exercise.numberPerSet,
                    weight: exercise.weightPerSet,
                    explanation: exercise.exerciseExplanation || 
                        (exercise.exerciseEquipmentCode && exercise.exerciseEquipmentCode.exerciseDescription) || '',
                    video: exercise.link
                }))
            };

        } catch (error) {
            console.error('오류 발생:', error);
        }
    };

    onMounted(() => {
        fetchRoutineDetails();
        fetchUserInfo();
    });
</script>
<style scoped>
    .container {
        display: flex;
        flex-direction: row;
        align-items: flex-start;
        background-color: black;
        height: calc(100vh - 65px);
        padding-top: 41.6px;
        overflow-y: auto; 
    }

    .routine-container {
        flex: 1; 
        margin-left: 50px;
    }

    .routine-title {
        margin-bottom: 20px;
    }

    .name-p {
        border-bottom: 1px solid rgba(109, 108, 108, 1);
        padding-bottom: 2px;
        display: inline-block;
    }

    .name-span {
        font-weight: bold;
    }

    .timer-border {
        position: sticky; 
        top: 0;
        background-color: #2b2e2e;
        border-radius: 20px;
        box-shadow: 0 2px 3px rgba(109, 108, 108, 0.5);
        margin-right: 50px;
    }

    .timer {
        font-size: 48px;
        margin: 30px;
        padding: 10px 20px;
        background-color: rgb(198, 198, 198);
        color: rgb(43, 43, 43);
        text-shadow: 
            1px 1px 0 rgba(189, 188, 188, 0.5), 
            -1px -1px 0 rgba(189, 188, 188, 0.5), 
            1px -1px 0 rgba(189, 188, 188, 0.5), 
            -1px 1px 0 rgba(0, 0, 0, 0.5);
        box-shadow: 
            inset 1px 1px 0 rgba(55, 53, 53, 0.7),
            inset -1px -1px 0 rgba(153, 152, 152, 0.2);
    }

    .timer-button {
        display: flex;
        gap: 20px;
        justify-content: center; 
        margin-bottom: 20px;
    }

    .start-button, .pause-button, .stop-button {
        background-color: white;
        border: white;
        font-size: 10px;
        width: 35px;
        height: 35px;
        padding: 10px;
        display: flex; 
        justify-content: center; 
        align-items: center;
        border-radius: 10px;
    }
    
    .start-icon, .pause-icon, .stop-icon {
        width: 20px;
        height: 20px;
    }

    p {
        color:#01FDFE;
        font-size: 20px;
    }

    h1 {
        color:#01FDFE;
    }

    .timer-instruction {
        color: white;
        text-align: center; 
        margin: 10px 0; 
        font-size: 18px; 
    }
</style>