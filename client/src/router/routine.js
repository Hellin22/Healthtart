import WorkoutInfoInputPage from "@/views/routine/WorkoutInfoInputPage.vue";
import UserRecommendationPage from "../views/routine/UserRecommendationPage.vue";
import UserRecommendationdetailPage from "../views/routine/UserRecommendationdetailPage.vue";
import AIGenerateRoutinePage from "../views/routine/AIGenerateRoutinePage.vue";
import RoutineFinishPage from "../views/routine/RoutineFinish.vue";
import StartWorkoutPage from "../views/routine/StartWorkoutPage.vue";

const RoutineRoutes = [
  {
    path: "/routine",
    component: WorkoutInfoInputPage,
    meta: { requiresAuth: true }, // 로그인 필요
  },
  {
    path: "/user-recommendation",
    component: UserRecommendationPage,
    meta: { requiresAuth: true }, // 로그인 필요
  },
  {
    path: "/routine-detail",
    component: UserRecommendationdetailPage
    , meta: { requiresAuth: true }, // 로그인 필요
  },
  {
    name : "GenerateRoutine",
    path: "/generate-routine",
    component: AIGenerateRoutinePage,
    meta: { requiresAuth: true }, // 로그인 필요
  },
  {
    path: "/start-workout",
    component: StartWorkoutPage,
    meta: { requiresAuth: true }, // 로그인 필요
  },
  {
    path: "/finished-routine",
    component: RoutineFinishPage,
    meta: { requiresAuth: true }, // 로그인 필요
  },
];

export default RoutineRoutes;
