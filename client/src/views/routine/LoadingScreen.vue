<template>
    <div class="loading-screen">
      <div class="rotating-monster">
        <img src="@/assets/icons/bluemonster.svg" alt="Blue Monster" />
      </div>
      <div class="loading-text">운동 루틴을 생성 중입니다!<br />잠시만 기다려 주세요.</div>
    </div>
  </template>
  
  <style scoped>
  .loading-screen {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: rgba(0, 0, 0, 0.8);
    z-index: 999;
  }
  
  .rotating-monster {
    width: 150px;
    height: 150px;
    animation: rotate 2s linear infinite;
  }
  
  .rotating-monster img {
    width: 100%;
    height: 100%;
  }
  
  .loading-text {
    color: white;
    font-size: 18px;
    text-align: center;
    margin-top: 20px;
  }
  
  @keyframes rotate {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
  </style>