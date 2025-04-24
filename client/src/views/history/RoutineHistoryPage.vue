<template>
    <div class="routine_Calendar">
      <SideMenu/>
      <BackGround/>
      <RoutineHistory />
      
    </div>
  </template>
  
  <script setup>
  import RoutineHistory from '@/components/history/RoutineHistory.vue';
  import BackGround from '@/components/BackGround.vue';
  import SideMenu from '@/components/SideMenu.vue';

  
  </script>
  
  <style scoped>
  .routine_Calendar {
    padding: 20px;
    height: 100%;
  }
  </style>
  