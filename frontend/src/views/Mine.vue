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

  <section class="info-list">
    <div class="glass-card info-card">
      <span>数据源</span>
      <strong>理杏仁开放 API</strong>
      <p>指数估值和便宜组合数据已接入。</p>
    </div>
    <div class="glass-card info-card">
      <span>更新频率</span>
      <strong>每月 1 日 / 16 日</strong>
      <p>匹配理杏仁分位点更新节奏。</p>
    </div>
    <div class="glass-card info-card">
      <span>账号</span>
      <strong>admin</strong>
      <p>本地测试账号。</p>
    </div>
  </section>
</template>

<style scoped>
.topbar { margin-bottom: 18px; }
.eyebrow { color: var(--wc-primary-2); font-size: 12px; font-weight: 800; letter-spacing: 0.14em; }
h1 { margin-top: 4px; font-size: 30px; line-height: 1.1; letter-spacing: -0.04em; }

.hero-card { padding: 20px; margin-bottom: 16px; }
.hero-label { color: var(--wc-primary-2); font-size: 13px; font-weight: 800; }
.hero-card h2 { margin-top: 8px; font-size: 22px; line-height: 1.3; }
.hero-card .muted { margin-top: 10px; font-size: 13px; }

.info-list { display: grid; gap: 12px; }
.info-card { padding: 17px; }
.info-card span { color: var(--wc-muted); font-size: 12px; }
.info-card strong { display: block; margin-top: 6px; font-size: 18px; }
.info-card p { margin-top: 6px; color: var(--wc-muted); font-size: 13px; line-height: 1.45; }
</style>
