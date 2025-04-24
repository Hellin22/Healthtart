import Gym from "@/views/gym/Gym.vue";

const GymRoutes = [
    { path: "/gym", component: Gym, meta: { requiresAuth: true } },
];

export default GymRoutes;
