<script setup lang="ts">
import type { Component } from 'vue'
import {
  ArrowRight,
  Bell,
  Clock,
  Document,
  InfoFilled,
  Key,
  User,
  View,
} from '@element-plus/icons-vue'

interface StatCard {
  label: string
  value: string
  trend: string
  trendClass: string
  icon: Component
  iconClass: string
}

interface QuickLink {
  title: string
  desc: string
  path: string
  icon: Component
  iconClass: string
}

interface ActivityItem {
  title: string
  time: string
  dotClass: string
}

const router = useRouter()

const stats: StatCard[] = [
  {
    label: '用户总数',
    value: '1,286',
    trend: '较上月 +12.5%',
    trendClass: 'text-[#00b96b]',
    icon: User,
    iconClass: 'bg-[#e8f8f1] text-[#00b96b]',
  },
  {
    label: '今日访问',
    value: '3,642',
    trend: '较昨日 +8.2%',
    trendClass: 'text-[#00b96b]',
    icon: View,
    iconClass: 'bg-[#e8f3ff] text-[#1677ff]',
  },
  {
    label: '待办事项',
    value: '12',
    trend: '3 项待处理',
    trendClass: 'text-[#fa8c16]',
    icon: Document,
    iconClass: 'bg-[#fff4e6] text-[#fa8c16]',
  },
  {
    label: '系统消息',
    value: '5',
    trend: '2 条未读',
    trendClass: 'text-[#8b5cf6]',
    icon: Bell,
    iconClass: 'bg-[#f4ecff] text-[#8b5cf6]',
  },
]

const quickLinks: QuickLink[] = [
  {
    title: '用户管理',
    desc: '查看和维护用户信息',
    path: '/system/user',
    icon: User,
    iconClass: 'bg-[#e8f8f1] text-[#00b96b]',
  },
  {
    title: '登录页面',
    desc: '查看账号登录界面',
    path: '/login',
    icon: Key,
    iconClass: 'bg-[#e8f3ff] text-[#1677ff]',
  },
  {
    title: '注册页面',
    desc: '查看邮箱注册流程',
    path: '/register',
    icon: Document,
    iconClass: 'bg-[#fff4e6] text-[#fa8c16]',
  },
]

const activities: ActivityItem[] = [
  { title: '新用户 qa.userinfo 注册成功', time: '10 分钟前', dotClass: 'bg-[#00b96b]' },
  { title: '管理员登录系统', time: '1 小时前', dotClass: 'bg-[#1677ff]' },
  { title: '系统完成每日数据备份', time: '3 小时前', dotClass: 'bg-[#fa8c16]' },
  { title: '检测到 2 条待处理系统消息', time: '昨天 18:30', dotClass: 'bg-[#8b5cf6]' },
]

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const today = new Intl.DateTimeFormat('zh-CN', { dateStyle: 'full' }).format(new Date())
</script>

<template>
  <div class="space-y-4">
    <section
      class="relative overflow-hidden rounded-xl bg-gradient-to-br from-[#e8f8f1] via-[#f4fbf8] to-[#eef6ff] px-8 py-8"
    >
      <div
        class="pointer-events-none absolute -right-10 -top-16 h-48 w-48 rounded-full bg-[rgba(0,185,107,0.10)] blur-2xl"
      />
      <div
        class="pointer-events-none absolute -bottom-20 left-1/3 h-56 w-56 rounded-full bg-[rgba(45,212,191,0.10)] blur-3xl"
      />
      <div
        class="pointer-events-none absolute right-6 top-1/2 hidden -translate-y-1/2 select-none text-[88px] font-black tracking-[0.18em] text-[rgba(0,185,107,0.10)] xl:block"
      >
        WELCOME
      </div>

      <div class="relative z-10 max-w-xl">
        <p class="text-[13px] font-medium text-[#00a35e]">{{ greeting }}，管理员</p>
        <h1 class="mt-2 text-[28px] font-bold leading-tight text-[#1f2d3d]">
          欢迎使用用户信息管理平台
        </h1>
        <p class="mt-2 text-sm text-[#606266]">
          今天也是高效管理用户与系统配置的一天，祝您工作顺利。
        </p>
        <div class="mt-6 flex flex-wrap items-center gap-3">
          <el-button type="primary" round @click="router.push('/system/user')">
            进入用户管理
            <el-icon class="ml-1"><ArrowRight /></el-icon>
          </el-button>
          <span class="text-xs text-[#909399]">{{ today }}</span>
        </div>
      </div>
    </section>

    <section class="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
      <div
        v-for="stat in stats"
        :key="stat.label"
        class="rounded-xl border border-[#eef1f5] bg-[#fafbfc] p-4 transition-shadow hover:shadow-[0_6px_16px_rgba(0,0,0,0.06)]"
      >
        <div class="flex items-start justify-between">
          <div>
            <p class="text-[13px] text-[#909399]">{{ stat.label }}</p>
            <p class="mt-2 text-2xl font-semibold text-[#1f2d3d]">{{ stat.value }}</p>
          </div>
          <div
            class="flex h-10 w-10 items-center justify-center rounded-lg"
            :class="stat.iconClass"
          >
            <el-icon :size="20"><component :is="stat.icon" /></el-icon>
          </div>
        </div>
        <p class="mt-3 text-xs" :class="stat.trendClass">{{ stat.trend }}</p>
      </div>
    </section>

    <section class="grid grid-cols-1 gap-4 xl:grid-cols-3">
      <div class="rounded-xl border border-[#eef1f5] bg-[#fafbfc] p-5">
        <h2 class="text-[15px] font-semibold text-[#1f2d3d]">快捷入口</h2>
        <div class="mt-4 space-y-2">
          <RouterLink
            v-for="link in quickLinks"
            :key="link.path"
            :to="link.path"
            class="flex items-center gap-3 rounded-lg border border-transparent bg-white px-3 py-3 no-underline transition hover:border-[#d1f2e3] hover:bg-[#f2fbf7]"
          >
            <div
              class="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg"
              :class="link.iconClass"
            >
              <el-icon :size="18"><component :is="link.icon" /></el-icon>
            </div>
            <div class="min-w-0 flex-1">
              <p class="text-[13px] font-medium text-[#303133]">{{ link.title }}</p>
              <p class="mt-0.5 truncate text-xs text-[#909399]">{{ link.desc }}</p>
            </div>
            <el-icon :size="14" class="text-[#c0c4cc]"><ArrowRight /></el-icon>
          </RouterLink>
        </div>
        <div class="mt-3 flex items-center gap-2 rounded-lg bg-white px-3 py-2.5 text-xs text-[#909399]">
          <el-icon :size="14" class="text-[#00b96b]"><InfoFilled /></el-icon>
          更多功能正在持续完善中
        </div>
      </div>

      <div class="rounded-xl border border-[#eef1f5] bg-[#fafbfc] p-5 xl:col-span-2">
        <div class="flex items-center justify-between">
          <h2 class="text-[15px] font-semibold text-[#1f2d3d]">最近动态</h2>
          <span class="flex items-center gap-1 text-xs text-[#909399]">
            <el-icon :size="13"><Clock /></el-icon>
            实时更新
          </span>
        </div>
        <ul class="mt-3 space-y-1">
          <li
            v-for="(item, index) in activities"
            :key="index"
            class="flex items-start gap-3 rounded-lg px-2 py-2.5 transition-colors hover:bg-white"
          >
            <span class="mt-1.5 h-2 w-2 shrink-0 rounded-full" :class="item.dotClass" />
            <div class="min-w-0 flex-1">
              <p class="text-[13px] text-[#303133]">{{ item.title }}</p>
              <p class="mt-1 text-xs text-[#909399]">{{ item.time }}</p>
            </div>
          </li>
        </ul>
      </div>
    </section>
  </div>
</template>
