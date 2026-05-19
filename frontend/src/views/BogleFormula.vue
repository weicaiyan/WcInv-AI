<template>
  <van-nav-bar
    title="博格公式"
    left-arrow
    @click-left="$router.back()"
  >
    <template #right>
      <span class="date-tag" v-if="bogle">{{ bogle.trade_date }}</span>
    </template>
  </van-nav-bar>

  <section v-if="loading" class="state-card">加载中...</section>
  <section v-else-if="error" class="state-card error">{{ error }}</section>

  <template v-else-if="bogle">
    <section class="hero-card">
      <p class="label">懒人结论</p>
      <h2>{{ bogle.lazy_conclusion }}</h2>
      <p class="desc">默认按 5 年投资期、盈利增长率 7.5% 估算。</p>
    </section>

    <section class="rule-card">
      <p class="rule-title">策略02规则</p>
      <p>预期年化收益率 = 股息率 + 盈利增长率 + 市盈率变化率。</p>
      <p>只适用于消费、医药这种盈利较稳定的行业指数。</p>
    </section>

    <section class="index-list">
      <article v-for="item in bogle.indices" :key="item.index_code" class="index-card">
        <div class="index-head">
          <div>
            <h3>{{ item.index_name }}</h3>
            <p>{{ item.index_code }} · PE {{ item.current_pe.toFixed(2) }} · 股息 {{ percent(item.dividend_yield) }}</p>
          </div>
          <span class="tag" :class="tagClass(item)">{{ tagText(item) }}</span>
        </div>

        <p class="conclusion">{{ item.conclusion }}</p>

        <div class="scenario-list">
          <div v-for="s in item.scenarios" :key="s.scenario" class="scenario" :class="scenarioClass(s)">
            <span>{{ s.scenario_name }}</span>
            <strong>{{ s.expected_annual_return.toFixed(2) }}%</strong>
            <em>{{ s.action_desc }}</em>
          </div>
        </div>
      </article>
    </section>
  </template>

  <van-button class="refresh" block round plain hairline :loading="loading" @click="load">
    重新加载当前数据
  </van-button>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Button as VanButton } from 'vant'
import { fetchBogle } from '../services/api'

const loading = ref(true)
const error = ref('')
const bogle = ref(null)

async function load() {
  loading.value = true
  error.value = ''
  try {
    bogle.value = await fetchBogle()
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

function percent(value) {
  return `${(Number(value || 0) * 100).toFixed(2)}%`
}

function conservative(item) {
  return item.scenarios?.find(s => s.scenario === 'CONSERVATIVE')
}

function tagText(item) {
  const s = conservative(item)
  if (!s) return '无数据'
  if (s.action === 'BUY') return '可考虑'
  if (s.action === 'DEFENSIVE_SELL_OR_AVOID') return '先别买'
  return '观察'
}

function tagClass(item) {
  const s = conservative(item)
  if (!s) return 'muted'
  if (s.action === 'BUY') return 'ok'
  if (s.action === 'DEFENSIVE_SELL_OR_AVOID') return 'bad'
  return 'watch'
}

function scenarioClass(s) {
  if (s.action === 'BUY') return 'ok'
  if (s.action === 'DEFENSIVE_SELL_OR_AVOID') return 'bad'
  return 'watch'
}

onMounted(load)
</script>

<style scoped>
.date-tag {
  padding: 7px 10px;
  border-radius: 999px;
  color: var(--wc-muted);
  background: rgba(148, 163, 184, 0.12);
  border: 1px solid rgba(148, 163, 184, 0.14);
  font-size: 12px;
}
.state-card,
.hero-card,
.rule-card,
.index-card {
  border: 1px solid rgba(148, 163, 184, 0.14);
  background: rgba(15, 23, 42, 0.72);
  box-shadow: var(--wc-card-shadow);
  backdrop-filter: blur(18px);
}
.state-card { padding: 22px; border-radius: 24px; color: var(--wc-muted); }
.state-card.error { color: #fca5a5; }
.hero-card {
  padding: 22px;
  border-radius: 28px;
  margin-bottom: 14px;
  border-color: rgba(245, 158, 11, 0.28);
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.14), rgba(15, 23, 42, 0.76));
}
.label,
.desc { margin: 0; color: var(--wc-muted); }
.hero-card h2 { margin: 8px 0; font-size: 24px; line-height: 1.28; }
.rule-card {
  border-radius: 20px;
  padding: 14px;
  margin-bottom: 14px;
  color: var(--wc-muted);
  line-height: 1.55;
  font-size: 13px;
}
.rule-card p { margin: 4px 0; }
.rule-title { color: #fbbf24 !important; font-weight: 700; }
.index-list { display: grid; gap: 12px; }
.index-card { border-radius: 24px; padding: 16px; }
.index-head { display: flex; justify-content: space-between; gap: 12px; align-items: flex-start; }
h3 { margin: 0; font-size: 18px; }
.index-head p { margin: 5px 0 0; color: var(--wc-muted); font-size: 12px; }
.tag {
  flex: 0 0 auto;
  padding: 5px 8px;
  border-radius: 999px;
  font-size: 12px;
  border: 1px solid transparent;
}
.tag.ok { color: #22c55e; background: rgba(34, 197, 94, 0.1); border-color: rgba(34, 197, 94, 0.2); }
.tag.watch { color: #fbbf24; background: rgba(245, 158, 11, 0.1); border-color: rgba(245, 158, 11, 0.2); }
.tag.bad { color: #ef4444; background: rgba(239, 68, 68, 0.1); border-color: rgba(239, 68, 68, 0.2); }
.conclusion { margin: 14px 0; font-size: 15px; line-height: 1.5; }
.scenario-list { display: grid; gap: 8px; }
.scenario {
  display: grid;
  grid-template-columns: 74px 70px 1fr;
  gap: 8px;
  align-items: center;
  padding: 11px 12px;
  border-radius: 16px;
  background: rgba(15, 23, 42, 0.78);
}
.scenario span { color: var(--wc-muted); font-size: 12px; }
.scenario strong { font-size: 17px; }
.scenario em { color: var(--wc-muted); font-size: 12px; font-style: normal; text-align: right; }
.scenario.ok strong { color: #22c55e; }
.scenario.watch strong { color: #fbbf24; }
.scenario.bad strong { color: #ef4444; }
.refresh {
  height: 46px;
  margin-top: 16px;
  color: var(--wc-primary-2);
  border-color: rgba(245,158,11,0.4);
  background: rgba(15,23,42,0.5);
}
</style>
