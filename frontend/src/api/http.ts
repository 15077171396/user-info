import axios, {
  type AxiosError,
  type AxiosInstance,
  type AxiosRequestConfig,
  type AxiosResponse,
} from 'axios'

/** 后端统一响应结构 */
export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

const SUCCESS_CODE = 200
/** 与后端 sa-token.token-name 保持一致 */
export const TOKEN_KEY = 'satoken'

const instance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.set(TOKEN_KEY, token)
  }
  return config
})

instance.interceptors.response.use(
  (response: AxiosResponse<ApiResult<unknown>>) => {
    if (response.data.code !== SUCCESS_CODE) {
      return Promise.reject(new Error(response.data.message))
    }
    return response
  },
  (error: AxiosError<ApiResult<unknown>>) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
    }
    const message = error.response?.data?.message ?? error.message
    return Promise.reject(new Error(message))
  },
)

async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const response = await instance.request<ApiResult<T>>(config)
  return response.data.data
}

/** 项目统一的请求入口，返回值已剥离 Result 外壳 */
export const http = {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request<T>({ ...config, url, method: 'GET' })
  },
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return request<T>({ ...config, url, method: 'POST', data })
  },
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return request<T>({ ...config, url, method: 'PUT', data })
  },
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request<T>({ ...config, url, method: 'DELETE' })
  },
}

export default instance
