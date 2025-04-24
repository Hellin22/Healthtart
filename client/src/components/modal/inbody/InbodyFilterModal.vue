<template>
  <div class="inbodyfilter-modal-overlay">
    <div class="inbodyfilter-modal-content">
      <h2>필터</h2>
      <div class="inbodyfilter-field">
        <input type="checkbox" v-model="filters.gender" />
        <label>성별</label>
        <select v-model="selectedGender" :disabled="!filters.gender">
          <option value="남">남</option>
          <option value="여">여</option>
        </select>
      </div>
      <div class="inbodyfilter-field">
        <input type="checkbox" v-model="filters.height" />
        <label>키</label>
        <input type="text" v-model="heightRange.min" :disabled="!filters.height" placeholder="0.0" />
        ~
        <input type="text" v-model="heightRange.max" :disabled="!filters.height" placeholder="300.0" />
      </div>
      <div class="inbodyfilter-field">
        <input type="checkbox" v-model="filters.weight" />
        <label>체중</label>
        <input type="text" v-model="weightRange.min" :disabled="!filters.weight" placeholder="0.0" />
        ~
        <input type="text" v-model="weightRange.max" :disabled="!filters.weight" placeholder="200.0" />
      </div>
      <div class="inbodyfilter-field">
        <input type="checkbox" v-model="filters.muscleWeight" />
        <label>골격근량</label>
        <input type="text" v-model="muscleWeightRange.min" :disabled="!filters.muscleWeight" placeholder="0.0" />
        ~
        <input type="text" v-model="muscleWeightRange.max" :disabled="!filters.muscleWeight" placeholder="200.0" />
      </div>
      <div class="inbodyfilter-field">
        <input type="checkbox" v-model="filters.fatWeight" />
        <label>체지방량</label>
        <input type="text" v-model="fatWeightRange.min" :disabled="!filters.fatWeight" placeholder="0.0" />
        ~
        <input type="text" v-model="fatWeightRange.max" :disabled="!filters.fatWeight" placeholder="100.0" />
      </div>
      <div class="inbodyfilter-field">
        <input type="checkbox" v-model="filters.bmi" />
        <label>BMI</label>
        <input type="text" v-model="bmiRange.min" :disabled="!filters.bmi" placeholder="0.0" />
        ~
        <input type="text" v-model="bmiRange.max" :disabled="!filters.bmi" placeholder="100.0" />
      </div>
      <div class="inbodyfilter-field">
        <input type="checkbox" v-model="filters.fatPercentage" />
        <label>체지방률</label>
        <input type="text" v-model="fatPercentageRange.min" :disabled="!filters.fatPercentage" placeholder="0.0" />
        ~
        <input type="text" v-model="fatPercentageRange.max" :disabled="!filters.fatPercentage" placeholder="100.0" />
      </div>
      <div class="inbodyfilter-field">
        <input type="checkbox" v-model="filters.basalMetabolicRate" />
        <label>기초대사량</label>
        <input type="text" v-model="basalMetabolicRateRange.min" :disabled="!filters.basalMetabolicRate" placeholder="0" />
        ~
        <input type="text" v-model="basalMetabolicRateRange.max" :disabled="!filters.basalMetabolicRate" placeholder="5000" />
      </div>
      <button @click="applyFilters" class="apply-button">확인</button>
    </div>
  </div>
</template>

<script setup>
import { ref, defineEmits } from 'vue';

const emit = defineEmits(['applyFilters', 'close']);

const filters = ref({
  gender: false,
  height: false,
  weight: false,
  muscleWeight: false,
  fatWeight: false,
  bmi: false,
  fatPercentage: false,
  basalMetabolicRate: false
});

const selectedGender = ref('남');
const heightRange = ref({ min: '', max: '' });
const weightRange = ref({ min: '', max: '' });
const muscleWeightRange = ref({ min: '', max: '' });
const fatWeightRange = ref({ min: '', max: '' });
const bmiRange = ref({ min: '', max: '' });
const fatPercentageRange = ref({ min: '', max: '' });
const basalMetabolicRateRange = ref({ min: '', max: '' });

const applyFilters = () => {
  const appliedFilters = {
    gender: filters.value.gender ? (selectedGender.value === '남' ? 'M' : 'F') : null,
    height: filters.value.height ? heightRange.value : null,
    weight: filters.value.weight ? weightRange.value : null,
    muscleWeight: filters.value.muscleWeight ? muscleWeightRange.value : null,
    fatWeight: filters.value.fatWeight ? fatWeightRange.value : null,
    bmi: filters.value.bmi ? bmiRange.value : null,
    fatPercentage: filters.value.fatPercentage ? fatPercentageRange.value : null,
    basalMetabolicRate: filters.value.basalMetabolicRate ? basalMetabolicRateRange.value : null,
  };

  emit('applyFilters', appliedFilters);
  emit('close');
};
</script>

<style scoped>
.inbodyfilter-modal-overlay {
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

.inbodyfilter-modal-content {
  background: #000000;
  color: #ffffff;
  padding: 20px;
  border-radius: 10px;
  width: 400px;
  box-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

.inbodyfilter-field {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-left: 10px;
}

.inbodyfilter-field label {
  flex: 1;
  margin-left: 10px;
}

.inbodyfilter-field input[type="checkbox"] {
  appearance: none;
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  border: 2px solid #00ffff;
  border-radius: 3px;
  outline: none;
  cursor: pointer;
  margin-right: 10px;
  position: relative;
  background-color: transparent;
}

.inbodyfilter-field input[type="checkbox"]:checked {
  background-color: #00ffff;
}

.inbodyfilter-field input[type="checkbox"]:checked::before {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #000000;
  font-size: 14px;
  font-weight: bold;
}

.inbodyfilter-field input[type="text"]:disabled, .inbodyfilter-field select:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.inbodyfilter-field label {
  flex: 1;
  margin-left: 10px;
  cursor: pointer;
}

.inbodyfilter-field input[type="text"], .inbodyfilter-field select {
  background-color: #333333;
  color: #ffffff;
  border: 1px solid #00ffff;
  border-radius: 5px;
  padding: 5px;
  margin: 0 5px;
  width: 80px;
}

.inbodyfilter-modal-content h2 {
  margin-bottom: 20px;
  font-size: 24px;
  text-align: center;
}

.apply-button {
  background-color: #00ffff;
  color: #000000;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  width: calc(100% - 40px);
  margin: 20px auto 0;
  display: block;
  font-weight: bold;
}

.apply-button:hover {
  background-color: #00cccc;
}
</style>
