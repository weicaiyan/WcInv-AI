<template>
  <van-nav-bar
    title="风险排雷"
    left-arrow
    @click-left="$router.back()"
  />

  <section class="glass-card hero-card">
    <p class="hero-label">懒人结论</p>
    <h2>{{ result?.lazy_conclusion || '先做排雷，再看估值。' }}</h2>
    <p v-if="result" class="muted">
      <span class="level-badge" :class="levelClass">{{ levelName }}</span>
      人工复核{{ result.manual_review_required ? '必需' : '不需要' }}
    </p>
  </section>

  <section class="glass-card form-card">
    <div class="field-grid">
      <label>
        <span>股票代码</span>
        <input v-model="form.code" placeholder="如 000423" />
      </label>
      <label>
        <span>股票名称</span>
        <input v-model="form.name" placeholder="如 东阿阿胶" />
      </label>
    </div>
    <div class="checks">
      <label class="check-item"><input type="checkbox" v-model="form.st" /> ST</label>
      <label class="check-item"><input type="checkbox" v-model="form.starSt" /> *ST</label>
      <label class="check-item"><input type="checkbox" v-model="form.nst" /> NST</label>
      <label class="check-item"><input type="checkbox" v-model="form.scandalFound" /> 重大丑闻</label>
      <label class="check-item"><input type="checkbox" v-model="form.csrcInvestigationFound" /> 证监会调查</label>
      <label class="check-item"><input type="checkbox" v-model="form.unresolvedRecentIssue" /> 近两年未落地</label>
      <label class="check-item"><input type="checkbox" v-model="form.businessDeteriorating" /> 经营恶化</label>
      <label class="check-item"><input type="checkbox" v-model="form.userUnderstandsBusiness" /> 了解业务</label>
    </div>
    <van-button block round color="#f59e0b" :loading="loading" @click="submit">开始排雷</van-button>
    <p v-if="error" class="error">{{ error }}</p>
  </section>

  <section v-if="result" class="glass-card tags-card">
    <h2>风险标签</h2>
    <div class="tags">
      <span v-for="tag in result.risk_tags" :key="tag" class="tag">{{ tag }}</span>
      <span v-if="result.risk_tags.length === 0" class="muted">未发现明显标签</span>
    </div>
    <p v-if="result.reject_reason" class="reject-reason">
      ⛔ {{ result.reject_reason }}
    </p>
  </section>

  <section class="glass-card note-card">
    <h2>排雷规则</h2>
    <p>1. ST / *ST / NST → 直接排除。</p>
    <p>2. 近两年证监会调查+未落地 → 排除。</p>
    <p>3. 有丑闻/调查/经营恶化 → 高风险，必须人工复核。</p>
    <p>4. 不了解业务 → 先观察。</p>
    <p>5. 即便是低风险，也不是买入结论。</p>
  </section>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { Button as VanButton } from 'vant'
import { evaluateCompanyRisk } from '../services/api'

const loading = ref(false)
const error = ref('')
const result = ref(null)

const form = reactive({
  code: '',
  name: '',
  st: false,
  starSt: false,
  nst: false,
  scandalFound: false,
  csrcInvestigationFound: false,
  unresolvedRecentIssue: false,
  businessDeteriorating: false,
  userUnderstandsBusiness: true
})

const levelName = computed(() => {
  const map = { LOW: '低风险', MEDIUM: '中风险', HIGH: '高风险', REJECT: '排除' }
  return map[result.value?.risk_level] || ''
})

const levelClass = computed(() => (result.value?.risk_level || '').toLowerCase())

function submit() {
  if (!form.code && !form.name) { error.value = '至少填代码或名称。'; return }
  loading.value = true
  error.value = ''
  evaluateCompanyRisk({
    code: form.code,
    name: form.name,
    st: form.st,
    star_st: form.starSt,
    nst: form.nst,
    scandal_found: form.scandalFound,
    csrc_investigation_found: form.csrcInvestigationFound,
    unresolved_recent_issue: form.unresolvedRecentIssue,
    business_deteriorating: form.businessDeteriorating,
    user_understands_business: form.userUnderstandsBusiness
  }).then(res => {
    result.value = res
    loading.value = false
  }).catch(err => {
    error.value = err.message || '请求失败'
    loading.value = false
  })
}
</script>

<style scoped>
.hero-card, .form-card, .tags-card, .note-card { padding: 18px; margin-bottom: 14px; }
.hero-label { color: var(--wc-primary-2); font-size: 13px; font-weight: 800; }
.hero-card h2 { margin-top: 8px; font-size: 21px; line-height: 1.35; letter-spacing: -0.03em; }
.hero-card .muted { margin-top: 10px; font-size: 13px; }
.level-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  margin-right: 6px;
}
.level-badge.low { background: rgba(34,197,94,0.14); color: #22c55e; }
.level-badge.medium { background: rgba(234,179,8,0.14); color: #eab308; }
.level-badge.high { background: rgba(249,115,22,0.14); color: #f97316; }
.level-badge.reject { background: rgba(239,68,68,0.14); color: #ef4444; }
.field-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 14px; }
label span { display: block; margin-bottom: 7px; color: var(--wc-muted); font-size: 12px; }
input[type="text"] {
  width: 100%;
  height: 44px;
  border: 1px solid var(--wc-border);
  border-radius: 14px;
  background: rgba(15,23,42,0.74);
  color: var(--wc-text);
  padding: 0 12px;
  outline: none;
}
.checks { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 16px; }
.check-item { display: flex; align-items: center; gap: 7px; color: var(--wc-muted); font-size: 13px; min-height: 44px; padding: 4px 0; cursor: pointer; }
.check-item input { accent-color: var(--wc-primary-2); width: 20px; height: 20px; flex: 0 0 auto; }
.error { margin-top: 10px; color: #f87171; font-size: 13px; }
.tags-card h2, .note-card h2 { font-size: 18px; margin-bottom: 10px; }
.tags { display: flex; flex-wrap: wrap; gap: 8px; }
.tag { padding: 4px 10px; border-radius: 999px; background: rgba(100,116,139,0.14); color: var(--wc-muted); font-size: 12px; }
.reject-reason { margin-top: 12px; color: #ef4444; font-size: 14px; font-weight: 600; }
.note-card p { margin-top: 8px; color: var(--wc-muted); font-size: 13px; line-height: 1.55; }
</style>
