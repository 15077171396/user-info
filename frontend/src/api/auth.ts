import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
  SendCodeRequest,
  UserInfo,
} from './index'
import {
  login as loginRequest,
  logout as logoutRequest,
  me as meRequest,
  register as registerRequest,
  sendRegisterCode as sendCodeRequest,
} from './index'

/** 邮箱密码登录，成功返回 token 与用户信息 */
export async function login(payload: LoginRequest): Promise<LoginResponse> {
  const { data } = await loginRequest({ body: payload, throwOnError: true })
  if (!data.data) {
    throw new Error(data.message ?? '登录失败')
  }
  return data.data
}

/** 邮箱注册，成功返回注册用户信息 */
export async function register(payload: RegisterRequest): Promise<RegisterResponse> {
  const { data } = await registerRequest({ body: payload, throwOnError: true })
  if (!data.data) {
    throw new Error(data.message ?? '注册失败')
  }
  return data.data
}

/** 发送注册验证码 */
export async function sendRegisterCode(payload: SendCodeRequest): Promise<void> {
  await sendCodeRequest({ body: payload, throwOnError: true })
}

/** 退出登录，使当前 token 失效 */
export async function logout(): Promise<void> {
  await logoutRequest({ throwOnError: true })
}

/** 获取当前登录用户信息 */
export async function getCurrentUser(): Promise<UserInfo> {
  const { data } = await meRequest({ throwOnError: true })
  if (!data.data) {
    throw new Error(data.message ?? '获取当前用户信息失败')
  }
  return data.data
}
