<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh; display: flex; flex-direction: column;">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://github.com/lantianit" target="_blank"> zh </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
</script>

<style scoped>
#basicLayout .header {
  padding-inline: 20px;
  background: white;
  color: unset;
  margin-bottom: 1px;
  overflow: visible; /* 允许搜索建议面板溢出显示 */
  position: relative;
  z-index: 10;
}

#basicLayout :deep(.ant-menu-root) {
  border-bottom: none !important;
  border-inline-end: none !important;
}

#basicLayout .content {
  padding: 28px;
  background: linear-gradient(to right, #fefefe, #fff) !important;
  flex: 1;
  min-height: 0;
  /* 强制白色背景，覆盖任何暗色主题 */
}

#basicLayout .footer {
  background: #efefef;
  padding: 16px;
  text-align: center;
  margin-top: auto;
}
</style>
