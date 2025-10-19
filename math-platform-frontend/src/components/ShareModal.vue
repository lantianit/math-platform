<template>
  <a-modal 
    v-model:open="visible" 
    :title="title"
    :footer="false"
    width="500px"
    centered
    :destroyOnClose="true"
    @cancel="closeModal"
    class="share-modal"
  >
    <!-- 原生分享（移动端） -->
    <div v-if="showNativeShare" class="share-section">
      <h4 class="section-title">📱 快速分享</h4>
      <a-button 
        type="primary" 
        block 
        size="large"
        @click="handleNativeShare"
        :loading="nativeShareLoading"
      >
        <template #icon>
          <ShareAltOutlined />
        </template>
        打开系统分享
      </a-button>
    </div>
    
    <a-divider v-if="showNativeShare" />
    
    <!-- 复制链接 -->
    <div class="share-section">
      <h4 class="section-title">🔗 复制分享链接</h4>
      <a-typography-paragraph 
        :copyable="{ 
          text: link,
          tooltips: ['点击复制', '复制成功！']
        }"
        class="link-text"
      >
        {{ link }}
      </a-typography-paragraph>
    </div>
    
    <a-divider />
    
    <!-- 二维码扫码 -->
    <div class="share-section">
      <h4 class="section-title">📲 手机扫码查看</h4>
      <div class="qrcode-container">
        <a-spin :spinning="qrcodeLoading" tip="生成中...">
          <a-qrcode 
            :value="link" 
            :size="200"
            :status="qrcodeStatus"
            @refresh="handleQRCodeRefresh"
          />
        </a-spin>
        <p class="qrcode-tip">使用手机相机或微信扫一扫</p>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { message } from 'ant-design-vue'
import { ShareAltOutlined } from '@ant-design/icons-vue'

/**
 * 组件属性类型定义
 */
interface Props {
  title?: string
  link?: string
}

/**
 * 组件属性，设置默认值
 */
const props = withDefaults(defineProps<Props>(), {
  title: '分享',
  link: ''
})

// 弹窗可见性状态
const visible = ref(false)

// 加载状态
const nativeShareLoading = ref(false)
const qrcodeLoading = ref(false)
const qrcodeStatus = ref<'active' | 'expired' | 'loading'>('active')

/**
 * 检测是否支持原生分享 API
 * 只在 HTTPS 环境和支持的浏览器中可用
 */
const showNativeShare = computed(() => {
  return typeof navigator !== 'undefined' && 
         'share' in navigator &&
         window.isSecureContext
})

/**
 * 打开分享弹窗
 */
const openModal = () => {
  if (!props.link) {
    message.warning('分享链接为空')
    return
  }
  visible.value = true
  qrcodeStatus.value = 'active'
}

/**
 * 关闭分享弹窗
 */
const closeModal = () => {
  visible.value = false
  nativeShareLoading.value = false
  qrcodeLoading.value = false
}

/**
 * 处理原生分享
 * 调用浏览器原生分享 API
 */
const handleNativeShare = async () => {
  if (!navigator.share) {
    message.error('当前浏览器不支持原生分享')
    return
  }

  nativeShareLoading.value = true

  try {
    await navigator.share({
      title: props.title,
      text: `分享：${props.title}`,
      url: props.link,
    })
    message.success('分享成功')
    closeModal()
  } catch (error: any) {
    // 用户取消分享不显示错误
    if (error.name !== 'AbortError') {
      console.error('分享失败：', error)
      message.error('分享失败，请重试')
    }
  } finally {
    nativeShareLoading.value = false
  }
}

/**
 * 处理二维码刷新
 */
const handleQRCodeRefresh = () => {
  qrcodeLoading.value = true
  qrcodeStatus.value = 'loading'
  
  // 模拟刷新延迟
  setTimeout(() => {
    qrcodeLoading.value = false
    qrcodeStatus.value = 'active'
  }, 500)
}

/**
 * 监听链接变化，重置二维码状态
 */
watch(() => props.link, () => {
  if (visible.value) {
    qrcodeStatus.value = 'active'
  }
})

/**
 * 暴露方法给父组件
 */
defineExpose({
  openModal,
  closeModal,
})
</script>

<style scoped lang="scss">
.share-modal {
  :deep(.ant-modal-body) {
    padding: 24px;
  }
}

.share-section {
  margin-bottom: 8px;

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0 0 16px 0;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .link-text {
    background: #f5f5f5;
    padding: 12px 16px;
    border-radius: 8px;
    border: 1px solid #e8e8e8;
    margin-bottom: 0;
    word-break: break-all;
    font-size: 14px;
    color: #595959;
    line-height: 1.6;
    
    :deep(.ant-typography-copy) {
      color: #1890ff;
      margin-left: 8px;
      
      &:hover {
        color: #40a9ff;
      }
    }
  }

  .qrcode-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16px;
    background: #fafafa;
    border-radius: 12px;
    border: 1px solid #f0f0f0;

    :deep(.ant-qrcode) {
      background: white;
      padding: 12px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }

    .qrcode-tip {
      margin-top: 16px;
      margin-bottom: 0;
      font-size: 13px;
      color: #8c8c8c;
      text-align: center;
      line-height: 1.5;
    }
  }
}

/* 响应式设计 */
@media (max-width: 576px) {
  .share-modal {
    :deep(.ant-modal) {
      max-width: calc(100vw - 32px);
      margin: 16px;
    }

    :deep(.ant-modal-body) {
      padding: 16px;
    }
  }

  .share-section {
    .section-title {
      font-size: 15px;
    }

    .link-text {
      font-size: 13px;
      padding: 10px 12px;
    }

    .qrcode-container {
      padding: 12px;

      :deep(.ant-qrcode) {
        padding: 8px;
      }

      :deep(.ant-qrcode canvas) {
        max-width: 160px !important;
        height: auto !important;
      }

      .qrcode-tip {
        font-size: 12px;
        margin-top: 12px;
      }
    }
  }
}

/* 动画效果 */
.share-section {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 按钮 hover 效果优化 */
:deep(.ant-btn-primary) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  }

  &:active {
    transform: translateY(0);
  }
}
</style>

