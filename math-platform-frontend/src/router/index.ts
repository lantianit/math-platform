import {createRouter, createWebHistory} from 'vue-router'
import { menuConfig, convertToRoutes } from '@/constants/menu.ts'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from "@/pages/admin/UserManagePage.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 从菜单配置生成的路由
    ...convertToRoutes(menuConfig),
    {
      path: '/post/create',
      name: '发布帖子',
      component: () => import('@/pages/PostCreatePage.vue'),
    },
    {
      path: '/post/:id',
      name: '帖子详情',
      component: () => import('@/pages/PostDetailPage.vue'),
    },
    {
      path: '/search',
      name: '搜索结果',
      component: () => import('@/pages/SearchPage.vue'),
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegisterPage,
    },
    {
      path: '/user/edit',
      name: '资料编辑',
      component: () => import('@/pages/user/UserEditPage.vue'),
    },
    {
      path: '/user/:id',
      name: '用户主页',
      component: () => import('@/pages/user/UserProfilePage.vue'),
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/notifications',
      name: '通知中心',
      component: () => import('@/pages/NotificationsPage.vue'),
    },
    // 壁纸相关路由
    {
      path: '/wallpaper',
      name: '学习加油壁纸',
      component: () => import('@/pages/WallpaperPageNatural.vue'),
    },
    {
      path: '/wallpaper/enterprise',
      name: '壁纸页面(企业版)',
      component: () => import('@/pages/WallpaperPageEnterprise.vue'),
    },
    {
      path: '/wallpaper/legacy',
      name: '壁纸页面(旧版)',
      component: () => import('@/pages/WallpaperPage.vue'),
    },
    {
      path: '/admin/wallpaper',
      name: '壁纸管理',
      component: () => import('@/pages/admin/WallpaperManagePage.vue'),
    },
    {
      path: '/admin/wallpaper/batch-crawl',
      name: '批量抓取壁纸',
      component: () => import('@/pages/admin/WallpaperBatchCrawlPage.vue'),
    },
  ],
})

export default router
