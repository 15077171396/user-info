<script setup lang="ts">
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'

interface UserRow {
  id: number
  username: string
  nickname: string
  email: string
  role: string
  status: 0 | 1
  createdAt: string
}

const users = ref<UserRow[]>([
  {
    id: 1,
    username: 'admin',
    nickname: '超级管理员',
    email: 'admin@example.com',
    role: '管理员',
    status: 1,
    createdAt: '2026-01-12 09:30',
  },
  {
    id: 2,
    username: 'zhangwei',
    nickname: '张伟',
    email: 'zhangwei@example.com',
    role: '运营',
    status: 1,
    createdAt: '2026-02-08 14:12',
  },
  {
    id: 3,
    username: 'liuyang',
    nickname: '刘洋',
    email: 'liuyang@example.com',
    role: '运营',
    status: 0,
    createdAt: '2026-02-19 10:05',
  },
  {
    id: 4,
    username: 'wangfang',
    nickname: '王芳',
    email: 'wangfang@example.com',
    role: '访客',
    status: 1,
    createdAt: '2026-03-03 16:48',
  },
  {
    id: 5,
    username: 'chenjie',
    nickname: '陈杰',
    email: 'chenjie@example.com',
    role: '运营',
    status: 1,
    createdAt: '2026-03-21 08:26',
  },
  {
    id: 6,
    username: 'zhaomin',
    nickname: '赵敏',
    email: 'zhaomin@example.com',
    role: '访客',
    status: 0,
    createdAt: '2026-04-02 11:37',
  },
  {
    id: 7,
    username: 'sunli',
    nickname: '孙丽',
    email: 'sunli@example.com',
    role: '管理员',
    status: 1,
    createdAt: '2026-04-18 15:20',
  },
  {
    id: 8,
    username: 'zhouqiang',
    nickname: '周强',
    email: 'zhouqiang@example.com',
    role: '访客',
    status: 1,
    createdAt: '2026-05-06 09:54',
  },
])

const query = reactive({ keyword: '', status: '' })
const page = ref(1)
const pageSize = 6

const filteredUsers = computed(() => {
  const keyword = query.keyword.trim().toLowerCase()
  return users.value.filter((user) => {
    const matchKeyword =
      !keyword ||
      user.username.toLowerCase().includes(keyword) ||
      user.nickname.toLowerCase().includes(keyword) ||
      user.email.toLowerCase().includes(keyword)
    const matchStatus = query.status === '' || String(user.status) === query.status
    return matchKeyword && matchStatus
  })
})

const pagedUsers = computed(() => {
  const start = (page.value - 1) * pageSize
  return filteredUsers.value.slice(start, start + pageSize)
})

watch(
  () => [query.keyword, query.status],
  () => {
    page.value = 1
  },
)

watch(filteredUsers, (list) => {
  const maxPage = Math.max(1, Math.ceil(list.length / pageSize))
  if (page.value > maxPage) {
    page.value = maxPage
  }
})

const avatarStyles: Record<string, string>[] = [
  { '--el-avatar-bg-color': '#e8f8f1', '--el-avatar-text-color': '#00b96b' },
  { '--el-avatar-bg-color': '#e8f3ff', '--el-avatar-text-color': '#1677ff' },
  { '--el-avatar-bg-color': '#fff4e6', '--el-avatar-text-color': '#fa8c16' },
  { '--el-avatar-bg-color': '#f4ecff', '--el-avatar-text-color': '#8b5cf6' },
]

function handleSearch() {
  page.value = 1
}

function handleReset() {
  query.keyword = ''
  query.status = ''
  page.value = 1
}

function handleAdd() {
  ElMessage.info('演示页面，暂未接入新增用户接口')
}

function handleEdit() {
  ElMessage.info('演示页面，暂未接入编辑用户接口')
}

async function handleDelete(row: UserRow) {
  try {
    await ElMessageBox.confirm(`确定删除用户「${row.username}」吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
    users.value = users.value.filter(user => user.id !== row.id)
    ElMessage.success('删除成功')
  } catch {
    // 取消删除
  }
}
</script>

<template>
  <div>
    <div class="flex flex-wrap items-center justify-between gap-3">
      <div>
        <h2 class="m-0 text-xl font-semibold text-[#1f2d3d]">用户管理</h2>
        <p class="mt-1 text-[13px] text-[#909399]">管理系统用户信息、角色与账号状态</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
    </div>

    <div class="mt-5 flex flex-wrap items-center gap-3 rounded-lg bg-[#f7f8fa] p-3">
      <div class="w-[260px]">
        <el-input
          v-model="query.keyword"
          placeholder="搜索用户名 / 昵称 / 邮箱"
          clearable
          :prefix-icon="Search"
        />
      </div>
      <div class="w-[140px]">
        <el-select v-model="query.status" placeholder="全部状态">
          <el-option label="全部状态" value="" />
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
      </div>
      <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
    </div>

    <el-table
      :data="pagedUsers"
      class="mt-4 w-full"
      style="--el-table-header-bg-color: #fafafa; --el-table-header-text-color: #606266"
    >
      <el-table-column label="用户" min-width="180">
        <template #default="{ row, $index }">
          <div class="flex items-center gap-2">
            <el-avatar :size="28" class="text-xs" :style="avatarStyles[$index % avatarStyles.length]">
              {{ row.nickname.slice(0, 1) }}
            </el-avatar>
            <div>
              <p class="text-[13px] font-medium text-[#303133]">{{ row.nickname }}</p>
              <p class="text-xs text-[#909399]">@{{ row.username }}</p>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="200" />
      <el-table-column prop="role" label="角色" width="110" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag
            :type="row.status === 1 ? 'success' : 'info'"
            effect="light"
            round
            size="small"
          >
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ $index }">
          <el-button link type="primary" :icon="Edit" @click="handleEdit">编辑</el-button>
          <el-button
            link
            type="danger"
            :icon="Delete"
            @click="handleDelete(pagedUsers[$index])"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="mt-4 flex justify-end">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="filteredUsers.length"
        layout="total, prev, pager, next"
        background
      />
    </div>
  </div>
</template>
