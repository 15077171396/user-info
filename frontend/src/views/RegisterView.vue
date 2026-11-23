<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import AuthLayout from '../layouts/AuthLayout.vue'
import { register, sendRegisterCode } from '../api/auth'
import type { RegisterRequest } from '../api'

interface RegisterForm extends RegisterRequest {
  confirmPassword: string
}

const router = useRouter()

const formRef = ref<FormInstance>()
const form = reactive<RegisterForm>({
  email: '',
  code: '',
  password: '',
  confirmPassword: '',
})
const loading = ref(false)
const sending = ref(false)
const countdown = ref(0)
let timer: ReturnType<typeof setInterval> | undefined

const rules: FormRules<RegisterForm> = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为 6 位数字', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      pattern: /^(?=.*[A-Za-z])(?=.*\d)\S{8,16}$/,
      message: '密码需 8-16 位且同时包含字母和数字',
      trigger: 'blur',
    },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule, value: string, callback: (error?: string | Error) => void) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
}

function startCountdown() {
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0 && timer) {
      clearInterval(timer)
      timer = undefined
    }
  }, 1000)
}

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

async function handleSendCode() {
  if (!formRef.value || sending.value || countdown.value > 0) return

  const valid = await formRef.value
    .validateField('email')
    .then(() => true)
    .catch(() => false)
  if (!valid) return

  sending.value = true
  try {
    await sendRegisterCode({ email: form.email })
    ElMessage.success('验证码已发送，请查收邮箱')
    startCountdown()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '验证码发送失败，请稍后重试')
  } finally {
    sending.value = false
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await register({
      email: form.email,
      code: form.code,
      password: form.password,
    })
    ElMessage.success('注册成功，请登录')
    await router.push('/login')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <AuthLayout>
    <h2 class="text-3xl font-bold tracking-tight text-white">创建一个账号</h2>
    <p class="mt-3 text-sm text-gray-400">使用邮箱注册，验证码将发送至您的邮箱</p>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      class="mt-9"
      label-position="top"
      @submit.prevent="handleSubmit"
    >
      <el-form-item prop="email">
        <el-input
          v-model="form.email"
          size="large"
          placeholder="邮箱"
          aria-label="邮箱"
          autocomplete="email"
        />
      </el-form-item>
      <el-form-item prop="code">
        <div class="flex w-full gap-3">
          <el-input
            v-model="form.code"
            size="large"
            placeholder="验证码"
            aria-label="验证码"
            maxlength="6"
            class="min-w-0 flex-1"
          />
          <el-button
            size="large"
            class="w-[128px] shrink-0"
            :disabled="countdown > 0"
            :loading="sending"
            @click="handleSendCode"
          >
            {{ countdown > 0 ? `${countdown}s 后重发` : '获取验证码' }}
          </el-button>
        </div>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="form.password"
          type="password"
          size="large"
          placeholder="密码"
          aria-label="密码"
          show-password
          autocomplete="new-password"
        />
      </el-form-item>
      <p class="-mt-2 mb-4 text-xs text-gray-500">8-16 位，需同时包含字母和数字</p>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="form.confirmPassword"
          type="password"
          size="large"
          placeholder="确认密码"
          aria-label="确认密码"
          show-password
          autocomplete="new-password"
        />
      </el-form-item>
      <el-button
        type="primary"
        size="large"
        class="mt-2 w-full"
        native-type="submit"
        :loading="loading"
      >
        注册
      </el-button>
    </el-form>

    <p class="mt-8 text-center text-sm text-gray-400">
      已经有账号了？
      <RouterLink to="/login" class="text-[#00b96b] transition hover:text-[#00a35e]">
        去登录
      </RouterLink>
    </p>
  </AuthLayout>
</template>
