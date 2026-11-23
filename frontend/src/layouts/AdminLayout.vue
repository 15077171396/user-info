<script setup lang="ts">
import type { Component } from 'vue'
import {
  ArrowDown,
  Bell,
  Expand,
  Fold,
  FullScreen,
  HomeFilled,
  Search,
  Setting,
  SwitchButton,
  User,
  UserFilled,
} from '@element-plus/icons-vue'
import TagsView from './components/TagsView.vue'
import { useUserStore } from '../stores/user'

interface MenuItem {
  path: string
  title: string
  icon: Component
  children?: MenuItem[]
}

const menuItems: MenuItem[] = [
  { path: '/', title: '首页', icon: HomeFilled },
  {
    path: '/system',
    title: '系统管理',
    icon: Setting,
    children: [{ path: '/system/user', title: '用户管理', icon: User }],
  },
]

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const isFullscreen = ref(false)

const breadcrumbs = computed(() => route.meta.breadcrumb ?? [])

function toggleCollapse() {
  isCollapse.value = !isCollapse.value
}

function syncFullscreen() {
  isFullscreen.value = Boolean(document.fullscreenElement)
}

function toggleFullscreen() {
  if (document.fullscreenElement) {
    void document.exitFullscreen()
  } else {
    void document.documentElement.requestFullscreen()
  }
}

onMounted(() => document.addEventListener('fullscreenchange', syncFullscreen))
onUnmounted(() => document.removeEventListener('fullscreenchange', syncFullscreen))

onMounted(async () => {
  if (!userStore.token) return

  try {
    await userStore.fetchCurrentUser()
  } catch {
    userStore.clear()
    await router.replace('/login')
  }
})

async function handleCommand(command: string | number | object) {
  if (command !== 'logout') return

  await userStore.logout()
  ElMessage.success('已退出登录')
  await router.replace('/login')
}
</script>

<template>
  <div class="flex h-screen w-full overflow-hidden bg-[#f0f2f5] text-[#303133]">
    <aside
      class="flex h-full shrink-0 flex-col border-r border-[#e5e7eb] bg-white transition-[width] duration-200"
      :class="isCollapse ? 'w-16' : 'w-[200px]'"
    >
      <div
        class="flex h-[50px] shrink-0 items-center gap-2 overflow-hidden border-b border-[#eef0f3] px-4"
        :class="isCollapse ? 'justify-center px-0' : ''"
      >
        <div
          class="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-gradient-to-br from-[#00b96b] to-[#2dd4bf] text-base font-bold text-white shadow-[0_4px_10px_rgba(0,185,107,0.25)]"
        >
          U
        </div>
        <span v-if="!isCollapse" class="truncate text-[15px] font-semibold tracking-wide">
          UserInfo Admin
        </span>
      </div>

      <el-scrollbar class="min-h-0 flex-1">
        <el-menu
          :default-active="route.path"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="border-r-0 [&_.el-menu-item.is-active]:bg-[#e8f8f1] [&_.el-menu-item.is-active]:shadow-[inset_3px_0_0_#00b96b]"
          style="
            --el-menu-item-height: 44px;
            --el-sub-menu-title-height: 44px;
            --el-menu-active-color: #00b96b;
            --el-menu-hover-bg-color: #f5f7fa;
            --el-menu-text-color: #4b5563;
            --el-menu-bg-color: #fff;
            --el-menu-base-level-padding: 18px;
            --el-menu-level-padding: 18px;
            --el-menu-item-font-size: 14px;
          "
        >
          <template v-for="item in menuItems" :key="item.path">
            <el-sub-menu v-if="item.children?.length" :index="item.path">
              <template #title>
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.title }}</span>
              </template>
              <el-menu-item
                v-for="child in item.children"
                :key="child.path"
                :index="child.path"
              >
                <el-icon><component :is="child.icon" /></el-icon>
                <template #title>{{ child.title }}</template>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="item.path">
              <el-icon><component :is="item.icon" /></el-icon>
              <template #title>{{ item.title }}</template>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </aside>

    <div class="flex min-w-0 flex-1 flex-col">
      <header
        class="flex h-[50px] shrink-0 items-center justify-between border-b border-[#e5e7eb] bg-white px-3"
      >
        <div class="flex items-center gap-2">
          <el-button text circle aria-label="折叠菜单" @click="toggleCollapse">
            <el-icon :size="18" class="text-[#606266]">
              <Expand v-if="isCollapse" />
              <Fold v-else />
            </el-icon>
          </el-button>

          <el-breadcrumb separator="/" class="text-[13px]">
            <el-breadcrumb-item :to="{ path: '/' }">
              <span class="flex items-center gap-1">
                <el-icon :size="14"><HomeFilled /></el-icon>
                首页
              </span>
            </el-breadcrumb-item>
            <el-breadcrumb-item v-for="crumb in breadcrumbs" :key="crumb">
              {{ crumb }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="flex items-center">
          <el-button text circle aria-label="搜索">
            <el-icon :size="17" class="text-[#606266]"><Search /></el-icon>
          </el-button>

          <el-badge :value="3" :offset="[-2, 2]">
            <el-button text circle aria-label="通知">
              <el-icon :size="17" class="text-[#606266]"><Bell /></el-icon>
            </el-button>
          </el-badge>

          <el-button
            text
            circle
            :aria-label="isFullscreen ? '退出全屏' : '进入全屏'"
            @click="toggleFullscreen"
          >
            <el-icon :size="17" class="text-[#606266]"><FullScreen /></el-icon>
          </el-button>

          <div class="mx-2 h-5 w-px bg-[#e5e7eb]" />

          <el-dropdown trigger="click" @command="handleCommand">
            <div
              class="flex cursor-pointer items-center gap-2 rounded-md px-2 py-1 transition-colors hover:bg-[#f5f7fa]"
            >
              <el-avatar
                :size="30"
                :icon="UserFilled"
                style="--el-avatar-bg-color: #00b96b"
              />
              <span class="max-w-[120px] truncate text-[13px] text-[#303133]">
                {{ userStore.nickname }}
              </span>
              <el-icon :size="12" class="text-[#909399]"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout" :icon="SwitchButton">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <TagsView />

      <main class="min-h-0 flex-1 overflow-y-auto bg-[#f0f2f5] p-3">
        <div class="flex min-h-[calc(100%-24px)] flex-col">
          <div class="flex-1 rounded-lg bg-white p-5 shadow-[0_1px_4px_rgba(0,21,41,0.06)]">
            <RouterView />
          </div>
          <footer class="flex h-11 shrink-0 items-center justify-center text-xs text-[#909399]">
            © 2026 UserInfo Admin
          </footer>
        </div>
      </main>
    </div>
  </div>
</template>
