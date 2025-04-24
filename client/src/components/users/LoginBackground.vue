<template>
  <div class="back-container">
      <div class="loginleft-container">
        <div class="background-text">
          도망친 곳에<br>
          낙원이 있을 리 없다.
        </div>
        <div class="background-img">
          <img src="@/assets/icons/loginpagelogo.svg" alt="Background Logo">
        </div>
      </div>
      <div class="loginright-container">
        <div class="background-blue">
          <img src="@/assets/icons/backgroundblue.svg" alt="Background Blue">
        </div>
      </div>
    </div>
</template>

<script setup>

</script>

<style scoped>
    .back-container {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: -1;
      overflow: hidden;
      pointer-events: none;
      background-color: black;
      display: flex;
    }

    .loginleft-container, .loginright-container {
      flex-basis: 50%;
      flex-grow: 1;
    }

    .background-text {
      color: white;
      text-align: left;
      font-size: 60px;
      font-weight: bold;
      padding: 20px;
    }

    .loginleft-container {
      display: flex;
      flex-direction: column;
      position: relative;
      justify-content: space-evenly;
      align-items: center;
    }

    .loginright-container {
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .background-img {
      width: 100%;
      height: auto;
    }

    .background-blue img {
      width: 100%;
      height: 100%;
      object-fit: contain;
    }
</style>