import type { AxiosStatic } from 'axios'
import { client } from './generated/client.gen'
import instance from './http'

// 让生成的 SDK 复用项目封装的 Axios 实例（satoken、统一错误处理），
// baseURL 使用当前站点源，覆盖实例默认的 '/api'，请求走同源 /api 路径，
// 由 Vite 代理或 Nginx 转发到后端
client.setConfig({
  baseURL: window.location.origin,
  axios: instance as unknown as AxiosStatic,
})

export * from './generated'
