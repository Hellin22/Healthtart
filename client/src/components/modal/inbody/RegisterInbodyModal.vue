<template>
  <div class="inbody-modal-overlay" v-if="isOpen">
    <div class="inbody-modal-content">
      <h2 class="registerinbodytitle">인바디 등록</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label class="lablefirst" for="inbodyImage">인바디 이미지 업로드:</label>
          <input
            type="file"
            id="inbodyImage"
            @change="onFileChange"
            accept="image/*"
            ref="fileInput"
            style="display: none;"
          />
          <span v-if="selectedFile" class="selected-file-name">{{ selectedFile.name }}</span>
          <button type="button" class="file-select-btn" @click="triggerFileInput">파일 선택</button>
        </div>
        <div v-if="imageUrl" class="image-preview-container">
          <img :src="imageUrl" alt="Preview" class="image-preview" />
        </div>
        <div class="inbody-btn-group">
          <button type="submit" class="submit-btn" :disabled="!imageUrl || isSubmitting">등록</button>
          <button type="button" class="cancel-btn" @click="closeModal">취소</button>
        </div>
      </form>
    </div>
    <InbodyLoadingScreen v-if="isLoading" />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import InbodyLoadingScreen from '../../../views/inbody/InbodyLoadingScreen.vue';

const props = defineProps({
isOpen: Boolean,
closeModal: Function,
updateUserData: Function,
});

const router = useRouter();
const imageUrl = ref(null);
const selectedFile = ref(null);
const isLoading = ref(false);
const isSubmitting = ref(false);
const fileInput = ref(null);

const triggerFileInput = () => {
  fileInput.value.click();
};

const onFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    selectedFile.value = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      imageUrl.value = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

const handleSubmit = async () => {
if (!selectedFile.value) {
  alert('파일을 선택해주세요.');
  return;
}

isSubmitting.value = true;
isLoading.value = true;

try {
  const token = localStorage.getItem('token');
  if (!token) {
    throw new Error('인증 토큰이 없습니다. 다시 로그인해주세요.');
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);
  const uploadResponse = await axios.post('/boot/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'Authorization': `Bearer ${token}`,
    },
  });

  const fullUrl = uploadResponse.data;
  const fileName = fullUrl.split('/').pop();

  const ocrResponse = await axios.get(`/boot/ocr/extract-text`, {
    params: { fileName: fileName },
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  
  const ocrData = ocrResponse.data;
  const decodedToken = jwtDecode(token);
  const userCode = decodedToken.sub;

  const dataToSend = {
    ...ocrData,
    userCode
  };

  const registerResponse = await axios.post('/boot/inbody/register', dataToSend, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  const updatedUserInfo = {
    userHeight: dataToSend.height,
    userWeight: dataToSend.weight,
  };

  await axios.patch('/boot/users/mypage/edit/userinfo', updatedUserInfo, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (typeof props.updateUserData === 'function') {
    props.updateUserData(updatedUserInfo);
  }

  alert('인바디 정보가 등록되었습니다!');
  props.closeModal();
  router.push('/mypage');
} catch (error) {
  console.error('Error registering inbody and updating user info:', error);
  if (error.response) {
    console.error('Server responded with:', error.response.status, error.response.data);
    alert(`인바디 등록 중 오류가 발생했습니다: ${error.response.data.message || error.response.statusText}`);
  } else {
    alert('인바디 등록 중 오류가 발생했습니다. 네트워크 연결을 확인해주세요.');
  }
} finally {
  isSubmitting.value = false;
  isLoading.value = false;
}
};
</script>

<style scoped>
.inbody-modal-overlay {
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

.inbody-modal-content {
  background: rgb(0, 0, 0);
  padding-left: 40px;
  padding-right: 40px;
  padding-top: 10px;
  padding-bottom: 10px;
  border-radius: 10px;
  width: 550px;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.registerinbodytitle {
  text-align: center;
  margin-bottom: 20px;
}

.image-preview {
  max-width: 100%;
  margin-top: 10px;
  margin-right: 10px;
}

.form-group {
  margin-bottom: 15px;
}

.lablefirst {
  width: 180px;
}

.inbody-btn-group {
  display: flex;
  justify-content: center;
  margin-top: 10px;
  font-size: 14px;
}

.submit-btn {
  background-color: #00ffff;
  color: #000;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

.submit-btn:disabled {
background-color: #cccccc;
cursor: not-allowed;
}

.cancel-btn {
  background-color: #f44336;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-left: 20px;
}

.image-preview-container {
margin-top: 20px;
text-align: center;
}

.image-preview {
max-width: 100%;
max-height: 300px;
object-fit: contain;
border: 1px solid #ccc;
border-radius: 5px;
}

.submit-btn:disabled {
background-color: #cccccc;
cursor: not-allowed;
}

.file-select-btn {
background-color: #4CAF50;
color: white;
padding: 5px 10px;
border: none;
border-radius: 3px;
cursor: pointer;
font-size: 14px;
}

.file-select-btn:hover {
background-color: #45a049;
}

.selected-file-name {
font-size: 14px;
color: #00ffff;
}
</style>