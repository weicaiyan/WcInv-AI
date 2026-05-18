<template>
  <van-overlay :show="visible" @click="emit('close')">
    <div class="modal-wrapper" @click.stop>
      <div class="modal-card">
        <h2>理杏仁凭证过期</h2>
        <p class="desc">请输入理杏仁账号和密码，系统将自动登录获取新 Token。</p>

        <van-field
          v-model="account"
          label="账号"
          placeholder="手机号"
          :disabled="submitting"
        />
        <van-field
          v-model="password"
          label="密码"
          type="password"
          placeholder="输入密码"
          :disabled="submitting"
        />

        <div class="actions">
          <van-button round block color="#f59e0b" :loading="submitting" @click="handleSubmit">
            登录并刷新
          </van-button>
        </div>

        <p v-if="error" class="error-msg">{{ error }}</p>
      </div>
    </div>
  </van-overlay>
</template>

<script setup>
import { ref } from 'vue'
import { Field as VanField, Button as VanButton, Overlay as VanOverlay } from 'vant'
import { loginLixinger } from '../services/api'

const props = defineProps({
  visible: Boolean
})

const emit = defineEmits(['close', 'done'])

const account = ref('')
const password = ref('')
const submitting = ref(false)
const error = ref('')

async function handleSubmit() {
  if (!account.value.trim() || !password.value.trim()) {
    error.value = '请填写账号和密码'
    return
  }

  submitting.value = true
  error.value = ''

  try {
    await loginLixinger(account.value.trim(), password.value)
    emit('done')
    account.value = ''
    password.value = ''
  } catch (err) {
    error.value = err.message || '登录失败'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.modal-wrapper {
  display: flex; align-items: center; justify-content: center;
  min-height: 100vh; padding: 24px;
}
.modal-card {
  width: 100%; max-width: 340px;
  padding: 24px 20px 20px;
  border-radius: 20px;
  background: #161b22;
  border: 1px solid #30363d;
}
.modal-card h2 {
  font-size: 18px; color: #f85149; margin-bottom: 8px;
}
.desc {
  font-size: 13px; color: #8b949e; margin-bottom: 16px; line-height: 1.5;
}
.actions {
  margin-top: 18px;
}
.error-msg {
  margin-top: 12px; font-size: 13px; color: #f85149; text-align: center;
}
</style>
