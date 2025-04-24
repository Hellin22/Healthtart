<template>
  <div class="myinbody-modal-overlay" @click.self="$emit('close')">
    <div class="myinbody-modal-content">
      <h2 class="myinbody-title">내 인바디 정보</h2>
      <div class="myinbody-info-container">
        <div class="myinbody-info-labels">
          <div v-for="key in Object.keys(inbodyData[0])" :key="key" class="myinbody-info-label">
            {{ formatLabel(key) }}
          </div>
        </div>
        <div class="myinbody-info-values-container">
          <div v-for="(info, index) in inbodyData" :key="index" class="myinbody-info-card">
            <div v-for="(value, key) in info" :key="key" class="myinbody-info-value">
              {{ formatValue(key, value) }}
            </div>
          </div>
        </div>
      </div>
      <button @click="$emit('close')" class="myinbody-close-btn">확인</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps(['inbodyData']);

const formatLabel = (key) => {
  const labels = {
    inbodyScore: '점수',
    height: '키',
    weight: '체중',
    muscleWeight: '골격근량',
    fatWeight: '체지방량',
    bmi: '체질량지수',
    fatPercentage: '체지방률',
    basalMetabolicRate: '기초대사량',
    dayOfInbody: '측정일'
  };
  return labels[key] || key;
};

const formatValue = (key, value) => {
  if (key === 'dayOfInbody') {
    return new Date(value).toLocaleDateString();
  }
  return value;
};
</script>

<style scoped>
.myinbody-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.myinbody-modal-content {
  background: #000000;
  color: #ffffff;
  padding: 20px;
  border-radius: 10px;
  width: 90%;
  max-width: 600px;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.myinbody-title {
  text-align: center;
  margin-bottom: 20px;
  color: #ffffff;
}

.myinbody-info-container {
  display: flex;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.myinbody-info-labels {
  flex: 0 0 auto;
  padding-right: 20px;
  border-right: 1px solid #00ffff;
}

.myinbody-info-label {
  color: #00ffff;
  margin-bottom: 10px;
  height: 24px;
  line-height: 24px;
}

.myinbody-info-values-container {
  display: flex;
  overflow-x: auto;
  padding-left: 20px;
}

.myinbody-info-card {
  flex: 0 0 auto;
  width: 100px;
  margin-right: 20px;
}

.myinbody-info-value {
  margin-bottom: 10px;
  height: 24px;
  line-height: 24px;
}

.myinbody-close-btn {
  background-color: #00ffff;
  color: #000000;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  width: 100%;
  margin-top: 20px;
}

.myinbody-close-btn:hover {
  background-color: #00cccc;
}
</style>