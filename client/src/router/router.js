import { createRouter, createWebHistory } from 'vue-router';

import MainPage from '@/views/MainPage.vue';

import InbodyRoutes from './inbody';
import UserRoutes from './users';
import RoutineRoutes from './routine';
import SignupRoutes from './signup';
import HistoryRoutes from './history';
import GymRoutes from './gym';

const routes = [
  { path: "/", component: MainPage },
  ...UserRoutes,
  ...InbodyRoutes,
  ...GymRoutes,
  ...RoutineRoutes,
  ...SignupRoutes,
  ...HistoryRoutes
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 네비게이션 가드 설정
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token'); // 로그인 상태 확인 (토큰 존재 여부)

  if (to.matched.some(record => record.meta.requiresAuth) && !token) {
    // 로그인이 필요한 페이지인데 로그인 안 되어 있으면 로그인 페이지로 이동
    next('/users/login');
  } else {
    next(); // 그 외에는 해당 페이지로 이동 허용
  }
});


export default router;