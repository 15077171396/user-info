import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { LoginRequest, UserInfo } from '../api'
import { getCurrentUser, login as loginRequest, logout as logoutRequest } from '../api/auth'
import { TOKEN_KEY } from '../api/http'

export const useUserStore = defineStore(
  'user',
  () => {
    const token = ref(localStorage.getItem(TOKEN_KEY) ?? '')
    const user = ref<UserInfo | null>(null)

    const nickname = computed(() => user.value?.nickname ?? '未登录')

    function setToken(value: string) {
      token.value = value
      localStorage.setItem(TOKEN_KEY, value)
    }

    function setUser(value: UserInfo) {
      user.value = value
    }

    function clear() {
      token.value = ''
      user.value = null
      localStorage.removeItem(TOKEN_KEY)
    }

    /** 邮箱密码登录，成功后保存 token 与用户信息 */
    async function login(payload: LoginRequest) {
      const data = await loginRequest(payload)
      if (!data.tokenValue) {
        throw new Error('登录响应异常，请稍后重试')
      }
      setToken(data.tokenValue)
      if (data.user) {
        setUser(data.user)
      }
      return data
    }

    /** 从后端刷新当前登录用户信息 */
    async function fetchCurrentUser() {
      if (!token.value) return null
      const info = await getCurrentUser()
      user.value = info
      return info
    }

    /** 退出登录：无论接口是否成功，都清理本地登录态 */
    async function logout() {
      try {
        await logoutRequest()
      } catch {
        // token 已失效时忽略接口错误
      } finally {
        clear()
      }
    }

    return {
      token,
      user,
      nickname,
      setToken,
      setUser,
      clear,
      login,
      fetchCurrentUser,
      logout,
    }
  },
  {
    persist: {
      key: 'user-info-store',
      pick: ['token', 'user'],
    },
  },
)
