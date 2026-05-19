<template>
  <header class="topbar">
    <div>
      <p class="eyebrow">我的</p>
      <h1>数据和设置</h1>
    </div>
  </header>

  <section class="glass-card hero-card">
    <p class="hero-label">当前版本</p>
    <h2>本地投资工具 MVP</h2>
    <p class="muted">先把策略跑通，持仓和提醒后面再接。</p>
  </section>

  <section class="glass-card refresh-card">
    <div class="refresh-head">
      <div>
        <span>全局数据刷新</span>
        <strong>刷新所有已接入数据源</strong>
      </div>
      <van-icon name="replay" class="refresh-icon" />
    </div>
    <p>包含宽基指数估值、博格公式行业指数、便宜组合股票池。点击后后台执行，不影响你切页面浏览。</p>
    <van-button
      block
      round
      color="#f59e0b"
      :loading="refreshing"
      loading-text="刷新中..."
      @click="handleRefreshAll"
    >
      一键刷新全部数据
    </van-button>
    <div v-if="lastResult" class="refresh-result" :class="lastResult.status.toLowerCase()">
      <strong>{{ lastResult.message }}</strong>
      <p>{{ resultSummary }}</p>
    </div>
  </section>

  <section class="info-list">
    <div class="glass-card info-card">
      <span>数据源</span>
      <strong>理杏仁开放 API</strong>
      <p>指数估值、博格公式和便宜组合数据已接入。</p>
    </div>
    <div class="glass-card info-card">
      <span>更新频率</span>
      <strong>每月 1 日 / 16 日</strong>
      <p>匹配理杏仁分位点更新节奏，也可以在这里手动刷新。</p>
    </div>
    <div class="glass-card info-card">
      <span>账号</span>
      <strong>admin</strong>
      <p>本地测试账号。</p>
    </div>
  </section>

  <LixingerCredModal
    :visible="showCredModal"
    @close="showCredModal = false"
    @done="onCredDone"
  />
</template>

<script setup>
import { computed, ref } from 'vue'
import { Button as VanButton, Icon as VanIcon, showToast } from 'vant'
import { refreshAllData } from '../services/api'
import LixingerCredModal from './LixingerCredModal.vue'

const refreshing = ref(false)
const showCredModal = ref(false)
const lastResult = ref(null)

const resultSummary = computed(() => {
  if (!lastResult.value?.items?.length) return ''
  const success = lastResult.value.items.filter(item => item.status === 'SUCCESS').length
  const failed = lastResult.value.items.filter(item => item.status === 'FAILED').length
  if (failed === 0) return `${success} 个数据源已更新到最新增量数据`
  return `${success} 个成功，${failed} 个失败`
})

async function handleRefreshAll() {
  if (refreshing.value) return
  refreshing.value = true
  lastResult.value = null
  showToast('已开始后台刷新，你可以继续浏览')
  try {
    lastResult.value = await refreshAllData()
    if (lastResult.value.status === 'SUCCESS') {
      showToast('全局数据刷新完成')
    } else {
      showToast('部分数据刷新失败')
    }
  } catch (err) {
    if (err.body?.error?.code === 'LIXINGER_TOKEN_EXPIRED') {
      showCredModal.value = true
      showToast('理杏仁登录过期，请重新登录')
    } else {
      showToast(err.message || '全局刷新失败')
    }
  } finally {
    refreshing.value = false
  }
}

async function onCredDone() {
  showCredModal.value = false
  await handleRefreshAll()
}
</script>

<style scoped>
.topbar { margin-bottom: 18px; }
.eyebrow { color: var(--wc-primary-2); font-size: 12px; font-weight: 800; letter-spacing: 0.14em; }
h1 { margin-top: 4px; font-size: 30px; line-height: 1.1; letter-spacing: -0.04em; }

.hero-card { padding: 20px; margin-bottom: 16px; }
.hero-label { color: var(--wc-primary-2); font-size: 13px; font-weight: 800; }
.hero-card h2 { margin-top: 8px; font-size: 22px; line-height: 1.3; }
.hero-card .muted { margin-top: 10px; font-size: 13px; }

.refresh-card { padding: 18px; margin-bottom: 16px; }
.refresh-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; }
.refresh-head span { color: var(--wc-muted); font-size: 12px; }
.refresh-head strong { display: block; margin-top: 6px; font-size: 18px; }
.refresh-icon { color: var(--wc-primary); font-size: 24px; }
.refresh-card p { margin: 10px 0 14px; color: var(--wc-muted); font-size: 13px; line-height: 1.45; }
.refresh-result { margin-top: 12px; padding: 12px; border-radius: 16px; background: rgba(15,23,42,0.62); border: 1px solid var(--wc-border); }
.refresh-result strong { font-size: 14px; }
.refresh-result p { margin: 4px 0 0; }
.refresh-result.success { border-color: rgba(34,197,94,0.25); }
.refresh-result.partial_success, .refresh-result.failed { border-color: rgba(245,158,11,0.32); }

.info-list { display: grid; gap: 12px; padding-bottom: 72px; }
.info-card { padding: 17px; }
.info-card span { color: var(--wc-muted); font-size: 12px; }
.info-card strong { display: block; margin-top: 6px; font-size: 18px; }
.info-card p { margin-top: 6px; color: var(--wc-muted); font-size: 13px; line-height: 1.45; }
</style>
