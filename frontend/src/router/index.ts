import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    breadcrumb?: string[]
    affix?: boolean
    dark?: boolean
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('../layouts/AdminLayout.vue'),
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('../views/HomeView.vue'),
        meta: { title: '首页', affix: true },
      },
      {
        path: 'system/user',
        name: 'system-user',
        component: () => import('../views/system/UserView.vue'),
        meta: { title: '用户管理', breadcrumb: ['系统管理', '用户管理'] },
      },
    ],
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { dark: true },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue'),
    meta: { dark: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 登录/注册页使用暗色主题，后台页面使用亮色主题
router.afterEach((to) => {
  document.documentElement.classList.toggle('dark', to.meta.dark === true)
})

export default router
