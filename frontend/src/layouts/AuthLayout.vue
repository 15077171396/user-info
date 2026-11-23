<template>
  <div class="flex min-h-screen bg-[#0b0f19] text-white">
    <aside class="relative hidden w-[58%] flex-col justify-between overflow-hidden p-10 lg:flex">
      <div class="pointer-events-none absolute -left-24 top-1/4 h-80 w-80 rounded-full bg-[rgba(0,185,107,0.16)] blur-[100px]" />
      <div class="pointer-events-none absolute -right-20 bottom-1/4 h-96 w-96 rounded-full bg-[rgba(6,182,212,0.12)] blur-[120px]" />

      <div class="relative z-10 flex items-center gap-3">
        <div class="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-[#00b96b] to-[#2dd4bf] text-lg font-bold">
          U
        </div>
        <span class="text-xl font-semibold tracking-wide">UserInfo Admin</span>
      </div>

      <div class="relative z-10 mx-auto w-full max-w-[460px]">
        <div class="rounded-2xl border border-white/10 bg-white/5 p-5 shadow-[0_24px_80px_-24px_rgba(0,0,0,0.85)] backdrop-blur">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="h-2 w-2 rounded-full bg-[#f87171]" />
              <span class="h-2 w-2 rounded-full bg-[#fbbf24]" />
              <span class="h-2 w-2 rounded-full bg-[#34d399]" />
            </div>
            <div class="h-5 w-24 rounded-full bg-white/10" />
          </div>

          <div class="mt-5 space-y-3">
            <div class="flex items-center gap-3 rounded-xl bg-white/5 px-3 py-3">
              <div class="h-9 w-9 shrink-0 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#6366f1]" />
              <div class="min-w-0 flex-1">
                <div class="h-2.5 w-24 rounded-full bg-white/20" />
                <div class="mt-2 h-2 w-40 rounded-full bg-white/10" />
              </div>
              <div class="h-5 w-14 rounded-full bg-[rgba(52,211,153,0.16)]" />
            </div>
            <div class="flex items-center gap-3 rounded-xl bg-white/5 px-3 py-3">
              <div class="h-9 w-9 shrink-0 rounded-full bg-gradient-to-br from-[#22d3ee] to-[#3b82f6]" />
              <div class="min-w-0 flex-1">
                <div class="h-2.5 w-20 rounded-full bg-white/20" />
                <div class="mt-2 h-2 w-32 rounded-full bg-white/10" />
              </div>
              <div class="h-5 w-14 rounded-full bg-[rgba(96,165,250,0.16)]" />
            </div>
            <div class="flex items-center gap-3 rounded-xl bg-white/5 px-3 py-3">
              <div class="h-9 w-9 shrink-0 rounded-full bg-gradient-to-br from-[#f472b6] to-[#a855f7]" />
              <div class="min-w-0 flex-1">
                <div class="h-2.5 w-28 rounded-full bg-white/20" />
                <div class="mt-2 h-2 w-36 rounded-full bg-white/10" />
              </div>
              <div class="h-5 w-14 rounded-full bg-[rgba(251,191,36,0.16)]" />
            </div>
          </div>
        </div>

        <h1 class="mt-10 text-4xl font-bold leading-tight tracking-tight">用户信息管理平台</h1>
        <p class="mt-4 text-base text-gray-400">安全、简洁的账号注册与登录体验</p>
      </div>

      <p class="relative z-10 text-xs text-gray-500">© 2026 UserInfo Admin</p>
    </aside>

    <main class="flex w-full items-center justify-center bg-[#101318] px-6 py-12 lg:w-[42%]">
      <div class="w-full max-w-[400px]">
        <div class="mb-10 flex items-center gap-3 lg:hidden">
          <div class="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-[#00b96b] to-[#2dd4bf] text-lg font-bold">
            U
          </div>
          <span class="text-xl font-semibold tracking-wide">UserInfo Admin</span>
        </div>
        <slot />
      </div>
    </main>
  </div>
</template>
