<template>
  <van-nav-bar
    title="网格策略"
    left-arrow
    @click-left="$router.back()"
  />

  <section class="glass-card hero-card">
    <p class="hero-label">懒人结论</p>
    <h2>{{ result?.lazy_conclusion || '低估指数才适合做，先填价格和资金。' }}</h2>
    <p class="muted">适合温度低于30°的指数基金；主力仍是定投，网格资金不超过总投资10%。</p>
  </section>

  <section class="glass-card form-card">
    <div class="field-grid">
      <label>
        <span>第一网买入价</span>
        <input v-model.number="form.firstBuyPrice" type="number" step="0.001" placeholder="如 2.72" />
      </label>
      <label>
        <span>网格总资金</span>
        <input v-model.number="form.totalGridAmount" type="number" step="100" placeholder="如 70000" />
      </label>
      <label>
        <span>网格比例</span>
        <select v-model.number="form.gridRatio">
          <option :value="0.07">7% 策略/行业指数</option>
          <option :value="0.05">5% 宽基指数</option>
        </select>
      </label>
      <label>
        <span>最大下跌幅度</span>
        <select v-model.number="form.maxDrawdown">
          <option :value="0.4">40% 默认</option>
          <option :value="0.3">30% 保守</option>
          <option :value="0.5">50% 激进</option>
        </select>
      </label>
    </div>
    <van-button block round color="#f59e0b" :loading="loading" @click="submit">生成网格表</van-button>
    <p v-if="error" class="error">{{ error }}</p>
  </section>

  <section v-if="result" class="summary-grid">
    <div class="glass-card metric-card">
      <span>档位</span>
      <strong>{{ result.levels.length }}档</strong>
    </div>
    <div class="glass-card metric-card">
      <span>最低价</span>
      <strong>{{ result.min_price }}</strong>
    </div>
    <div class="glass-card metric-card">
      <span>实际投入</span>
      <strong>{{ money(result.actual_invest_amount) }}</strong>
    </div>
    <div class="glass-card metric-card danger">
      <span>最坏亏损</span>
      <strong>{{ money(result.worst_case_loss) }}</strong>
    </div>
  </section>

  <section v-if="result" class="glass-card table-card">
    <div class="section-title">
      <h2>网格档位</h2>
      <span>每档独立，不挪用资金</span>
    </div>
    <div class="grid-table">
      <div class="thead row">
        <span>档</span><span>买入</span><span>卖出</span><span>股数</span>
      </div>
      <div v-for="item in result.levels" :key="item.level" class="row">
        <span>{{ item.level }}</span>
        <span>{{ item.buy_price }}</span>
        <span>{{ item.sell_price }}</span>
        <span>{{ item.shares }}</span>
      </div>
    </div>
  </section>

  <section class="glass-card note-card">
    <h2>使用提醒</h2>
    <p>1. 只有指数温度低于30°才启动。</p>
    <p>2. 涨到卖出价手动卖；跌到下一档买入价手动买。</p>
    <p>3. APP里的止盈止损只当提醒，不自动交易。</p>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Button as VanButton } from 'vant'
import { calculateGridPlan } from '../services/api'

const loading = ref(false)
const error = ref('')
const result = ref(null)

const form = reactive({
  firstBuyPrice: 2.72,
  totalGridAmount: 70000,
  gridRatio: 0.07,
  maxDrawdown: 0.4
})

function money(value) {
  return Number(value || 0).toLocaleString('zh-CN', { maximumFractionDigits: 0 })
}

async function submit() {
  if (!form.firstBuyPrice || !form.totalGridAmount) {
    error.value = '先填买入价和网格资金。'
    return
  }
  loading.value = true
  error.value = ''
  try {
    result.value = await calculateGridPlan({
      first_buy_price: form.firstBuyPrice,
      total_grid_amount: form.totalGridAmount,
      grid_ratio: form.gridRatio,
      max_drawdown: form.maxDrawdown,
      lot_size: 100
    })
  } catch (err) {
    error.value = err.message || '生成失败'
  } finally {
    loading.value = false
  }
}

onMounted(submit)
</script>

<style scoped>
.back-btn { display: inline-block; margin-right: 12px; color: var(--wc-primary-2, #f59e0b); font-size: 14px; text-decoration: none; cursor: pointer; user-select: none; }
.topbar { margin-bottom: 18px; }
.eyebrow { color: var(--wc-primary-2); font-size: 12px; font-weight: 800; letter-spacing: 0.14em; }
h1 { margin-top: 4px; font-size: 30px; line-height: 1.1; letter-spacing: -0.04em; }
.hero-card, .form-card, .table-card, .note-card { padding: 18px; margin-bottom: 14px; }
.hero-label { color: var(--wc-primary-2); font-size: 13px; font-weight: 800; }
.hero-card h2 { margin-top: 8px; font-size: 21px; line-height: 1.35; letter-spacing: -0.03em; }
.hero-card .muted { margin-top: 10px; font-size: 13px; }
.field-grid { display: grid; gap: 12px; margin-bottom: 16px; }
label span { display: block; margin-bottom: 7px; color: var(--wc-muted); font-size: 12px; }
input, select {
  width: 100%;
  height: 44px;
  border: 1px solid var(--wc-border);
  border-radius: 14px;
  background: rgba(15, 23, 42, 0.74);
  color: var(--wc-text);
  padding: 0 12px;
  outline: none;
}
.error { margin-top: 10px; color: #f87171; font-size: 13px; }
.summary-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; margin-bottom: 14px; }
.metric-card { padding: 14px; }
.metric-card span { display: block; color: var(--wc-muted); font-size: 12px; }
.metric-card strong { display: block; margin-top: 6px; font-size: 19px; }
.metric-card.danger strong { color: #fb7185; }
.section-title { display: flex; justify-content: space-between; gap: 10px; align-items: flex-end; margin-bottom: 12px; }
.section-title h2, .note-card h2 { font-size: 18px; }
.section-title span { color: var(--wc-muted); font-size: 12px; }
.grid-table { display: grid; gap: 7px; }
.row { display: grid; grid-template-columns: 0.6fr 1fr 1fr 1fr; gap: 8px; align-items: center; font-size: 13px; }
.row span { padding: 8px 0; color: var(--wc-text); }
.thead span { color: var(--wc-muted); font-size: 12px; padding-top: 0; }
.note-card p { margin-top: 8px; color: var(--wc-muted); font-size: 13px; line-height: 1.55; }
</style>
