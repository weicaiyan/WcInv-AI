<template>
  <header class="topbar">
    <div>
      <p class="eyebrow">指数温度</p>
      <h1>今天怎么投</h1>
    </div>
    <span class="date-tag" v-if="firstDate">{{ firstDate }}</span>
  </header>

  <section class="glass-card hero-card">
    <p class="hero-label">懒人结论</p>
    <h2>{{ lazyConclusion }}</h2>
    <p class="muted">按温度从低到高排序，越冷越值得多投。</p>
  </section>

  <van-loading v-if="loading" class="loading" color="#f59e0b">加载中...</van-loading>

  <section v-else-if="error" class="state-card error">{{ error }}</section>

  <van-empty
    v-else-if="!temperatures.length"
    class="empty"
    image="search"
    description="还没有指数估值数据"
  >
    <van-button round color="#f59e0b" @click="loadTemperatures">刷新</van-button>
  </van-empty>

  <section v-else class="temperature-list" aria-label="指数温度列表">
    <article
      v-for="item in temperatures"
      :key="item.index_code"
      class="glass-card temp-card"
      :style="{ '--temp-color': temperatureColor(item.temperature) }"
      @click="$router.push({ name: 'TemperatureHistory', query: { index_code: item.index_code } })"
    >
      <div class="card-head">
        <div>
          <h3>{{ item.index_name }} <van-icon name="arrow" class="arrow-icon" /></h3>
          <p class="code">{{ item.index_code }}</p>
        </div>
        <div class="temp-badge">
          {{ fmt(item.temperature) }}°
        </div>
      </div>

      <div class="bar" aria-hidden="true">
        <span :style="{ width: `${clamp(item.temperature)}%` }" />
      </div>

      <div class="metrics">
        <div>
          <span>PE温度</span>
          <strong>{{ fmt(item.pe_temperature) }}°</strong>
        </div>
        <div>
          <span>PB温度</span>
          <strong>{{ fmt(item.pb_temperature) }}°</strong>
        </div>
        <div>
          <span>操作建议</span>
          <strong>{{ item.action_desc || actionText(item.action) }}</strong>
        </div>
      </div>

      <div class="action-box">
        <span class="dot" aria-hidden="true" />
        <p>{{ actionDetail(item) }}</p>
      </div>
    </article>
  </section>

  <van-button class="refresh" block round plain hairline :loading="loading" @click="loadTemperatures">
    刷新数据
  </van-button>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Button as VanButton, Empty as VanEmpty, Icon as VanIcon, Loading as VanLoading } from 'vant'
import { fetchTemperatures } from '../services/api'

const loading = ref(false)
const error = ref('')
const temperatures = ref([])

const firstDate = computed(() => {
  if (!temperatures.value.length) return ''
  return temperatures.value[0].trade_date || ''
})

const lazyConclusion = computed(() => {
  if (loading.value) return '正在看盘……'
  if (!temperatures.value.length) return '暂无数据'

  const coldest = temperatures.value[0]
  const action = coldest.action_desc || actionText(coldest.action)
  const extra = coldest.ratio < 0 ? '不建议投。' : '可以关注。'
  return `${coldest.index_name} ${fmt(coldest.temperature)}°，${action}，${extra}`
})

onMounted(loadTemperatures)

async function loadTemperatures() {
  loading.value = true
  error.value = ''
  try {
    temperatures.value = await fetchTemperatures()
  } catch (err) {
    error.value = err.message || '数据加载失败'
  } finally {
    loading.value = false
  }
}

function fmt(value) {
  const n = Number(value || 0)
  return Number.isInteger(n) ? String(n) : n.toFixed(2)
}

function clamp(value) {
  return Math.max(0, Math.min(100, Number(value || 0)))
}

function temperatureColor(value) {
  const t = Number(value || 0)
  if (t < 30) return '#22c55e'
  if (t < 60) return '#eab308'
  if (t < 80) return '#f97316'
  return '#ef4444'
}

function actionText(action) {
  const map = {
    HEAVY_BUY: '重仓买入',
    NORMAL_BUY: '正常买入',
    REDUCED_BUY: '减少买入',
    LIGHT_BUY: '轻仓买入',
    HOLD: '持有不动',
    SELL_HALF: '卖出半仓',
    CLEAR: '清仓'
  }
  return map[action] || action || ''
}

function actionDetail(item) {
  const ratio = item.ratio
  if (ratio == null) return '暂无建议'
  if (ratio === 0) return '暂停定投，观望'
  if (ratio > 0) return `建议投入计划金额的 ${Math.round(ratio * 100)}%`
  return '不投——估值偏高'
}
</script>

<style scoped>
.topbar {
  display: flex; align-items: center; justify-content: space-between;
  gap: 14px; margin-bottom: 18px;
}
.eyebrow { color: var(--wc-primary-2); font-size: 12px; font-weight: 800; letter-spacing: 0.14em; }
h1 { margin-top: 4px; font-size: 30px; line-height: 1.1; letter-spacing: -0.04em; }
.date-tag { color: var(--wc-muted); font-size: 12px; }

.hero-card { padding: 20px; margin-bottom: 16px; }
.hero-label { color: var(--wc-primary-2); font-size: 13px; font-weight: 800; }
.hero-card h2 { margin-top: 8px; font-size: 24px; line-height: 1.28; letter-spacing: -0.03em; }
.hero-card .muted { margin-top: 10px; font-size: 13px; }

.loading, .empty { padding: 42px 0; }

.state-card { padding: 22px 18px; border-radius: 24px; color: var(--wc-muted); background: rgba(15,23,42,0.72); border: 1px solid var(--wc-border); }
.state-card.error { color: #fca5a5; border-color: rgba(239,68,68,0.2); }

.temperature-list { display: grid; gap: 14px; }

.temp-card { padding: 18px; overflow: hidden; cursor: pointer; }
.card-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; }
.card-head h3 { font-size: 19px; line-height: 1.25; }
.arrow-icon { font-size: 14px; color: var(--wc-muted); margin-left: 4px; vertical-align: middle; }
.code { margin-top: 4px; font-size: 12px; color: var(--wc-muted); }

.temp-badge {
  min-width: 68px; height: 42px;
  border: 1px solid color-mix(in srgb, var(--temp-color), white 18%);
  border-radius: 999px;
  display: grid; place-items: center;
  color: var(--temp-color);
  background: color-mix(in srgb, var(--temp-color), transparent 82%);
  font-size: 18px; font-weight: 900;
}

.bar {
  height: 9px; margin: 18px 0; border-radius: 999px;
  background: rgba(148,163,184,0.16); overflow: hidden;
}
.bar span {
  display: block; height: 100%; min-width: 6px; border-radius: inherit;
  background: linear-gradient(90deg, var(--temp-color), #fbbf24);
}

.metrics {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px;
}
.metrics div { padding: 10px 8px; border-radius: 16px; background: rgba(15,23,42,0.78); }
.metrics span, .metrics strong { display: block; }
.metrics span { color: var(--wc-muted); font-size: 11px; }
.metrics strong { margin-top: 4px; font-size: 16px; }

.action-box {
  display: flex; gap: 10px; align-items: flex-start;
  margin-top: 14px; padding: 12px; border-radius: 16px;
  color: #f8fafc; background: rgba(245,158,11,0.1);
}
.dot {
  flex: 0 0 auto; width: 9px; height: 9px; margin-top: 5px; border-radius: 50%;
  background: var(--temp-color);
  box-shadow: 0 0 0 5px color-mix(in srgb, var(--temp-color), transparent 76%);
}
.action-box p { font-size: 14px; line-height: 1.45; }

.refresh {
  height: 46px; margin-top: 18px;
  color: var(--wc-primary-2); border-color: rgba(245,158,11,0.4);
  background: rgba(15,23,42,0.5);
}
</style>