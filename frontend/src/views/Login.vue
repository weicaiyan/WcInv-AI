<template>
  <main class="page-shell login-page">
    <section class="safe-page login-wrap">
      <div class="brand">
        <div class="brand-mark" aria-hidden="true">W</div>
        <p class="eyebrow">WcInv</p>
        <h1>懒人投资仪表盘</h1>
        <p class="muted">登录后只看一句话：现在该不该投。</p>
      </div>

      <van-form class="glass-card login-card" @submit="handleSubmit">
        <van-cell-group inset>
          <van-field
            v-model="form.username"
            name="username"
            label="账号"
            placeholder="请输入账号"
            autocomplete="username"
            :rules="[{ required: true, message: '账号不能为空' }]"
          />
          <van-field
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            name="password"
            label="密码"
            placeholder="请输入密码"
            autocomplete="current-password"
            :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
            :rules="[{ required: true, message: '密码不能为空' }]"
            @click-right-icon="showPassword = !showPassword"
          />
        </van-cell-group>

        <van-button
          round
          block
          native-type="submit"
          color="linear-gradient(135deg, #fbbf24, #f59e0b)"
          :loading="loading"
          loading-text="登录中..."
        >
          进入仪表盘
        </van-button>

        <p class="hint">测试账号：admin / 123456</p>
      </van-form>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, Form as VanForm, Field as VanField, CellGroup as VanCellGroup, Button as VanButton } from 'vant'
import { login } from '../services/api'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const showPassword = ref(false)
const form = reactive({
  username: 'admin',
  password: '123456'
})

async function handleSubmit() {
  loading.value = true
  try {
    await login(form.username.trim(), form.password)
    showToast('登录成功')
    await router.replace(route.query.redirect || '/dashboard')
  } catch (error) {
    showToast(error.message || '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
}

.login-wrap {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 28px;
}

.brand {
  padding: 18px 4px 0;
}

.brand-mark {
  width: 56px;
  height: 56px;
  margin-bottom: 20px;
  border-radius: 18px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: #111827;
  font-size: 28px;
  font-weight: 800;
  box-shadow: 0 18px 40px rgba(245, 158, 11, 0.28);
}

.eyebrow {
  color: var(--wc-primary-2);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

h1 {
  margin-top: 8px;
  font-size: 34px;
  line-height: 1.08;
  letter-spacing: -0.04em;
}

.brand .muted {
  margin-top: 12px;
  font-size: 15px;
  line-height: 1.6;
}

.login-card {
  padding: 18px 14px 16px;
}

:deep(.van-cell-group--inset) {
  margin: 0;
  overflow: hidden;
  background: rgba(15, 23, 42, 0.72);
  border: 1px solid var(--wc-border);
}

:deep(.van-cell) {
  min-height: 58px;
  background: transparent;
  color: var(--wc-text);
}

:deep(.van-field__label),
:deep(.van-field__control),
:deep(.van-field__right-icon) {
  color: var(--wc-text);
}

:deep(.van-field__control::placeholder) {
  color: #64748b;
}

.van-button {
  height: 50px;
  margin-top: 18px;
  color: #111827 !important;
  font-weight: 800;
}

.hint {
  margin-top: 14px;
  color: var(--wc-muted);
  font-size: 13px;
  text-align: center;
}
</style>
