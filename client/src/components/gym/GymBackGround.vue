<template>
    <div class="gym-background">
      <img src="@/assets/icons/blueLabelFirst.svg" alt="블루라벨1" class="blue-label blue-label-left" />
      <img src="@/assets/icons/blueLabelSecond.svg" alt="블루라벨2" class="blue-label blue-label-right" />
    </div>
  </template>
  
  <script setup>
  </script>
  
  <style scoped>
  .gym-background {
    position: fixed;
    top: -65px;
    left: 0;
    width: 100%;
    height: calc(100% + 65px);
    z-index: -1;
    overflow: hidden;
    pointer-events: none;
    background-color: black;
    margin-top: 30px;
  }
  
  .blue-label {
    position: absolute;
    z-index: 0;
    width: 50%;
    height: auto;
    user-select: none;
    -webkit-user-drag: none;
    -khtml-user-drag: none;
    -moz-user-drag: none;
    -o-user-drag: none;
  }
  
  .blue-label-left {
    left: 15%;
    top: 50%;
    transform: translateY(-50%);
  }
  
  .blue-label-right {
    left: 34%;
    top: 50%;
    transform: translateY(-50%);
  }
  </style>