<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import AuthLayout from '../layouts/AuthLayout.vue'
import type { LoginRequest } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const form = reactive<LoginRequest>({
  email: '',
  password: '',
})
const loading = ref(false)

const rules: FormRules<LoginRequest> = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    await router.push('/')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <AuthLayout>
    <h2 class="text-3xl font-bold tracking-tight text-white">欢迎回来</h2>
    <p class="mt-3 text-sm text-gray-400">请输入您的邮箱和密码以登录账号</p>

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
          autocomplete="username"
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="form.password"
          type="password"
          size="large"
          placeholder="密码"
          aria-label="密码"
          show-password
          autocomplete="current-password"
        />
      </el-form-item>
      <el-button
        type="primary"
        size="large"
        class="mt-2 w-full"
        native-type="submit"
        :loading="loading"
      >
        登录
      </el-button>
    </el-form>

    <p class="mt-8 text-center text-sm text-gray-400">
      还没有账号？
      <RouterLink to="/register" class="text-[#00b96b] transition hover:text-[#00a35e]">
        创建账号
      </RouterLink>
    </p>
  </AuthLayout>
</template>
