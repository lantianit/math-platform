<template>
  <div 
    :class="cardClasses"
    :style="cardStyles"
    @click="handleClick"
  >
    <div v-if="loading" class="card-loading">
      <a-skeleton :loading="true" :paragraph="{ rows: 3 }" />
    </div>
    <div v-else class="card-content">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, type CSSProperties } from 'vue'

interface Props {
  /** 卡片变体 */
  variant?: 'default' | 'elevated' | 'outlined' | 'ghost'
  /** 尺寸 */
  size?: 'sm' | 'md' | 'lg'
  /** 是否可悬停 */
  hoverable?: boolean
  /** 是否可点击 */
  clickable?: boolean
  /** 加载状态 */
  loading?: boolean
  /** 自定义圆角 */
  borderRadius?: string
  /** 自定义阴影 */
  shadow?: string
  /** 是否全宽 */
  fullWidth?: boolean
}

interface Emits {
  (e: 'click', event: MouseEvent): void
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  size: 'md',
  hoverable: false,
  clickable: false,
  loading: false,
  fullWidth: false,
})

const emit = defineEmits<Emits>()

const cardClasses = computed(() => [
  'base-card',
  `base-card--${props.variant}`,
  `base-card--${props.size}`,
  {
    'base-card--hoverable': props.hoverable,
    'base-card--clickable': props.clickable,
    'base-card--loading': props.loading,
    'base-card--full-width': props.fullWidth,
  }
])

const cardStyles = computed((): CSSProperties => ({
  borderRadius: props.borderRadius || undefined,
  boxShadow: props.shadow || undefined,
}))

const handleClick = (event: MouseEvent) => {
  if (props.clickable && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped lang="scss">
.base-card {
  position: relative;
  background: var(--color-surface-card);
  border-radius: var(--radius-lg);
  transition: var(--transition-normal);
  overflow: hidden;

  // 变体样式
  &--default {
    border: 1px solid var(--color-neutral-200);
  }

  &--elevated {
    box-shadow: var(--shadow-md);
    border: none;
  }

  &--outlined {
    border: 2px solid var(--color-neutral-200);
    background: transparent;
  }

  &--ghost {
    background: transparent;
    border: none;
  }

  // 尺寸样式
  &--sm {
    padding: var(--spacing-3);
  }

  &--md {
    padding: var(--spacing-4);
  }

  &--lg {
    padding: var(--spacing-6);
  }

  // 交互状态
  &--hoverable:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-lg);
  }

  &--clickable {
    cursor: pointer;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: var(--shadow-md);
    }

    &:active {
      transform: translateY(0);
    }
  }

  &--full-width {
    width: 100%;
  }

  // 加载状态
  &--loading {
    pointer-events: none;
  }

  .card-loading {
    padding: var(--spacing-4);
  }

  .card-content {
    position: relative;
    z-index: 1;
  }
}
</style>
