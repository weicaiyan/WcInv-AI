<template>
  <header class="topbar">
    <div>
      <p class="eyebrow">策略中心</p>
      <h1>所有策略卡片</h1>
    </div>
  </header>

  <section class="glass-card hero-card">
    <p class="hero-label">懒人用法</p>
    <h2>先看首页结论，再点具体策略。</h2>
    <p class="muted">底部不塞满入口，所有策略统一放这里。</p>
  </section>

  <section class="strategy-list" aria-label="策略列表">
    <router-link
      v-for="item in strategies"
      :key="item.path"
      class="glass-card strategy-card"
      :to="item.path"
    >
      <div class="icon" :class="item.tone">{{ item.icon }}</div>
      <div class="content">
        <div class="title-row">
          <h3>{{ item.title }}</h3>
          <span class="status" :class="item.statusClass">{{ item.status }}</span>
        </div>
        <p>{{ item.desc }}</p>
      </div>
      <van-icon name="arrow" class="arrow" />
    </router-link>
  </section>
</template>

<script setup>
import { Icon as VanIcon } from 'vant'

const strategies = [
  {
    title: '博格公式',
    desc: '估算消费、医药行业指数的预期年化收益率。',
    status: '已接入',
    statusClass: 'ok',
    icon: '🧮',
    tone: 'purple',
    path: '/bogle-formula'
  },
  {
    title: '网格策略',
    desc: '低估指数的中短期波段计算器，自动生成买卖档位。',
    status: '已接入',
    statusClass: 'ok',
    icon: '🕸️',
    tone: 'cyan',
    path: '/grid-strategy'
  },
  {
    title: '风险排雷',
    desc: '买股票前先做风险排查：ST/丑闻/经营恶化/不熟不做。',
    status: '已接入',
    statusClass: 'ok',
    icon: '⚠️',
    tone: 'red',
    path: '/company-risk-filter'
  },
  {
    title: '市场入场',
    desc: '判断现在能不能启动股票/指数策略。',
    status: '已接入',
    statusClass: 'ok',
    icon: '🚦',
    tone: 'amber',
    path: '/market-entry'
  },
  {
    title: '指数温度定投',
    desc: '看沪深300、中证500、上证50、创业板50冷热。',
    status: '首页展示',
    statusClass: 'ok',
    icon: '🌡️',
    tone: 'green',
    path: '/dashboard'
  },
  {
    title: '便宜组合',
    desc: '从A股里筛便宜、高股息、低PB分位股票。',
    status: '已接入',
    statusClass: 'ok',
    icon: '🧺',
    tone: 'blue',
    path: '/cheap-portfolio'
  },
  {
    title: '历史温度走势',
    desc: '看市场温度是升温还是降温。',
    status: '待开发',
    statusClass: 'todo',
    icon: '📈',
    tone: 'gray',
    path: '/strategies'
  },
  {
    title: '持仓再平衡',
    desc: '未来根据你的持仓给调仓建议。',
    status: '待开发',
    statusClass: 'todo',
    icon: '⚖️',
    tone: 'gray',
    path: '/strategies'
  }
]
</script>

<style scoped>
.topbar { margin-bottom: 18px; }
.eyebrow { color: var(--wc-primary-2); font-size: 12px; font-weight: 800; letter-spacing: 0.14em; }
h1 { margin-top: 4px; font-size: 30px; line-height: 1.1; letter-spacing: -0.04em; }

.hero-card { padding: 20px; margin-bottom: 16px; }
.hero-label { color: var(--wc-primary-2); font-size: 13px; font-weight: 800; }
.hero-card h2 { margin-top: 8px; font-size: 22px; line-height: 1.3; letter-spacing: -0.03em; }
.hero-card .muted { margin-top: 10px; font-size: 13px; }

.strategy-list { display: grid; gap: 12px; }
.strategy-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  color: inherit;
  text-decoration: none;
}
.icon {
  width: 46px;
  height: 46px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  font-size: 22px;
}
.icon.amber { background: rgba(245, 158, 11, 0.16); }
.icon.green { background: rgba(34, 197, 94, 0.14); }
.icon.blue { background: rgba(59, 130, 246, 0.14); }
.icon.purple { background: rgba(168, 85, 247, 0.14); }
.icon.cyan { background: rgba(6, 182, 212, 0.14); }
.icon.red { background: rgba(239, 68, 68, 0.14); }
.icon.gray { background: rgba(100, 116, 139, 0.16); }
.content { min-width: 0; flex: 1; }
.title-row { display: flex; align-items: center; gap: 8px; justify-content: space-between; }
h3 { font-size: 17px; line-height: 1.25; }
.content p { margin-top: 6px; color: var(--wc-muted); font-size: 13px; line-height: 1.45; }
.status {
  flex: 0 0 auto;
  padding: 4px 7px;
  border-radius: 999px;
  font-size: 11px;
  border: 1px solid transparent;
}
.status.ok { color: #22c55e; background: rgba(34, 197, 94, 0.1); border-color: rgba(34, 197, 94, 0.18); }
.status.todo { color: #94a3b8; background: rgba(148, 163, 184, 0.1); border-color: rgba(148, 163, 184, 0.14); }
.arrow { color: var(--wc-muted); flex: 0 0 auto; }
</style>
