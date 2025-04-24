<template>
    <div class="modal-overlay" v-if="isOpen">
      <div class="delete-modal-content">
        <div class="delete-modal-header">
          <button class="delete-close-btn" @click="closeModal">✖</button>
        </div>
        <div class="delete-modal-body">삭제하시겠습니까?</div>
        <div class="delete-modal-actions">
          <button class="delete-cancel-btn" @click="closeModal">취소</button>
          <button class="delete-confirm-btn" @click="confirmDeletion">확인</button>
        </div>
      </div>
    </div>
  </template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
    isOpen: Boolean
});

const emit = defineEmits(['close', 'confirm']);

const closeModal = () => {
    emit('close');
};

const confirmDeletion = () => {
    emit('confirm');
    closeModal();
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.delete-modal-content {
  background-color: #ffffff;
  border-radius: 10px;
  width: 300px;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.delete-modal-header {
  background-color: #000000;
  padding: 10px;
  text-align: right;
}

.delete-close-btn {
  background-color: transparent;
  border: none;
  color: #ffffff;
  font-size: 20px;
  cursor: pointer;
}

.delete-modal-body {
  padding: 30px 20px;
  background-color: #ffffff;
  font-size: 18px;
  margin: 0;
  word-break: keep-all;
  color: #000000;
  text-align: center;
  font-weight: bold;
}

.delete-modal-actions {
  display: flex;
  border-top: 1px solid #e0e0e0;
}

.delete-cancel-btn,
.delete-confirm-btn {
  flex: 1;
  padding: 15px 0;
  font-size: 16px;
  border: none;
  cursor: pointer;
  background-color: #ffffff;
}

.delete-cancel-btn {
  border-right: 1px solid #e0e0e0;
  color: #000000;
}

.delete-confirm-btn {
  color: #ff0000;
}

.delete-cancel-btn:hover {
    background-color: #e0e0e0;
}

.delete-confirm-btn:hover {
  background-color: #ff0000;
  color : white;
}

</style>