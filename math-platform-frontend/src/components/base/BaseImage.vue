<template>
  <div 
    :class="imageContainerClasses"
    :style="containerStyles"
  >
    <img
      v-if="!imageError && !loading"
      :src="currentSrc"
      :alt="alt"
      :class="imageClasses"
      @load="handleLoad"
      @error="handleError"
      @click="handleClick"
    />
    
    <!-- 加载状态 -->
    <div v-if="loading" class="image-loading">
      <a-spin size="large" />
    </div>
    
    <!-- 错误状态 -->
    <div v-if="imageError" class="image-error">
      <div class="error-content">
        <PictureOutlined class="error-icon" />
        <span class="error-text">{{ errorText }}</span>
      </div>
    </div>
    
    <!-- 悬浮遮罩 -->
    <div v-if="showOverlay && !loading && !imageError" class="image-overlay">
      <slot name="overlay">
        <div class="default-overlay">
          <slot name="overlay-content" />
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, type CSSProperties } from 'vue'
import { PictureOutlined } from '@ant-design/icons-vue'

interface Props {
  /** 图片源地址 */
  src: string
  /** 备用图片地址 */
  fallbackSrc?: string
  /** 替代文本 */
  alt?: string
  /** 宽度 */
  width?: string | number
  /** 高度 */
  height?: string | number
  /** 对象适应方式 */
  objectFit?: 'cover' | 'contain' | 'fill' | 'scale-down' | 'none'
  /** 圆角 */
  borderRadius?: string
  /** 是否显示悬浮遮罩 */
  showOverlay?: boolean
  /** 是否可点击 */
  clickable?: boolean
  /** 是否懒加载 */
  lazy?: boolean
  /** 错误提示文本 */
  errorText?: string
  /** 纵横比 */
  aspectRatio?: string
}

interface Emits {
  (e: 'click', event: MouseEvent): void
  (e: 'load', event: Event): void
  (e: 'error', event: Event): void
}

const props = withDefaults(defineProps<Props>(), {
  alt: '',
  objectFit: 'cover',
  showOverlay: false,
  clickable: false,
  lazy: true,
  errorText: '图片加载失败',
})

const emit = defineEmits<Emits>()

const loading = ref(true)
const imageError = ref(false)
const currentSrc = ref('')

const imageContainerClasses = computed(() => [
  'base-image-container',
  {
    'base-image-container--clickable': props.clickable,
    'base-image-container--loading': loading.value,
    'base-image-container--error': imageError.value,
  }
])

const imageClasses = computed(() => [
  'base-image',
  `base-image--${props.objectFit}`,
])

const containerStyles = computed((): CSSProperties => {
  const styles: CSSProperties = {}
  
  if (props.width) {
    styles.width = typeof props.width === 'number' ? `${props.width}px` : props.width
  }
  
  if (props.height) {
    styles.height = typeof props.height === 'number' ? `${props.height}px` : props.height
  }
  
  if (props.aspectRatio) {
    styles.aspectRatio = props.aspectRatio
  }
  
  if (props.borderRadius) {
    styles.borderRadius = props.borderRadius
  }
  
  return styles
})

const handleLoad = (event: Event) => {
  loading.value = false
  imageError.value = false
  emit('load', event)
}

const handleError = (event: Event) => {
  loading.value = false
  
  if (props.fallbackSrc && currentSrc.value !== props.fallbackSrc) {
    // 尝试加载备用图片
    currentSrc.value = props.fallbackSrc
    return
  }
  
  imageError.value = true
  emit('error', event)
}

const handleClick = (event: MouseEvent) => {
  if (props.clickable) {
    emit('click', event)
  }
}

onMounted(() => {
  currentSrc.value = props.src
})
</script>

<style scoped lang="scss">
.base-image-container {
  position: relative;
  display: inline-block;
  overflow: hidden;
  background: var(--color-neutral-100);
  
  &--clickable {
    cursor: pointer;
  }
  
  &--loading,
  &--error {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 120px;
  }
}

.base-image {
  width: 100%;
  height: 100%;
  transition: var(--transition-normal);
  
  &--cover {
    object-fit: cover;
  }
  
  &--contain {
    object-fit: contain;
  }
  
  &--fill {
    object-fit: fill;
  }
  
  &--scale-down {
    object-fit: scale-down;
  }
  
  &--none {
    object-fit: none;
  }
}

.image-loading {
  color: var(--color-neutral-400);
}

.image-error {
  color: var(--color-neutral-400);
  
  .error-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-2);
  }
  
  .error-icon {
    font-size: 2rem;
  }
  
  .error-text {
    font-size: var(--font-size-sm);
  }
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--color-surface-overlay);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: var(--transition-fast);
  
  .base-image-container:hover & {
    opacity: 1;
  }
  
  .default-overlay {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: var(--spacing-3);
  }
}
</style>
