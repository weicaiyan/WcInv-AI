<template>
  <van-nav-bar
    title="市场入场"
    left-arrow
    @click-left="$router.back()"
  >
    <template #right>
      <span class="date-tag" v-if="decision">{{ decision.trade_date }}</span>
    </template>
  </van-nav-bar>

  <section v-if="error" class="state-card error">{{ error }}</section>
  <van-loading v-else-if="loading" class="loading" color="#f59e0b">加载中...</van-loading>

  <section v-else-if="decision" class="decision-area">
    <div class="glass-card hero-card" :class="levelClass">
      <p class="hero-label">结论</p>
      <h2>{{ levelText }}</h2>
      <p class="hero-desc">{{ levelDescText }}</p>
    </div>

    <div class="glass-card rule-card">
      <h3>入场条件</h3>
      <div class="rule-box">
        <p>沪深300 或 中证500 任一指数满足：</p>
        <ul>
          <li>PE 十年分位点 ≤ <strong>50%</strong></li>
          <li>PB 十年分位点 ≤ <strong>20%</strong></li>
        </ul>
      </div>
    </div>

    <section class="snapshots" aria-label="指数估值快照">
      <article
        v-for="s in decision.snapshots"
        :key="s.index_code"
        class="glass-card snap-card"
      >
        <div class="snap-head">
          <h3>{{ s.index_name }}</h3>
          <span class="code">{{ s.index_code }}</span>
        </div>
        <div class="snap-metrics">
          <div class="metric pe" :class="metricClass(s.pe_percentile_10y, 50)">
            <span>PE分位</span>
            <strong>{{ fmt(s.pe_percentile_10y) }}%</strong>
          </div>
          <div class="metric pb" :class="metricClass(s.pb_percentile_10y, 20)">
            <span>PB分位</span>
            <strong>{{ fmt(s.pb_percentile_10y) }}%</strong>
          </div>
        </div>
        <div class="verdict">
          {{ peOk(s.pe_percentile_10y) ? '✓' : '✗' }} PE≤50%
          &nbsp;&nbsp;
          {{ pbOk(s.pb_percentile_10y) ? '✓' : '✗' }} PB≤20%
        </div>
      </article>
    </section>

    <div class="warnings" v-if="decision.warnings && decision.warnings.length">
      <p v-for="w in decision.warnings" :key="w">⚠️ {{ w }}</p>
    </div>

    <section class="glass-card trend-card" v-if="trendChartOption.series">
      <h3>温度走势（3年）</h3>
      <v-chart :option="trendChartOption" style="height: 240px" autoresize />
    </section>
  </section>

  <van-button class="refresh" block round plain hairline :loading="loading" @click="load">
    刷新数据
  </van-button>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Button as VanButton, Loading as VanLoading } from 'vant'
import VChart from 'vue-echarts'
import 'echarts'
import { fetchMarketEntry, fetchTemperatureHistory } from '../services/api'

const loading = ref(false)
const error = ref('')
const decision = ref(null)

// 温度趋势图
const temp300 = ref(null)
const temp500 = ref(null)
const tempDays = 1095 // 3年
const INDICES = [
  { code: '000300', name: '沪深300', color: '#f59e0b', ref: temp300 },
  { code: '000905', name: '中证500', color: '#22d3ee', ref: temp500 }
]

onMounted(() => { load(); loadTrends() })

async function load() {
  loading.value = true
  error.value = ''
  try {
    decision.value = await fetchMarketEntry()
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

async function loadTrends() {
  try {
    const [d300, d500] = await Promise.all([
      fetchTemperatureHistory('000300', tempDays),
      fetchTemperatureHistory('000905', tempDays)
    ])
    temp300.value = d300
    temp500.value = d500
  } catch (_) { /* silent */ }
}

const levelClass = computed(() => {
  if (!decision.value) return ''
  return { LOW: 'level-low', NEUTRAL: 'level-neutral', HIGH_RISK: 'level-high' }[decision.value.level] || ''
})

const levelText = computed(() => {
  if (!decision.value) return ''
  return { LOW: '可以入场', NEUTRAL: '继续等待', HIGH_RISK: '不建议入场' }[decision.value.level] || decision.value.level
})

const levelDescText = computed(() => {
  if (!decision.value) return ''
  const d = decision.value
  if (d.level === 'LOW') return `${d.matched_index_name}同时满足PE≤50%且PB≤20%，市场低估，可进入选股流程。`
  if (d.level === 'HIGH_RISK') return 'PE或PB分位点偏高，风险较大，不建议启动股票策略。'
  return '不满足入场条件，建议继续观察等待。'
})

function peOk(v) { return v != null && v <= 50 }
function pbOk(v) { return v != null && v <= 20 }
function metricClass(v, threshold) {
  if (v == null) return 'na'
  return v <= threshold ? 'pass' : 'fail'
}
function fmt(v) { return v != null ? v.toFixed(1) : '--' }

const trendChartOption = computed(() => {
  const d300 = temp300.value?.history
  const d500 = temp500.value?.history
  if (!d300?.length && !d500?.length) return {}

  const allDates = [...new Set([
    ...(d300 || []).map(i => i.date?.slice(0, 7)),
    ...(d500 || []).map(i => i.date?.slice(0, 7))
  ])].sort()

  return {
    backgroundColor: 'transparent',
    legend: {
      data: INDICES.map(i => i.name),
      top: 0,
      textStyle: { color: '#94a3b8', fontSize: 11 },
      itemWidth: 12,
      itemHeight: 8
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(15, 23, 42, 0.96)',
      borderColor: 'rgba(148, 163, 184, 0.18)',
      textStyle: { color: '#f8fafc', fontSize: 12 }
    },
    grid: { top: 24, right: 24, bottom: 10, left: 44 },
    xAxis: {
      type: 'category',
      data: allDates,
      axisLine: { lineStyle: { color: '#334155' } },
      axisLabel: { color: '#94a3b8', fontSize: 10, interval: Math.max(Math.floor(allDates.length / 6), 1) }
    },
    yAxis: {
      type: 'value', min: 0, max: 100,
      splitLine: { lineStyle: { color: '#1e293b' } },
      axisLabel: { color: '#94a3b8', fontSize: 10, formatter: '{value}°' }
    },
    series: INDICES.map(idx => {
      const data = idx.ref.value?.history || []
      const dateMap = Object.fromEntries(data.map(i => [i.date?.slice(0, 7), Number(i.temperature)]))
      return {
        name: idx.name,
        type: 'line',
        data: allDates.map(d => dateMap[d] ?? null),
        smooth: true,
        symbol: 'none',
        lineStyle: { color: idx.color, width: 2 },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: idx.color + '22' },
              { offset: 1, color: idx.color + '05' }
            ]
          }
        }
      }
    })
  }
})
</script>

<style scoped>
.date-tag { color: var(--wc-muted); font-size: 12px; }

.loading { padding: 42px 0; }

.state-card { padding: 22px 18px; border-radius: 24px; color: var(--wc-muted); background: rgba(15,23,42,0.72); border: 1px solid var(--wc-border); }
.state-card.error { color: #fca5a5; border-color: rgba(239,68,68,0.2); }

.hero-card { padding: 20px; margin-bottom: 16px; border-left: 4px solid; }
.hero-label { font-size: 13px; font-weight: 800; }
.hero-card h2 { margin-top: 8px; font-size: 24px; line-height: 1.28; }
.hero-desc { margin-top: 10px; font-size: 13px; color: var(--wc-muted); line-height: 1.6; }

.level-low { border-left-color: #22c55e; }
.level-low .hero-label, .level-low h2 { color: #22c55e; }
.level-neutral { border-left-color: #eab308; }
.level-neutral .hero-label, .level-neutral h2 { color: #eab308; }
.level-high { border-left-color: #ef4444; }
.level-high .hero-label, .level-high h2 { color: #ef4444; }

.rule-card { padding: 18px; margin-bottom: 16px; }
.rule-card h3 { font-size: 16px; }
.rule-box { margin-top: 12px; padding: 14px; border-radius: 16px; background: rgba(15,23,42,0.78); }
.rule-box p { font-size: 13px; margin-bottom: 8px; }
.rule-box ul { padding-left: 18px; font-size: 14px; line-height: 1.8; }
.rule-box strong { color: var(--wc-primary-2); }

.snapshots { display: grid; gap: 14px; margin-bottom: 16px; }

.snap-card { padding: 18px; }
.snap-head { display: flex; align-items: baseline; justify-content: space-between; }
.snap-head h3 { font-size: 18px; }
.code { color: var(--wc-muted); font-size: 12px; }

.snap-metrics { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-top: 16px; }
.metric { padding: 14px; border-radius: 16px; text-align: center; }
.metric span { display: block; font-size: 12px; margin-bottom: 6px; }
.metric strong { font-size: 22px; }
.metric.pass { background: rgba(34,197,94,0.12); color: #22c55e; }
.metric.pass span { color: #6ee7a7; }
.metric.fail { background: rgba(239,68,68,0.12); color: #ef4444; }
.metric.fail span { color: #fca5a5; }
.metric.na { background: rgba(100,116,139,0.12); color: #94a3b8; }

.verdict { margin-top: 14px; font-size: 13px; font-family: monospace; color: var(--wc-muted); }

.warnings { margin-bottom: 16px; }
.warnings p {
  padding: 12px; border-radius: 14px;
  background: rgba(234,179,8,0.1); color: #facc15;
  font-size: 13px; line-height: 1.5;
}

.trend-card { padding: 18px; margin-bottom: 16px; }
.trend-card h3 { font-size: 16px; margin-bottom: 12px; }

.refresh {
  height: 46px; margin-top: 8px;
  color: var(--wc-primary-2); border-color: rgba(245,158,11,0.4);
  background: rgba(15,23,42,0.5);
}
</style>