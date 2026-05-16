<template>
  <main class="page-shell">
    <section class="safe-page layout-shell">
      <router-view />
    </section>

    <van-tabbar v-model="active" route :fixed="true" :border="false" safe-area-inset-bottom>
      <van-tabbar-item replace to="/dashboard" icon="home-o" name="home">
        首页
      </van-tabbar-item>
      <van-tabbar-item replace to="/strategies" icon="apps-o" name="strategies">
        策略
      </van-tabbar-item>
      <van-tabbar-item replace to="/mine" icon="manager-o" name="mine">
        我的
      </van-tabbar-item>
    </van-tabbar>
  </main>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Tabbar, TabbarItem } from 'vant'

const route = useRoute()
const active = ref('home')

watch(() => route.path, (path) => {
  if (path.startsWith('/strategies') || path.startsWith('/market-entry') || path.startsWith('/cheap-portfolio')) {
    active.value = 'strategies'
  } else if (path.startsWith('/mine')) {
    active.value = 'mine'
  } else {
    active.value = 'home'
  }
}, { immediate: true })
</script>

<style scoped>
.page-shell {
  min-height: 100dvh;
  background: #0a0f1f;
  color: #f1f5f9;
}

.safe-page {
  padding: 18px 16px 120px;
}

:deep(.van-tabbar) {
  left: 50% !important;
  width: min(100%, 480px) !important;
  transform: translateX(-50%);
  background: rgba(15, 23, 42, 0.94) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(148, 163, 184, 0.14) !important;
  border-bottom: 0 !important;
  border-radius: 24px 24px 0 0;
  height: 64px !important;
  box-shadow: 0 -16px 50px rgba(0, 0, 0, 0.38);
}

:deep(.van-tabbar-item) {
  color: #64748b !important;
  font-size: 12px;
}

:deep(.van-tabbar-item--active) {
  color: #f59e0b !important;
  background: transparent !important;
}

:deep(.van-tabbar-item__icon) {
  font-size: 22px;
  margin-bottom: 2px;
}
</style>
