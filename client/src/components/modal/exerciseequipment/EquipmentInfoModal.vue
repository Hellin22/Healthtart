<template>
  <div class="equipment-modal-overlay" @click.self="close">
    <div class="equipment-modal-content">
      <div class="equipment-modal-header">
        <img :src="equipmentImage" alt="운동기구 이미지" class="equipment-image" />
        <div class="equipment-details">
          <div class="equipment-detail-row">
            <span class="equipment-detail-label">운동기구 명</span>
            <span class="equipment-detail-value">{{ equipmentName }}</span>
          </div>
          <div class="equipment-detail-row">
            <span class="equipment-detail-label">운동 부위</span>
            <span class="equipment-detail-value">{{ equipmentPart }}</span>
          </div>
          <div class="equipment-detail-row">
            <span class="equipment-detail-label">추천 영상</span>
            <a :href="videoUrl" target="_blank" class="equipment-detail-value equipment-video-link">참고 영상 링크</a>
          </div>
        </div>
      </div>
      <div class="equipment-modal-description">
        <p>운동 설명: {{ description }}</p>
      </div>
      <div class="equipment-modal-footer">
        <button @click="confirm" class="equipment-confirm-button">확인</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, toRefs } from 'vue';

const props = defineProps({
  equipment: {
    type: Object,
    required: true,
  },
  equipmentImage: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['close', 'confirm']);

const { name: equipmentName, part: equipmentPart, videoUrl, description } = toRefs(props.equipment);

const close = () => {
  emit('close');
};

const confirm = () => {
  emit('confirm');
};
</script>

<style scoped>
.equipment-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 4;
}

.equipment-modal-content {
  background: #1a1a1a;
  color: #ffffff;
  padding: 20px;
  border-radius: 10px;
  max-width: 600px;
  height: auto;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.equipment-modal-header {
  display: flex;
  margin-bottom: 20px;
}

.equipment-image {
  width: 150px;
  height: 157px;
  object-fit: cover;
  border-radius: 10px;
  margin-right: 20px;
}

.equipment-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.equipment-detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  font-size: 18px;
}

.equipment-detail-label {
  font-weight: bold;
}

.equipment-detail-value {
  text-align: center;
}

.equipment-video-link {
  color: #ffffff;
  text-decoration: none;
}

.equipment-video-link:hover {
  text-decoration: underline;
}

.equipment-modal-description {
  text-align: center;
  margin-left: 20px;
  margin-top: 20px;
  font-size: 16px;
}

.equipment-modal-footer {
  margin-top: 50px;
  text-align: center;
}

.equipment-confirm-button {
  background-color: #00ffff;
  color: #1a1a1a;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
}

.equipment-confirm-button:hover {
  background-color: #00cccc;
}
</style>