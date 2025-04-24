import RoutineHistoryPage from '@/views/history/RoutineHistoryPage.vue';

const HistoryRoutes = [
  {
    path: '/history',
    component: RoutineHistoryPage,
    meta: { requiresAuth: true }, // 로그인 필요
  },
];

export default HistoryRoutes;
