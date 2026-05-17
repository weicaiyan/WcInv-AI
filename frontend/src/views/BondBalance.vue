<template>
  <van-nav-bar
    title="股债平衡"
    left-arrow
    @click-left="$router.back()"
  />

  <section class="glass-card hero-card">
    <p class="hero-label">懒人结论</p>
    <h2>{{ result?.lazy_conclusion || '输入中证全指温度，查看股债配比' }}</h2>
    <p class="muted">温度 = (PE分位点 + PB分位点) / 2，每 10° 一档，每年再平衡一次</p>
  </section>

  <section class="glass-card form-card">
    <div class="field-grid">
      <label>
        <span>中证全指温度</span>
        <div class="input-with-unit">
          <input v-model.number="form.temperature" type="number" step="0.1" placeholder="如 38.1" />
          <span class="unit">°</span>
        </div>
      </label>
    </div>
    <van-button block round color="#f59e0b" :loading="loading" @click="submit">计算配比</van-button>
    <p v-if="error" class="error">{{ error }}</p>
  </section>

  <section v-if="result" class="summary-grid">
    <div class="glass-card metric-card">
      <span>股票类</span>
      <strong class="stock">{{ result.stock_ratio }}%</strong>
    </div>
    <div class="glass-card metric-card">
      <span>债券类</span>
      <strong class="bond">{{ result.bond_ratio }}%</strong>
    </div>
  </section>

  <section v-if="result" class="ratio-section">
    <div class="ratio-track">
      <div class="ratio-fill stock-fill" :style="{ width: result.stock_ratio + '%' }" />
      <div class="ratio-fill bond-fill" :style="{ width: result.bond_ratio + '%' }" />
    </div>
    <div class="ratio-labels">
      <span>股票 {{ result.stock_ratio }}%</span>
      <span>债券 {{ result.bond_ratio }}%</span>
    </div>
  </section>

  <section v-if="result" class="glass-card tip-card">
    <p>💡 股票类内部：50% 宽基 + 30% 策略/境外 + 20% 行业（可调）</p>
    <p>💡 债券类：选 2 只纯债基金，平均分配</p>
    <p>💡 每年按新温度查表再平衡一次</p>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { calculateBondBalance } from '../services/api'

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
    result.value = await calculateBondBalance({ temperature: form.value.temperature })
  } catch (e) {
    error.value = e.message || '请求失败'
    result.value = null
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.hero-label { color: var(--wc-primary-2); font-size: 12px; font-weight: 800; letter-spacing: 0.16em; }
.hero-card h2 { margin-top: 6px; font-size: 19px; line-height: 1.4; }
.hero-card .muted { margin-top: 8px; font-size: 13px; }

.form-card { padding: 18px; }

.field-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}
.field-grid label {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.field-grid label > span {
  font-size: 14px;
  color: var(--wc-muted);
}
.input-with-unit {
  display: flex;
  align-items: center;
  gap: 8px;
}
.input-with-unit input {
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
.input-with-unit .unit {
  font-size: 22px;
  color: var(--wc-muted);
}

.error { margin-top: 10px; font-size: 13px; color: var(--wc-danger); }

.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 14px;
}
.metric-card {
  padding: 16px;
  text-align: center;
}
.metric-card span {
  display: block;
  font-size: 13px;
  color: var(--wc-muted);
  margin-bottom: 8px;
}
.metric-card strong {
  font-size: 28px;
  font-weight: 800;
}
.metric-card .stock { color: var(--wc-primary); }
.metric-card .bond { color: #3b82f6; }

.ratio-section {
  margin-bottom: 14px;
}
.ratio-track {
  height: 44px;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  background: rgba(255,255,255,0.04);
}
.ratio-fill {
  height: 100%;
  display: flex;
  align-items: center;
  transition: width 0.5s ease;
}
.stock-fill {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  border-radius: 12px 0 0 12px;
}
.bond-fill {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border-radius: 0 12px 12px 0;
}
.ratio-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 13px;
  color: var(--wc-muted);
}

.tip-card p {
  margin: 8px 0;
  font-size: 13px;
  color: var(--wc-muted);
  line-height: 1.6;
}
</style>