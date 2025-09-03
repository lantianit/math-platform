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
  ],
})

export default router
