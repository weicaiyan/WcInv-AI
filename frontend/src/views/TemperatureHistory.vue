<template>
  <van-nav-bar
    :title="history?.index_name || '温度走势'"
    left-arrow
    @click-left="$router.back()"
  />

  <section class="picker-card">
    <select v-model="indexCode" @change="switchIndex" class="index-select">
      <option v-for="idx in indexList" :key="idx.code" :value="idx.code">
        {{ idx.code }} {{ idx.name }}
      </option>
    </select>
  </section>

  <van-tabs v-model:active="activeTab" @change="onTabChange" class="time-tabs">
    <van-tab v-for="t in tabs" :key="t.days" :title="t.label" />
  </van-tabs>

  <section v-if="loading" class="state-card">加载中…</section>
  <section v-else-if="error" class="state-card error">{{ error }}</section>
  <section v-else-if="!history || !history.history?.length" class="state-card">暂无历史数据</section>

  <template v-else>
    <section class="temp-hero">
      <div class="temp-circle" :style="{ borderColor: tempColor(currentTemp) }">
        <span class="temp-value" :style="{ color: tempColor(currentTemp) }">{{ currentTemp }}°</span>
        <span class="temp-label">综合温度</span>
      </div>
      <div class="temp-meter">
        <div class="meter-bar">
          <div class="meter-fill" :style="{ width: Math.min(currentTemp, 100) + '%', background: meterGradient }" />
        </div>
        <div class="meter-labels">
          <span>0° 低估</span>
          <span>30°</span>
          <span>70°</span>
          <span>100° 高估</span>
        </div>
      </div>
    </section>

    <section class="chart-wrap">
      <v-chart :option="chartOption" style="height: 300px" autoresize />
    </section>

    <section class="summary-grid">
      <div class="glass-card metric-card">
        <span>趋势</span>
        <strong>{{ trendText }}</strong>
      </div>
      <div class="glass-card metric-card">
        <span>数据点</span>
        <strong>{{ history.history.length }} 个月</strong>
      </div>
    </section>

    <section class="glass-card note-card">
      <h2>温度说明</h2>
      <p>综合温度 = (PE温度 + PB温度) / 2，按月取最后交易日。</p>
      <div class="temp-legend">
        <span class="dot" style="background:#4ade80"></span>0~30° 低估
        <span class="dot" style="background:#fbbf24"></span>30~70° 正常
        <span class="dot" style="background:#f97316"></span>70~85° 偏高
        <span class="dot" style="background:#ef4444"></span>85~100° 高估
      </div>
    </section>
  </template>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import VChart from 'vue-echarts'
import 'echarts'
import { fetchTemperatureHistory } from '../services/api'

const route = useRoute()

const indexList = [
  { code: '000300', name: '沪深300' },
  { code: '000016', name: '上证50' },
  { code: '000905', name: '中证500' },
  { code: '000852', name: '中证1000' },
  { code: '399006', name: '创业板指' },
  { code: '399005', name: '中小板指' },
  { code: '000688', name: '科创50' },
  { code: '000015', name: '红利指数' },
  { code: '000922', name: '中证红利' },
  { code: '399324', name: '深证红利' },
  { code: '000925', name: '基本面50' },
  { code: '930782', name: '500SNLV' }
]

const indexCode = ref(route.query.index_code || '000300')

watch(() => route.query.index_code, (newVal) => {
  if (newVal && newVal !== indexCode.value) {
    indexCode.value = newVal
    load(newVal, tabs[activeTab.value].days)
  }
})
const loading = ref(true)
const error = ref('')
const history = ref(null)
const activeTab = ref(0)

const tabs = [
  { label: '1年', days: 365 },
  { label: '3年', days: 1095 },
  { label: '5年', days: 1825 },
  { label: '10年', days: 3650 }
]

async function load(code, days) {
  loading.value = true
  error.value = ''
  try {
    history.value = await fetchTemperatureHistory(code, days)
  } catch (err) {
    error.value = err.message || '加载失败'
    history.value = null
  } finally {
    loading.value = false
  }
}

function switchIndex() {
  load(indexCode.value, tabs[activeTab.value].days)
}

function onTabChange() {
  load(indexCode.value, tabs[activeTab.value].days)
}

const last = computed(() => {
  const h = history.value?.history
  return h?.length ? h[h.length - 1] : null
})

const currentTemp = computed(() => {
  const t = last.value?.temperature
  return t != null ? Number(t) : 0
})

const trendText = computed(() => {
  const h = history.value?.history
  if (!h || h.length < 2) return '—'
  const prev = Number(h[h.length - 2].temperature)
  const curr = Number(h[h.length - 1].temperature)
  const diff = curr - prev
  if (diff > 1) return '↑ 升温'
  if (diff < -1) return '↓ 降温'
  return '→ 持平'
})

const meterGradient = computed(() => {
  const t = currentTemp.value
  if (t <= 30) return '#4ade80'
  if (t <= 70) return 'linear-gradient(90deg, #4ade80, #fbbf24)'
  if (t <= 85) return 'linear-gradient(90deg, #fbbf24, #f97316)'
  return 'linear-gradient(90deg, #f97316, #ef4444)'
})

function tempColor(t) {
  if (t <= 30) return '#4ade80'
  if (t <= 70) return '#fbbf24'
  if (t <= 85) return '#f97316'
  return '#ef4444'
}

const chartOption = computed(() => {
  const h = history.value?.history
  if (!h?.length) return {}
  const dates = h.map(i => i.date?.slice(0, 7))
  const tempData = h.map(i => Number(i.temperature))

  return {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(15, 23, 42, 0.96)',
      borderColor: 'rgba(148, 163, 184, 0.18)',
      textStyle: { color: '#f8fafc', fontSize: 12 },
      formatter(params) {
        if (!params?.length) return ''
        const p = params[0]
        return `<div style="font-weight:600;margin-bottom:6px">${p.axisValue}</div>
          <div style="display:flex;align-items:center;gap:6px">
            <span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:${p.color}"></span>
            综合温度: ${Number(p.value).toFixed(2)}°
          </div>`
      }
    },
    grid: { top: 10, right: 24, bottom: 10, left: 44 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#334155' } },
      axisLabel: {
        color: '#94a3b8',
        fontSize: 10,
        interval: Math.max(Math.floor(dates.length / 6), 1)
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      splitLine: { lineStyle: { color: '#1e293b' } },
      axisLabel: { color: '#94a3b8', fontSize: 10, formatter: '{value}°' }
    },
    series: [
      {
        name: '综合温度',
        type: 'line',
        data: tempData,
        smooth: true,
        symbol: 'none',
        lineStyle: { color: '#f59e0b', width: 2 },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(245,158,11,0.14)' },
              { offset: 1, color: 'rgba(245,158,11,0.02)' }
            ]
          }
        }
      }
    ]
  }
})

onMounted(() => load(indexCode.value, tabs[0].days))
</script>

<style scoped>
.picker-card { padding: 0 0 12px 0; }
.index-select {
  width: 100%;
  height: 44px;
  border: 1px solid var(--wc-border);
  border-radius: 14px;
  background: rgba(15, 23, 42, 0.74);
  color: var(--wc-text);
  padding: 0 14px;
  font-size: 14px;
  outline: none;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%2394a3b8' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
}

.time-tabs { margin-bottom: 8px; }
.time-tabs :deep(.van-tabs__nav) { background: transparent; }
.time-tabs :deep(.van-tab) { color: var(--wc-muted); font-size: 14px; }
.time-tabs :deep(.van-tab--active) { color: var(--wc-primary); font-weight: 600; }
.time-tabs :deep(.van-tabs__line) { background: var(--wc-primary); }

.temp-hero { margin-bottom: 10px; }
.temp-circle {
  min-width: 96px; min-height: 96px;
  padding: 12px 10px;
  border-radius: 50%;
  border: 3px solid;
  display: flex;
  width: fit-content;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 auto 14px auto;
}
.temp-value { font-size: 28px; font-weight: 800; line-height: 1.1; white-space: nowrap; }
.temp-label { font-size: 12px; color: var(--wc-muted); margin-top: 2px; }

.temp-meter { padding: 0 4px; }
.meter-bar {
  height: 6px;
  border-radius: 3px;
  background: #1e293b;
  overflow: hidden;
}
.meter-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.6s ease;
}
.meter-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 6px;
  font-size: 10px;
  color: #64748b;
}

.chart-wrap { margin-bottom: 14px; }

.summary-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; margin-bottom: 14px; }
.metric-card { padding: 14px; }
.metric-card span { display: block; color: var(--wc-muted); font-size: 12px; }
.metric-card strong { display: block; margin-top: 6px; font-size: 19px; }

.note-card { padding: 18px; }
.note-card h2 { font-size: 18px; }
.note-card p { margin-top: 8px; color: var(--wc-muted); font-size: 13px; line-height: 1.55; }
.temp-legend {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 12px;
  color: var(--wc-muted);
}
.temp-legend .dot {
  display: inline-block;
  width: 8px; height: 8px;
  border-radius: 50%;
  margin-right: 2px;
}

.state-card { padding: 22px 18px; border-radius: 24px; color: var(--wc-muted); background: rgba(15,23,42,0.72); border: 1px solid var(--wc-border); }
.state-card.error { color: #fca5a5; border-color: rgba(239,68,68,0.2); }
</style>
