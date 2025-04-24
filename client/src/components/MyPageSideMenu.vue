<template>
    <aside class="mypageside">
      <router-link to="/history" class="menu-item" exact-active-class="active-link">
        <img :src="calendarIcon" alt="달력 아이콘" class="menu-icon" />
        <span class="menu-text" style="font-size: 14px; font-weight: 500;">My Health Calendar</span>
        <img :src="arrowIcon" alt="화살표 아이콘" class="arrow-icon" />
      </router-link>
      <router-link to="/user-recommendation" class="menu-item" exact-active-class="active-link">
        <img :src="messageIcon" alt="메시지 아이콘" class="menu-icon" />
        <span class="menu-text" style="font-size: 14px; font-weight: 500;">Recommended Routine</span>
        <img :src="arrowIcon" alt="화살표 아이콘" class="arrow-icon" />
      </router-link>
    </aside>
  </template>
  
  <script setup>
  import calendarIcon from '@/assets/icons/icon.svg';
  import messageIcon from '@/assets/icons/icon2.svg';
  import arrowIcon from '@/assets/icons/icon3.svg';
  </script>
    
    <style scoped>
    .mypageside {
    z-index: -1; 
    background-color: black;
    width: 280px;
    color: #fff;
    padding-left: 30px;
    padding-bottom: 50px;
    position: fixed;
    top: 65px;
    bottom: 0;
    left: 0;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
  }
  
  .menu-item {
    display: flex;
    align-items: center;
    justify-content: space-between; 
    gap: 10px;
    margin-bottom: 15px; 
    cursor: pointer;
  }
  
  .menu-text {
    display: inline-block;
    width: 150px; 
    white-space: nowrap; 
    overflow: hidden; 
    text-overflow: ellipsis; 
    
  }
  
  .menu-icon {
    width: 17px; 
    height: 17px;
  }
  
  .arrow-icon {
    width: 13px; 
    height: 13px;
  }
  
  .menu-item span {
    font-size: 16px;
    color: #fff;
  }
  
  .menu-item:hover {
    color: #00ffff;
  }
  
  
  .menu-item:hover .menu-text, 
  .menu-item:hover {
    color: inherit;
    background-color: transparent;
    text-decoration: none;
  }
  </style>