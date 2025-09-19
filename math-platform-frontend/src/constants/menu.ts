import { h } from 'vue'
import { HomeOutlined, PictureOutlined } from '@ant-design/icons-vue'
import type { MenuProps } from 'ant-design-vue'

// 菜单项配置接口
export interface MenuItem {
  key: string
  label: string
  title: string
  icon?: () => any
  component?: any
  props?: boolean
  requireAdmin?: boolean
  external?: boolean
  href?: string
}

// 统一的菜单配置
export const menuConfig: MenuItem[] = [
  {
    key: '/',
    label: '主页',
    title: '主页',
    icon: () => h(HomeOutlined),
    component: () => import('@/pages/HomePage.vue'),
  },
  {
    key: '/wallpaper',
    label: '学习加油壁纸',
    title: '学习加油壁纸',
    icon: () => h(PictureOutlined),
    component: () => import('@/pages/WallpaperPage.vue'),
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
    component: () => import('@/pages/admin/UserManagePage.vue'),
    requireAdmin: true,
  }
  ,
  {
    key: '/admin/avatarReview',
    label: '头像审核',
    title: '头像审核',
    component: () => import('@/pages/admin/AvatarReviewPage.vue'),
    requireAdmin: true,
  },
  {
    key: '/admin/wallpaper',
    label: '壁纸管理',
    title: '壁纸管理',
    component: () => import('@/pages/admin/WallpaperManagePage.vue'),
    requireAdmin: true,
  }
]

// 根据用户权限过滤菜单项
export const filterMenusByPermission = (menus: MenuItem[], isAdmin: boolean): MenuItem[] => {
  return menus.filter((menu) => {
    if (menu.requireAdmin && !isAdmin) {
      return false
    }
    return true
  })
}

// 转换为 Ant Design Menu 格式
export const convertToAntMenuItems = (menus: MenuItem[]): MenuProps['items'] => {
  return menus.map((menu) => {
    const item: any = {
      key: menu.key,
      label: menu.external
        ? h('a', { href: menu.href, target: '_blank' }, menu.label)
        : menu.label,
      title: menu.title,
    }

    if (menu.icon) {
      item.icon = menu.icon
    }

    return item
  })
}

// 转换为路由配置
export const convertToRoutes = (menus: MenuItem[]) => {
  return menus
    .filter(menu => !menu.external) // 排除外部链接
    .map((menu) => ({
      path: menu.key,
      name: menu.title,
      component: menu.component,
      props: menu.props,
    }))
}
