<template>
  <van-nav-bar
    title="便宜组合"
    left-arrow
    @click-left="$router.back()"
  >
    <template #right>
      <span class="date-tag" v-if="portfolio">{{ portfolio.trade_date }}</span>
    </template>
  </van-nav-bar>

  <section v-if="loading" class="state-card">加载中...</section>
  <section v-else-if="error" class="state-card error">{{ error }}</section>

  <template v-else-if="portfolio">
    <section class="hero-card" :class="portfolio.entry_allowed ? 'ok' : 'warn'">
      <p class="label">懒人结论</p>
      <h2>{{ lazyConclusion }}</h2>
      <p class="desc">{{ portfolio.entry_message }}</p>
    </section>

    <section class="summary-grid">
      <div class="summary-item">
        <span>候选股票</span>
        <strong>{{ portfolio.candidate_count }}只</strong>
      </div>
      <div class="summary-item">
        <span>组合股票</span>
        <strong>{{ portfolio.selected_count }}只</strong>
      </div>
      <div class="summary-item">
        <span>单只仓位</span>
        <strong>{{ allocationText }}</strong>
      </div>
    </section>

    <section class="rule-card">
      <p class="rule-title">策略06规则</p>
      <p>PE扣非≤10，PB不含商誉≤1.5，股息率≥3%，PB十年分位&lt;20%。</p>
      <p>按 PE、PB、股息率排名合成综合排名；同行业最多30%；默认10只等权。</p>
    </section>

    <section class="stock-list">
      <article v-for="stock in portfolio.stocks" :key="stock.stock_code" class="stock-card">
        <div class="stock-head">
          <div>
            <h3>{{ stock.stock_name }}</h3>
            <p>{{ stock.stock_code }} · {{ stock.industry_name }}</p>
          </div>
          <div class="rank">#{{ stock.composite_rank }}</div>
        </div>
        <div class="metrics">
          <div><span>PE</span><strong>{{ stock.pe.toFixed(1) }}</strong></div>
          <div><span>PB</span><strong>{{ stock.pb.toFixed(2) }}</strong></div>
          <div><span>股息</span><strong>{{ percent(stock.dividend_yield) }}</strong></div>
          <div><span>PB分位</span><strong>{{ percent(stock.pb_percentile_10y) }}</strong></div>
        </div>
        <div class="stock-foot">
          <span>价格 {{ stock.price.toFixed(2) }}</span>
          <span>建议仓位 {{ percent(stock.allocation_ratio) }}</span>
        </div>
      </article>
    </section>
  </template>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { fetchCheapPortfolio } from '../services/api'

const loading = ref(true)
const error = ref('')
const portfolio = ref(null)

const lazyConclusion = computed(() => {
  if (!portfolio.value) return ''
  if (!portfolio.value.entry_allowed) {
    return `筛出${portfolio.value.selected_count}只便宜股，但现在先别买`
  }
  return `可以买，按${portfolio.value.selected_count}只等权配置`
})

const allocationText = computed(() => {
  const first = portfolio.value?.stocks?.[0]
  return first ? percent(first.allocation_ratio) : '-'
})

function percent(value) {
  return `${(Number(value || 0) * 100).toFixed(1)}%`
}

async function loadPortfolio() {
  loading.value = true
  error.value = ''
  try {
    portfolio.value = await fetchCheapPortfolio()
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(loadPortfolio)
</script>

<style scoped>
.back-btn { display: inline-block; margin-right: 12px; color: var(--wc-primary-2, #f59e0b); font-size: 14px; text-decoration: none; cursor: pointer; user-select: none; }
.topbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}
.eyebrow {
  margin: 0 0 4px;
  font-size: 12px;
  letter-spacing: 0.18em;
  color: var(--wc-muted);
}
h1 {
  margin: 0;
  font-size: 28px;
  line-height: 1.1;
}
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
.stock-card,
.summary-item {
  border: 1px solid rgba(148, 163, 184, 0.14);
  background: rgba(15, 23, 42, 0.72);
  box-shadow: var(--wc-card-shadow);
  backdrop-filter: blur(18px);
}
.state-card {
  padding: 22px;
  border-radius: 24px;
  color: var(--wc-muted);
}
.state-card.error {
  color: #fca5a5;
}
.hero-card {
  padding: 22px;
  border-radius: 28px;
  margin-bottom: 14px;
}
.hero-card.warn {
  border-color: rgba(245, 158, 11, 0.35);
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.16), rgba(15, 23, 42, 0.76));
}
.hero-card.ok {
  border-color: rgba(34, 197, 94, 0.35);
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.16), rgba(15, 23, 42, 0.76));
}
.label,
.desc {
  margin: 0;
  color: var(--wc-muted);
}
.hero-card h2 {
  margin: 8px 0 8px;
  font-size: 24px;
  line-height: 1.25;
}
.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 14px;
}
.summary-item {
  border-radius: 18px;
  padding: 12px 10px;
}
.summary-item span,
.metrics span,
.stock-foot,
.rule-card {
  color: var(--wc-muted);
}
.summary-item strong {
  display: block;
  margin-top: 4px;
  font-size: 18px;
}
.rule-card {
  border-radius: 20px;
  padding: 14px;
  margin-bottom: 14px;
  line-height: 1.55;
  font-size: 13px;
}
.rule-card p {
  margin: 4px 0;
}
.rule-title {
  color: #fbbf24 !important;
  font-weight: 700;
}
.stock-list {
  display: grid;
  gap: 12px;
  padding-bottom: 8px;
}
.stock-card {
  border-radius: 22px;
  padding: 16px;
}
.stock-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}
.stock-head h3 {
  margin: 0 0 4px;
  font-size: 18px;
}
.stock-head p {
  margin: 0;
  color: var(--wc-muted);
  font-size: 12px;
}
.rank {
  color: #fbbf24;
  font-weight: 800;
}
.metrics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-top: 14px;
}
.metrics div {
  border-radius: 14px;
  padding: 9px 8px;
  background: rgba(255, 255, 255, 0.045);
}
.metrics span,
.metrics strong {
  display: block;
}
.metrics strong {
  margin-top: 3px;
  font-size: 15px;
}
.stock-foot {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  font-size: 12px;
}
</style>
