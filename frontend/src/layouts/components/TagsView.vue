<script setup lang="ts">
import { Close } from '@element-plus/icons-vue'

interface ViewTag {
  path: string
  title: string
  affix?: boolean
}

const route = useRoute()
const router = useRouter()
const tags = ref<ViewTag[]>([{ path: '/', title: '首页', affix: true }])

function addCurrentTag() {
  const title = route.meta.title
  if (!title) return
  if (tags.value.some(tag => tag.path === route.path)) return
  tags.value.push({ path: route.path, title, affix: route.meta.affix === true })
}

watch(() => route.path, addCurrentTag, { immediate: true })

function isActive(tag: ViewTag) {
  return route.path === tag.path
}

function handleClick(tag: ViewTag) {
  if (!isActive(tag)) {
    void router.push(tag.path)
  }
}

function handleClose(tag: ViewTag) {
  if (tag.affix) return

  const index = tags.value.findIndex(item => item.path === tag.path)
  if (index === -1) return

  tags.value.splice(index, 1)
  if (isActive(tag)) {
    const fallback = tags.value[tags.value.length - 1]
    void router.push(fallback ? fallback.path : '/')
  }
}
</script>

<template>
  <div
    class="flex h-10 shrink-0 items-center gap-1 overflow-x-auto border-b border-[#e5e7eb] bg-white px-2"
  >
    <button
      v-for="tag in tags"
      :key="tag.path"
      type="button"
      class="group flex h-full shrink-0 items-center gap-1.5 border-x-0 border-t-0 border-b-2 px-3 text-[13px] transition-colors"
      :class="
        isActive(tag)
          ? 'border-b-[#00b96b] bg-[#e8f8f1] text-[#00b96b]'
          : 'border-b-transparent text-[#606266] hover:bg-[#f5f7fa] hover:text-[#00b96b]'
      "
      @click="handleClick(tag)"
    >
      {{ tag.title }}
      <el-icon
        v-if="!tag.affix"
        :size="12"
        class="opacity-60 transition hover:text-[#f56c6c] group-hover:opacity-100"
        @click.stop="handleClose(tag)"
      >
        <Close />
      </el-icon>
    </button>
  </div>
</template>
