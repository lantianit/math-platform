import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { useBreakpoints, breakpointsTailwind } from '@vueuse/core'

/**
 * UI状态管理
 */
export const useUIStore = defineStore('ui', () => {
  // 响应式断点
  const breakpoints = useBreakpoints(breakpointsTailwind)
  
  // 侧边栏状态
  const leftSidebarVisible = ref(true)
  const rightSidebarVisible = ref(true)
  const mobileMenuVisible = ref(false)
  
  // 主题设置（仅保留主色调，移除暗色模式）
  const primaryColor = ref('#1890ff')
  
  // 加载状态
  const globalLoading = ref(false)
  const pageLoading = ref(false)
  
  // 滚动状态
  const scrollY = ref(0)
  const isScrollingUp = ref(false)
  const showBackToTop = ref(false)
  
  // 搜索框状态
  const searchFocused = ref(false)
  const searchExpanded = ref(false)

  // 响应式计算属性
  const isMobile = computed(() => breakpoints.smaller('md'))
  const isTablet = computed(() => breakpoints.between('md', 'lg'))
  const isDesktop = computed(() => breakpoints.greaterOrEqual('lg'))
  const isWide = computed(() => breakpoints.greaterOrEqual('xl'))

  // 布局计算属性
  const shouldShowLeftSidebar = computed(() => {
    if (isMobile.value) return false
    return leftSidebarVisible.value
  })

  const shouldShowRightSidebar = computed(() => {
    if (isMobile.value || isTablet.value) return false
    return rightSidebarVisible.value
  })

  const mainContentSpan = computed(() => {
    if (isMobile.value) return 24
    if (isTablet.value) return shouldShowLeftSidebar.value ? 18 : 24
    
    // 桌面端
    let span = 24
    if (shouldShowLeftSidebar.value) span -= 5
    if (shouldShowRightSidebar.value) span -= 5
    return span
  })

  const leftSidebarSpan = computed(() => {
    if (isMobile.value) return 0
    if (isTablet.value) return 6
    return 5
  })

  const rightSidebarSpan = computed(() => {
    if (isMobile.value || isTablet.value) return 0
    return 5
  })

  /**
   * 切换左侧边栏
   */
  const toggleLeftSidebar = () => {
    leftSidebarVisible.value = !leftSidebarVisible.value
  }

  /**
   * 切换右侧边栏
   */
  const toggleRightSidebar = () => {
    rightSidebarVisible.value = !rightSidebarVisible.value
  }

  /**
   * 切换移动端菜单
   */
  const toggleMobileMenu = () => {
    mobileMenuVisible.value = !mobileMenuVisible.value
  }

  /**
   * 关闭移动端菜单
   */
  const closeMobileMenu = () => {
    mobileMenuVisible.value = false
  }

  // 主题切换功能已移除，统一使用白色模式

  /**
   * 设置主色调
   */
  const setPrimaryColor = (color: string) => {
    primaryColor.value = color
    // 这里可以添加动态主题色更改逻辑
  }

  /**
   * 显示全局加载
   */
  const showGlobalLoading = () => {
    globalLoading.value = true
  }

  /**
   * 隐藏全局加载
   */
  const hideGlobalLoading = () => {
    globalLoading.value = false
  }

  /**
   * 显示页面加载
   */
  const showPageLoading = () => {
    pageLoading.value = true
  }

  /**
   * 隐藏页面加载
   */
  const hidePageLoading = () => {
    pageLoading.value = false
  }

  /**
   * 更新滚动位置
   */
  const updateScrollPosition = (y: number) => {
    const previousY = scrollY.value
    scrollY.value = y
    isScrollingUp.value = y < previousY
    showBackToTop.value = y > 300
  }

  /**
   * 滚动到顶部
   */
  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }

  /**
   * 设置搜索框焦点状态
   */
  const setSearchFocus = (focused: boolean) => {
    searchFocused.value = focused
    if (focused && isMobile.value) {
      searchExpanded.value = true
    }
  }

  /**
   * 切换搜索框展开状态
   */
  const toggleSearchExpanded = () => {
    searchExpanded.value = !searchExpanded.value
  }

  /**
   * 重置UI状态
   */
  const resetUIState = () => {
    leftSidebarVisible.value = true
    rightSidebarVisible.value = true
    mobileMenuVisible.value = false
    globalLoading.value = false
    pageLoading.value = false
    searchFocused.value = false
    searchExpanded.value = false
  }

  /**
   * 获取响应式栅格配置
   */
  const getResponsiveGrid = () => {
    return {
      xs: 24,
      sm: 24,
      md: shouldShowLeftSidebar.value ? 18 : 24,
      lg: mainContentSpan.value,
      xl: mainContentSpan.value,
      xxl: mainContentSpan.value
    }
  }

  /**
   * 获取侧边栏栅格配置
   */
  const getSidebarGrid = (side: 'left' | 'right') => {
    const span = side === 'left' ? leftSidebarSpan.value : rightSidebarSpan.value
    return {
      xs: 0,
      sm: 0,
      md: side === 'left' ? 6 : 0,
      lg: span,
      xl: span,
      xxl: span
    }
  }

  return {
    // 响应式状态
    breakpoints,
    isMobile,
    isTablet,
    isDesktop,
    isWide,
    
    // UI状态
    leftSidebarVisible,
    rightSidebarVisible,
    mobileMenuVisible,
    primaryColor,
    globalLoading,
    pageLoading,
    scrollY,
    isScrollingUp,
    showBackToTop,
    searchFocused,
    searchExpanded,
    
    // 布局计算属性
    shouldShowLeftSidebar,
    shouldShowRightSidebar,
    mainContentSpan,
    leftSidebarSpan,
    rightSidebarSpan,
    
    // 方法
    toggleLeftSidebar,
    toggleRightSidebar,
    toggleMobileMenu,
    closeMobileMenu,
    setPrimaryColor,
    showGlobalLoading,
    hideGlobalLoading,
    showPageLoading,
    hidePageLoading,
    updateScrollPosition,
    scrollToTop,
    setSearchFocus,
    toggleSearchExpanded,
    resetUIState,
    getResponsiveGrid,
    getSidebarGrid
  }
})
