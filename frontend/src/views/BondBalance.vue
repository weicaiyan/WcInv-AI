<template>
  <header class="topbar">
    <div>
      <p class="eyebrow">策略03</p>
      <h1>股债平衡</h1>
    </div>
  </header>

  <section class="glass-card hero-card">
    <p class="hero-label">懒人结论</p>
    <h2>{{ result?.lazy_conclusion || '输入中证全指温度，查看股债配比。' }}</h2>
    <p class="muted">温度 = (PE分位点 + PB分位点) / 2，每10°一档，每年再平衡一次。</p>
  </section>

  <section class="glass-card form-card">
    <label class="temp-input">
      <span>中证全指温度</span>
      <div class="temp-row">
        <input v-model.number="form.temperature" type="number" step="0.1" placeholder="如 38.1" />
        <span class="unit">°</span>
      </div>
    </label>
    <van-button block round color="#f59e0b" :loading="loading" @click="submit">计算配比</van-button>
    <p v-if="error" class="error">{{ error }}</p>
  </section>

  <section v-if="result" class="result-area">
    <div class="ratio-bars">
      <div class="ratio-bar stock-bar" :style="{ width: result.stock_ratio + '%' }">
        <span>股票 {{ result.stock_ratio }}%</span>
      </div>
      <div class="ratio-bar bond-bar" :style="{ width: result.bond_ratio + '%' }">
        <span>债券 {{ result.bond_ratio }}%</span>
      </div>
    </div>

    <div class="glass-card tip-card">
      <p>💡 股票类内部：50%宽基 + 30%策略/境外 + 20%行业（可调）</p>
      <p>💡 债券类：选2只纯债基金，平均分配</p>
      <p>💡 每年按新温度查表再平衡一次</p>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { calculateBondBalance } from '@/services/api'

const form = ref({ temperature: null })
const result = ref(null)
const loading = ref(false)
const error = ref('')

async function submit() {
  error.value = ''
  if (!form.value.temperature && form.value.temperature !== 0) {
    error.value = '请输入中证全指温度'
    return
  }
  loading.value = true
  try {
    const res = await calculateBondBalance({ temperature: form.value.temperature })
    result.value = res.data
  } catch (e) {
    error.value = e.message || '请求失败'
    result.value = null
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.temp-input {
  display: block;
  margin-bottom: 20px;
}
.temp-input > span {
  display: block;
  margin-bottom: 8px;
  color: var(--muted, #888);
  font-size: 14px;
}
.temp-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.temp-row input {
  flex: 1;
  padding: 12px 16px;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  outline: none;
}
.temp-row .unit {
  font-size: 22px;
  color: var(--muted, #888);
}
.ratio-bars {
  margin-bottom: 16px;
}
.ratio-bar {
  padding: 14px 16px;
  border-radius: 10px;
  margin-bottom: 8px;
  font-weight: 600;
  font-size: 15px;
  transition: width 0.5s ease;
}
.stock-bar {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: #111;
}
.bond-bar {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: #fff;
}
.tip-card p {
  margin: 8px 0;
  font-size: 13px;
  color: var(--muted, #888);
  line-height: 1.6;
}
</style>
